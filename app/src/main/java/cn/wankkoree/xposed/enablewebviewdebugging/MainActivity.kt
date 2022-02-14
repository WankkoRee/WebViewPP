package cn.wankkoree.xposed.enablewebviewdebugging

import android.app.AndroidAppHelper
import android.content.Context
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.io.File


class MainActivity : IXposedHookLoadPackage {
    private val webViewClassesHashSet = HashSet<String>()
    private val webSettingsClassesHashSet = HashSet<String>()
    private val webViewClientClassesHashSet = HashSet<String>()
    private val uCInspectClassesHashSet = HashSet<String>()
    private val vConsole = Util.vConsoleRaw
    private val libUC7zSo = "libWebViewCore_3.21.0.174.200825145737_7z_uc.so"

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        val packageName = lpparam.packageName
        val cpuArch = with(lpparam.appInfo.nativeLibraryDir) {
            when {
                endsWith("arm64") -> "arm64-v8a"
                endsWith("arm") -> "armeabi-v7a"
                else -> {
                    log("info", packageName, "the cpuArch(${toString()}) is not supported")
                    null
                }
            }
        }

        if (packageName == "com.android.webview")
            return // 不 hook WebView 本身

        log("info", packageName, "loading")
        // WebView 通用
        hookWebView("android.webkit.WebView", lpparam.classLoader, packageName)
        hookWebViewClient(arrayOf(
            "android.webkit.WebViewClient",
            "onPageFinished",
            "android.webkit.ValueCallback",
        ), lpparam.classLoader, packageName)
        // TBS X5 通用
        hookWebView("com.tencent.smtt.sdk.WebView", lpparam.classLoader, packageName)
        hookWebViewClient(arrayOf(
            "com.tencent.smtt.sdk.WebViewClient",
            "onPageFinished",
            "com.tencent.smtt.sdk.ValueCallback",
        ), lpparam.classLoader, packageName)
        // UC U4 通用
        hookWebView("com.uc.webview.export.WebView", lpparam.classLoader, packageName)
        hookWebViewClient(arrayOf(
            "com.uc.webview.export.WebViewClient",
            "onPageFinished",
            "android.webkit.ValueCallback",
        ), lpparam.classLoader, packageName)
        hookWebViewClient(arrayOf(
            "com.alipay.mobile.nebulauc.impl.UCWebViewClient",
            "onPageFinished",
            "android.webkit.ValueCallback",
        ), lpparam.classLoader, packageName)
        if (cpuArch != null)
            hookUCInspect(lpparam.classLoader, packageName, cpuArch)

