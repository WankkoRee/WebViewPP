package cn.wankkoree.xposed.enablewebviewdebugging.data

import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object ResourcesSP {
    val vConsole_versions = PrefsData("vConsole_versions", hashSetOf<String>()) // 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_vConsole_$version"子SP用于单独存储资源，防止文件过大拖慢性能
    //val vConsole = PrefsData("vConsole", "") in "resources_vConsole_$version"
    val eruda_versions = PrefsData("eruda_versions", hashSetOf<String>()) // 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_eruda_$version"子SP用于单独存储资源，防止文件过大拖慢性能
    //val eruda = PrefsData("eruda", "") in "resources_eruda_$version"
    val nebulaUCSDK_versions = PrefsData("nebulaUCSDK_versions", hashSetOf<String>()) // 哈希去重的版本集合，以|分隔，并且有多个对应的"resources_nebulaUCSDK_$version"子SP用于单独存储资源，防止文件过大拖慢性能
    //val nebulaUCSDK_arm64v8a = PrefsData("nebulaUCSDK_arm64-v8a", "") in "resources_nebulaUCSDK_$version"
    //val nebulaUCSDK_armeabiv7a = PrefsData("nebulaUCSDK_armeabi-v7a", "") in "resources_nebulaUCSDK_$version"
}