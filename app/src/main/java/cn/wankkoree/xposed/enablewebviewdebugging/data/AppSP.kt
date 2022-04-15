package cn.wankkoree.xposed.enablewebviewdebugging.data

import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object AppSP {
    val is_enabled = PrefsData("is_enabled", false)
    val is_debugging = PrefsData("is_debugging", false)
    val hooks = PrefsData("hooks", hashSetOf<String>()) // 哈希去重的 Hook 点集合，以|分隔，并且有多个对应的"hook_entry_$hash"、"hook_times_$hash"子变量
    //val "hook_entry_$hash" = PrefsData("hook_entry_$hash", ListOf<String>()) // 有序的 Hook 参数列表
    //val "hook_times_$hash" = PrefsData("hook_times_$hash", 0) // Hook 成功次数
    val vConsole = PrefsData("vConsole", false)
    val vConsole_version = PrefsData("vConsole_version", "")
    val nebulaUCSDK = PrefsData("nebulaUCSDK", false)
    val nebulaUCSDK_version = PrefsData("nebulaUCSDK_version", "")
}