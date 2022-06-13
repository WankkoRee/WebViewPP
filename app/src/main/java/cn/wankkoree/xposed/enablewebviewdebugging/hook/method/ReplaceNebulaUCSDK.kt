package cn.wankkoree.xposed.enablewebviewdebugging.hook.method

import android.util.Base64
import cn.wankkoree.xposed.enablewebviewdebugging.data.AppSP
import cn.wankkoree.xposed.enablewebviewdebugging.hook.Main
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.param.PackageParam
import java.io.File

/** Hook UcServiceSetup类，实现：
 *
 * ucServiceSetup.updateUCVersionAndSdcardPath({sInitUcFromSdcardPath=$nebulaUCSDK})
 **/
fun PackageParam.replaceNebulaUCSDK (
    Class_UcServiceSetup: String = "com.alipay.mobile.nebulauc.impl.UcServiceSetup",
    Method_updateUCVersionAndSdcardPath: String = "updateUCVersionAndSdcardPath",
    Field_sInitUcFromSdcardPath: String = "sInitUcFromSdcardPath",
    cpuArch: String,
) {
    Class_UcServiceSetup.hook {
        injectMember {
            allMethods(Method_updateUCVersionAndSdcardPath)
            afterHook {
                if (Main.debug) loggerD(msg = "${instanceClass.name}.updateUCVersionAndSdcardPath({sInitUcFromSdcardPath=\$nebulaUCSDK})")
                if (prefs("apps_${Main.mProcessName}").get(AppSP.nebulaUCSDK)) {
                    File(appContext.getExternalFilesDir("nebulaUCSDK"), "libWebViewCore_ri_7z_uc.so").also {
                        if (!it.exists()) {
                            val nebulaUCSDK = Base64.decode(
                                prefs("resources_nebulaUCSDK_${prefs("apps_${Main.mProcessName}").get(AppSP.nebulaUCSDK_version)}").getString("nebulaUCSDK_$cpuArch"),
                                Base64.NO_WRAP
                            )
                            it.writeBytes(nebulaUCSDK)
                        }
                        this@afterHook.field {
                            name(Field_sInitUcFromSdcardPath)
                            modifiers {
                                asStatic()
                            }
                        }.result {
                            onNoSuchField {
                                loggerE(
                                    msg = "Hook.Field.NoSuchField at replaceNebulaUCSDK\uD83D\uDC49updateUCVersionAndSdcardPath\uD83D\uDC49sInitUcFromSdcardPath",
                                    e = it
                                )
                            }
                            get().set(it.absolutePath)
                        }
                    }
                }
            }
        }
    }.result {
        onHookClassNotFoundFailure {
            loggerE(msg = "Hook.Class.NotFound at replaceNebulaUCSDK\uD83D\uDC49$Class_UcServiceSetup", e = it)
        }
        onPrepareHook {
            loggerI(msg = "Hook.Class.Started at replaceNebulaUCSDK\uD83D\uDC49$Class_UcServiceSetup")
        }
    }
}