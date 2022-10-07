package cn.wankkoree.xposed.enablewebviewdebugging.hook.debug

import com.highcapable.yukihookapi.hook.factory.allMethods
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.android.WebViewClass

fun PackageParam.hookWebViewAllMembers() {
    WebViewClass.hook {
        this.instanceClass.allMethods { _, method ->
            injectMember {
                allConstructors()
                allMethods(method.name)
                beforeHook {
                    loggerD(msg = "!!!! !!!! !!!! !!!!")
                    loggerD(msg = "WebView.${method.name}(${args.joinToString(", ")})")
                    printStackTrace()
                }
            }.result {
                onNoSuchMemberFailure {
                    loggerE(msg = "Hook.Member.NoSuchMember at hookWebViewAllMembers\uD83D\uDC49${method.name}", e = it)
                }
                onHookingFailure {
                    loggerE(msg = "Hook.Member.HookFailure at hookWebViewAllMembers\uD83D\uDC49${method.name}", e = it)
                }
                onHooked {
                    loggerI(msg = "Hook.Member.Ended at hookWebViewAllMembers\uD83D\uDC49${method.name} as [$it]")
                }
                onConductFailure { hookParam, it ->
                    loggerE(msg = "Hook.Member.ConductFailure at hookWebViewAllMembers\uD83D\uDC49${method.name}(${hookParam.args.joinToString(", ")})", e = it)
                }
            }
        }
    }.result {
        onHookClassNotFoundFailure {
            loggerE(msg = "Hook.Class.NotFound at hookWebViewAllMembers", e = it)
        }
        onPrepareHook {
            loggerI(msg = "Hook.Class.Started at hookWebViewAllMembers")
        }
    }
}
