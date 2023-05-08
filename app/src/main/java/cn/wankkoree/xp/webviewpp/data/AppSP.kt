package cn.wankkoree.xp.webviewpp.data

import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object AppSP {
    val is_enabled = PrefsData("is_enabled", false)
    val debug_mode = PrefsData("debug_mode", false)
    val app_is_protected = PrefsData("app_is_protected", false)
    /**
     * 哈希去重的 Hook 规则名称集合，以|分隔，并且有多个对应的"hook_entry_$name"子变量
     */
    val hooks = PrefsData("hooks", hashSetOf<String>())
    //val "hook_entry_$name" = PrefsData("hook_entry_$name", "{}") // Hook 规则的 Json
    val vConsole = PrefsData("vConsole", false)
    val vConsole_version = PrefsData("vConsole_version", "")
    val vConsole_plugin_sources = PrefsData("vConsole_plugin_sources", false)
    val vConsole_plugin_sources_version = PrefsData("vConsole_plugin_sources_version", "")
    val vConsole_plugin_stats = PrefsData("vConsole_plugin_stats", false)
    val vConsole_plugin_stats_version = PrefsData("vConsole_plugin_stats_version", "")
    val vConsole_plugin_vue_devtools = PrefsData("vConsole_plugin_vue_devtools", false)
    val vConsole_plugin_vue_devtools_version = PrefsData("vConsole_plugin_vue_devtools_version", "")
    val vConsole_plugin_outputlog = PrefsData("vConsole_plugin_outputlog", false)
    val vConsole_plugin_outputlog_version = PrefsData("vConsole_plugin_outputlog_version", "")
    val eruda = PrefsData("eruda", false)
    val eruda_version = PrefsData("eruda_version", "")
    val eruda_plugin_fps = PrefsData("eruda_plugin_fps", false)
    val eruda_plugin_fps_version = PrefsData("eruda_plugin_fps_version", "")
    val eruda_plugin_features = PrefsData("eruda_plugin_features", false)
    val eruda_plugin_features_version = PrefsData("eruda_plugin_features_version", "")
    val eruda_plugin_timing = PrefsData("eruda_plugin_timing", false)
    val eruda_plugin_timing_version = PrefsData("eruda_plugin_timing_version", "")
    val eruda_plugin_memory = PrefsData("eruda_plugin_memory", false)
    val eruda_plugin_memory_version = PrefsData("eruda_plugin_memory_version", "")
    val eruda_plugin_code = PrefsData("eruda_plugin_code", false)
    val eruda_plugin_code_version = PrefsData("eruda_plugin_code_version", "")
    val eruda_plugin_benchmark = PrefsData("eruda_plugin_benchmark", false)
    val eruda_plugin_benchmark_version = PrefsData("eruda_plugin_benchmark_version", "")
    val eruda_plugin_geolocation = PrefsData("eruda_plugin_geolocation", false)
    val eruda_plugin_geolocation_version = PrefsData("eruda_plugin_geolocation_version", "")
    val eruda_plugin_dom = PrefsData("eruda_plugin_dom", false)
    val eruda_plugin_dom_version = PrefsData("eruda_plugin_dom_version", "")
    val eruda_plugin_orientation = PrefsData("eruda_plugin_orientation", false)
    val eruda_plugin_orientation_version = PrefsData("eruda_plugin_orientation_version", "")
    val eruda_plugin_touches = PrefsData("eruda_plugin_touches", false)
    val eruda_plugin_touches_version = PrefsData("eruda_plugin_touches_version", "")
    val nebulaUCSDK = PrefsData("nebulaUCSDK", false)
    val nebulaUCSDK_version = PrefsData("nebulaUCSDK_version", "")
}