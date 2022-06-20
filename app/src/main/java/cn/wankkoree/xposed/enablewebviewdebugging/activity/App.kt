package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityOptionsCompat
import cn.wankkoree.xposed.enablewebviewdebugging.BuildConfig
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.ValueAlreadyExistedInSet
import cn.wankkoree.xposed.enablewebviewdebugging.ValueNotExistedInSet
import cn.wankkoree.xposed.enablewebviewdebugging.activity.component.Code
import cn.wankkoree.xposed.enablewebviewdebugging.data.*
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.AppBinding
import cn.wankkoree.xposed.enablewebviewdebugging.http.bean.HookRules
import com.google.gson.Gson
import com.highcapable.yukihookapi.hook.factory.modulePrefs

class App : AppCompatActivity() {
    private lateinit var viewBinding: AppBinding
    private var toast: Toast? = null
    private val ruleResultContract = registerForActivityResult(RuleResultContract()) {
        refresh()
    }
    private val resourcesResultContract = registerForActivityResult(ResourcesResultContract()) {
        refresh()
    }

    private lateinit var icon: Drawable
    private lateinit var name: String
    private lateinit var versionName: String
    private var versionCode: Int = 0
    private lateinit var pkg: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.enterTransition = Slide()
        window.exitTransition = Slide()
        viewBinding = AppBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        pkg = intent.getStringExtra(Intent.EXTRA_PACKAGE_NAME)!!
        val app = packageManager.getPackageInfo(pkg, PackageManager.GET_META_DATA)
        icon = app.applicationInfo.loadIcon(packageManager)
        name = app.applicationInfo.loadLabel(packageManager) as String
        versionName = app.versionName ?: ""
        versionCode = app.versionCode

        viewBinding.appToolbarName.text = name
        viewBinding.appIcon.setImageDrawable(icon)
        viewBinding.appIcon.contentDescription = name
        viewBinding.appText.text = name
        viewBinding.appPackage.text = pkg
        viewBinding.appVersion.text = getString(R.string.version_format).format(versionName, versionCode)
        refresh()

