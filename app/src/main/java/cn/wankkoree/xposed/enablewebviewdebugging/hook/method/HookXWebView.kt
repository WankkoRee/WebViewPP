package cn.wankkoree.xposed.enablewebviewdebugging.hook.method

import android.widget.Toast
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.param.PackageParam

/** Hook XWeb WebView类，实现：
 *
 * webView.Method_initWebviewCore({Toast(XWeb Engine)})
 **/
fun PackageParam.hookXWebView (
    Class_XWebView: String = "com.tencent.xweb.WebView",
    Method_initWebviewCore: String = "initWebviewCore",
    Method_isXWalk: String = "isXWalk",
    Method_isPinus: String = "isPinus",
    Method_isX5: String = "isX5",
    Method_isSys: String = "isSys",
) {
    Class_XWebView.hook {
        injectMember {
            allMethods(Method_initWebviewCore)
            afterHook {
                val isXWalk = method {
                    name(Method_isXWalk)
                }.result {
                    onNoSuchMethod {
                        loggerE(msg = "Hook.Method.NoSuchMethod at hookXWebView\uD83D\uDC49initWebviewCore\uD83D\uDC49isXWalk", e = it)
                    }
                }.get().call() as Boolean
                val isPinus = method {
                    name(Method_isPinus)
                }.result {
                    onNoSuchMethod {
                        loggerE(msg = "Hook.Method.NoSuchMethod at hookXWebView\uD83D\uDC49initWebviewCore\uD83D\uDC49isPinus", e = it)
                    }
                }.get().call() as Boolean
                val isX5 = method {
                    name(Method_isX5)
                }.result {
                    onNoSuchMethod {
                        loggerE(msg = "Hook.Method.NoSuchMethod at hookXWebView\uD83D\uDC49initWebviewCore\uD83D\uDC49isX5", e = it)
                    }
                }.get().call() as Boolean
                val isSys = method {
                    name(Method_isSys)
                }.result {
                    onNoSuchMethod {
                        loggerE(msg = "Hook.Method.NoSuchMethod at hookXWebView\uD83D\uDC49initWebviewCore\uD83D\uDC49isSys", e = it)
                    }
                }.get().call() as Boolean

                loggerI(msg = "Current XWeb Engine is" + if (isXWalk || isPinus || isX5 || isSys) (arrayOf(
                    if (isXWalk) "XWalk" else null,
                    if (isPinus) "Pinus" else null,
                    if (isX5) "TBS X5" else null,
                    if (isSys) "System" else null,
                ).filterNotNull().joinToString(" + ", " ")) else (" " + "unknown" + ", " + "please report a issue for it!"))
            }
        }.result {
            onNoSuchMemberFailure {
                loggerE(msg = "Hook.Member.NoSuchMember at hookXWebView\uD83D\uDC49initWebviewCore", e = it)
            }
            onHookingFailure {
                loggerE(msg = "Hook.Member.HookFailure at hookXWebView\uD83D\uDC49initWebviewCore", e = it)
            }
            onHooked {
                loggerI(msg = "Hook.Member.Ended at hookXWebView\uD83D\uDC49initWebviewCore as [$it]")
            }
            onConductFailure { hookParam, it ->
                loggerE(msg = "Hook.Member.ConductFailure at hookXWebView\uD83D\uDC49initWebviewCore(${hookParam.args.joinToString(", ")})", e = it)
            }
        }
    }.result {
        onHookClassNotFoundFailure {
            loggerE(msg = "Hook.Class.NotFound at hookXWebView\uD83D\uDC49$Class_XWebView", e = it)
        }
        onPrepareHook {
            loggerI(msg = "Hook.Class.Started at hookXWebView\uD83D\uDC49$Class_XWebView")
        }
    }
}