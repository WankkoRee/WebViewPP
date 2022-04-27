package cn.wankkoree.xposed.enablewebviewdebugging.hook

import android.util.Base64
import cn.wankkoree.xposed.enablewebviewdebugging.BuildConfig
import cn.wankkoree.xposed.enablewebviewdebugging.data.AppSP
import cn.wankkoree.xposed.enablewebviewdebugging.data.getList
import cn.wankkoree.xposed.enablewebviewdebugging.data.getSet
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.normalClass
import com.highcapable.yukihookapi.hook.xposed.proxy.YukiHookXposedInitProxy
import com.highcapable.yukihookapi.hook.log.*
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.StringType
import java.io.File

@InjectYukiHookWithXposed
class Main : YukiHookXposedInitProxy {
    companion object {
        private val webSettingsClassHashSet = HashSet<String>()
        private val debug = BuildConfig.DEBUG || BuildConfig.BUILD_TYPE == "dev"
    }

    override fun onInit() = YukiHookAPI.configs {
        isDebug = debug
        isAllowPrintingLogs = false // TODO: 用户可选开启 API 日志
        isEnableModulePrefsCache = false
        isEnableMemberCache = false
    }

    override fun onHook() = YukiHookAPI.encase {
        if (!isFirstApplication)
            return@encase // 不 hook 憨批 MIUI 等会被重复 hook 的情况
        if (packageName == BuildConfig.APPLICATION_ID)
            return@encase // 不 hook 自己
        if (packageName == "com.android.webview" || packageName == "com.google.android.webview")
            return@encase // 不 hook WebView 本身

        YukiHookAPI.Configs.debugTag = "EnableWebViewDebugging<$packageName>"
        val pref = prefs("apps_$packageName")
        if (!pref.get(AppSP.is_enabled))
            return@encase // 目标 App 的 Hook 未启用
        val cpuArch = with(appInfo.nativeLibraryDir) {
            when {
                endsWith("arm64") -> "arm64-v8a"
                endsWith("arm") -> "armeabi-v7a"
                else -> {
                    loggerE(msg = "the cpuArch(${toString()}) is not supported")
                    null
                }
            }
        }
        loggerI(msg = "loading rules")

        pref.getSet(AppSP.hooks).forEach { name ->
            val hookEntry = pref.getList<String>("hook_entry_$name")
            when (hookEntry[0]) {
                "hookWebView" -> {
                    hookWebView(
                        Class_WebView = hookEntry[1],
                        Method_getSettings = hookEntry[2],
                        Method_setWebContentsDebuggingEnabled = hookEntry[3],
                        Method_setJavaScriptEnabled = hookEntry[4],
                        Method_loadUrl = hookEntry[5],
                        Method_setWebViewClient = hookEntry[6],
                    )
                }
                "hookWebViewClient" -> {
                    hookWebViewClient(
                        Class_WebView = hookEntry[1],
                        Class_WebViewClient = hookEntry[2],
                        Method_onPageFinished = hookEntry[3],
                        Method_evaluateJavascript = hookEntry[4],
                        Class_ValueCallback = hookEntry[5],
                    )
                }
                "replaceNebulaUCSDK" -> {
                    cpuArch?.let {
                        replaceNebulaUCSDK(
                            Class_UcServiceSetup = hookEntry[1],
                            Method_updateUCVersionAndSdcardPath = hookEntry[2],
                            Field_sInitUcFromSdcardPath = hookEntry[3],
                            it
                        )
                    }
                }
                else -> {
                    loggerE(msg = "Unknown Hook Method: ${hookEntry[0]}")
                }
            }
        }
    }

