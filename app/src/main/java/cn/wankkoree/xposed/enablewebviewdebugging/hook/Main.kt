package cn.wankkoree.xposed.enablewebviewdebugging.hook

import cn.wankkoree.xposed.enablewebviewdebugging.BuildConfig
import cn.wankkoree.xposed.enablewebviewdebugging.data.AppSP
import cn.wankkoree.xposed.enablewebviewdebugging.data.ModuleSP
import cn.wankkoree.xposed.enablewebviewdebugging.data.getSet
import cn.wankkoree.xposed.enablewebviewdebugging.hook.debug.*
import cn.wankkoree.xposed.enablewebviewdebugging.hook.method.*
import cn.wankkoree.xposed.enablewebviewdebugging.http.bean.HookRules
import com.google.gson.Gson
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.highcapable.yukihookapi.hook.log.*

@InjectYukiHookWithXposed(entryClassName = "Entry", isUsingResourcesHook = false)
class Main : IYukiHookXposedInit {
    companion object {
        val debug = BuildConfig.DEBUG || BuildConfig.BUILD_TYPE == "dev"
        var mProcessName = ""
    }

    override fun onInit() = YukiHookAPI.configs {
        isDebug = debug
        isAllowPrintingLogs = debug
        isEnableModulePrefsCache = false
        isEnableMemberCache = false
    }

    override fun onHook() = YukiHookAPI.encase {
        loadApp {
            mProcessName = processName.substringBefore(':')

            loggerI(msg = "Welcome to EnableWebViewDebugging ${BuildConfig.VERSION_NAME}-${BuildConfig.BUILD_TYPE}(${BuildConfig.VERSION_CODE})!")

            if (packageName != mProcessName) { // 不为主进程和私有进程 TODO: 判断公有进程
                loggerI(msg = "do not hook other application process：$mProcessName")
                return@encase // 不 hook 憨批 MIUI 等会被重复 hook 的情况
            }
            if (mProcessName == BuildConfig.APPLICATION_ID) {
                loggerI(msg = "do not hook self：$mProcessName")
                return@encase // 不 hook 自己
            }
            if (mProcessName == "com.android.webview" || mProcessName == "com.google.android.webview") {
                loggerW(msg = "do not hook webview library：$mProcessName")
                return@encase // 不 hook WebView 本身
            }
            val pref = prefs("apps_$mProcessName")
            if (!pref.get(AppSP.is_enabled)) {
                loggerI(msg = "$mProcessName hooking not enabled")
                return@encase // 目标 App 的 Hook 未启用
            }

            YukiHookAPI.Configs.debugTag = "EnableWebViewDebugging<$packageName>"

            loggerI(msg = "hook $mProcessName which run in $processName")

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
                val hookJson = pref.getString("hook_entry_$name", "{}")
                try {
                    when(val hookMethod = Gson().fromJson(hookJson, HookRules.HookRule::class.java).name) {
                        // TODO: 添加更多 hook 方法
                        "hookWebView" -> {
                            val hookEntry = Gson().fromJson(hookJson, HookRules.HookRuleWebView::class.java)
                            hookWebView(
                                Class_WebView = hookEntry.Class_WebView,
                                Method_getSettings = hookEntry.Method_getSettings,
                                Method_setWebContentsDebuggingEnabled = hookEntry.Method_setWebContentsDebuggingEnabled,
                                Method_setJavaScriptEnabled = hookEntry.Method_setJavaScriptEnabled,
                                Method_loadUrl = hookEntry.Method_loadUrl,
                                Method_setWebViewClient = hookEntry.Method_setWebViewClient,
                            )
                        }
                        "hookWebViewClient" -> {
                            val hookEntry = Gson().fromJson(hookJson, HookRules.HookRuleWebViewClient::class.java)
                            hookWebViewClient(
                                Class_WebViewClient = hookEntry.Class_WebViewClient,
                                Method_onPageFinished = hookEntry.Method_onPageFinished,
                                Class_WebView = hookEntry.Class_WebView,
                                Method_evaluateJavascript = hookEntry.Method_evaluateJavascript,
                            )
                        }
                        "replaceNebulaUCSDK" -> {
                            if (cpuArch != null) {
                                val hookEntry = Gson().fromJson(hookJson, HookRules.ReplaceNebulaUCSDK::class.java)
                                replaceNebulaUCSDK(
                                    Class_UcServiceSetup = hookEntry.Class_UcServiceSetup,
                                    Method_updateUCVersionAndSdcardPath = hookEntry.Method_updateUCVersionAndSdcardPath,
                                    Field_sInitUcFromSdcardPath = hookEntry.Field_sInitUcFromSdcardPath,
                                    cpuArch = cpuArch,
                                )
                            }
                        }
                        "hookCrossWalk" -> {
                            val hookEntry = Gson().fromJson(hookJson, HookRules.HookCrossWalk::class.java)
                            hookCrossWalk(
                                Class_XWalkView = hookEntry.Class_XWalkView,
                                Method_getSettings = hookEntry.Method_getSettings,
                                Method_setJavaScriptEnabled = hookEntry.Method_setJavaScriptEnabled,
                                Method_loadUrl = hookEntry.Method_loadUrl,
                                Method_setResourceClient = hookEntry.Method_setResourceClient,
                                Class_XWalkPreferences = hookEntry.Class_XWalkPreferences,
                                Method_setValue = hookEntry.Method_setValue,
                            )
                        }
                        "hookXWebPreferences" -> {
                            val hookEntry = Gson().fromJson(hookJson, HookRules.HookXWebPreferences::class.java)
                            hookXWebPreferences(
                                Class_XWebPreferences = hookEntry.Class_XWebPreferences,
                                Method_setValue = hookEntry.Method_setValue,
                            )
                        }
                        "hookXWebView" -> {
                            val hookEntry = Gson().fromJson(hookJson, HookRules.HookXWebView::class.java)
                            hookXWebView(
                                Class_XWebView = hookEntry.Class_XWebView,
                                Method_initWebviewCore = hookEntry.Method_initWebviewCore,
                                Method_isXWalk = hookEntry.Method_isXWalk,
                                Method_isPinus = hookEntry.Method_isPinus,
                                Method_isX5 = hookEntry.Method_isX5,
                                Method_isSys = hookEntry.Method_isSys,
                            )
                        }
                        else -> {
                            loggerE(msg = "Unknown Hook Method: $hookMethod")
                        }
                    }
                } catch (e: Exception) {
                    loggerE(msg = "Parse Failed!", e = e)
                    return@forEach // continue
                }
            }
        }
    }
}