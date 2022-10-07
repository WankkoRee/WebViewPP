package cn.wankkoree.xp.webviewpp.data

import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object AppsSP {
    val enabled = PrefsData("enabled", hashSetOf<String>()) // 哈希去重的包名集合，以|分隔
    val show_system_app = PrefsData("show_system_app", false)
    val show_no_network = PrefsData("show_no_network", false)
}