        viewBinding.appToolbarBack.setOnClickListener {
            finishAfterTransition()
        }
        viewBinding.appToolbarPreset.setOnClickListener { v ->
            PopupMenu(this, v).apply {
                menuInflater.inflate(R.menu.app_toolbar_preset, menu)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        // TODO: 添加更多 hook 方法
                        R.id.app_toolbar_preset_webview -> {
                            with(modulePrefs("apps_$pkg")) {
                                (getString(R.string.standard_s, getString(R.string.webview_rules)) + " " + "hookWebView").also { ruleName ->
                                    try {
                                        put(AppSP.hooks, ruleName)
                                        putString("hook_entry_$ruleName", Gson().toJson(HookRules.HookRuleWebView(
                                            "hookWebView",
                                            "",
                                            "android.webkit.WebView",
                                            "getSettings",
                                            "setWebContentsDebuggingEnabled",
                                            "setJavaScriptEnabled",
                                            "loadUrl",
                                            "setWebViewClient",
                                        )))
                                    } catch (_: ValueAlreadyExistedInSet) {
                                        toast?.cancel()
                                        toast = Toast.makeText(this@App, getString(R.string.s_already_exists).format(getString(R.string.rule_name) + """ "$ruleName" """), Toast.LENGTH_SHORT)
                                        toast!!.show()
                                    }
                                }
                                (getString(R.string.standard_s, getString(R.string.webview_rules)) + " " + "hookWebViewClient").also { ruleName ->
                                    try {
                                        put(AppSP.hooks, ruleName)
                                        putString("hook_entry_$ruleName", Gson().toJson(HookRules.HookRuleWebViewClient(
                                            "hookWebViewClient",
                                            "",
                                            "android.webkit.WebView",
                                            "android.webkit.WebViewClient",
                                            "onPageFinished",
                                            "evaluateJavascript",
                                            "android.webkit.ValueCallback",
                                        )))
                                    } catch (_: ValueAlreadyExistedInSet) {
                                        toast?.cancel()
                                        toast = Toast.makeText(this@App, getString(R.string.s_already_exists).format(getString(R.string.rule_name) + """ "$ruleName" """), Toast.LENGTH_SHORT)
                                        toast!!.show()
                                    }
                                }
                            }
                            refresh()
                        }
                        R.id.app_toolbar_preset_tbsx5 -> {
                            with(modulePrefs("apps_$pkg")) {
                                (getString(R.string.standard_s, getString(R.string.tbsx5_rules)) + " " + "hookWebView").also { ruleName ->
                                    try {
                                        put(AppSP.hooks, ruleName)
                                        putString("hook_entry_$ruleName", Gson().toJson(HookRules.HookRuleWebView(
                                            "hookWebView",
                                            "",
                                            "com.tencent.smtt.sdk.WebView",
                                            "getSettings",
                                            "setWebContentsDebuggingEnabled",
                                            "setJavaScriptEnabled",
                                            "loadUrl",
                                            "setWebViewClient",
                                        )))
                                    } catch (_: ValueAlreadyExistedInSet) {
                                        toast?.cancel()
                                        toast = Toast.makeText(this@App, getString(R.string.s_already_exists).format(getString(R.string.rule_name) + """ "$ruleName" """), Toast.LENGTH_SHORT)
                                        toast!!.show()
                                    }
                                }
                                (getString(R.string.standard_s, getString(R.string.tbsx5_rules)) + " " + "hookWebViewClient").also { ruleName ->
                                    try {
                                        put(AppSP.hooks, ruleName)
                                        putString("hook_entry_$ruleName", Gson().toJson(HookRules.HookRuleWebViewClient(
                                            "hookWebViewClient",
                                            "",
                                            "com.tencent.smtt.sdk.WebView",
                                            "com.tencent.smtt.sdk.WebViewClient",
                                            "onPageFinished",
                                            "evaluateJavascript",
                                            "com.tencent.smtt.sdk.ValueCallback",
                                        )))
                                    } catch (_: ValueAlreadyExistedInSet) {
                                        toast?.cancel()
                                        toast = Toast.makeText(this@App, getString(R.string.s_already_exists).format(getString(R.string.rule_name) + """ "$ruleName" """), Toast.LENGTH_SHORT)
                                        toast!!.show()
                                    }
                                }
                            }
                            refresh()
                        }
                        R.id.app_toolbar_preset_ucu4 -> {
                            with(modulePrefs("apps_$pkg")) {
                                (getString(R.string.standard_s, getString(R.string.ucu4_rules)) + " " + "hookWebView").also { ruleName ->
                                    try {
                                        put(AppSP.hooks, ruleName)
                                        putString("hook_entry_$ruleName", Gson().toJson(HookRules.HookRuleWebView(
                                            "hookWebView",
                                            "",
                                            "com.uc.webview.export.WebView",
                                            "getSettings",
                                            "setWebContentsDebuggingEnabled",
                                            "setJavaScriptEnabled",
                                            "loadUrl",
                                            "setWebViewClient",
                                        )))
                                    } catch (_: ValueAlreadyExistedInSet) {
                                        toast?.cancel()
                                        toast = Toast.makeText(this@App, getString(R.string.s_already_exists).format(getString(R.string.rule_name) + """ "$ruleName" """), Toast.LENGTH_SHORT)
                                        toast!!.show()
                                    }
                                }
                                (getString(R.string.standard_s, getString(R.string.ucu4_rules)) + " " + "hookWebViewClient").also { ruleName ->
                                    try {
                                        put(AppSP.hooks, ruleName)
                                        putString("hook_entry_$ruleName", Gson().toJson(HookRules.HookRuleWebViewClient(
                                            "hookWebViewClient",
                                            "",
                                            "com.uc.webview.export.WebView",
                                            "com.alipay.mobile.nebulauc.impl.UCWebViewClient",
                                            "onPageFinished",
                                            "evaluateJavascript",
                                            "android.webkit.ValueCallback",
                                        )))
                                    } catch (_: ValueAlreadyExistedInSet) {
                                        toast?.cancel()
                                        toast = Toast.makeText(this@App, getString(R.string.s_already_exists).format(getString(R.string.rule_name) + """ "$ruleName" """), Toast.LENGTH_SHORT)
                                        toast!!.show()
                                    }
                                }
                                (getString(R.string.standard_s, getString(R.string.ucu4_rules)) + " " + "replaceNebulaUCSDK").also { ruleName ->
                                    try {
                                        put(AppSP.hooks, ruleName)
                                        putString("hook_entry_$ruleName", Gson().toJson(HookRules.ReplaceNebulaUCSDK(
                                            "replaceNebulaUCSDK",
                                            "",
                                            "com.alipay.mobile.nebulauc.impl.UcServiceSetup",
                                            "updateUCVersionAndSdcardPath",
                                            "sInitUcFromSdcardPath",
                                        )))
                                    } catch (_: ValueAlreadyExistedInSet) {
                                        toast?.cancel()
                                        toast = Toast.makeText(this@App, getString(R.string.s_already_exists).format(getString(R.string.rule_name) + """ "$ruleName" """), Toast.LENGTH_SHORT)
                                        toast!!.show()
                                    }
                                }
                            }
                            refresh()
                        }
                        R.id.app_toolbar_preset_crosswalk -> {
                            with(modulePrefs("apps_$pkg")) {
                                (getString(R.string.standard_s, getString(R.string.crosswalk_rules)) + " " + "hookCrossWalk").also { ruleName ->
                                    try {
                                        put(AppSP.hooks, ruleName)
                                        putString("hook_entry_$ruleName", Gson().toJson(HookRules.HookCrossWalk(
                                            "hookCrossWalk",
                                            "",
                                            "org.xwalk.core.XWalkView",
                                            "getSettings",
                                            "setJavaScriptEnabled",
                                            "loadUrl",
                                            "setResourceClient",
                                            "org.xwalk.core.XWalkPreferences",
                                            "setValue",
                                        )))
                                    } catch (_: ValueAlreadyExistedInSet) {
                                        toast?.cancel()
                                        toast = Toast.makeText(this@App, getString(R.string.s_already_exists).format(getString(R.string.rule_name) + """ "$ruleName" """), Toast.LENGTH_SHORT)
                                        toast!!.show()
                                    }
                                }
                                (getString(R.string.standard_s, getString(R.string.crosswalk_rules)) + " " + "hookWebViewClient").also { ruleName ->
                                    try {
                                        put(AppSP.hooks, ruleName)
                                        putString("hook_entry_$ruleName", Gson().toJson(HookRules.HookRuleWebViewClient(
                                            "hookWebViewClient",
                                            "",
                                            "org.xwalk.core.XWalkView",
                                            "org.xwalk.core.XWalkResourceClient",
                                            "onLoadFinished",
                                            "evaluateJavascript",
                                            "android.webkit.ValueCallback",
                                        )))
                                    } catch (_: ValueAlreadyExistedInSet) {
                                        toast?.cancel()
                                        toast = Toast.makeText(this@App, getString(R.string.s_already_exists).format(getString(R.string.rule_name) + """ "$ruleName" """), Toast.LENGTH_SHORT)
                                        toast!!.show()
                                    }
                                }
                            }
                            refresh()
                        }
                        R.id.app_toolbar_preset_xweb -> {
                            toast?.cancel()
                            toast = Toast.makeText(this@App, getString(R.string.xweb_currently_has_no_standard_implementation_so_no_standard_rules_is_provided_please_use_the_cloud_rules), Toast.LENGTH_SHORT)
                            toast!!.show()
                        }
                    }
                    true
                }
            }.show()
        }
        viewBinding.appToolbarMenu.setOnClickListener {
            PopupMenu(this, it).apply {
                menuInflater.inflate(R.menu.app_toolbar_menu, menu)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.app_toolbar_menu_configure_in_other_apps -> {
                            startActivity(Intent.createChooser(
                                Intent(Intent.ACTION_SHOW_APP_INFO).putExtra(Intent.EXTRA_PACKAGE_NAME, pkg),
                                getString(R.string.configure_in_other_apps)
                            ).apply {
                                putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, arrayOf(ComponentName.createRelative(applicationContext, this@App.javaClass.name)))
                                putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(
                                    Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:$pkg")), // 系统设置
                                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg")).setPackage("com.coolapk.market"), // 酷安
                                ))
                            })
                        }
                        R.id.app_toolbar_menu_reset -> {
                            AlertDialog.Builder(this@App).apply {
                                setMessage(R.string.do_you_really_reset_this_application_hooking_rules)
                                setPositiveButton(R.string.confirm) { _, _ ->
                                    reset()
                                    refresh()
                                }
                                setNegativeButton(R.string.cancel) { _, _ -> }
                            }.create().show()
                        }
                    }
                    true
                }
            }.show()
        }
        viewBinding.appCard.setOnClickListener {
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.is_enabled)
                put(AppSP.is_enabled, state)
                state
            }
            if (state)
                modulePrefs("apps").put(AppsSP.enabled, pkg)
            else
                modulePrefs("apps").remove(AppsSP.enabled, pkg)
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appHooksAdd.setOnClickListener {
            ruleResultContract.launch(Intent(this@App, Rule::class.java).apply {
                putExtra("pkg", pkg)
                putExtra("version", getString(R.string.version_format).format(versionName, versionCode))
            }, ActivityOptionsCompat.makeSceneTransitionAnimation(this))
        }
        viewBinding.appResourcesVconsoleCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesVconsoleCard.setOnClickListener {
            if (viewBinding.appResourcesVconsoleVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.vConsole)
                put(AppSP.vConsole, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesVconsoleVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.vConsole_version, viewBinding.appResourcesVconsoleVersion.adapter.getItem(p) as String)
            }
        }
        viewBinding.appResourcesNebulaucsdkCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesNebulaucsdkCard.setOnClickListener {
            if (viewBinding.appResourcesNebulaucsdkVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.nebulaUCSDK)
                put(AppSP.nebulaUCSDK, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesNebulaucsdkVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.nebulaUCSDK_version, viewBinding.appResourcesNebulaucsdkVersion.adapter.getItem(p) as String)
            }
        }
    }

    private fun refresh() {
        with(modulePrefs) {
            name("resources")
            val vConsoleAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.vConsole_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesVconsoleVersion.adapter = vConsoleAdapter
            val nebulaUCSDKAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.nebulaUCSDK_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesNebulaucsdkVersion.adapter = nebulaUCSDKAdapter

            name("apps_$pkg")
            get(AppSP.is_enabled).also {
                val iconTemp = icon.mutate().also { d ->
                    d.colorFilter = if (it) null else grayColorFilter
                }
                viewBinding.appIcon.setImageDrawable(iconTemp)
                val c = getPrimaryColor(iconTemp, this@App)
                viewBinding.appCard.backgroundTintList = colorStateSingle((c.third or 0xff000000.toInt()) and 0x33ffffff)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) viewBinding.appCard.outlineSpotShadowColor = c.third
                viewBinding.appText.setTextColor(c.first)
                viewBinding.appVersion.setTextColor(c.second)
                viewBinding.appPackage.setTextColor(c.second)
            }
            get(AppSP.vConsole).also {
                viewBinding.appResourcesVconsoleCard.backgroundTintList = colorStateSingle((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) viewBinding.appResourcesVconsoleCard.outlineSpotShadowColor = getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError)
                viewBinding.appResourcesVconsoleVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val p = vConsoleAdapter.getPosition(get(AppSP.vConsole_version))
                    viewBinding.appResourcesVconsoleVersion.setSelection(if (p >= 0) p else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        0
                    })
                }
            }
            get(AppSP.nebulaUCSDK).also {
                viewBinding.appResourcesNebulaucsdkCard.backgroundTintList = colorStateSingle((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) viewBinding.appResourcesNebulaucsdkCard.outlineSpotShadowColor = getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError)
                viewBinding.appResourcesNebulaucsdkVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val p = nebulaUCSDKAdapter.getPosition(get(AppSP.nebulaUCSDK_version))
                    viewBinding.appResourcesNebulaucsdkVersion.setSelection(if (p >= 0) p else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        0
                    })
                }
            }
            viewBinding.appHooksList.removeAllViews()
            getSet(AppSP.hooks).forEach { ruleName ->
                val v = Code(this@App)
                val hookJson = getString("hook_entry_$ruleName", "{}")
                try {
                    when(Gson().fromJson(hookJson, HookRules.HookRule::class.java).name) {
                        // TODO: 添加更多 hook 方法
                        "hookWebView" -> {
                            val hookEntry = Gson().fromJson(hookJson, HookRules.HookRuleWebView::class.java)
                            v.code = getString(R.string.code_hookFunction, if (hookEntry.remark != null && hookEntry.remark != "") getString(R.string.code_hookRemark, hookEntry.remark) else "", ruleName, hookEntry.name, arrayOf(
                                getString(R.string.code_hookParam, "Class_WebView", hookEntry.Class_WebView),
                                getString(R.string.code_hookParam, "Method_getSettings", hookEntry.Method_getSettings),
                                getString(R.string.code_hookParam, "Method_setWebContentsDebuggingEnabled", hookEntry.Method_setWebContentsDebuggingEnabled),
                                getString(R.string.code_hookParam, "Method_setJavaScriptEnabled", hookEntry.Method_setJavaScriptEnabled),
                                getString(R.string.code_hookParam, "Method_loadUrl", hookEntry.Method_loadUrl),
                                getString(R.string.code_hookParam, "Method_setWebViewClient", hookEntry.Method_setWebViewClient),
                            ).joinToString(""))
                        }
                        "hookWebViewClient" -> {
                            val hookEntry = Gson().fromJson(hookJson, HookRules.HookRuleWebViewClient::class.java)
                            v.code = getString(R.string.code_hookFunction, if (hookEntry.remark != null && hookEntry.remark != "") getString(R.string.code_hookRemark, hookEntry.remark) else "", ruleName, hookEntry.name, arrayOf(
                                getString(R.string.code_hookParam, "Class_WebView", hookEntry.Class_WebView),
                                getString(R.string.code_hookParam, "Class_WebViewClient", hookEntry.Class_WebViewClient),
                                getString(R.string.code_hookParam, "Method_onPageFinished", hookEntry.Method_onPageFinished),
                                getString(R.string.code_hookParam, "Method_evaluateJavascript", hookEntry.Method_evaluateJavascript),
                                getString(R.string.code_hookParam, "Class_ValueCallback", hookEntry.Class_ValueCallback),
                            ).joinToString(""))
                        }
                        "replaceNebulaUCSDK" -> {
                            val hookEntry = Gson().fromJson(hookJson, HookRules.ReplaceNebulaUCSDK::class.java)
                            v.code = getString(R.string.code_hookFunction, if (hookEntry.remark != null && hookEntry.remark != "") getString(R.string.code_hookRemark, hookEntry.remark) else "", ruleName, hookEntry.name, arrayOf(
                                getString(R.string.code_hookParam, "Class_UcServiceSetup", hookEntry.Class_UcServiceSetup),
                                getString(R.string.code_hookParam, "Method_updateUCVersionAndSdcardPath", hookEntry.Method_updateUCVersionAndSdcardPath),
                                getString(R.string.code_hookParam, "Field_sInitUcFromSdcardPath", hookEntry.Field_sInitUcFromSdcardPath),
                            ).joinToString(""))
                        }
                        "hookCrossWalk" -> {
                            val hookEntry = Gson().fromJson(hookJson, HookRules.HookCrossWalk::class.java)
                            v.code = getString(R.string.code_hookFunction, if (hookEntry.remark != null && hookEntry.remark != "") getString(R.string.code_hookRemark, hookEntry.remark) else "", ruleName, hookEntry.name, arrayOf(
                                getString(R.string.code_hookParam, "Class_XWalkView", hookEntry.Class_XWalkView),
                                getString(R.string.code_hookParam, "Method_getSettings", hookEntry.Method_getSettings),
                                getString(R.string.code_hookParam, "Method_setJavaScriptEnabled", hookEntry.Method_setJavaScriptEnabled),
                                getString(R.string.code_hookParam, "Method_loadUrl", hookEntry.Method_loadUrl),
                                getString(R.string.code_hookParam, "Method_setResourceClient", hookEntry.Method_setResourceClient),
                                getString(R.string.code_hookParam, "Class_XWalkPreferences", hookEntry.Class_XWalkPreferences),
                                getString(R.string.code_hookParam, "Method_setValue", hookEntry.Method_setValue),
                            ).joinToString(""))
                        }
                        "hookXWebPreferences" -> {
                            val hookEntry = Gson().fromJson(hookJson, HookRules.HookXWebPreferences::class.java)
                            v.code = getString(R.string.code_hookFunction, if (hookEntry.remark != null && hookEntry.remark != "") getString(R.string.code_hookRemark, hookEntry.remark) else "", ruleName, hookEntry.name, arrayOf(
                                getString(R.string.code_hookParam, "Class_XWebPreferences", hookEntry.Class_XWebPreferences),
                                getString(R.string.code_hookParam, "Method_setValue", hookEntry.Method_setValue),
                            ).joinToString(""))
                        }
                        "hookXWebView" -> {
                            val hookEntry = Gson().fromJson(hookJson, HookRules.HookXWebView::class.java)
                            v.code = getString(R.string.code_hookFunction, if (hookEntry.remark != null && hookEntry.remark != "") getString(R.string.code_hookRemark, hookEntry.remark) else "", ruleName, hookEntry.name, arrayOf(
                                getString(R.string.code_hookParam, "Class_XWebView", hookEntry.Class_XWebView),
                                getString(R.string.code_hookParam, "Method_initWebviewCore", hookEntry.Method_initWebviewCore),
                                getString(R.string.code_hookParam, "Method_isXWalk", hookEntry.Method_isXWalk),
                                getString(R.string.code_hookParam, "Method_isPinus", hookEntry.Method_isPinus),
                                getString(R.string.code_hookParam, "Method_isX5", hookEntry.Method_isX5),
                                getString(R.string.code_hookParam, "Method_isSys", hookEntry.Method_isSys),
                            ).joinToString(""))
                        }
                        else -> {
                            v.code = getString(R.string.unknown_hook_method)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(BuildConfig.APPLICATION_ID, getString(R.string.parse_failed), e)
                    toast?.cancel()
                    toast = Toast.makeText(this@App, getString(R.string.parse_failed)+"\n"+getString(R.string.please_reset), Toast.LENGTH_SHORT)
                    toast!!.show()
                    return@forEach // continue
                }
                v.isClickable = true
                v.setOnClickListener {
                    ruleResultContract.launch(Intent(this@App, Rule::class.java).apply {
                        putExtra("pkg", pkg)
                        putExtra("version", getString(R.string.version_format).format(versionName, versionCode))
                        putExtra("rule_name", ruleName)
                    }, ActivityOptionsCompat.makeSceneTransitionAnimation(this@App, it, "targetRule"))
                }
                v.setOnLongClickListener {
                    AlertDialog.Builder(this@App).apply {
                        setMessage(R.string.do_you_really_delete_this_rule)
                        setPositiveButton(R.string.confirm) { _, _ ->
                            with(modulePrefs("apps_$pkg")) {
                                remove(AppSP.hooks, ruleName)
                                remove("hook_entry_$ruleName")
                            }
                            refresh()
                        }
                        setNegativeButton(R.string.cancel) { _, _ -> }
                    }.create().show()
                    true
                }
                viewBinding.appHooksList.addView(v)
            }
        }
    }

    private fun reset() {
        try { modulePrefs("apps").remove(AppsSP.enabled, pkg) } catch (_: ValueNotExistedInSet) { }
        modulePrefs("apps_$pkg").clear()
        toast?.cancel()
        toast = Toast.makeText(this@App, getString(R.string.reset_completed), Toast.LENGTH_SHORT)
        toast!!.show()
    }

    class RuleResultContract : ActivityResultContract<Intent, Unit>() {
        override fun createIntent(context: Context, input: Intent): Intent {
            return input
        }
        override fun parseResult(resultCode: Int, intent: Intent?) { }
    }

    class ResourcesResultContract : ActivityResultContract<Unit, Unit>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(context, Resources::class.java)
        }
        override fun parseResult(resultCode: Int, intent: Intent?) { }
    }
}