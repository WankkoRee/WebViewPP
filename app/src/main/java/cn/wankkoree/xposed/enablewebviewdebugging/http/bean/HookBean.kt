package cn.wankkoree.xposed.enablewebviewdebugging.http.bean

data class HookBean (
    val code: Int,
    val data: List<Hook>,
) {
    class Hook (
        val hook_method: Int,
        val hook_args: String,
    )
}