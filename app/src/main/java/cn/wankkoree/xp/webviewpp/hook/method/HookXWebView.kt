package cn.wankkoree.xp.webviewpp.hook.method

import cn.wankkoree.xp.webviewpp.hook.Main
import cn.wankkoree.xp.webviewpp.hook.methodX
import com.highcapable.yukihookapi.hook.factory.MembersType
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.param.PackageParam

/** Hook XWeb WebView类，实现:
 *
 * webView.initWebviewCore({Toast(XWeb Engine)})
 *
 * webView.initWebviewCore({XWebPreferences.setValue(IXWebPreferences.REMOTE_DEBUGGING, true)})
 *
 * webView.initWebviewCore({XWebPreferences.setValue(IXWebPreferences.ENABLE_JAVASCRIPT, true)})
 **/
fun PackageParam.hookXWebView (
    Class_XWebView: String,
    Method_initWebviewCore: String,
    Method_isXWeb: String,
    Method_isSys: String,
    Class_XWebPreferences: String,
    Method_setValue: String,
) {
    Class_XWebView.hook {
        injectMember {
            allMembers(MembersType.CONSTRUCTOR)
            afterHook {
                val xWebView = instance
                val isXWeb = method {
                    methodX(Method_isXWeb)
                }.result {
                    onNoSuchMethod {
                        loggerE(msg = "Hook.Method.NoSuchMethod at hookXWebView\uD83D\uDC49initWebviewCore\uD83D\uDC49Method_isXWeb", e = it)
                    }
                }.get(xWebView).call() as Boolean
                val isSys = method {
                    methodX(Method_isSys)
                }.result {
                    onNoSuchMethod {
                        loggerE(msg = "Hook.Method.NoSuchMethod at hookXWebView\uD83D\uDC49initWebviewCore\uD83D\uDC49isSys", e = it)
                    }
                }.get(xWebView).call() as Boolean

                loggerI(msg = "Current XWeb Engine is" + if (isXWeb || isSys) (arrayOf(
                    if (isXWeb) "XWeb" else null,
                    if (isSys) "System" else null,
                ).filterNotNull().joinToString(" + ", " ")) else (" " + "unknown" + ", " + "please report a issue for it!"))
            }
        }.result {
            onNoSuchMemberFailure {
                loggerE(msg = "Hook.Member.NoSuchMember at hookXWebView\uD83D\uDC49<init>", e = it)
            }
            onHookingFailure {
                loggerE(msg = "Hook.Member.HookFailure at hookXWebView\uD83D\uDC49<init>", e = it)
            }
            onHooked {
                loggerI(msg = "Hook.Member.Ended at hookXWebView\uD83D\uDC49<init> as [$it]")
            }
            onConductFailure { hookParam, it ->
                loggerE(msg = "Hook.Member.ConductFailure at hookXWebView\uD83D\uDC49<init>(${hookParam.args.joinToString(", ")})", e = it)
            }
        }
        injectMember {
            methodX(Method_initWebviewCore)
            afterHook {
                Class_XWebPreferences.toClass().method {
                    methodX(Method_setValue)
                }.result {
                    onNoSuchMethod {
                        loggerE(msg = "Hook.Method.NoSuchMethod at hookXWebView\uD83D\uDC49initWebviewCore\uD83D\uDC49XWebPreferences\uD83D\uDC49setValue", e = it)
                    }
                }.get().apply {
                    if (Main.debug) loggerD(msg = "${instanceClass.name}.initWebviewCore({XWebPreferences.setValue(IXWebPreferences.REMOTE_DEBUGGING, true)})")
                    call("remote-debugging", true)
                    if (Main.debug) loggerD(msg = "${instanceClass.name}.initWebviewCore({XWebPreferences.setValue(IXWebPreferences.ENABLE_JAVASCRIPT, true)})")
                    call("enable-javascript", true)
                }
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

    Class_XWebPreferences.hook {
        injectMember {
            methodX(Method_setValue)
            beforeHook {
                if (args[0] == "remote-debugging") {
                    if (args[1] != true) {
                        if (Main.debug) loggerD(msg = "${instanceClass.name}.setValue(XWebPreferences.REMOTE_DEBUGGING, ${args[1]} -> true)")
                        args(1).set(true)
                    }
                } else if (args[0] == "enable-javascript") {
                    if (args[1] != true) {
                        if (Main.debug) loggerD(msg = "${instanceClass.name}.setValue(XWebPreferences.ENABLE_JAVASCRIPT, ${args[1]} -> true)")
                        args(1).set(true)
                    }
                }
            }
        }.result {
            onNoSuchMemberFailure {
                loggerE(msg = "Hook.Member.NoSuchMember at hookXWebView\uD83D\uDC49XWebPreferences\uD83D\uDC49setValue", e = it)
            }
            onHookingFailure {
                loggerE(msg = "Hook.Member.HookFailure at hookXWebView\uD83D\uDC49XWebPreferences\uD83D\uDC49setValue", e = it)
            }
            onHooked {
                loggerI(msg = "Hook.Member.Ended at hookXWebView\uD83D\uDC49XWebPreferences\uD83D\uDC49setValue as [$it]")
            }
            onConductFailure { hookParam, it ->
                loggerE(msg = "Hook.Member.ConductFailure at hookXWebView\uD83D\uDC49XWebPreferences\uD83D\uDC49setValue(${hookParam.args.joinToString(", ")})", e = it)
            }
        }
    }.result {
        onHookClassNotFoundFailure {
            loggerE(msg = "Hook.Class.NotFound at hookXWebView\uD83D\uDC49XWebPreferences\uD83D\uDC49$Class_XWebPreferences", e = it)
        }
        onPrepareHook {
            loggerI(msg = "Hook.Class.Started at hookXWebView\uD83D\uDC49XWebPreferences\uD83D\uDC49$Class_XWebPreferences")
        }
    }
}