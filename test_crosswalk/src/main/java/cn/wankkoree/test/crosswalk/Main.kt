package cn.wankkoree.test.crosswalk

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import org.xwalk.core.XWalkView
import org.xwalk.core.XWalkResourceClient
import android.net.http.SslError
import android.webkit.ValueCallback
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.wankkoree.test.crosswalk.databinding.MainBinding
import org.xwalk.core.XWalkPreferences

class Main : AppCompatActivity() {
    private lateinit var viewBinding: MainBinding
    private var toast: Toast? = null

    private var debugging: Boolean? = null
    private var javascript: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = MainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        refresh()
        toast?.cancel()
        toast = Toast.makeText(applicationContext, "debugging = $debugging\njavascript = $javascript", Toast.LENGTH_SHORT)
        toast?.show()
        webViewInit(viewBinding.webView, debugging = debugging, javascript = javascript)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        when (debugging) {
            null -> menu.findItem(R.id.menu_debugging_null).isChecked = true
            false -> menu.findItem(R.id.menu_debugging_false).isChecked = true
            true -> menu.findItem(R.id.menu_debugging_true).isChecked = true
        }
        when (javascript) {
            null -> menu.findItem(R.id.menu_javascript_null).isChecked = true
            false -> menu.findItem(R.id.menu_javascript_false).isChecked = true
            true -> menu.findItem(R.id.menu_javascript_true).isChecked = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var flag = true
        when (item.itemId) {
            R.id.menu_debugging_null -> getPreferences(MODE_PRIVATE).edit().putString("debugging", "null").apply()
            R.id.menu_debugging_false -> getPreferences(MODE_PRIVATE).edit().putString("debugging", "false").apply()
            R.id.menu_debugging_true -> getPreferences(MODE_PRIVATE).edit().putString("debugging", "true").apply()
            R.id.menu_javascript_null -> getPreferences(MODE_PRIVATE).edit().putString("javascript", "null").apply()
            R.id.menu_javascript_false -> getPreferences(MODE_PRIVATE).edit().putString("javascript", "false").apply()
            R.id.menu_javascript_true -> getPreferences(MODE_PRIVATE).edit().putString("javascript", "true").apply()
            else -> flag = false
        }
        if (flag) {
            refresh()
            toast?.cancel()
            toast = Toast.makeText(applicationContext, getString(R.string.settings_will_be_applied_after_restarting_the_app), Toast.LENGTH_SHORT)
            toast?.show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refresh() {
        debugging = when (getPreferences(MODE_PRIVATE).getString("debugging", "null")) {
            "null" -> null
            "false" -> false
            "true" -> true
            else -> null
        }
        javascript = when (getPreferences(MODE_PRIVATE).getString("javascript", "null")) {
            "null" -> null
            "false" -> false
            "true" -> true
            else -> null
        }
    }

    private fun webViewInit(webView: XWalkView, debugging: Boolean?, javascript: Boolean?) {
        if (debugging != null) {
            XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, debugging)
        }
        val webClient = object : XWalkResourceClient(webView){
            override fun shouldOverrideUrlLoading(view: XWalkView?, url: String?): Boolean {
                return false
            }
            override fun onReceivedSslError(
                view: XWalkView?,
                callback: ValueCallback<Boolean>?,
                error: SslError?
            ) {
                callback?.onReceiveValue(true)
            }
        }
        webView.setResourceClient(webClient)

        val webSettings = webView.settings
        if (javascript != null) {
            XWalkPreferences.setValue(XWalkPreferences.ENABLE_JAVASCRIPT, javascript)
            webSettings.javaScriptEnabled = javascript  // 开启或关闭 JavaScript 交互
        }

        // 禁用缩放
        webSettings.setSupportZoom(false) // 支持缩放 默认为true 是下面那个的前提
        webSettings.builtInZoomControls = false // 设置内置的缩放控件 若为false 则该WebView不可缩放

        webSettings.javaScriptCanOpenWindowsAutomatically = true // 支持通过JS打开新窗口

        // 设置自适应屏幕, 两者合用
        webSettings.useWideViewPort = true  // 将图片调整到适合WebView的大小
        webSettings.loadWithOverviewMode = true  // 缩放至屏幕的大小
        webSettings.allowFileAccess = true // 设置可以访问文件

        webView.setLayerType(View.LAYER_TYPE_HARDWARE,null)

        webView.loadUrl("https://liulanmi.com/core")
    }
}