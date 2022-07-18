package cn.wankkoree.xposed.enablewebviewdebugging.data

import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object ResourcesSP {
    val vConsole_latest = PrefsData("vConsole_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_vConsole_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val vConsole_versions = PrefsData("vConsole_versions", hashSetOf<String>())
    //val vConsole = PrefsData("vConsole", "") in "resources_vConsole_$version"

    val vConsole_plugin_sources_latest = PrefsData("vConsole_plugin_sources_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_vConsole_plugin_sources_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val vConsole_plugin_sources_versions = PrefsData("vConsole_plugin_sources_versions", hashSetOf<String>())
    //val vConsole_plugin_sources = PrefsData("vConsole_plugin_sources", "") in "resources_vConsole_plugin_sources_$version"

    val vConsole_plugin_stats_latest = PrefsData("vConsole_plugin_stats_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_vConsole_plugin_stats_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val vConsole_plugin_stats_versions = PrefsData("vConsole_plugin_stats_versions", hashSetOf<String>())
    //val vConsole_plugin_stats = PrefsData("vConsole_plugin_stats", "") in "resources_vConsole_plugin_stats_$version"

    val vConsole_plugin_vue_devtools_latest = PrefsData("vConsole_plugin_vue_devtools_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_vConsole_plugin_vue_devtools_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val vConsole_plugin_vue_devtools_versions = PrefsData("vConsole_plugin_vue_devtools_versions", hashSetOf<String>())
    //val vConsole_plugin_vue_devtools = PrefsData("vConsole_plugin_vue_devtools", "") in "resources_vConsole_plugin_vue_devtools_$version"

    val vConsole_plugin_outputlog_latest = PrefsData("vConsole_plugin_vue_devtools_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_vConsole_plugin_outputlog_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val vConsole_plugin_outputlog_versions = PrefsData("vConsole_plugin_outputlog_versions", hashSetOf<String>())
    //val vConsole_plugin_outputlog = PrefsData("vConsole_plugin_outputlog", "") in "resources_vConsole_plugin_outputlog_$version"

    val eruda_latest = PrefsData("eruda_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_eruda_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val eruda_versions = PrefsData("eruda_versions", hashSetOf<String>())
    //val eruda = PrefsData("eruda", "") in "resources_eruda_$version"

    val eruda_plugin_fps_latest = PrefsData("eruda_plugin_fps_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_eruda_plugin_fps_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val eruda_plugin_fps_versions = PrefsData("eruda_plugin_fps_versions", hashSetOf<String>())
    //val eruda_plugin_fps = PrefsData("eruda_plugin_fps", "") in "resources_eruda_plugin_fps_$version"

    val eruda_plugin_features_latest = PrefsData("eruda_plugin_features_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_eruda_plugin_features_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val eruda_plugin_features_versions = PrefsData("eruda_plugin_features_versions", hashSetOf<String>())
    //val eruda_plugin_features = PrefsData("eruda_plugin_features", "") in "resources_eruda_plugin_features_$version"

    val eruda_plugin_timing_latest = PrefsData("eruda_plugin_timing_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_eruda_plugin_timing_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val eruda_plugin_timing_versions = PrefsData("eruda_plugin_timing_versions", hashSetOf<String>())
    //val eruda_plugin_timing = PrefsData("eruda_plugin_timing", "") in "resources_eruda_plugin_timing_$version"

    val eruda_plugin_memory_latest = PrefsData("eruda_plugin_memory_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_eruda_plugin_memory_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val eruda_plugin_memory_versions = PrefsData("eruda_plugin_memory_versions", hashSetOf<String>())
    //val eruda_plugin_memory = PrefsData("eruda_plugin_memory", "") in "resources_eruda_plugin_memory_$version"

    val eruda_plugin_code_latest = PrefsData("eruda_plugin_code_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_eruda_plugin_code_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val eruda_plugin_code_versions = PrefsData("eruda_plugin_code_versions", hashSetOf<String>())
    //val eruda_plugin_code = PrefsData("eruda_plugin_code", "") in "resources_eruda_plugin_code_$version"

    val eruda_plugin_benchmark_latest = PrefsData("eruda_plugin_benchmark_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_eruda_plugin_benchmark_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val eruda_plugin_benchmark_versions = PrefsData("eruda_plugin_benchmark_versions", hashSetOf<String>())
    //val eruda_plugin_benchmark = PrefsData("eruda_plugin_benchmark", "") in "resources_eruda_plugin_benchmark_$version"

    val eruda_plugin_geolocation_latest = PrefsData("eruda_plugin_geolocation_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_eruda_plugin_geolocation_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val eruda_plugin_geolocation_versions = PrefsData("eruda_plugin_geolocation_versions", hashSetOf<String>())
    //val eruda_plugin_geolocation = PrefsData("eruda_plugin_geolocation", "") in "resources_eruda_plugin_geolocation_$version"

    val eruda_plugin_dom_latest = PrefsData("eruda_plugin_dom_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_eruda_plugin_dom_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val eruda_plugin_dom_versions = PrefsData("eruda_plugin_dom_versions", hashSetOf<String>())
    //val eruda_plugin_dom = PrefsData("eruda_plugin_dom", "") in "resources_eruda_plugin_dom_$version"

    val eruda_plugin_orientation_latest = PrefsData("eruda_plugin_orientation_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_eruda_plugin_orientation_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val eruda_plugin_orientation_versions = PrefsData("eruda_plugin_orientation_versions", hashSetOf<String>())
    //val eruda_plugin_orientation = PrefsData("eruda_plugin_orientation", "") in "resources_eruda_plugin_orientation_$version"

    val eruda_plugin_touches_latest = PrefsData("eruda_plugin_touches_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_eruda_plugin_touches_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val eruda_plugin_touches_versions = PrefsData("eruda_plugin_touches_versions", hashSetOf<String>())
    //val eruda_plugin_touches = PrefsData("eruda_plugin_touches", "") in "resources_eruda_plugin_touches_$version"

    val nebulaUCSDK_latest = PrefsData("nebulaUCSDK_latest", "")
    /**
     * 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_nebulaUCSDK_$version"子SP用于单独存储资源，防止文件过大拖慢性能
     */
    val nebulaUCSDK_versions = PrefsData("nebulaUCSDK_versions", hashSetOf<String>())
    //val nebulaUCSDK_arm64v8a = PrefsData("nebulaUCSDK_arm64-v8a", "") in "resources_nebulaUCSDK_$version"
    //val nebulaUCSDK_armeabiv7a = PrefsData("nebulaUCSDK_armeabi-v7a", "") in "resources_nebulaUCSDK_$version"
}