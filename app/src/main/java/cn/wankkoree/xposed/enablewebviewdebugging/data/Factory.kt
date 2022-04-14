package cn.wankkoree.xposed.enablewebviewdebugging.data

import cn.wankkoree.xposed.enablewebviewdebugging.ResourcesVersionAlreadyExisted
import cn.wankkoree.xposed.enablewebviewdebugging.ResourcesVersionNotExisted
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookModulePrefs
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

inline fun <reified T> YukiHookModulePrefs.getSet(prefs: PrefsData<HashSet<T>>): HashSet<T> = when (T::class) {
    String::class -> getString(prefs.key).split('|').toMutableList().also { it.remove("") }.map{it as T}.toHashSet()
    Int::class -> getString(prefs.key).split('|').toMutableList().also { it.remove("") }.map{it.toInt() as T}.toHashSet()
    else -> error("Key-Value type ${T::class.java.name} is not allowed")
}

inline fun <reified T> YukiHookModulePrefs.put(prefs: PrefsData<HashSet<T>>, value: T) {
    val oldSet = getSet(prefs)
    if (oldSet.contains(value)) throw ResourcesVersionAlreadyExisted("$value is already in ${prefs.key}")
    oldSet.add(value)
    putString(prefs.key, oldSet.joinToString("|"))
}

inline fun <reified T> YukiHookModulePrefs.remove(prefs: PrefsData<HashSet<T>>, value: T) {
    val oldSet = getSet(prefs)
    if (!oldSet.contains(value)) throw ResourcesVersionNotExisted("$value is not in ${prefs.key}")
    oldSet.remove(value)
    putString(prefs.key, oldSet.joinToString("|"))
}
