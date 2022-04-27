package cn.wankkoree.xposed.enablewebviewdebugging.http.bean

import com.google.gson.annotations.SerializedName

data class HookRules (
    @SerializedName("hookWebView")
    val hookWebView: List<HookRuleWebView>?,
    @SerializedName("hookWebViewClient")
    val hookWebViewClient: List<HookRuleWebViewClient>?,
    @SerializedName("replaceNebulaUCSDK")
    val replaceNebulaUCSDK: List<ReplaceNebulaUCSDK>?,
) {
    class HookRuleWebView (
        @SerializedName("name")
        val name: String,
        @SerializedName("Class_WebView")
        val Class_WebView: String,
        @SerializedName("Method_getSettings")
        val Method_getSettings: String,
        @SerializedName("Method_setWebContentsDebuggingEnabled")
        val Method_setWebContentsDebuggingEnabled: String,
        @SerializedName("Method_setJavaScriptEnabled")
        val Method_setJavaScriptEnabled: String,
        @SerializedName("Method_loadUrl")
        val Method_loadUrl: String,
        @SerializedName("Method_setWebViewClient")
        val Method_setWebViewClient: String,
    )
    class HookRuleWebViewClient (
        @SerializedName("name")
        val name: String,
        @SerializedName("Class_WebView")
        val Class_WebView: String,
        @SerializedName("Class_WebViewClient")
        val Class_WebViewClient: String,
        @SerializedName("Method_onPageFinished")
        val Method_onPageFinished: String,
        @SerializedName("Method_evaluateJavascript")
        val Method_evaluateJavascript: String,
        @SerializedName("Class_ValueCallback")
        val Class_ValueCallback: String,
    )
    class ReplaceNebulaUCSDK (
        @SerializedName("name")
        val name: String,
        @SerializedName("Class_UcServiceSetup")
        val Class_UcServiceSetup: String,
        @SerializedName("Method_updateUCVersionAndSdcardPath")
        val Method_updateUCVersionAndSdcardPath: String,
        @SerializedName("Field_sInitUcFromSdcardPath")
        val Field_sInitUcFromSdcardPath: String,
    )
}