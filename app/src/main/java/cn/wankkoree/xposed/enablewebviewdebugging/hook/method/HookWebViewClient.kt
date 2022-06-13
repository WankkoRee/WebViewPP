package cn.wankkoree.xposed.enablewebviewdebugging.hook.method

import cn.wankkoree.xposed.enablewebviewdebugging.data.AppSP
import cn.wankkoree.xposed.enablewebviewdebugging.hook.Main
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.normalClass
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.StringType

/** Hook WebViewClient类，实现：
 *
 * webViewClient.onPageFinished({webView.evaluateJavascript($vConsole)})
 **/
fun PackageParam.hookWebViewClient (
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

                if (Main.debug) loggerD(msg = "${instanceClass.name}.onPageFinished({webView.evaluateJavascript(\$vConsole)})")
                findClass(Class_WebView).normalClass!!.method {
                    name = Method_evaluateJavascript
                    param(StringType, Class_ValueCallback)
                }.result {
                    onNoSuchMethod {
                        loggerE(msg = "Hook.Method.NoSuchMethod at hookWebViewClient\uD83D\uDC49onPageFinished\uD83D\uDC49evaluateJavascript", e = it)
                    }
                    get(webView).call(
                        "javascript:" + (if (prefs("apps_${Main.mProcessName}").get(AppSP.vConsole)) {
                            "if (typeof vConsole === 'undefined'){" +
                                    "   ${prefs("resources_vConsole_${prefs("apps_${Main.mProcessName}").get(AppSP.vConsole_version)}").getString("vConsole")};" +
                                    "   var vConsole=new VConsole();" + // 创建全局变量以供用户使用
                                    "   document.getElementById('__vconsole').style.zIndex=2147483647;" + // 将 vConsole 提升到最顶层
                                    "}"
                        } else ""), null
                    )
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