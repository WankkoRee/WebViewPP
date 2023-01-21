package cn.wankkoree.xp.webviewpp.hook.method

import cn.wankkoree.xp.webviewpp.hook.Main
import cn.wankkoree.xp.webviewpp.hook.debug.printStackTrace
import cn.wankkoree.xp.webviewpp.hook.methodX
import com.highcapable.yukihookapi.hook.factory.MembersType
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.param.PackageParam

private val webSettingsClassHashSet = HashSet<String>()

/** Hook WebView类，实现:
 *
 * WebView.setWebContentsDebuggingEnabled(true)
 *
 * webView.getSettings().setJavaScriptEnabled(true)
 *
 * webView.loadUrl() debug breakpoint
 *
 * webView.setWebViewClient() debug breakpoint
 **/
fun PackageParam.hookWebView (
    Class_WebView : String,
    Method_getSettings : String,
    Method_setWebContentsDebuggingEnabled : String,
    Method_setJavaScriptEnabled : String,
    Method_loadUrl : String,
    Method_setWebViewClient : String,
) {
    Class_WebView.hook {
        injectMember {
            allMembers(MembersType.CONSTRUCTOR)
            afterHook {
                val webView = instance
                val webSettings = method {
                    methodX(Method_getSettings)
                }.result {
                    onNoSuchMethod {
                        loggerE(msg = "Hook.Method.NoSuchMethod at hookWebView\uD83D\uDC49<init>\uD83D\uDC49getSettings", e = it)
                    }
                }.get(webView).call()

                if (Main.debug) loggerD(msg = "${instanceClass.name} new().static setWebContentsDebuggingEnabled(true)")
                method {
                    methodX(Method_setWebContentsDebuggingEnabled)
                }.result {
                    onNoSuchMethod {
                        loggerE(msg = "Hook.Method.NoSuchMethod at hookWebView\uD83D\uDC49<init>\uD83D\uDC49setWebContentsDebuggingEnabled", e = it)
                    }
                    get().call(true)
                }

                if (Main.debug) loggerD(msg = "${instanceClass.name} new().getSettings().setJavaScriptEnabled(true)")
                webSettings!!.javaClass.method {
                    methodX(Method_setJavaScriptEnabled)
                }.result {
                    onNoSuchMethod {
                        loggerE(msg = "Hook.Method.NoSuchMethod at hookWebView\uD83D\uDC49<init>\uD83D\uDC49setJavaScriptEnabled", e = it)
                    }
                    get(webSettings).call(true)
                }

                if (!webSettingsClassHashSet.contains(webSettings.javaClass.name)) {
                    hookWebSettings(webSettings.javaClass, Method_setJavaScriptEnabled)
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
            methodX(Method_setWebContentsDebuggingEnabled)
            beforeHook {
                if (args[0] != true) {
                    if (Main.debug) loggerD(msg = "${instanceClass.name}.setWebContentsDebuggingEnabled(${args[0]} -> true)")
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

        if (Main.debug) {
            injectMember {
                methodX(Method_loadUrl)
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

        if (Main.debug) {
            injectMember {
                methodX(Method_setWebViewClient)
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
/** Hook WebSettings类，实现:
 *
 * webSettings().setJavaScriptEnabled(true)
 **/
fun PackageParam.hookWebSettings(
    Class_WebSettings : Class<*>,
    Method_setJavaScriptEnabled : String,
) {
    Class_WebSettings.hook(isForceUseAbsolute = false) {
        injectMember {
            methodX(Method_setJavaScriptEnabled)
            beforeHook {
                if (args[0] != true) {
                    if (Main.debug) loggerD(msg = "${instanceClass.name}.setJavaScriptEnabled(${args[0]} -> true)")
                    args(0).set(true)
                }
            }
        }.result {
            onNoSuchMemberFailure {
                loggerE(msg = "Hook.Member.NoSuchMember at hookWebSettings\uD83D\uDC49setJavaScriptEnabled", e = it)
            }
            onHookingFailure {
                loggerE(msg = "Hook.Member.HookFailure at hookWebSettings\uD83D\uDC49setJavaScriptEnabled", e = it)
            }
            onHooked {
                loggerI(msg = "Hook.Member.Ended at hookWebSettings\uD83D\uDC49setJavaScriptEnabled as [$it]")
            }
            onConductFailure { hookParam, it ->
                loggerE(msg = "Hook.Member.ConductFailure at hookWebSettings\uD83D\uDC49setJavaScriptEnabled(${hookParam.args.joinToString(", ")})", e = it)
            }
        }
    }.result {
        onHookClassNotFoundFailure {
            loggerE(msg = "Hook.Class.NotFound at hookWebSettings\uD83D\uDC49${Class_WebSettings.name}", e = it)
        }
        onPrepareHook {
            loggerI(msg = "Hook.Class.Started at hookWebSettings\uD83D\uDC49${Class_WebSettings.name}")
        }
    }
}