package cn.wankkoree.xp.webviewpp.data

import cn.wankkoree.xp.webviewpp.ValueAlreadyExistedInSet
import cn.wankkoree.xp.webviewpp.ValueNotExistedInSet
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

inline fun <reified T> YukiHookPrefsBridge.getSet(prefs : PrefsData<HashSet<T>>) : HashSet<T> = when (T::class) {
    String::class -> getString(prefs.key).split('|').toMutableList().also { it.remove("") }.map{it as T}.toHashSet()
    Int::class -> getString(prefs.key).split('|').toMutableList().also { it.remove("") }.map{it.toInt() as T}.toHashSet()
    else -> error("Key-Value type ${T::class.java.name} is not allowed")
}

inline fun <reified T> YukiHookPrefsBridge.getSet(key : String) : HashSet<T> = when (T::class) {
    String::class -> getString(key).split('|').toMutableList().also { it.remove("") }.map{it as T}.toHashSet()
    Int::class -> getString(key).split('|').toMutableList().also { it.remove("") }.map{it.toInt() as T}.toHashSet()
    else -> error("Key-Value type ${T::class.java.name} is not allowed")
}

inline fun <reified T> YukiHookPrefsBridge.getList(key : String) : List<T> = when (T::class) {
    String::class -> getString(key).split('|').toMutableList().also { it.remove("") }.map{it as T}.toList()
    Int::class -> getString(key).split('|').toMutableList().also { it.remove("") }.map{it.toInt() as T}.toList()
    else -> error("Key-Value type ${T::class.java.name} is not allowed")
}

inline fun <reified T> YukiHookPrefsBridge.put(prefs : PrefsData<HashSet<T>>, value : T) {
    val oldSet = getSet(prefs)
    if (oldSet.contains(value)) throw ValueAlreadyExistedInSet("$value is already in ${prefs.key}")
    oldSet.add(value)
    edit { putString(prefs.key, oldSet.joinToString("|")) }
}

inline fun <reified T> YukiHookPrefsBridge.putSet(key : String, value: HashSet<T>) {
    edit { putString(key, value.joinToString("|")) }
}

inline fun <reified T> YukiHookPrefsBridge.putList(key: String, value: List<T>) {
    edit { putString(key, value.joinToString("|")) }
}

inline fun <reified T> YukiHookPrefsBridge.remove(prefs: PrefsData<HashSet<T>>, value: T) {
    val oldSet = getSet(prefs)
    if (!oldSet.contains(value)) throw ValueNotExistedInSet("$value is not in ${prefs.key}")
    oldSet.remove(value)
    edit { putString(prefs.key, oldSet.joinToString("|")) }
}