        // tv.danmaku.bili 专用
        if (packageName == "tv.danmaku.bili") {
            log("info", packageName, "Special Hook")
            hookWebViewClient(arrayOf(
                "com.bilibili.app.comm.bh.g",
                "g",
                "com.bilibili.app.comm.bh.interfaces.i",
            ), lpparam.classLoader, packageName)
        }
        // com.netease.cloudmusic 专用
        if (packageName == "com.netease.cloudmusic") {
            log("info", packageName, "Special Hook")
            hookWebViewClient(arrayOf(
                "com.netease.cloudmusic.module.webview.a.c\$b",
                "onPageFinished",
                "android.webkit.ValueCallback",
            ), lpparam.classLoader, packageName)
        }
        // com.zhihu.android 专用
        if (packageName == "com.zhihu.android") {
            log("info", packageName, "Special Hook")
            hookWebViewClient(arrayOf(
                "com.zhihu.android.app.ui.widget.webview.c",
                "onPageFinished",
                "android.webkit.ValueCallback",
            ), lpparam.classLoader, packageName)
        }
    }

    /** Hook WebView类，实现：
     *
     * WebView.setWebContentsDebuggingEnabled(true)
     *
     * webView.getSettings().setJavaScriptEnabled(true)
     **/
    private fun hookWebView(targetClass: String, classLoader: ClassLoader, packageName: String) {
        val clazz = try{ XposedHelpers.findClass(targetClass, classLoader) }catch(e: XposedHelpers.ClassNotFoundError){null}
        if (clazz != null && checkWebView(clazz)){  // 目标类存在且未hook
            log("info", packageName, "${getClassString(clazz)} hooking")

            var hookResult = XposedBridge.hookAllConstructors(clazz, object: XC_MethodHook() {
                // 创建对象时hook为默认开启调试、JS
                override fun afterHookedMethod(param: MethodHookParam) {
                    val webView = param.thisObject
                    val webSettings = XposedHelpers.callMethod(webView, "getSettings")

                    log("debug", packageName, "${getClassString(clazz)} new().setWebContentsDebuggingEnabled(true)")
                    XposedHelpers.callStaticMethod(clazz, "setWebContentsDebuggingEnabled", true)

                    log("debug", packageName, "${getClassString(clazz)} new().setJavaScriptEnabled(true)")
                    XposedHelpers.callMethod(webSettings, "setJavaScriptEnabled", true)

                    val clazz = webSettings.javaClass
                    if (checkWebSettings(clazz)) {
                        log("info", packageName, "${getClassString(clazz)} hooking")

                        val hookResult = XposedBridge.hookAllMethods(clazz, "setJavaScriptEnabled", object: XC_MethodHook() {
                            // 声明不开启JS时hook为开启JS
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                if (param.args[0] != true) {
                                    log("debug", packageName, "${getClassString(clazz)}.setJavaScriptEnabled(${param.args[0]} -> true)")
                                    param.args[0] = true
                                }
                            }
                        })
                        log("info", packageName, "${getClassString(clazz)}.setJavaScriptEnabled() hooked x${hookResult.size}")
                    }
                }
            })
            log("info", packageName, "${getClassString(clazz)} new() hooked x${hookResult.size}")

            hookResult = XposedBridge.hookAllMethods(clazz, "setWebContentsDebuggingEnabled", object: XC_MethodHook() {
                // 声明不开启调试时hook为开启调试
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (param.args[0] != true) {
                        log("debug", packageName, "${getClassString(clazz)}.setWebContentsDebuggingEnabled(${param.args[0]} -> true)")
                        param.args[0] = true
                    }
                }
            })
            log("info", packageName, "${getClassString(clazz)}.setWebContentsDebuggingEnabled() hooked x${hookResult.size}")

            hookResult = XposedBridge.hookAllMethods(clazz, "loadUrl", object: XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    log("debug", packageName, "${getClassString(clazz)}.loadUrl(\"${param.args[0]}\")")
                    printStackTrace("debug", packageName)
                }
            })
            log("info", packageName, "${getClassString(clazz)}.loadUrl() hooked x${hookResult.size}")
        }
    }

    /** Hook WebViewClient类，实现：
     *
     * webViewClient.onPageFinished({webView.evaluateJavascript(vConsole)})
     **/
    private fun hookWebViewClient(targetClass: Array<String>, classLoader: ClassLoader, packageName: String) {
        val clazz = try{ XposedHelpers.findClass(targetClass[0], classLoader) }catch(e: XposedHelpers.ClassNotFoundError){null}
        if (clazz != null && checkWebViewClient(clazz)){  // 目标类存在且未hook
            log("info", packageName, "${getClassString(clazz)} hooking")

            val hookResult = XposedBridge.hookAllMethods(clazz, targetClass[1], object: XC_MethodHook() {
                // 设置WebViewClient时，设置页面开始加载时注入vConsole
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val webView = param.args[0]

                    log("debug", packageName, "${getClassString(clazz)}.onPageFinished({webView.evaluateJavascript(vConsole)})")
                    XposedHelpers.callMethod(webView, "evaluateJavascript", arrayOf(String::class.java, XposedHelpers.findClass(targetClass[2], classLoader)), "javascript:$vConsole;new VConsole();document.getElementById('__vconsole').style.zIndex=2147483647;", null)
                }
            })
            log("info", packageName, "${getClassString(clazz)}.onPageFinished() hooked x${hookResult.size}")
        }
    }

    private fun hookUCInspect(classLoader: ClassLoader, packageName: String, cpuArch: String) {
        val clazz = try{ XposedHelpers.findClass("com.alipay.mobile.nebulauc.impl.UcServiceSetup", classLoader) }catch(e: XposedHelpers.ClassNotFoundError){null}
        if (clazz != null && checkUCInspect(clazz)) {  // 目标类存在且未hook
            log("info", packageName, "${getClassString(clazz)} hooking")

            val hookResult = XposedBridge.hookAllMethods(clazz, "updateUCVersionAndSdcardPath", object: XC_MethodHook() {
                // 初始化UC U4时，强制返回一个调试lib包
                override fun afterHookedMethod(param: MethodHookParam) {
                    val context = AndroidAppHelper.currentApplication() as Context
                    val fooBar = context.getExternalFilesDir("foo/bar")
                    val libUC7zSoTmp = File(fooBar, "libWebViewCore_ri_7z_uc.so")
                    if (!libUC7zSoTmp.exists()) {
                        log("debug", packageName, "Write $cpuArch/$libUC7zSo to ${libUC7zSoTmp.absolutePath}")
                        val moduleContext = context.createPackageContext("cn.wankkoree.xposed.enablewebviewdebugging", Context.CONTEXT_IGNORE_SECURITY)
                        val inputStream = moduleContext.assets.open("libUC7zSo/$cpuArch/$libUC7zSo")
                        val outputStream = libUC7zSoTmp.outputStream()
                        // copy
                        val buf = ByteArray(8192)
                        var length: Int
                        while (inputStream.read(buf).also { length = it } > 0) {
                            outputStream.write(buf, 0, length)
                        }
                        outputStream.flush()
                        log("debug", packageName, "Write result: ${libUC7zSoTmp.exists()}")
                    }

                    log("debug", packageName, "${getClassString(clazz)}.sInitUcFromSdcardPath=\"${libUC7zSoTmp.absolutePath}\"")
                    XposedHelpers.setStaticObjectField(clazz, "sInitUcFromSdcardPath", libUC7zSoTmp.absolutePath)
                }
            })
            log("info", packageName, "${getClassString(clazz)}.updateUCVersionAndSdcardPath() hooked x${hookResult.size}")
        }

    }

    private fun getClassString(clazz: Class<*>): String {
        return "${clazz.classLoader.javaClass.name}=>${clazz.name}"
    }

    private fun getClassStringWithHash(clazz: Class<*>): String {
        return "${clazz.classLoader.javaClass.name}@${clazz.classLoader!!.hashCode()}=>${clazz.name}@${clazz.hashCode()}"
    }

    private fun checkWebView(targetClass: Class<*>): Boolean {
        val targetClassS = getClassStringWithHash(targetClass)
        return if (webViewClassesHashSet.contains(targetClassS)) {
            false
        } else {
            webViewClassesHashSet.add(targetClassS)
            true
        }
    }

    private fun checkWebSettings(targetClass: Class<*>): Boolean {
        val targetClassS = getClassStringWithHash(targetClass)
        return if (webSettingsClassesHashSet.contains(targetClassS)) {
            false
        } else {
            webSettingsClassesHashSet.add(targetClassS)
            true
        }
    }

    private fun checkWebViewClient(targetClass: Class<*>): Boolean {
        val targetClassS = getClassStringWithHash(targetClass)
        return if (webViewClientClassesHashSet.contains(targetClassS)) {
            false
        } else {
            webViewClientClassesHashSet.add(targetClassS)
            true
        }
    }

    private fun checkUCInspect(targetClass: Class<*>): Boolean {
        val targetClassS = getClassStringWithHash(targetClass)
        return if (uCInspectClassesHashSet.contains(targetClassS)) {
            false
        } else {
            uCInspectClassesHashSet.add(targetClassS)
            true
        }
    }

    private fun log(level: String, packageName: String, message: String) {
        if ((level == "debug" && BuildConfig.DEBUG) || (level == "info"))
            XposedBridge.log("[EnableWebViewDebugging]<$packageName>[$level]: $message")
    }

    private fun printStackTrace(level: String, packageName: String) {
        log(level, packageName, "---- ---- ---- ----")
        val stackElements = Throwable().stackTrace
        for (i in stackElements.indices) {
            val element = stackElements[i]
            log(level, packageName, "at ${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})")
        }
        log(level, packageName, "---- ---- ---- ----")
    }
}