package cn.wankkoree.xp.webviewpp.hook

import android.content.Context
import cn.wankkoree.xp.webviewpp.BuildConfig
import cn.wankkoree.xp.webviewpp.data.AppSP
import cn.wankkoree.xp.webviewpp.data.getSet
import cn.wankkoree.xp.webviewpp.hook.debug.*
import cn.wankkoree.xp.webviewpp.hook.method.*
import cn.wankkoree.xp.webviewpp.http.bean.HookRules
import com.google.gson.Gson
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.log.*
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed(entryClassName = "Entry", isUsingResourcesHook = false)
class Main : IYukiHookXposedInit {
    companion object {
        val debug = BuildConfig.DEBUG || BuildConfig.BUILD_TYPE == "dev"
        var mProcessName = ""
    }

    override fun onInit() {
        YukiHookAPI.configs {
            debugLog {
                isEnable = debug
                isRecord = false
                elements(TAG, PRIORITY, PACKAGE_NAME, USER_ID)
            }
            isDebug = debug
            isEnableModuleAppResourcesCache = true
            isEnableHookModuleStatus = true
            isEnableHookSharedPreferences = false
            isEnableDataChannel = false
        }
    }

    override fun onHook() = YukiHookAPI.encase {
        loadApp {
            mProcessName = processName.substringBefore(':')

            loggerI(msg = "Welcome to WebViewPP ${BuildConfig.VERSION_NAME}-${BuildConfig.BUILD_TYPE}(${BuildConfig.VERSION_CODE})!")

            if (packageName != mProcessName) { // 不为主进程和私有进程 TODO : 判断公有进程
                loggerI(msg = "do not hook other application package: $packageName or application process: $mProcessName")
                return@encase // 不 hook 憨批 MIUI 等会被重复 hook 的情况
            }
            if (mProcessName == BuildConfig.APPLICATION_ID) {
                loggerI(msg = "do not hook self: $mProcessName")
                return@encase // 不 hook 自己
            }
            if (mProcessName == "com.android.webview" || mProcessName == "com.google.android.webview") {
                loggerW(msg = "do not hook webview library: $mProcessName")
                return@encase // 不 hook WebView 本身
            }
            prefs("apps_$mProcessName").let { pref ->
                if (!pref.get(AppSP.is_enabled)) {
                    loggerI(msg = "$mProcessName hooking not enabled")
                    return@encase // 目标 App 的 Hook 未启用
                }

                YukiHookLogger.Configs.tag = "WebViewPP<$packageName>"

                loggerI(msg = "hook $mProcessName which run in $processName")

                val mAppClassName = this.appInfo.className
                if (pref.get(AppSP.app_is_protected)) {
                    loggerI(msg = "try to get classloader of protection")
                    when (mAppClassName) {
                        null -> {
                            // todo 感觉这个情况是不存在的？
                            loggerE(msg = "the className of applicationInfo is null, get classloader failed")
                            doHook()
                        }
                        "android.app.Application" -> {
                            loggerE(msg = "the className of applicationInfo is Application, don't need to replace classloader")
                            doHook()
                        }
                        else -> {
                            mAppClassName.hook {
                                injectMember {
                                    method {
                                        name = "attachBaseContext"
                                        param(ContextClass)
                                    }
                                    afterHook {
                                        val context = args(0).cast<Context>()!!
                                        appClassLoader = context.classLoader
                                        loggerI(msg = "replace classloader to protection succeed")
                                        doHook()
                                    }
                                }.result {
                                    onNoSuchMemberFailure {
                                        loggerE(msg = "Hook.Member.NoSuchMember at preHook\uD83D\uDC49attachBaseContext", e = it)
                                        doHook()
                                    }
                                    onHookingFailure {
                                        loggerE(msg = "Hook.Member.HookFailure at preHook\uD83D\uDC49attachBaseContext", e = it)
                                        doHook()
                                    }
                                    onHooked {
                                        loggerI(msg = "Hook.Member.Ended at preHook\uD83D\uDC49attachBaseContext as [$it]")
                                    }
                                    onConductFailure { hookParam, it ->
                                        loggerE(msg = "Hook.Member.ConductFailure at preHook\uD83D\uDC49attachBaseContext(${hookParam.args.joinToString(", ")})", e = it)
                                        doHook()
                                    }
                                }
                            }.result {
                                onHookClassNotFoundFailure {
                                    loggerE(msg = "Hook.Class.NotFound at preHook\uD83D\uDC49$mAppClassName", e = it)
                                    doHook()
                                }
                                onPrepareHook {
                                    loggerI(msg = "Hook.Class.Started at preHook\uD83D\uDC49$mAppClassName")
                                }
                            }
                        }
                    }
                } else {
                    doHook()
                }
            }
        }
    }

}

