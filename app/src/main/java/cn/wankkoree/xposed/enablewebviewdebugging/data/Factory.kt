package cn.wankkoree.xposed.enablewebviewdebugging.data

import cn.wankkoree.xposed.enablewebviewdebugging.ValueAlreadyExistedInSet
import cn.wankkoree.xposed.enablewebviewdebugging.ValueNotExistedInSet
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookModulePrefs
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

inline fun <reified T> YukiHookModulePrefs.getSet(prefs: PrefsData<HashSet<T>>): HashSet<T> = when (T::class) {
    String::class -> getString(prefs.key).split('|').toMutableList().also { it.remove("") }.map{it as T}.toHashSet()
    Int::class -> getString(prefs.key).split('|').toMutableList().also { it.remove("") }.map{it.toInt() as T}.toHashSet()
    else -> error("Key-Value type ${T::class.java.name} is not allowed")
}

inline fun <reified T> YukiHookModulePrefs.getSet(key: String): HashSet<T> = when (T::class) {
    String::class -> getString(key).split('|').toMutableList().also { it.remove("") }.map{it as T}.toHashSet()
    Int::class -> getString(key).split('|').toMutableList().also { it.remove("") }.map{it.toInt() as T}.toHashSet()
    else -> error("Key-Value type ${T::class.java.name} is not allowed")
}

inline fun <reified T> YukiHookModulePrefs.getList(key: String): List<T> = when (T::class) {
    String::class -> getString(key).split('|').toMutableList().also { it.remove("") }.map{it as T}.toList()
    Int::class -> getString(key).split('|').toMutableList().also { it.remove("") }.map{it.toInt() as T}.toList()
    else -> error("Key-Value type ${T::class.java.name} is not allowed")
}

inline fun <reified T> YukiHookModulePrefs.put(prefs: PrefsData<HashSet<T>>, value: T) {
    val oldSet = getSet(prefs)
    if (oldSet.contains(value)) throw ValueAlreadyExistedInSet("$value is already in ${prefs.key}")
    oldSet.add(value)
    putString(prefs.key, oldSet.joinToString("|"))
}

inline fun <reified T> YukiHookModulePrefs.putSet(key: String, value: HashSet<T>) {
    putString(key, value.joinToString("|"))
}

inline fun <reified T> YukiHookModulePrefs.putList(key: String, value: List<T>) {
    putString(key, value.joinToString("|"))
}

inline fun <reified T> YukiHookModulePrefs.remove(prefs: PrefsData<HashSet<T>>, value: T) {
    val oldSet = getSet(prefs)
    if (!oldSet.contains(value)) throw ValueNotExistedInSet("$value is not in ${prefs.key}")
    oldSet.remove(value)
    putString(prefs.key, oldSet.joinToString("|"))
}
