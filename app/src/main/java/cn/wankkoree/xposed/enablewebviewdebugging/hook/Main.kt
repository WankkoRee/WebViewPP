package cn.wankkoree.xposed.enablewebviewdebugging.hook

import cn.wankkoree.xposed.enablewebviewdebugging.BuildConfig
import cn.wankkoree.xposed.enablewebviewdebugging.data.AppSP
import cn.wankkoree.xposed.enablewebviewdebugging.data.getList
import cn.wankkoree.xposed.enablewebviewdebugging.data.getSet
import cn.wankkoree.xposed.enablewebviewdebugging.hook.debug.*
import cn.wankkoree.xposed.enablewebviewdebugging.hook.method.*
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.highcapable.yukihookapi.hook.log.*

@InjectYukiHookWithXposed(entryClassName = "HookEntry")
class Main : IYukiHookXposedInit {
    companion object {
        val debug = BuildConfig.DEBUG || BuildConfig.BUILD_TYPE == "dev"
    }

    override fun onInit() = YukiHookAPI.configs {
        isDebug = debug
        isAllowPrintingLogs = debug
        isEnableModulePrefsCache = false
        isEnableMemberCache = false
    }

    override fun onHook() = YukiHookAPI.encase {
        YukiHookAPI.Configs.debugTag = "EnableWebViewDebugging<$packageName>"

        if (packageName != processName && !processName.startsWith("$packageName:")) { // 不为主进程和私有进程 TODO: 判断公有进程
            loggerI(msg = "do not hook other application process")
            return@encase // 不 hook 憨批 MIUI 等会被重复 hook 的情况
        }
        if (packageName == BuildConfig.APPLICATION_ID) {
            loggerI(msg = "do not hook self")
            return@encase // 不 hook 自己
        }
        if (packageName == "com.android.webview" || packageName == "com.google.android.webview") {
            loggerI(msg = "do not hook webview library")
            return@encase // 不 hook WebView 本身
        }
        loggerI(msg = "hook $packageName which run in $processName")

        val pref = prefs("apps_$packageName")
        if (!pref.get(AppSP.is_enabled)) {
            loggerI(msg = "$packageName hooking not enabled")
            return@encase // 目标 App 的 Hook 未启用
        }
        val cpuArch = with(appInfo.nativeLibraryDir) {
            when {
                endsWith("arm64") -> "arm64-v8a"
                endsWith("arm") -> "armeabi-v7a"
                else -> {
                    loggerE(msg = "the cpuArch(${toString()}) is not supported")
                    null
                }
            }
        }

        loggerI(msg = "loading rules")

        pref.getSet(AppSP.hooks).forEach { name ->
            val hookEntry = pref.getList<String>("hook_entry_$name")
            when (hookEntry[0]) {
                // TODO: 添加更多 hook 方法
                "hookWebView" -> {
                    hookWebView(
                        Class_WebView = hookEntry[1],
                        Method_getSettings = hookEntry[2],
                        Method_setWebContentsDebuggingEnabled = hookEntry[3],
                        Method_setJavaScriptEnabled = hookEntry[4],
                        Method_loadUrl = hookEntry[5],
                        Method_setWebViewClient = hookEntry[6],
                    )
                }
                "hookWebViewClient" -> {
                    hookWebViewClient(
                        Class_WebView = hookEntry[1],
                        Class_WebViewClient = hookEntry[2],
                        Method_onPageFinished = hookEntry[3],
                        Method_evaluateJavascript = hookEntry[4],
                        Class_ValueCallback = hookEntry[5],
                    )
                }
                "replaceNebulaUCSDK" -> {
                    if (cpuArch != null) replaceNebulaUCSDK(
                        Class_UcServiceSetup = hookEntry[1],
                        Method_updateUCVersionAndSdcardPath = hookEntry[2],
                        Field_sInitUcFromSdcardPath = hookEntry[3],
                        cpuArch,
                    )
                }
                "hookCrossWalk" -> {
                    hookCrossWalk(
                        Class_XWalkView = hookEntry[1],
                        Method_getSettings = hookEntry[2],
                        Method_setJavaScriptEnabled = hookEntry[3],
                        Method_loadUrl = hookEntry[4],
                        Method_setResourceClient = hookEntry[5],
                        Class_XWalkPreferences = hookEntry[6],
                        Method_setValue = hookEntry[7],
                    )
                }
                "hookXWebPreferences" -> {
                    hookXWebPreferences(
                        Class_XWebPreferences = hookEntry[1],
                        Method_setValue = hookEntry[2],
                    )
                }
                else -> {
                    loggerE(msg = "Unknown Hook Method: ${hookEntry[0]}")
                }
            }
        }
    }
}