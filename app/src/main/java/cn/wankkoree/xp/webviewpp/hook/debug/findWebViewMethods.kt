package cn.wankkoree.xp.webviewpp.hook.debug

import cn.wankkoree.xp.webviewpp.BuildConfig
import com.highcapable.yukihookapi.hook.factory.listOfClasses
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.highcapable.yukihookapi.hook.type.java.JavaClassLoader
import com.highcapable.yukihookapi.hook.type.java.StringType

private val hooked = HashSet<Class<*>>()

private fun PackageParam.hookTarget(clazz: Class<*>) {
    if (clazz.name.startsWith(BuildConfig.APPLICATION_ID)) return
    if (clazz.name.startsWith("com.highcapable.yukihookapi.")) return
    if (clazz.name.startsWith("org.lsposed.")) return
    if (clazz.name.startsWith("de.robv.android.xposed.")) return
    if (clazz.name.startsWith("com.google.android.material.")) return
    if (clazz.name.startsWith("android.") && !clazz.name.startsWith("android.webkit.")) return
    if (clazz.name.startsWith("androidx.")) return
    if (clazz.name.startsWith("org.jetbrains.")) return
    if (clazz.name.startsWith("org.intellij.")) return
    if (clazz.name.startsWith("kotlin.")) return
    if (clazz.name.startsWith("java.")) return

    if (hooked.contains(clazz)) return

    hooked.add(clazz)

    loggerD(msg = "find clazz: ${clazz.name}")
    clazz.hook(isForceUseAbsolute = true) {
        injectMember {
            method {
                param(StringType)
            }.all()
            beforeHook {
                with(args(0).string()) {
                    if (
                        startsWith("http://") ||
                        startsWith("https://") ||
                        startsWith("file://") ||
                        startsWith("javascript:") ||
                        startsWith("about:")
                    ) {
                        loggerD(msg = "called: ${clazz.name}.${method.name}(${args.joinToString(", ")})")
                        printStackTrace()
                    }
                }
            }
        }
        injectMember {
            method {
                param(StringType, VagueType)
            }.all()
            beforeHook {
                with(args(0).string()) {
                    if (
                        startsWith("http://") ||
                        startsWith("https://") ||
                        startsWith("file://") ||
                        startsWith("javascript:") ||
                        startsWith("about:")
                    ) {
                        loggerD(msg = "called: ${clazz.name}.${method.name}(${args.joinToString(", ")})")
                        printStackTrace()
                    }
                }
            }
        }
        injectMember {
            method {
                param(VagueType, StringType)
            }.all()
            beforeHook {
                with(args(1).string()) {
                    if (
                        startsWith("http://") ||
                        startsWith("https://") ||
                        startsWith("file://") ||
                        startsWith("javascript:") ||
                        startsWith("about:")
                    ) {
                        loggerD(msg = "called: ${clazz.name}.${method.name}(${args.joinToString(", ")})")
                        printStackTrace()
                    }
                }
            }
        }
        injectMember {
            method {
                param(VagueType, StringType, VagueType)
            }.all()
            beforeHook {
                with(args(1).string()) {
                    if (
                        startsWith("http://") ||
                        startsWith("https://") ||
                        startsWith("file://") ||
                        startsWith("javascript:") ||
                        startsWith("about:")
                    ) {
                        loggerD(msg = "called: ${clazz.name}.${method.name}(${args.joinToString(", ")})")
                        printStackTrace()
                    }
                }
            }
        }
    }
}

fun PackageParam.findWebViewMethods() {
    JavaClassLoader.hook(isForceUseAbsolute = true) {
        useDangerousOperation("Yes do as I say!")
        injectMember {
            method {
                name = "loadClass"
                param(StringType)
            }
            afterHook {
                if (hasThrowable == true) return@afterHook
                if (result != null) {
                    (result as Class<*>).also { clazz ->
                        hookTarget(clazz)
                    }
                }
            }
        }.result {
            onNoSuchMemberFailure {
                loggerE(msg = "Hook.Member.NoSuchMember at findWebViewMethods\uD83D\uDC49loadClass", e = it)
            }
            onHookingFailure {
                loggerE(msg = "Hook.Member.HookFailure at findWebViewMethods\uD83D\uDC49loadClass", e = it)
            }
            onHooked {
                loggerI(msg = "Hook.Member.Ended at findWebViewMethods\uD83D\uDC49loadClass as [$it]")
            }
            onConductFailure { hookParam, it ->
                loggerE(msg = "Hook.Member.ConductFailure at findWebViewMethods\uD83D\uDC49loadClass(${hookParam.args.joinToString(", ")})", e = it)
            }
        }
    }.result {
        onHookClassNotFoundFailure {
            loggerE(msg = "Hook.Class.NotFound at findWebViewMethods", e = it)
        }
        onPrepareHook {
            loggerI(msg = "Hook.Class.Started at findWebViewMethods")
        }
    }
    appClassLoader.listOfClasses().forEach {
        try {
            hookTarget(it.toClass())
        } catch (e: Exception) {
            loggerE(msg = "hook Failed!", e = e)
            return@forEach // continue
        }
    }
}
