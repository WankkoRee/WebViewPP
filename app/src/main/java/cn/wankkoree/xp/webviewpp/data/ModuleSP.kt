package cn.wankkoree.xp.webviewpp.data

import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object ModuleSP {
    val data_source = PrefsData("data_source", "https://raw.githubusercontent.com/WankkoRee/WebViewPP-Rules/master")
    val auto_check_update = PrefsData("auto_check_update", true)
}