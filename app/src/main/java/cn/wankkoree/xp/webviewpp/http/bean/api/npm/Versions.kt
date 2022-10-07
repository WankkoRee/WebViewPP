package cn.wankkoree.xp.webviewpp.http.bean.api.npm

import com.google.gson.annotations.SerializedName

data class Versions (
    @SerializedName("tags")
    val tags: VConsoleVersionTag,
    @SerializedName("versions")
    val versions: List<String>,
) {
    data class VConsoleVersionTag (
        @SerializedName("latest")
        val latest: String,
    )
}