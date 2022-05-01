package cn.wankkoree.xposed.enablewebviewdebugging.hook.debug

import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.StringType

fun PackageParam.hookAllLoadUrl() {
    ClassLoader::class.java.hook(isUseAppClassLoader = false) {
        injectMember {
            method{
                name("loadClass")
                param(StringType)
            }
            afterHook {
                val clazz = result!!.javaClass
                loggerD(msg = clazz.name)
                if (clazz.name == "LSPHooker_") return@afterHook
                clazz.hook {
                    injectMember {
                        method {
                            param(StringType).index(0)
                        }
                        beforeHook {
                            loggerD(msg = "hookAllLoadUrl -> ${this@beforeHook.instanceClass.name}.${this@beforeHook.method}(${this@beforeHook.args[0]})")
                            printStackTrace()
                        }
                    }.result {
                        onNoSuchMemberFailure {
                            loggerE(msg = "Hook.Member.NoSuchMember at hookAllLoadUrl\uD83D\uDC49loadUrl", e = it)
                        }
                        onHookingFailure {
                            loggerE(msg = "Hook.Member.HookFailure at hookAllLoadUrl\uD83D\uDC49loadUrl", e = it)
                        }
                        onHooked {
                            loggerI(msg = "Hook.Member.Ended at hookAllLoadUrl\uD83D\uDC49loadUrl as [$it]")
                        }
                        onConductFailure { hookParam, it ->
                            loggerE(msg = "Hook.Member.ConductFailure at hookAllLoadUrl\uD83D\uDC49loadUrl(${hookParam.args.joinToString(", ")})", e = it)
                        }
                    }
                }.result {
                    onHookClassNotFoundFailure {
                        loggerE(msg = "Hook.Class.NotFound at hookAllLoadUrl\uD83D\uDC49${ClassLoader::class.java.name}", e = it)
                    }
                    onPrepareHook {
                        loggerI(msg = "Hook.Class.Started at hookAllLoadUrl\uD83D\uDC49${ClassLoader::class.java.name}")
                    }
                }
            }
        }.result {
            onNoSuchMemberFailure {
                loggerE(msg = "Hook.Member.NoSuchMember at hookAllLoadUrl\uD83D\uDC49loadClass", e = it)
            }
            onHookingFailure {
                loggerE(msg = "Hook.Member.HookFailure at hookAllLoadUrl\uD83D\uDC49loadClass", e = it)
            }
            onHooked {
                loggerI(msg = "Hook.Member.Ended at hookAllLoadUrl\uD83D\uDC49loadClass as [$it]")
            }
            onConductFailure { hookParam, it ->
                loggerE(msg = "Hook.Member.ConductFailure at hookAllLoadUrl\uD83D\uDC49loadClass(${hookParam.args.joinToString(", ")})", e = it)
            }
        }
    }.result {
        onHookClassNotFoundFailure {
            loggerE(msg = "Hook.Class.NotFound at hookAllLoadUrl\uD83D\uDC49${ClassLoader::class.java.name}", e = it)
        }
        onPrepareHook {
            loggerI(msg = "Hook.Class.Started at hookAllLoadUrl\uD83D\uDC49${ClassLoader::class.java.name}")
        }
    }
}
