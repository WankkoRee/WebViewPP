package cn.wankkoree.xposed.enablewebviewdebugging.hook.method

import cn.wankkoree.xposed.enablewebviewdebugging.hook.Main
import cn.wankkoree.xposed.enablewebviewdebugging.hook.debug.printStackTrace
import cn.wankkoree.xposed.enablewebviewdebugging.hook.methodX
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.normalClass
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.param.PackageParam

private val webSettingsClassHashSet = HashSet<String>()

/** Hook XWalkView类和XWalkPreferences类，实现：
 *
 * xWalkView.getSettings().setJavaScriptEnabled(true)
 *
 * XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true)
 *
 * XWalkPreferences.setValue(XWalkPreferences.ENABLE_JAVASCRIPT, true)
 *
 * webView.loadUrl() debug breakpoint
 *
 * webView.setResourceClient() debug breakpoint
 **/
fun PackageParam.hookCrossWalk (
    Class_XWalkView: String,
    Method_getSettings: String,
    Method_setJavaScriptEnabled: String,
    Method_loadUrl: String,
    Method_setResourceClient: String,
    Class_XWalkPreferences: String,
    Method_setValue: String,
) {
    Class_XWalkView.hook {
        injectMember {
            allConstructors()
            afterHook {
                val webView = instance
                val webSettings = method {
                    methodX(Method_getSettings)
                }.result {
                    onNoSuchMethod {
                        loggerE(msg = "Hook.Method.NoSuchMethod at hookCrossWalk\uD83D\uDC49<init>\uD83D\uDC49getSettings", e = it)
                    }
                }.get(webView).call()

                findClass(Class_XWalkPreferences).normalClass!!.method {
                    methodX(Method_setValue)
                }.result {
                    onNoSuchMethod {
                        loggerE(msg = "Hook.Method.NoSuchMethod at hookCrossWalk\uD83D\uDC49<init>\uD83D\uDC49setValue", e = it)
                    }
                    if (Main.debug) loggerD(msg = "${instanceClass.name}.setValue(XWalkPreferences.REMOTE_DEBUGGING, true)")
                    get().call("remote-debugging", true)
                    if (Main.debug) loggerD(msg = "${instanceClass.name}.setValue(XWalkPreferences.ENABLE_JAVASCRIPT, true)")
                    get().call("enable-javascript", true)
                }

                if (Main.debug) loggerD(msg = "${instanceClass.name} new().getSettings().setJavaScriptEnabled(true)")
                webSettings!!.javaClass.method {
                    methodX(Method_setJavaScriptEnabled)
                }.result {
                    onNoSuchMethod {
                        loggerE(msg = "Hook.Method.NoSuchMethod at hookCrossWalk\uD83D\uDC49<init>\uD83D\uDC49setJavaScriptEnabled", e = it)
                    }
                    get(webSettings).call(true)
                }

                if (!webSettingsClassHashSet.contains(webSettings.javaClass.name)) {
                    webSettings.javaClass.hook(isUseAppClassLoader = false) {
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
                                loggerE(msg = "Hook.Member.NoSuchMember at hookCrossWalk\uD83D\uDC49<init>\uD83D\uDC49setJavaScriptEnabled", e = it)
                            }
                            onHookingFailure {
                                loggerE(msg = "Hook.Member.HookFailure at hookCrossWalk\uD83D\uDC49<init>\uD83D\uDC49setJavaScriptEnabled", e = it)
                            }
                            onHooked {
                                loggerI(msg = "Hook.Member.Ended at hookCrossWalk\uD83D\uDC49<init>\uD83D\uDC49setJavaScriptEnabled as [$it]")
                            }
                            onConductFailure { hookParam, it ->
                                loggerE(
                                    msg = "Hook.Member.ConductFailure at hookCrossWalk\uD83D\uDC49<init>\uD83D\uDC49setJavaScriptEnabled(${
                                        hookParam.args.joinToString(
                                            ", "
                                        )
                                    })", e = it
                                )
                            }
                        }
                    }.result {
                        onHookClassNotFoundFailure {
                            loggerE(
                                msg = "Hook.Class.NotFound at hookCrossWalk\uD83D\uDC49$Class_XWalkView\uD83D\uDC49${webSettings.javaClass.name}",
                                e = it
                            )
                        }
                        onPrepareHook {
                            loggerI(msg = "Hook.Class.Started at hookCrossWalk\uD83D\uDC49$Class_XWalkView\uD83D\uDC49${webSettings.javaClass.name}")
                        }
                    }
                    webSettingsClassHashSet.add(webSettings.javaClass.name)
                }
            }
        }.result {
            onNoSuchMemberFailure {
                loggerE(msg = "Hook.Member.NoSuchMember at hookCrossWalk\uD83D\uDC49<init>", e = it)
            }
            onHookingFailure {
                loggerE(msg = "Hook.Member.HookFailure at hookCrossWalk\uD83D\uDC49<init>", e = it)
            }
            onHooked {
                loggerI(msg = "Hook.Member.Ended at hookCrossWalk\uD83D\uDC49<init> as [$it]")
            }
            onConductFailure { hookParam, it ->
                loggerE(msg = "Hook.Member.ConductFailure at hookCrossWalk\uD83D\uDC49<init>(${hookParam.args.joinToString(", ")})", e = it)
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
                    loggerE(msg = "Hook.Member.NoSuchMember at hookCrossWalk\uD83D\uDC49loadUrl", e = it)
                }
                onHookingFailure {
                    loggerE(msg = "Hook.Member.HookFailure at hookCrossWalk\uD83D\uDC49loadUrl", e = it)
                }
                onHooked {
                    loggerI(msg = "Hook.Member.Ended at hookCrossWalk\uD83D\uDC49loadUrl as [$it]")
                }
                onConductFailure { hookParam, it ->
                    loggerE(msg = "Hook.Member.ConductFailure at hookCrossWalk\uD83D\uDC49loadUrl(${hookParam.args.joinToString(", ")})", e = it)
                }
            }
        }

        if (Main.debug) {
            injectMember {
                methodX(Method_setResourceClient)
                afterHook {
                    if (args[0] != null) {
                        loggerD(msg = "${instanceClass.name}.setResourceClient(${args[0]!!.javaClass.name})")
                    } else { // 撤销设置 WebViewClient
                        loggerD(msg = "${instanceClass.name}.setResourceClient(null)")
                    }
                }
            }.result {
                onNoSuchMemberFailure {
                    loggerE(msg = "Hook.Member.NoSuchMember at hookCrossWalk\uD83D\uDC49setResourceClient", e = it)
                }
                onHookingFailure {
                    loggerE(msg = "Hook.Member.HookFailure at hookCrossWalk\uD83D\uDC49setResourceClient", e = it)
                }
                onHooked {
                    loggerI(msg = "Hook.Member.Ended at hookCrossWalk\uD83D\uDC49setResourceClient as [$it]")
                }
                onConductFailure { hookParam, it ->
                    loggerE(msg = "Hook.Member.ConductFailure at hookCrossWalk\uD83D\uDC49setResourceClient(${hookParam.args.joinToString(", ")})", e = it)
                }
            }
        }
    }.result {
        onHookClassNotFoundFailure {
            loggerE(msg = "Hook.Class.NotFound at hookCrossWalk\uD83D\uDC49$Class_XWalkView", e = it)
        }
        onPrepareHook {
            loggerI(msg = "Hook.Class.Started at hookCrossWalk\uD83D\uDC49$Class_XWalkView")
        }
    }
    Class_XWalkPreferences.hook {
        injectMember {
            methodX(Method_setValue)
            beforeHook {
                if (args[0] == "remote-debugging") {
                    if (args[1] != true) {
                        if (Main.debug) loggerD(msg = "${instanceClass.name}.setValue(XWalkPreferences.REMOTE_DEBUGGING, ${args[1]} -> true)")
                        args(1).set(true)
                    }
                } else if (args[0] == "enable-javascript") {
                    if (args[1] != true) {
                        if (Main.debug) loggerD(msg = "${instanceClass.name}.setValue(XWalkPreferences.ENABLE_JAVASCRIPT, ${args[1]} -> true)")
                        args(1).set(true)
                    }
                }
            }
        }.result {
            onNoSuchMemberFailure {
                loggerE(msg = "Hook.Member.NoSuchMember at hookCrossWalkPreferences\uD83D\uDC49<init>", e = it)
            }
            onHookingFailure {
                loggerE(msg = "Hook.Member.HookFailure at hookCrossWalkPreferences\uD83D\uDC49<init>", e = it)
            }
            onHooked {
                loggerI(msg = "Hook.Member.Ended at hookCrossWalkPreferences\uD83D\uDC49<init> as [$it]")
            }
            onConductFailure { hookParam, it ->
                loggerE(msg = "Hook.Member.ConductFailure at hookCrossWalkPreferences\uD83D\uDC49<init>(${hookParam.args.joinToString(", ")})", e = it)
            }
        }
    }.result {
        onHookClassNotFoundFailure {
            loggerE(msg = "Hook.Class.NotFound at hookCrossWalkPreferences\uD83D\uDC49$Class_XWalkPreferences", e = it)
        }
        onPrepareHook {
            loggerI(msg = "Hook.Class.Started at hookCrossWalkPreferences\uD83D\uDC49$Class_XWalkPreferences")
        }
    }
}