package cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.github

import com.google.gson.annotations.SerializedName
import java.util.Date

data class RepoRelease (
    @SerializedName("url")
    val url: String,
    @SerializedName("assets_url")
    val assets_url: String,
    @SerializedName("upload_url")
    val upload_url: String,
    @SerializedName("html_url")
    val html_url: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("author")
    val author: User,
    @SerializedName("node_id")
    val node_id: String,
    @SerializedName("tag_name")
    val tag_name: String,
    @SerializedName("target_commitish")
    val target_commitish: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("draft")
    val draft: Boolean,
    @SerializedName("prerelease")
    val prerelease: Boolean,
    @SerializedName("created_at")
    val created_at: Date,
    @SerializedName("published_at")
    val published_at: Date,
    @SerializedName("assets")
    val assets: List<Asset>,
    @SerializedName("tarball_url")
    val tarball_url: String,
    @SerializedName("zipball_url")
    val zipball_url: String,
    @SerializedName("body")
    val body: String,
) {
    data class User (
        @SerializedName("login")
        val login: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("node_id")
        val node_id: String,
        @SerializedName("avatar_url")
        val avatar_url: String,
        @SerializedName("gravatar_id")
        val gravatar_id: String,
        @SerializedName("url")
        val url: String,
        @SerializedName("html_url")
        val html_url: String,
        @SerializedName("followers_url")
        val followers_url: String,
        @SerializedName("following_url")
        val following_url: String,
        @SerializedName("gists_url")
        val gists_url: String,
        @SerializedName("starred_url")
        val starred_url: String,
        @SerializedName("subscriptions_url")
        val subscriptions_url: String,
        @SerializedName("organizations_url")
        val organizations_url: String,
        @SerializedName("repos_url")
        val repos_url: String,
        @SerializedName("events_url")
        val events_url: String,
        @SerializedName("received_events_url")
        val received_events_url: String,
        @SerializedName("type")
        val type: String,
        @SerializedName("site_admin")
        val site_admin: Boolean,
    )
    data class Asset (
        @SerializedName("url")
        val url: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("node_id")
        val node_id: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("label")
        val label: String,
        @SerializedName("uploader")
        val uploader: User,
        @SerializedName("content_type")
        val content_type: String,
        @SerializedName("state")
        val state: String,
        @SerializedName("size")
        val size: Int,
        @SerializedName("download_count")
        val download_count: Int,
        @SerializedName("created_at")
        val created_at: Date,
        @SerializedName("updated_at")
        val updated_at: Date,
        @SerializedName("browser_download_url")
        val browser_download_url: String,
    )
}
