package cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.github

import com.google.gson.annotations.SerializedName

data class RepoContent (
    @SerializedName("name")
    val name: String,
    @SerializedName("path")
    val path: String,
    @SerializedName("sha")
    val sha: String,
    @SerializedName("size")
    val size: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("html_url")
    val html_url: String,
    @SerializedName("git_url")
    val git_url: String,
    @SerializedName("download_url")
    val download_url: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("_links")
    val _links: Links,
) {
    data class Links (
        @SerializedName("self")
        val self: String,
        @SerializedName("git")
        val git: String,
        @SerializedName("html")
        val html: String,
    )
}
