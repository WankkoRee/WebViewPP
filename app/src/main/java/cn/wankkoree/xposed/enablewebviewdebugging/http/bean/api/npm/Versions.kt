package cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm

data class Versions (
    val tags: VConsoleVersionTag,
    val versions: List<String>,
) {
    data class VConsoleVersionTag (
        val latest: String,
    )
}