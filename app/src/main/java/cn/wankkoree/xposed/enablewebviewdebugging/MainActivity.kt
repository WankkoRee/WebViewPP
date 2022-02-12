package cn.wankkoree.xposed.enablewebviewdebugging

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import kotlin.collections.HashSet


class MainActivity : IXposedHookLoadPackage {
    private val webViewClassesHashSet = HashSet<String>()
    private val webViewClientClassesHashSet = HashSet<String>()
    private val loadVConsole = """
        if(typeof loadJS === 'undefined'){
          function loadJS(url, callback) {
            var script = document.createElement("script"),
            fn = callback || (()=>{});
            script.type = "text/javascript";
            script.onload = ()=>fn();
            script.src = url;
            document.getElementsByTagName("head")[0].appendChild(script);
          }
        }
        if(typeof VConsole === 'undefined')
          loadJS("https://cdn.bootcdn.net/ajax/libs/vConsole/3.11.2/vconsole.min.js", ()=>new VConsole());
    """.trimIndent()

    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        val packageName = lpparam.packageName

        if (packageName.equals("com.android.webview"))
            return // 不 hook WebView 本身

        XposedBridge.log("[EnableWebViewDebugging]: <$packageName> loading.")
        hookWebView(lpparam.classLoader, packageName)
        hookWebViewClient(lpparam.classLoader, packageName)
    }

    /** Hook WebView类，实现：
     *
     * WebView.setWebContentsDebuggingEnabled(true)
     *
     * webView.getSettings().setJavaScriptEnabled(true)
     **/
    private fun hookWebView(classLoader: ClassLoader?, packageName: String?) {
        val targetClasses = arrayOf(
            "android.webkit.WebView",
            "com.tencent.smtt.sdk.WebView",
        )
        for (targetClass in targetClasses) {
            val clazz = try{ XposedHelpers.findClass(targetClass, classLoader) }catch(e: XposedHelpers.ClassNotFoundError){null}
            if (clazz != null && checkWebView(clazz)){  // 目标类存在且未hook
                XposedBridge.log("[EnableWebViewDebugging]: <$packageName>(${getClassString(clazz)}) hooking.")
                XposedBridge.hookAllConstructors(clazz, object: XC_MethodHook() {
                    // 创建对象时hook为默认开启调试、JS
                    override fun afterHookedMethod(param: MethodHookParam) {
                        XposedBridge.log("[EnableWebViewDebugging]: <$packageName>(new ${getClassString(clazz)}).setWebContentsDebuggingEnabled(true)")
                        XposedHelpers.callStaticMethod(clazz, "setWebContentsDebuggingEnabled", true)

                        XposedBridge.log("[EnableWebViewDebugging]: <$packageName>(new ${getClassString(clazz)}).setJavaScriptEnabled(true)")
                        val webView = param.thisObject
                        val webSettings = XposedHelpers.callMethod(webView, "getSettings")
                        XposedHelpers.callMethod(webSettings, "setJavaScriptEnabled", true)
                        XposedBridge.hookAllMethods(webSettings.javaClass, "setJavaScriptEnabled", object: XC_MethodHook() {
                            // 声明不开启JS时hook为开启JS
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                if (param.args[0] != true) {
                                    XposedBridge.log("[EnableWebViewDebugging]: <$packageName>(${getClassString(clazz)}).setJavaScriptEnabled(${param.args[0]} -> true)")
                                    param.args[0] = true
                                }
                            }
                        })
                    }
                })
                XposedBridge.hookAllMethods(clazz, "setWebContentsDebuggingEnabled", object: XC_MethodHook() {
                    // 声明不开启调试时hook为开启调试
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        if (param.args[0] != true) {
                            XposedBridge.log("[EnableWebViewDebugging]: <$packageName>(${getClassString(clazz)}).setWebContentsDebuggingEnabled(${param.args[0]} -> true)")
                            param.args[0] = true
                        }
                    }
                })

            }
        }
    }

    /** Hook WebViewClient类，实现：
     *
     * webViewClient.onPageFinished({webView.evaluateJavascript(vConsole)})
     **/
    private fun hookWebViewClient(classLoader: ClassLoader?, packageName: String?) {
        val targetClasses = arrayOf(
            arrayOf("android.webkit.WebViewClient", "android.webkit.ValueCallback"),
            arrayOf("com.tencent.smtt.sdk.WebViewClient", "com.tencent.smtt.sdk.ValueCallback"),
        )
        for (targetClass in targetClasses) {
            val clazz = try{ XposedHelpers.findClass(targetClass[0], classLoader) }catch(e: XposedHelpers.ClassNotFoundError){null}
            if (clazz != null && checkWebViewClient(clazz)){  // 目标类存在且未hook
                XposedBridge.log("[EnableWebViewDebugging]: <$packageName>(${getClassString(clazz)}) hooking.")
                XposedBridge.hookAllMethods(clazz, "onPageFinished", object: XC_MethodHook() {
                    // 设置WebViewClient时，设置页面开始加载时注入vConsole
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        XposedBridge.log("[EnableWebViewDebugging]: <$packageName>(new ${getClassString(clazz)}).onPageFinished({webView.evaluateJavascript(vConsole)})")
                        val webView = param.args[0]
                        XposedHelpers.callMethod(webView, "evaluateJavascript", arrayOf(String::class.java, XposedHelpers.findClass(targetClass[1], classLoader)), "javascript:$loadVConsole", null)
                    }
                })
            }
        }
    }

    private fun getClassString(clazz: Class<*>): String {
        return "${clazz.name}@${clazz.hashCode()}<=${clazz.classLoader.javaClass.name}@${clazz.classLoader!!.hashCode()}"
    }

    private fun checkWebView(targetClass: Class<*>): Boolean {
        val targetClassS = getClassString(targetClass)
        return if (webViewClassesHashSet.contains(targetClassS)) {
            false
        } else {
            webViewClassesHashSet.add(targetClassS)
            true
        }
    }

    private fun checkWebViewClient(targetClass: Class<*>): Boolean {
        val targetClassS = getClassString(targetClass)
        return if (webViewClientClassesHashSet.contains(targetClassS)) {
            false
        } else {
            webViewClientClassesHashSet.add(targetClassS)
            true
        }
    }

}