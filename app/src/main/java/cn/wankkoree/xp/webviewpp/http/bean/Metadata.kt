package cn.wankkoree.xp.webviewpp.http.bean

import com.google.gson.annotations.SerializedName
data class Metadata (
    @SerializedName("name")
    val name : String,
    @SerializedName("remark")
    val remark : String,
    @SerializedName("versions")
    val versions: List<String>,
    @SerializedName("latest")
    val latest: String,
)