    /** Hook WebView类，实现：
     *
     * WebView.setWebContentsDebuggingEnabled(true)
     *
     * webView.getSettings().setJavaScriptEnabled(true)
     *
     * webView.loadUrl() debug breakpoint
     *
     * webView.setWebViewClient() debug breakpoint
     **/
    private fun PackageParam.hookWebView(
        Class_WebView: String = "android.webkit.WebView",
        Method_getSettings: String = "getSettings",
        Method_setWebContentsDebuggingEnabled: String = "setWebContentsDebuggingEnabled",
        Method_setJavaScriptEnabled: String = "setJavaScriptEnabled",
        Method_loadUrl: String = "loadUrl",
        Method_setWebViewClient: String = "setWebViewClient",
    ) {
        Class_WebView.hook {
            injectMember {
                allConstructors()
                afterHook {
                    val webView = instance
                    val webSettings = method {
                        name = Method_getSettings
                    }.result {
                        onNoSuchMethod {
                            loggerE(msg = "Hook.Method.NoSuchMethod at hookWebView\uD83D\uDC49<init>\uD83D\uDC49getSettings", e = it)
                        }
                    }.get(webView).call()

                    if (debug) loggerD(msg = "${instanceClass.name} new().static setWebContentsDebuggingEnabled(true)")
                    method {
                        name = Method_setWebContentsDebuggingEnabled
                        param(BooleanType)
                    }.result {
                        onNoSuchMethod {
                            loggerE(msg = "Hook.Method.NoSuchMethod at hookWebView\uD83D\uDC49<init>\uD83D\uDC49setWebContentsDebuggingEnabled", e = it)
                        }
                        get().call(true)
                    }

                    if (debug) loggerD(msg = "${instanceClass.name} new().getSettings().setJavaScriptEnabled(true)")
                    webSettings!!.javaClass.method {
                        name = Method_setJavaScriptEnabled
                        param(BooleanType)
                    }.result {
                        onNoSuchMethod {
                            loggerE(msg = "Hook.Method.NoSuchMethod at hookWebView\uD83D\uDC49<init>\uD83D\uDC49setJavaScriptEnabled", e = it)
                        }
                        get(webSettings).call(true)
                    }

                    if (!webSettingsClassHashSet.contains(webSettings.javaClass.name)) {
                        webSettings.javaClass.hook(isUseAppClassLoader = false) {
                            injectMember {
                                allMethods(name = Method_setJavaScriptEnabled)
                                beforeHook {
                                    if (args[0] != true) {
                                        if (debug) loggerD(msg = "${instanceClass.name}.setJavaScriptEnabled(${args[0]} -> true)")
                                        args(0).set(true)
                                    }
                                }
                            }.result {
                                onNoSuchMemberFailure {
                                    loggerE(msg = "Hook.Member.NoSuchMember at hookWebView\uD83D\uDC49<init>\uD83D\uDC49setJavaScriptEnabled", e = it)
                                }
                                onHookingFailure {
                                    loggerE(msg = "Hook.Member.HookFailure at hookWebView\uD83D\uDC49<init>\uD83D\uDC49setJavaScriptEnabled", e = it)
                                }
                                onHooked {
                                    loggerI(msg = "Hook.Member.Ended at hookWebView\uD83D\uDC49<init>\uD83D\uDC49setJavaScriptEnabled as [$it]")
                                }
                                onConductFailure { hookParam, it ->
                                    loggerE(msg = "Hook.Member.ConductFailure at hookWebView\uD83D\uDC49<init>\uD83D\uDC49setJavaScriptEnabled(${hookParam.args.joinToString(", ")})", e = it)
                                }
                            }
                        }.result {
                            onHookClassNotFoundFailure {
                                loggerE(msg = "Hook.Class.NotFound at hookWebView\uD83D\uDC49$Class_WebView\uD83D\uDC49${webSettings.javaClass.name}", e = it)
                            }
                            onPrepareHook {
                                loggerI(msg = "Hook.Class.Started at hookWebView\uD83D\uDC49$Class_WebView\uD83D\uDC49${webSettings.javaClass.name}")
                            }
                        }
                        webSettingsClassHashSet.add(webSettings.javaClass.name)
                    }
                }
            }.result {
                onNoSuchMemberFailure {
                    loggerE(msg = "Hook.Member.NoSuchMember at hookWebView\uD83D\uDC49<init>", e = it)
                }
                onHookingFailure {
                    loggerE(msg = "Hook.Member.HookFailure at hookWebView\uD83D\uDC49<init>", e = it)
                }
                onHooked {
                    loggerI(msg = "Hook.Member.Ended at hookWebView\uD83D\uDC49<init> as [$it]")
                }
                onConductFailure { hookParam, it ->
                    loggerE(msg = "Hook.Member.ConductFailure at hookWebView\uD83D\uDC49<init>(${hookParam.args.joinToString(", ")})", e = it)
                }
            }

            injectMember {
                allMethods(name = Method_setWebContentsDebuggingEnabled)
                beforeHook {
                    if (args[0] != true) {
                        if (debug) loggerD(msg = "${instanceClass.name}.setWebContentsDebuggingEnabled(${args[0]} -> true)")
                        args(0).set(true)
                    }
                }
            }.result {
                onNoSuchMemberFailure {
                    loggerE(msg = "Hook.Member.NoSuchMember at hookWebView\uD83D\uDC49setWebContentsDebuggingEnabled", e = it)
                }
                onHookingFailure {
                    loggerE(msg = "Hook.Member.HookFailure at hookWebView\uD83D\uDC49setWebContentsDebuggingEnabled", e = it)
                }
                onHooked {
                    loggerI(msg = "Hook.Member.Ended at hookWebView\uD83D\uDC49setWebContentsDebuggingEnabled as [$it]")
                }
                onConductFailure { hookParam, it ->
                    loggerE(msg = "Hook.Member.ConductFailure at hookWebView\uD83D\uDC49setWebContentsDebuggingEnabled(${hookParam.args.joinToString(", ")})", e = it)
                }
            }

            if (debug) {
                injectMember {
                    allMethods(name = Method_loadUrl)
                    afterHook {
                        loggerD(msg = "${instanceClass.name}.loadUrl(\"${args[0]}\")")
                        printStackTrace()
                    }
                }.result {
                    onNoSuchMemberFailure {
                        loggerE(msg = "Hook.Member.NoSuchMember at hookWebView\uD83D\uDC49loadUrl", e = it)
                    }
                    onHookingFailure {
                        loggerE(msg = "Hook.Member.HookFailure at hookWebView\uD83D\uDC49loadUrl", e = it)
                    }
                    onHooked {
                        loggerI(msg = "Hook.Member.Ended at hookWebView\uD83D\uDC49loadUrl as [$it]")
                    }
                    onConductFailure { hookParam, it ->
                        loggerE(msg = "Hook.Member.ConductFailure at hookWebView\uD83D\uDC49loadUrl(${hookParam.args.joinToString(", ")})", e = it)
                    }
                }
            }

            if (debug) {
                injectMember {
                    allMethods(name = Method_setWebViewClient)
                    afterHook {
                        if (args[0] != null) {
                            loggerD(msg = "${instanceClass.name}.setWebViewClient(${args[0]!!.javaClass.name})")
                        } else { // 撤销设置 WebViewClient
                            loggerD(msg = "${instanceClass.name}.setWebViewClient(null)")
                        }
                    }
                }.result {
                    onNoSuchMemberFailure {
                        loggerE(msg = "Hook.Member.NoSuchMember at hookWebView\uD83D\uDC49setWebViewClient", e = it)
                    }
                    onHookingFailure {
                        loggerE(msg = "Hook.Member.HookFailure at hookWebView\uD83D\uDC49setWebViewClient", e = it)
                    }
                    onHooked {
                        loggerI(msg = "Hook.Member.Ended at hookWebView\uD83D\uDC49setWebViewClient as [$it]")
                    }
                    onConductFailure { hookParam, it ->
                        loggerE(msg = "Hook.Member.ConductFailure at hookWebView\uD83D\uDC49setWebViewClient(${hookParam.args.joinToString(", ")})", e = it)
                    }
                }
            }
        }.result {
            onHookClassNotFoundFailure {
                loggerE(msg = "Hook.Class.NotFound at hookWebView\uD83D\uDC49$Class_WebView", e = it)
            }
            onPrepareHook {
                loggerI(msg = "Hook.Class.Started at hookWebView\uD83D\uDC49$Class_WebView")
            }
        }
    }

