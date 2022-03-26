package cn.wankkoree.xposed.enablewebviewdebugging

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
    private val crosswalkClassesHashSet = HashSet<String>()
    private val weChatClassesHashSet = HashSet<String>()

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        val packageName = lpparam.packageName
        val cpuArch = with(lpparam.appInfo.nativeLibraryDir) {
            when {
                endsWith("arm64") -> "arm64-v8a"
                endsWith("arm") -> "armeabi-v7a"
                else -> {
                    Util.log("info", packageName, "the cpuArch(${toString()}) is not supported")
                    null
                }
            }
        }

        if (packageName == "com.android.webview")
            return // 不 hook WebView 本身

        Util.log("info", packageName, "loading")
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
        // Crosswalk 通用
        hookCrosswalk(arrayOf(
            "org.xwalk.core.XWalkView",
            "org.xwalk.core.XWalkPreferences",
        ), lpparam.classLoader, packageName)
        hookWebViewClient(arrayOf(
            "org.xwalk.core.XWalkResourceClient",
            "onLoadFinished",
            "android.webkit.ValueCallback",
        ), lpparam.classLoader, packageName)

        // tv.danmaku.bili 专用
        if (packageName == "tv.danmaku.bili") {
            Util.log("info", packageName, "Special Hook")
            hookWebViewClient(arrayOf(
                "com.bilibili.app.comm.bh.g",
                "g",
                "com.bilibili.app.comm.bh.interfaces.i",
            ), lpparam.classLoader, packageName)
        }
        // com.netease.cloudmusic 专用
        if (packageName == "com.netease.cloudmusic") {
            Util.log("info", packageName, "Special Hook")
            hookWebViewClient(arrayOf(
                "com.netease.cloudmusic.module.webview.a.c\$b",
                "onPageFinished",
                "android.webkit.ValueCallback",
            ), lpparam.classLoader, packageName)
        }
        // com.zhihu.android 专用
        if (packageName == "com.zhihu.android") {
            Util.log("info", packageName, "Special Hook")
            hookWebViewClient(arrayOf(
                "com.zhihu.android.app.ui.widget.webview.c",
                "onPageFinished",
                "android.webkit.ValueCallback",
            ), lpparam.classLoader, packageName)
        }
        // com.tencent.androidqqmail 专用
        if (packageName == "com.tencent.androidqqmail") {
            Util.log("info", packageName, "Special Hook")
            hookWebViewClient(arrayOf(
                "com.tencent.qqmail.activity.readmail.ReadMailFragment\$69",
                "onSafePageFinished",
                "android.webkit.ValueCallback",
            ), lpparam.classLoader, packageName)
        }
        // com.tencent.mm 专用
        if (packageName == "com.tencent.mm") {
            Util.log("info", packageName, "Special Hook")
            hookWeChat("org.xwalk.core.XWalkPreferences", lpparam.classLoader, packageName)
            hookWebViewClient(arrayOf(
                "com.tencent.xweb.ag",
                "b",
                "android.webkit.ValueCallback",
            ), lpparam.classLoader, packageName)
        }
    }

    /** Hook WebView类，实现：
     *
     * WebView.setWebContentsDebuggingEnabled(true)
     *
     * webView.getSettings().setJavaScriptEnabled(true)
     *
     * webView.loadUrl() breakpoint
     *
     * webView.setWebViewClient() breakpoint
     **/
    private fun hookWebView(targetClass: String, classLoader: ClassLoader, packageName: String) {
        val clazz = try{ XposedHelpers.findClass(targetClass, classLoader) }catch(e: XposedHelpers.ClassNotFoundError){null}
        if (clazz != null && checkWebView(clazz)){  // 目标类存在且未hook
            Util.log("info", packageName, "${Util.getClassString(clazz)} hooking")

            var hookResult = XposedBridge.hookAllConstructors(clazz, object: XC_MethodHook() {
                // 创建对象时hook为默认开启调试、JS
                override fun afterHookedMethod(param: MethodHookParam) {
                    val webView = param.thisObject
                    val webSettings = XposedHelpers.callMethod(webView, "getSettings")

                    Util.log("debug", packageName, "${Util.getClassString(clazz)} new().setWebContentsDebuggingEnabled(true)")
                    XposedHelpers.callStaticMethod(clazz, "setWebContentsDebuggingEnabled", true)

                    Util.log("debug", packageName, "${Util.getClassString(clazz)} new().setJavaScriptEnabled(true)")
                    XposedHelpers.callMethod(webSettings, "setJavaScriptEnabled", true)

                    val clazz = webSettings.javaClass
                    if (checkWebSettings(clazz)) {
                        Util.log("info", packageName, "${Util.getClassString(clazz)} hooking")

                        val hookResult = XposedBridge.hookAllMethods(clazz, "setJavaScriptEnabled", object: XC_MethodHook() {
                            // 声明不开启JS时hook为开启JS
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                if (param.args[0] != true) {
                                    Util.log("debug", packageName, "${Util.getClassString(clazz)}.setJavaScriptEnabled(${param.args[0]} -> true)")
                                    param.args[0] = true
                                }
                            }
                        })
                        Util.log("info", packageName, "${Util.getClassString(clazz)}.setJavaScriptEnabled() hooked x${hookResult.size}")
                    }
                }
            })
            Util.log("info", packageName, "${Util.getClassString(clazz)} new() hooked x${hookResult.size}")

            hookResult = XposedBridge.hookAllMethods(clazz, "setWebContentsDebuggingEnabled", object: XC_MethodHook() {
                // 声明不开启调试时hook为开启调试
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (param.args[0] != true) {
                        Util.log("debug", packageName, "${Util.getClassString(clazz)}.setWebContentsDebuggingEnabled(${param.args[0]} -> true)")
                        param.args[0] = true
                    }
                }
            })
            Util.log("info", packageName, "${Util.getClassString(clazz)}.setWebContentsDebuggingEnabled() hooked x${hookResult.size}")

            hookResult = XposedBridge.hookAllMethods(clazz, "loadUrl", object: XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    Util.log("debug", packageName, "${Util.getClassString(clazz)}.loadUrl(\"${param.args[0]}\")")
                    Util.printStackTrace("debug", packageName)
                }
            })
            Util.log("info", packageName, "${Util.getClassString(clazz)}.loadUrl() hooked x${hookResult.size}")

            hookResult = XposedBridge.hookAllMethods(clazz, "setWebViewClient", object: XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (param.args[0] != null) {
                        Util.log("debug", packageName, "${Util.getClassString(clazz)}.setWebViewClient(${Util.getClassString(param.args[0].javaClass)})")
                    } else { // 撤销设置 WebViewClient
                        Util.log("debug", packageName, "${Util.getClassString(clazz)}.setWebViewClient(null)")
                    }
                    Util.printStackTrace("debug", packageName)
                }
            })
            Util.log("info", packageName, "${Util.getClassString(clazz)}.setWebViewClient() hooked x${hookResult.size}")

        }
    }

    /** Hook WebViewClient类，实现：
     *
     * webViewClient.onPageFinished({webView.evaluateJavascript(vConsole)})
     **/
    private fun hookWebViewClient(targetClass: Array<String>, classLoader: ClassLoader, packageName: String) {
        val clazz = try{ XposedHelpers.findClass(targetClass[0], classLoader) }catch(e: XposedHelpers.ClassNotFoundError){null}
        if (clazz != null && checkWebViewClient(clazz)){  // 目标类存在且未hook
            Util.log("info", packageName, "${Util.getClassString(clazz)} hooking")

            val hookResult = XposedBridge.hookAllMethods(clazz, targetClass[1], object: XC_MethodHook() {
                // 设置WebViewClient时，设置页面开始加载时注入vConsole
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val webView = param.args[0]
                    Util.log("debug", packageName, "${Util.getClassString(clazz)}.onPageFinished({webView.evaluateJavascript(vConsole)})")
                    XposedHelpers.callMethod(webView, "evaluateJavascript", arrayOf(String::class.java, XposedHelpers.findClass(targetClass[2], classLoader)), "javascript:" +
                            "if (typeof vConsole === 'undefined'){" +
                            "   ${Util.getVConsole()};" +
                            "   var vConsole=new VConsole();" + // 创建全局变量以供用户使用
                            "   document.getElementById('__vconsole').style.zIndex=2147483647;" +
                            "}"
                        , null)
                }
            })
            Util.log("info", packageName, "${Util.getClassString(clazz)}.onPageFinished() hooked x${hookResult.size}")
        }
    }

    /** Hook UcServiceSetup类，实现：
     *
     * UcServiceSetup.sInitUcFromSdcardPath = "/sdcard/Android/packageName/foo/bar/libWebViewCore_ri_7z_uc.so"
     **/
    private fun hookUCInspect(classLoader: ClassLoader, packageName: String, cpuArch: String) {
        val clazz = try{ XposedHelpers.findClass("com.alipay.mobile.nebulauc.impl.UcServiceSetup", classLoader) }catch(e: XposedHelpers.ClassNotFoundError){null}
        if (clazz != null && checkUCInspect(clazz)) {  // 目标类存在且未hook
            Util.log("info", packageName, "${Util.getClassString(clazz)} hooking")

            val hookResult = XposedBridge.hookAllMethods(clazz, "updateUCVersionAndSdcardPath", object: XC_MethodHook() {
                // 初始化UC U4时，强制返回一个调试lib包
                override fun afterHookedMethod(param: MethodHookParam) {
                    val fooBar = Util.getApplication().getExternalFilesDir("foo/bar")
                    val libUC7zSoTmp = File(fooBar, "libWebViewCore_ri_7z_uc.so")
                    if (!libUC7zSoTmp.exists()) {
                        Util.log("debug", packageName, "Write $cpuArch/libUC7zSo to ${libUC7zSoTmp.absolutePath}")
                        val inputStream = Util.getLibUC7zSo(cpuArch)
                        val outputStream = libUC7zSoTmp.outputStream()
                        // copy
                        val buf = ByteArray(8192)
                        var length: Int
                        while (inputStream.read(buf).also { length = it } > 0) {
                            outputStream.write(buf, 0, length)
                        }
                        outputStream.flush()
                        Util.log("debug", packageName, "Write result: ${libUC7zSoTmp.exists()}")
                    }

                    Util.log("debug", packageName, "${Util.getClassString(clazz)}.sInitUcFromSdcardPath=\"${libUC7zSoTmp.absolutePath}\"")
                    XposedHelpers.setStaticObjectField(clazz, "sInitUcFromSdcardPath", libUC7zSoTmp.absolutePath)
                }
            })
            Util.log("info", packageName, "${Util.getClassString(clazz)}.updateUCVersionAndSdcardPath() hooked x${hookResult.size}")
        }

    }

    /** Hook Crosswalk XwalkView、XwalkPreferences类，实现：
     *
     * XwalkPreferences.setValue("remote-debugging", true)
     *
     * XwalkPreferences.setValue("enable-javascript", true)
     *
     * xwalkView.loadUrl() breakpoint
     **/
    private fun hookCrosswalk(targetClass: Array<String>, classLoader: ClassLoader, packageName: String) {
        val clazz = try{ XposedHelpers.findClass(targetClass[0], classLoader) }catch(e: XposedHelpers.ClassNotFoundError){null}
        if (clazz != null && checkCrosswalk(clazz)){  // 目标类存在且未hook
            Util.log("info", packageName, "${Util.getClassString(clazz)} hooking")
            val xwalkPreferences = XposedHelpers.findClass(targetClass[1], classLoader)

            var hookResult = XposedBridge.hookAllConstructors(clazz, object: XC_MethodHook() {
                // 创建对象时hook为默认开启调试、JS
                override fun beforeHookedMethod(param: MethodHookParam) {
                    try {
                        Util.log("debug", packageName, "${Util.getClassString(xwalkPreferences)}.setValue(\"remote-debugging\", true)")
                        XposedHelpers.callStaticMethod(xwalkPreferences, "setValue", "remote-debugging", true)
                        Util.log("debug", packageName, "${Util.getClassString(xwalkPreferences)}.setValue(\"enable-javascript\", true)")
                        XposedHelpers.callStaticMethod(xwalkPreferences, "setValue", "enable-javascript", true)
                    } catch (e: java.lang.NullPointerException) { } // 防止微信的二改引擎 XWeb 引发报错
                }
            })
            Util.log("info", packageName, "${Util.getClassString(clazz)} new() hooked x${hookResult.size}")

            hookResult = XposedBridge.hookAllMethods(xwalkPreferences, "setValue", object: XC_MethodHook() {
                // 声明不开启调试时hook为开启调试、JS
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (param.args[0] == "remote-debugging" && param.args[1] != true) {
                        Util.log("debug", packageName, "${Util.getClassString(xwalkPreferences)}.setValue(\"remote-debugging\", ${param.args[1]} -> true)")
                        param.args[1] = true
                    } else if (param.args[0] == "enable-javascript" && param.args[1] != true) {
                        Util.log("debug", packageName, "${Util.getClassString(xwalkPreferences)}.setValue(\"enable-javascript\", ${param.args[1]} -> true)")
                        param.args[1] = true
                    }
                }
            })
            Util.log("info", packageName, "${Util.getClassString(xwalkPreferences)}.setValue() hooked x${hookResult.size}")

            hookResult = XposedBridge.hookAllMethods(clazz, "loadUrl", object: XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    Util.log("debug", packageName, "${Util.getClassString(clazz)}.loadUrl(\"${param.args[0]}\")")
                    Util.printStackTrace("debug", packageName)
                }
            })
            Util.log("info", packageName, "${Util.getClassString(clazz)}.loadUrl() hooked x${hookResult.size}")
        }
    }

    /** Hook XWeb XwalkPreferences类，实现：
     *
     * XwalkPreferences.setValue("remote-debugging", true)
     *
     * XwalkPreferences.setValue("enable-javascript", true)
     **/
    private fun hookWeChat(targetClass: String, classLoader: ClassLoader, packageName: String) {
        val clazz = try{ XposedHelpers.findClass(targetClass, classLoader) }catch(e: XposedHelpers.ClassNotFoundError){null}
        if (clazz != null && checkWeChat(clazz)){  // 目标类存在且未hook
            Util.log("info", packageName, "${Util.getClassString(clazz)} hooking")

            var hookResult = XposedBridge.hookAllConstructors(clazz, object: XC_MethodHook() {
                // 创建对象时hook为默认开启调试、JS
                override fun afterHookedMethod(param: MethodHookParam) {
                    val xwalkPreferences = param.thisObject

                    Util.log("debug", packageName, "${Util.getClassString(clazz)}.setValue(\"remote-debugging\", true)")
                    XposedHelpers.callMethod(xwalkPreferences, "setValue", "remote-debugging", true)

                    Util.log("debug", packageName, "${Util.getClassString(clazz)}.setValue(\"enable-javascript\", true)")
                    XposedHelpers.callMethod(xwalkPreferences, "setValue", "enable-javascript", true)
                }
            })
            Util.log("info", packageName, "${Util.getClassString(clazz)} new() hooked x${hookResult.size}")
        }
    }

    private fun checkWebView(targetClass: Class<*>): Boolean {
        val targetClassS = Util.getClassStringWithHash(targetClass)
        return if (webViewClassesHashSet.contains(targetClassS)) {
            false
        } else {
            webViewClassesHashSet.add(targetClassS)
            true
        }
    }

    private fun checkWebSettings(targetClass: Class<*>): Boolean {
        val targetClassS = Util.getClassStringWithHash(targetClass)
        return if (webSettingsClassesHashSet.contains(targetClassS)) {
            false
        } else {
            webSettingsClassesHashSet.add(targetClassS)
            true
        }
    }

    private fun checkWebViewClient(targetClass: Class<*>): Boolean {
        val targetClassS = Util.getClassStringWithHash(targetClass)
        return if (webViewClientClassesHashSet.contains(targetClassS)) {
            false
        } else {
            webViewClientClassesHashSet.add(targetClassS)
            true
        }
    }

    private fun checkUCInspect(targetClass: Class<*>): Boolean {
        val targetClassS = Util.getClassStringWithHash(targetClass)
        return if (uCInspectClassesHashSet.contains(targetClassS)) {
            false
        } else {
            uCInspectClassesHashSet.add(targetClassS)
            true
        }
    }

    private fun checkCrosswalk(targetClass: Class<*>): Boolean {
        val targetClassS = Util.getClassStringWithHash(targetClass)
        return if (crosswalkClassesHashSet.contains(targetClassS)) {
            false
        } else {
            crosswalkClassesHashSet.add(targetClassS)
            true
        }
    }

    private fun checkWeChat(targetClass: Class<*>): Boolean {
        val targetClassS = Util.getClassStringWithHash(targetClass)
        return if (weChatClassesHashSet.contains(targetClassS)) {
            false
        } else {
            weChatClassesHashSet.add(targetClassS)
            true
        }
    }
}