private fun PackageParam.doHook() {
    prefs("apps_${Main.mProcessName}").let { pref ->
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

        if (pref.get(AppSP.debug_mode)) {
            findWebViewMethods()
        }

        pref.getSet(AppSP.hooks).forEach { name ->
            val hookJson = pref.getString("hook_entry_$name", "{}")
            try {
                when (val hookMethod = Gson().fromJson(hookJson, HookRules.HookRule::class.java).name) {
                    // TODO: 添加更多 hook 方法
                    "hookWebView" -> {
                        val hookEntry = Gson().fromJson(hookJson, HookRules.HookRuleWebView::class.java)
                        hookWebView(
                            Class_WebView = hookEntry.Class_WebView,
                            Method_getSettings = hookEntry.Method_getSettings,
                            Method_setWebContentsDebuggingEnabled = hookEntry.Method_setWebContentsDebuggingEnabled,
                            Method_setJavaScriptEnabled = hookEntry.Method_setJavaScriptEnabled,
                            Method_loadUrl = hookEntry.Method_loadUrl,
                            Method_setWebViewClient = hookEntry.Method_setWebViewClient,
                        )
                    }

                    "hookWebViewClient" -> {
                        val hookEntry = Gson().fromJson(hookJson, HookRules.HookRuleWebViewClient::class.java)
                        hookWebViewClient(
                            Class_WebViewClient = hookEntry.Class_WebViewClient,
                            Method_onPageFinished = hookEntry.Method_onPageFinished,
                            Class_WebView = hookEntry.Class_WebView,
                            Method_evaluateJavascript = hookEntry.Method_evaluateJavascript,
                        )
                    }

                    "replaceNebulaUCSDK" -> {
                        if (cpuArch != null) {
                            val hookEntry = Gson().fromJson(hookJson, HookRules.ReplaceNebulaUCSDK::class.java)
                            replaceNebulaUCSDK(
                                Class_UcServiceSetup = hookEntry.Class_UcServiceSetup,
                                Method_updateUCVersionAndSdcardPath = hookEntry.Method_updateUCVersionAndSdcardPath,
                                Field_sInitUcFromSdcardPath = hookEntry.Field_sInitUcFromSdcardPath,
                                cpuArch = cpuArch,
                            )
                        }
                    }

                    "hookCrossWalk" -> {
                        val hookEntry = Gson().fromJson(hookJson, HookRules.HookCrossWalk::class.java)
                        hookCrossWalk(
                            Class_XWalkView = hookEntry.Class_XWalkView,
                            Method_getSettings = hookEntry.Method_getSettings,
                            Method_setJavaScriptEnabled = hookEntry.Method_setJavaScriptEnabled,
                            Method_loadUrl = hookEntry.Method_loadUrl,
                            Method_setResourceClient = hookEntry.Method_setResourceClient,
                            Class_XWalkPreferences = hookEntry.Class_XWalkPreferences,
                            Method_setValue = hookEntry.Method_setValue,
                        )
                    }

                    "hookXWebView" -> {
                        val hookEntry = Gson().fromJson(hookJson, HookRules.HookXWebView::class.java)
                        hookXWebView(
                            Class_XWebView = hookEntry.Class_XWebView,
                            Method_initWebviewCore = hookEntry.Method_initWebviewCore,
                            Method_isXWeb = hookEntry.Method_isXWeb,
                            Method_isSys = hookEntry.Method_isSys,
                            Class_XWebPreferences = hookEntry.Class_XWebPreferences,
                            Method_setValue = hookEntry.Method_setValue,
                        )
                    }

                    else -> {
                        loggerE(msg = "Unknown Hook Method: $hookMethod")
                    }
                }
            } catch (e: Exception) {
                loggerE(msg = "Parse Failed!", e = e)
                return@forEach // continue
            }
        }
    }
}