    /** Hook WebViewClient类，实现：
     *
     * webViewClient.onPageFinished({webView.evaluateJavascript(vConsole)})
     **/
    private fun PackageParam.hookWebViewClient(
        Class_WebView: String = "android.webkit.WebView",
        Class_WebViewClient: String = "android.webkit.WebViewClient",
        Method_onPageFinished: String = "onPageFinished",
        Method_evaluateJavascript: String = "evaluateJavascript",
        Class_ValueCallback: String = "android.webkit.ValueCallback",
    ) {
        Class_WebViewClient.hook {
            injectMember {
                allMethods(Method_onPageFinished)
                beforeHook {
                    val webView = args[0]

                    if (debug) loggerD(msg = "${instanceClass.name}.onPageFinished({webView.evaluateJavascript(vConsole)})")
                    findClass(Class_WebView).normalClass!!.method {
                        name = Method_evaluateJavascript
                        param(StringType, findClass(Class_ValueCallback).normalClass!!)
                    }.result {
                        onNoSuchMethod {
                            loggerE(msg = "Hook.Method.NoSuchMethod at hookWebViewClient\uD83D\uDC49onPageFinished\uD83D\uDC49evaluateJavascript", e = it)
                        }
                        get(webView).call(
                            "javascript:" + (if (prefs("apps_$packageName").get(AppSP.vConsole)) {
                                "if (typeof vConsole === 'undefined'){" +
                                        "   ${prefs("resources_vConsole_${prefs("apps_$packageName").get(AppSP.vConsole_version)}").getString("vConsole")};" +
                                        "   var vConsole=new VConsole();" + // 创建全局变量以供用户使用
                                        "   document.getElementById('__vconsole').style.zIndex=2147483647;" + // 将 vConsole 提升到最顶层
                                        "}"
                            } else "")
                        , null)
                    }
                }
            }.result {
                onNoSuchMemberFailure {
                    loggerE(msg = "Hook.Member.NoSuchMember at hookWebViewClient\uD83D\uDC49onPageFinished", e = it)
                }
                onHookingFailure {
                    loggerE(msg = "Hook.Member.HookFailure at hookWebViewClient\uD83D\uDC49onPageFinished", e = it)
                }
                onHooked {
                    loggerI(msg = "Hook.Member.Ended at hookWebViewClient\uD83D\uDC49onPageFinished as [$it]")
                }
                onConductFailure { hookParam, it ->
                    loggerE(msg = "Hook.Member.ConductFailure at hookWebViewClient\uD83D\uDC49onPageFinished(${hookParam.args.joinToString(", ")})", e = it)
                }
            }
        }.result {
            onHookClassNotFoundFailure {
                loggerE(msg = "Hook.Class.NotFound at hookWebViewClient\uD83D\uDC49$Class_WebViewClient", e = it)
            }
            onPrepareHook {
                loggerI(msg = "Hook.Class.Started at hookWebViewClient\uD83D\uDC49$Class_WebViewClient")
            }
        }
    }

