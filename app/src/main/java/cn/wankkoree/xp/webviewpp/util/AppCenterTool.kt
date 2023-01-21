package cn.wankkoree.xp.webviewpp.util

import cn.wankkoree.xp.webviewpp.BuildConfig
import cn.wankkoree.xp.webviewpp.application.Application
import cn.wankkoree.xp.webviewpp.data.ModuleSP
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.analytics.EventProperties
import com.microsoft.appcenter.crashes.Crashes

object AppCenterTool {
    private val application = ModuleApplication.appContext as Application

    private var initialized = false
    fun init() {
        if (application.modulePrefs("module").get(ModuleSP.app_center)) { // 开启
            if (!initialized) { // 未初始化
                AppCenter.start(application, BuildConfig.APP_CENTER_SECRET, Analytics::class.java, Crashes::class.java)
                if (AppCenter.isConfigured()) initialized = true
            } else {
                AppCenter.setEnabled(true)
            }
        } else {
            if (initialized) {
                AppCenter.setEnabled(false)
            }
        }
    }
    fun trackEvent (name : String) {
        if (!initialized) return
        if (!application.modulePrefs("module").get(ModuleSP.app_center)) return
        Analytics.trackEvent(name)
    }
    fun trackEvent (name : String, properties : Map<String, String>) {
        if (!initialized) return
        if (!application.modulePrefs("module").get(ModuleSP.app_center)) return
        Analytics.trackEvent(name, properties)
    }
    fun trackEvent (name : String, properties : Map<String, String>, flags : Int) {
        if (!initialized) return
        if (!application.modulePrefs("module").get(ModuleSP.app_center)) return
        Analytics.trackEvent(name, properties, flags)
    }
    fun trackEvent (name : String, properties : EventProperties) {
        if (!initialized) return
        if (!application.modulePrefs("module").get(ModuleSP.app_center)) return
        Analytics.trackEvent(name, properties)
    }
    fun trackEvent (name : String, properties : EventProperties, flags : Int) {
        if (!initialized) return
        if (!application.modulePrefs("module").get(ModuleSP.app_center)) return
        Analytics.trackEvent(name, properties, flags)
    }
}