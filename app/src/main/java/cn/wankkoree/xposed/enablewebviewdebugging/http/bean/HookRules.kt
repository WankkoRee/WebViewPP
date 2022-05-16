package cn.wankkoree.xposed.enablewebviewdebugging.http.bean

import com.google.gson.annotations.SerializedName
// TODO: 添加更多 hook 方法
data class HookRules (
    @SerializedName("hookWebView")
    val hookWebView: List<HookRuleWebView>?,
    @SerializedName("hookWebViewClient")
    val hookWebViewClient: List<HookRuleWebViewClient>?,
    @SerializedName("replaceNebulaUCSDK")
    val replaceNebulaUCSDK: List<ReplaceNebulaUCSDK>?,
    @SerializedName("hookCrossWalk")
    val hookCrossWalk: List<HookCrossWalk>?,
    @SerializedName("hookXWebPreferences")
    val hookXWebPreferences: List<HookXWebPreferences>?,
    @SerializedName("hookXWebView")
    val hookXWebView: List<HookXWebView>?,
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
    class HookCrossWalk (
        @SerializedName("name")
        val name: String,
        @SerializedName("Class_XWalkView")
        val Class_XWalkView: String,
        @SerializedName("Method_getSettings")
        val Method_getSettings: String,
        @SerializedName("Method_setJavaScriptEnabled")
        val Method_setJavaScriptEnabled: String,
        @SerializedName("Method_loadUrl")
        val Method_loadUrl: String,
        @SerializedName("Method_setResourceClient")
        val Method_setResourceClient: String,
        @SerializedName("Class_XWalkPreferences")
        val Class_XWalkPreferences: String,
        @SerializedName("Method_setValue")
        val Method_setValue: String,
    )
    class HookXWebPreferences (
        @SerializedName("name")
        val name: String,
        @SerializedName("Class_XWebPreferences")
        val Class_XWebPreferences: String,
        @SerializedName("Method_setValue")
        val Method_setValue: String,
    )
    class HookXWebView (
        @SerializedName("name")
        val name: String,
        @SerializedName("Class_XWebView")
        val Class_XWebView: String,
        @SerializedName("Method_initWebviewCoreInternal")
        val Method_initWebviewCoreInternal: String,
        @SerializedName("Method_isXWalk")
        val Method_isXWalk: String,
        @SerializedName("Method_isPinus")
        val Method_isPinus: String,
        @SerializedName("Method_isX5")
        val Method_isX5: String,
        @SerializedName("Method_isSys")
        val Method_isSys: String,
    )
}