    private fun PackageParam.replaceNebulaUCSDK(
        Class_UcServiceSetup: String = "com.alipay.mobile.nebulauc.impl.UcServiceSetup",
        Method_updateUCVersionAndSdcardPath: String = "updateUCVersionAndSdcardPath",
        Field_sInitUcFromSdcardPath: String = "sInitUcFromSdcardPath",
        cpuArch: String,
    ) {
        Class_UcServiceSetup.hook {
            injectMember {
                allMethods(Method_updateUCVersionAndSdcardPath)
                afterHook {
                    if (debug) loggerD(msg = "${instanceClass.name}.updateUCVersionAndSdcardPath({sInitUcFromSdcardPath=nebulaUCSDK})")
                    if (prefs("apps_$packageName").get(AppSP.nebulaUCSDK)) {
                        File(appContext.getExternalFilesDir("nebulaUCSDK"), "libWebViewCore_ri_7z_uc.so").let {
                            if (!it.exists()) {
                                val nebulaUCSDK = Base64.decode(prefs("resources_nebulaUCSDK_${prefs("apps_$packageName").get(AppSP.nebulaUCSDK_version)}").getString("nebulaUCSDK_$cpuArch"), Base64.NO_WRAP)
                                it.writeBytes(nebulaUCSDK)
                            }
                            this@afterHook.field {
                                name(Field_sInitUcFromSdcardPath)
                                modifiers {
                                    asStatic()
                                }
                            }.result {
                                onNoSuchField {
                                    loggerE(msg = "Hook.Field.NoSuchField at replaceNebulaUCSDK\uD83D\uDC49updateUCVersionAndSdcardPath\uD83D\uDC49sInitUcFromSdcardPath", e = it)
                                }
                                get().set(it.absolutePath)
                            }
                        }
                    }
                }
            }
        }.result {
            onHookClassNotFoundFailure {
                loggerE(msg = "Hook.Class.NotFound at replaceNebulaUCSDK\uD83D\uDC49$Class_UcServiceSetup", e = it)
            }
            onPrepareHook {
                loggerI(msg = "Hook.Class.Started at replaceNebulaUCSDK\uD83D\uDC49$Class_UcServiceSetup")
            }
        }
    }

    private fun printStackTrace() {
        loggerD(msg = "---- ---- ---- ----")
        val stackElements = Throwable().stackTrace
        for (i in stackElements.indices) {
            val element = stackElements[i]
            loggerD(msg = "at ${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})")
        }
        loggerD(msg = "---- ---- ---- ----")
    }
}
