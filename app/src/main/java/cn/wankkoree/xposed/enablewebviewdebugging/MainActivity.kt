package cn.wankkoree.xposed.enablewebviewdebugging

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam


class MainActivity : IXposedHookLoadPackage {
    private val webViewClassesHashSet = HashSet<String>()
    private val webSettingsClassesHashSet = HashSet<String>()
    private val webViewClientClassesHashSet = HashSet<String>()
    private val vConsole = Util.vConsoleRaw

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        val packageName = lpparam.packageName

        if (packageName == "com.android.webview")
            return // 不 hook WebView 本身

        log("info", packageName, "loading")
        // WebView 通用
        hookWebView("android.webkit.WebView", lpparam.classLoader, packageName)
        hookWebViewClient(arrayOf(
            "android.webkit.WebViewClient",
            "android.webkit.ValueCallback",
            "onPageFinished",
        ), lpparam.classLoader, packageName)
        // TBS X5 通用
        hookWebView("com.tencent.smtt.sdk.WebView", lpparam.classLoader, packageName)
        hookWebViewClient(arrayOf(
            "com.tencent.smtt.sdk.WebViewClient",
            "com.tencent.smtt.sdk.ValueCallback",
            "onPageFinished",
        ), lpparam.classLoader, packageName)
        // tv.danmaku.bili 专用
        if (packageName == "tv.danmaku.bili") {
            log("info", packageName, "Special Hook")
            hookWebViewClient(arrayOf(
                "com.bilibili.app.comm.bh.g",
                "com.bilibili.app.comm.bh.interfaces.i",
                "g",
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

            val hookResult = XposedBridge.hookAllMethods(clazz, targetClass[2], object: XC_MethodHook() {
                // 设置WebViewClient时，设置页面开始加载时注入vConsole
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val webView = param.args[0]

                    log("debug", packageName, "${getClassString(clazz)}.onPageFinished({webView.evaluateJavascript(vConsole)})")
                    XposedHelpers.callMethod(webView, "evaluateJavascript", arrayOf(String::class.java, XposedHelpers.findClass(targetClass[1], classLoader)), "javascript:$vConsole;new VConsole();", null)
                }
            })
            log("info", packageName, "${getClassString(clazz)}.onPageFinished() hooked x${hookResult.size}")
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