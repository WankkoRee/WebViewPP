package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityOptionsCompat
import cn.wankkoree.xposed.enablewebviewdebugging.BuildConfig
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.ValueAlreadyExistedInSet
import cn.wankkoree.xposed.enablewebviewdebugging.ValueNotExistedInSet
import cn.wankkoree.xposed.enablewebviewdebugging.activity.component.Code
import cn.wankkoree.xposed.enablewebviewdebugging.data.*
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.ActivityAppBinding
import cn.wankkoree.xposed.enablewebviewdebugging.http.bean.HookRules
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.highcapable.yukihookapi.hook.factory.modulePrefs

class App : AppCompatActivity() {
    private lateinit var viewBinding: ActivityAppBinding
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
        viewBinding = ActivityAppBinding.inflate(layoutInflater)
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
                                            Class_WebView = "com.tencent.smtt.sdk.WebView",
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
                                            Class_WebView = "com.tencent.smtt.sdk.WebView",
                                            Class_WebViewClient = "com.tencent.smtt.sdk.WebViewClient",
                                            Class_ValueCallback = "com.tencent.smtt.sdk.ValueCallback",
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
                                            Class_WebView = "com.uc.webview.export.WebView",
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
                                            Class_WebView = "com.uc.webview.export.WebView",
                                            Class_WebViewClient = "com.alipay.mobile.nebulauc.impl.UCWebViewClient",
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
                                            Class_WebView = "org.xwalk.core.XWalkView",
                                            Class_WebViewClient = "org.xwalk.core.XWalkResourceClient",
                                            Method_onPageFinished = "onLoadFinished",
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
                            MaterialAlertDialogBuilder(this@App).apply {
                                setTitle(R.string.reset)
                                setMessage(R.string.do_you_really_reset_this_application_hooking_rules)
                                setNegativeButton(R.string.cancel) { _, _ -> }
                                setPositiveButton(R.string.confirm) { _, _ ->
                                    reset()
                                    refresh()
                                }
                            }.show()
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
        viewBinding.appResourcesVconsolePluginSourcesCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesVconsolePluginSourcesCard.setOnClickListener {
            if (viewBinding.appResourcesVconsolePluginSourcesVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.vConsole_plugin_sources)
                put(AppSP.vConsole_plugin_sources, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesVconsolePluginSourcesVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.vConsole_plugin_sources_version, viewBinding.appResourcesVconsolePluginSourcesVersion.adapter.getItem(p) as String)
            }
        }
        viewBinding.appResourcesVconsolePluginStatsCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesVconsolePluginStatsCard.setOnClickListener {
            if (viewBinding.appResourcesVconsolePluginStatsVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.vConsole_plugin_stats)
                put(AppSP.vConsole_plugin_stats, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesVconsolePluginStatsVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.vConsole_plugin_stats_version, viewBinding.appResourcesVconsolePluginStatsVersion.adapter.getItem(p) as String)
            }
        }
        viewBinding.appResourcesVconsolePluginVueDevtoolsCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesVconsolePluginVueDevtoolsCard.setOnClickListener {
            if (viewBinding.appResourcesVconsolePluginVueDevtoolsVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.vConsole_plugin_vue_devtools)
                put(AppSP.vConsole_plugin_vue_devtools, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesVconsolePluginVueDevtoolsVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.vConsole_plugin_vue_devtools_version, viewBinding.appResourcesVconsolePluginVueDevtoolsVersion.adapter.getItem(p) as String)
            }
        }
        viewBinding.appResourcesVconsolePluginOutputlogCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesVconsolePluginOutputlogCard.setOnClickListener {
            if (viewBinding.appResourcesVconsolePluginOutputlogVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.vConsole_plugin_outputlog)
                put(AppSP.vConsole_plugin_outputlog, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesVconsolePluginOutputlogVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.vConsole_plugin_outputlog_version, viewBinding.appResourcesVconsolePluginOutputlogVersion.adapter.getItem(p) as String)
            }
        }
        viewBinding.appResourcesErudaCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesErudaCard.setOnClickListener {
            if (viewBinding.appResourcesErudaVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.eruda)
                put(AppSP.eruda, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesErudaVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.eruda_version, viewBinding.appResourcesErudaVersion.adapter.getItem(p) as String)
            }
        }
        viewBinding.appResourcesErudaPluginFpsCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesErudaPluginFpsCard.setOnClickListener {
            if (viewBinding.appResourcesErudaPluginFpsVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.eruda_plugin_fps)
                put(AppSP.eruda_plugin_fps, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesErudaPluginFpsVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.eruda_plugin_fps_version, viewBinding.appResourcesErudaPluginFpsVersion.adapter.getItem(p) as String)
            }
        }
        viewBinding.appResourcesErudaPluginFeaturesCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesErudaPluginFeaturesCard.setOnClickListener {
            if (viewBinding.appResourcesErudaPluginFeaturesVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.eruda_plugin_features)
                put(AppSP.eruda_plugin_features, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesErudaPluginFeaturesVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.eruda_plugin_features_version, viewBinding.appResourcesErudaPluginFeaturesVersion.adapter.getItem(p) as String)
            }
        }
        viewBinding.appResourcesErudaPluginTimingCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesErudaPluginTimingCard.setOnClickListener {
            if (viewBinding.appResourcesErudaPluginTimingVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.eruda_plugin_timing)
                put(AppSP.eruda_plugin_timing, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesErudaPluginTimingVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.eruda_plugin_timing_version, viewBinding.appResourcesErudaPluginTimingVersion.adapter.getItem(p) as String)
            }
        }
        viewBinding.appResourcesErudaPluginMemoryCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesErudaPluginMemoryCard.setOnClickListener {
            if (viewBinding.appResourcesErudaPluginMemoryVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.eruda_plugin_memory)
                put(AppSP.eruda_plugin_memory, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesErudaPluginMemoryVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.eruda_plugin_memory_version, viewBinding.appResourcesErudaPluginMemoryVersion.adapter.getItem(p) as String)
            }
        }
        viewBinding.appResourcesErudaPluginCodeCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesErudaPluginCodeCard.setOnClickListener {
            if (viewBinding.appResourcesErudaPluginCodeVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.eruda_plugin_code)
                put(AppSP.eruda_plugin_code, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesErudaPluginCodeVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.eruda_plugin_code_version, viewBinding.appResourcesErudaPluginCodeVersion.adapter.getItem(p) as String)
            }
        }
        viewBinding.appResourcesErudaPluginBenchmarkCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesErudaPluginBenchmarkCard.setOnClickListener {
            if (viewBinding.appResourcesErudaPluginBenchmarkVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.eruda_plugin_fps)
                put(AppSP.eruda_plugin_fps, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesErudaPluginBenchmarkVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.eruda_plugin_fps_version, viewBinding.appResourcesErudaPluginBenchmarkVersion.adapter.getItem(p) as String)
            }
        }
        viewBinding.appResourcesErudaPluginBenchmarkCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesErudaPluginBenchmarkCard.setOnClickListener {
            if (viewBinding.appResourcesErudaPluginBenchmarkVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.eruda_plugin_benchmark)
                put(AppSP.eruda_plugin_benchmark, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesErudaPluginBenchmarkVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.eruda_plugin_benchmark_version, viewBinding.appResourcesErudaPluginBenchmarkVersion.adapter.getItem(p) as String)
            }
        }
        viewBinding.appResourcesErudaPluginGeolocationCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesErudaPluginGeolocationCard.setOnClickListener {
            if (viewBinding.appResourcesErudaPluginGeolocationVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.eruda_plugin_geolocation)
                put(AppSP.eruda_plugin_geolocation, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesErudaPluginGeolocationVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.eruda_plugin_geolocation_version, viewBinding.appResourcesErudaPluginGeolocationVersion.adapter.getItem(p) as String)
            }
        }
        viewBinding.appResourcesErudaPluginDomCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesErudaPluginDomCard.setOnClickListener {
            if (viewBinding.appResourcesErudaPluginDomVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.eruda_plugin_dom)
                put(AppSP.eruda_plugin_dom, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesErudaPluginDomVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.eruda_plugin_dom_version, viewBinding.appResourcesErudaPluginDomVersion.adapter.getItem(p) as String)
            }
        }
        viewBinding.appResourcesErudaPluginOrientationCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesErudaPluginOrientationCard.setOnClickListener {
            if (viewBinding.appResourcesErudaPluginOrientationVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.eruda_plugin_orientation)
                put(AppSP.eruda_plugin_orientation, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesErudaPluginOrientationVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.eruda_plugin_orientation_version, viewBinding.appResourcesErudaPluginOrientationVersion.adapter.getItem(p) as String)
            }
        }
        viewBinding.appResourcesErudaPluginTouchesCard.setOnLongClickListener {
            this@App.resourcesResultContract.launch(Unit)
            true
        }
        viewBinding.appResourcesErudaPluginTouchesCard.setOnClickListener {
            if (viewBinding.appResourcesErudaPluginTouchesVersion.adapter.count == 0) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_download_resources_at_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val state = modulePrefs("apps_$pkg").run {
                val state = !get(AppSP.eruda_plugin_touches)
                put(AppSP.eruda_plugin_touches, state)
                state
            }
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
            refresh()
        }
        viewBinding.appResourcesErudaPluginTouchesVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                modulePrefs("apps_$pkg").put(AppSP.eruda_plugin_touches_version, viewBinding.appResourcesErudaPluginTouchesVersion.adapter.getItem(p) as String)
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
            val vConsolePluginSourcesAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.vConsole_plugin_sources_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesVconsolePluginSourcesVersion.adapter = vConsolePluginSourcesAdapter
            val vConsolePluginStatsAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.vConsole_plugin_stats_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesVconsolePluginStatsVersion.adapter = vConsolePluginStatsAdapter
            val vConsolePluginVueDevtoolsAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.vConsole_plugin_vue_devtools_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesVconsolePluginVueDevtoolsVersion.adapter = vConsolePluginVueDevtoolsAdapter
            val vConsolePluginOutputlogAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.vConsole_plugin_outputlog_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesVconsolePluginOutputlogVersion.adapter = vConsolePluginOutputlogAdapter
            val erudaAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.eruda_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesErudaVersion.adapter = erudaAdapter
            val erudaPluginFpsAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.eruda_plugin_fps_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesErudaPluginFpsVersion.adapter = erudaPluginFpsAdapter
            val erudaPluginFeaturesAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.eruda_plugin_features_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesErudaPluginFeaturesVersion.adapter = erudaPluginFeaturesAdapter
            val erudaPluginTimingAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.eruda_plugin_timing_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesErudaPluginTimingVersion.adapter = erudaPluginTimingAdapter
            val erudaPluginMemoryAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.eruda_plugin_memory_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesErudaPluginMemoryVersion.adapter = erudaPluginMemoryAdapter
            val erudaPluginCodeAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.eruda_plugin_code_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesErudaPluginCodeVersion.adapter = erudaPluginCodeAdapter
            val erudaPluginBenchmarkAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.eruda_plugin_benchmark_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesErudaPluginBenchmarkVersion.adapter = erudaPluginBenchmarkAdapter
            val erudaPluginGeolocationAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.eruda_plugin_geolocation_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesErudaPluginGeolocationVersion.adapter = erudaPluginGeolocationAdapter
            val erudaPluginDomAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.eruda_plugin_dom_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesErudaPluginDomVersion.adapter = erudaPluginDomAdapter
            val erudaPluginOrientationAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.eruda_plugin_orientation_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesErudaPluginOrientationVersion.adapter = erudaPluginOrientationAdapter
            val erudaPluginTouchesAdapter = ArrayAdapter(this@App, R.layout.component_spinneritem, getSet(ResourcesSP.eruda_plugin_touches_versions).toArray()).apply {
                setDropDownViewResource(R.layout.component_spinneritem)
            }
            viewBinding.appResourcesErudaPluginTouchesVersion.adapter = erudaPluginTouchesAdapter
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
                viewBinding.appCard.setCardBackgroundColor((c.third or 0xff000000.toInt()) and 0x33ffffff)
                viewBinding.appText.setTextColor(c.first)
                viewBinding.appVersion.setTextColor(c.second)
                viewBinding.appPackage.setTextColor(c.second)
            }

            name("apps_$pkg")
            get(AppSP.vConsole).also {
                viewBinding.appResourcesVconsoleCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesVconsoleVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = vConsoleAdapter.getPosition(get(AppSP.vConsole_version))
                    if (last >= 0) viewBinding.appResourcesVconsoleVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = vConsoleAdapter.getPosition(get(ResourcesSP.vConsole_latest))
                        viewBinding.appResourcesVconsoleVersion.setSelection(if (latest >= 0) latest else 0)
                    }

                    viewBinding.appResourcesVconsolePluginSourcesCard.visibility = View.VISIBLE
                    viewBinding.appResourcesVconsolePluginStatsCard.visibility = View.VISIBLE
                    viewBinding.appResourcesVconsolePluginVueDevtoolsCard.visibility = View.VISIBLE
                    viewBinding.appResourcesVconsolePluginOutputlogCard.visibility = View.VISIBLE
                } else {
                    viewBinding.appResourcesVconsolePluginSourcesCard.visibility = View.GONE
                    viewBinding.appResourcesVconsolePluginStatsCard.visibility = View.GONE
                    viewBinding.appResourcesVconsolePluginVueDevtoolsCard.visibility = View.GONE
                    viewBinding.appResourcesVconsolePluginOutputlogCard.visibility = View.GONE
                }
            }
            name("apps_$pkg")
            get(AppSP.vConsole_plugin_sources).also {
                viewBinding.appResourcesVconsolePluginSourcesCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesVconsolePluginSourcesVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = vConsolePluginSourcesAdapter.getPosition(get(AppSP.vConsole_plugin_sources_version))
                    if (last >= 0) viewBinding.appResourcesVconsolePluginSourcesVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = vConsolePluginSourcesAdapter.getPosition(get(ResourcesSP.vConsole_plugin_sources_latest))
                        viewBinding.appResourcesVconsolePluginSourcesVersion.setSelection(if (latest >= 0) latest else 0)
                    }
                }
            }
            name("apps_$pkg")
            get(AppSP.vConsole_plugin_stats).also {
                viewBinding.appResourcesVconsolePluginStatsCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesVconsolePluginStatsVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = vConsolePluginStatsAdapter.getPosition(get(AppSP.vConsole_plugin_stats_version))
                    if (last >= 0) viewBinding.appResourcesVconsolePluginStatsVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = vConsolePluginStatsAdapter.getPosition(get(ResourcesSP.vConsole_plugin_stats_latest))
                        viewBinding.appResourcesVconsolePluginStatsVersion.setSelection(if (latest >= 0) latest else 0)
                    }
                }
            }
            name("apps_$pkg")
            get(AppSP.vConsole_plugin_vue_devtools).also {
                viewBinding.appResourcesVconsolePluginVueDevtoolsCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesVconsolePluginVueDevtoolsVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = vConsolePluginVueDevtoolsAdapter.getPosition(get(AppSP.vConsole_plugin_vue_devtools_version))
                    if (last >= 0) viewBinding.appResourcesVconsolePluginVueDevtoolsVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = vConsolePluginVueDevtoolsAdapter.getPosition(get(ResourcesSP.vConsole_plugin_vue_devtools_latest))
                        viewBinding.appResourcesVconsolePluginVueDevtoolsVersion.setSelection(if (latest >= 0) latest else 0)
                    }
                }
            }
            name("apps_$pkg")
            get(AppSP.vConsole_plugin_outputlog).also {
                viewBinding.appResourcesVconsolePluginOutputlogCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesVconsolePluginOutputlogVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = vConsolePluginOutputlogAdapter.getPosition(get(AppSP.vConsole_plugin_outputlog_version))
                    if (last >= 0) viewBinding.appResourcesVconsolePluginOutputlogVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = vConsolePluginOutputlogAdapter.getPosition(get(ResourcesSP.vConsole_plugin_outputlog_latest))
                        viewBinding.appResourcesVconsolePluginOutputlogVersion.setSelection(if (latest >= 0) latest else 0)
                    }
                }
            }
            name("apps_$pkg")
            get(AppSP.eruda).also {
                viewBinding.appResourcesErudaCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesErudaVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = erudaAdapter.getPosition(get(AppSP.eruda_version))
                    if (last >= 0) viewBinding.appResourcesErudaVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = erudaAdapter.getPosition(get(ResourcesSP.eruda_latest))
                        viewBinding.appResourcesErudaVersion.setSelection(if (latest >= 0) latest else 0)
                    }

                    viewBinding.appResourcesErudaPluginFpsCard.visibility = View.VISIBLE
                    viewBinding.appResourcesErudaPluginFeaturesCard.visibility = View.VISIBLE
                    viewBinding.appResourcesErudaPluginTimingCard.visibility = View.VISIBLE
                    viewBinding.appResourcesErudaPluginMemoryCard.visibility = View.VISIBLE
                    viewBinding.appResourcesErudaPluginCodeCard.visibility = View.VISIBLE
                    viewBinding.appResourcesErudaPluginBenchmarkCard.visibility = View.VISIBLE
                    viewBinding.appResourcesErudaPluginGeolocationCard.visibility = View.VISIBLE
                    viewBinding.appResourcesErudaPluginDomCard.visibility = View.VISIBLE
                    viewBinding.appResourcesErudaPluginOrientationCard.visibility = View.VISIBLE
                    viewBinding.appResourcesErudaPluginTouchesCard.visibility = View.VISIBLE
                } else {
                    viewBinding.appResourcesErudaPluginFpsCard.visibility = View.GONE
                    viewBinding.appResourcesErudaPluginFeaturesCard.visibility = View.GONE
                    viewBinding.appResourcesErudaPluginTimingCard.visibility = View.GONE
                    viewBinding.appResourcesErudaPluginMemoryCard.visibility = View.GONE
                    viewBinding.appResourcesErudaPluginCodeCard.visibility = View.GONE
                    viewBinding.appResourcesErudaPluginBenchmarkCard.visibility = View.GONE
                    viewBinding.appResourcesErudaPluginGeolocationCard.visibility = View.GONE
                    viewBinding.appResourcesErudaPluginDomCard.visibility = View.GONE
                    viewBinding.appResourcesErudaPluginOrientationCard.visibility = View.GONE
                    viewBinding.appResourcesErudaPluginTouchesCard.visibility = View.GONE
                }
            }
            name("apps_$pkg")
            get(AppSP.eruda_plugin_fps).also {
                viewBinding.appResourcesErudaPluginFpsCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesErudaPluginFpsVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = erudaPluginFpsAdapter.getPosition(get(AppSP.eruda_plugin_fps_version))
                    if (last >= 0) viewBinding.appResourcesErudaPluginFpsVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = erudaPluginFpsAdapter.getPosition(get(ResourcesSP.eruda_plugin_fps_latest))
                        viewBinding.appResourcesErudaPluginFpsVersion.setSelection(if (latest >= 0) latest else 0)
                    }
                }
            }
            name("apps_$pkg")
            get(AppSP.eruda_plugin_features).also {
                viewBinding.appResourcesErudaPluginFeaturesCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesErudaPluginFeaturesVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = erudaPluginFeaturesAdapter.getPosition(get(AppSP.eruda_plugin_features_version))
                    if (last >= 0) viewBinding.appResourcesErudaPluginFeaturesVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = erudaPluginFeaturesAdapter.getPosition(get(ResourcesSP.eruda_plugin_features_latest))
                        viewBinding.appResourcesErudaPluginFeaturesVersion.setSelection(if (latest >= 0) latest else 0)
                    }
                }
            }
            name("apps_$pkg")
            get(AppSP.eruda_plugin_timing).also {
                viewBinding.appResourcesErudaPluginTimingCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesErudaPluginTimingVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = erudaPluginTimingAdapter.getPosition(get(AppSP.eruda_plugin_timing_version))
                    if (last >= 0) viewBinding.appResourcesErudaPluginTimingVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = erudaPluginTimingAdapter.getPosition(get(ResourcesSP.eruda_plugin_timing_latest))
                        viewBinding.appResourcesErudaPluginTimingVersion.setSelection(if (latest >= 0) latest else 0)
                    }
                }
            }
            name("apps_$pkg")
            get(AppSP.eruda_plugin_memory).also {
                viewBinding.appResourcesErudaPluginMemoryCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesErudaPluginMemoryVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = erudaPluginMemoryAdapter.getPosition(get(AppSP.eruda_plugin_memory_version))
                    if (last >= 0) viewBinding.appResourcesErudaPluginMemoryVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = erudaPluginMemoryAdapter.getPosition(get(ResourcesSP.eruda_plugin_memory_latest))
                        viewBinding.appResourcesErudaPluginMemoryVersion.setSelection(if (latest >= 0) latest else 0)
                    }
                }
            }
            name("apps_$pkg")
            get(AppSP.eruda_plugin_code).also {
                viewBinding.appResourcesErudaPluginCodeCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesErudaPluginCodeVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = erudaPluginCodeAdapter.getPosition(get(AppSP.eruda_plugin_code_version))
                    if (last >= 0) viewBinding.appResourcesErudaPluginCodeVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = erudaPluginCodeAdapter.getPosition(get(ResourcesSP.eruda_plugin_code_latest))
                        viewBinding.appResourcesErudaPluginCodeVersion.setSelection(if (latest >= 0) latest else 0)
                    }
                }
            }
            name("apps_$pkg")
            get(AppSP.eruda_plugin_benchmark).also {
                viewBinding.appResourcesErudaPluginBenchmarkCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesErudaPluginBenchmarkVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = erudaPluginBenchmarkAdapter.getPosition(get(AppSP.eruda_plugin_benchmark_version))
                    if (last >= 0) viewBinding.appResourcesErudaPluginBenchmarkVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = erudaPluginBenchmarkAdapter.getPosition(get(ResourcesSP.eruda_plugin_benchmark_latest))
                        viewBinding.appResourcesErudaPluginBenchmarkVersion.setSelection(if (latest >= 0) latest else 0)
                    }
                }
            }
            name("apps_$pkg")
            get(AppSP.eruda_plugin_geolocation).also {
                viewBinding.appResourcesErudaPluginGeolocationCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesErudaPluginGeolocationVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = erudaPluginGeolocationAdapter.getPosition(get(AppSP.eruda_plugin_geolocation_version))
                    if (last >= 0) viewBinding.appResourcesErudaPluginGeolocationVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = erudaPluginGeolocationAdapter.getPosition(get(ResourcesSP.eruda_plugin_geolocation_latest))
                        viewBinding.appResourcesErudaPluginGeolocationVersion.setSelection(if (latest >= 0) latest else 0)
                    }
                }
            }
            name("apps_$pkg")
            get(AppSP.eruda_plugin_dom).also {
                viewBinding.appResourcesErudaPluginDomCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesErudaPluginDomVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = erudaPluginDomAdapter.getPosition(get(AppSP.eruda_plugin_dom_version))
                    if (last >= 0) viewBinding.appResourcesErudaPluginDomVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = erudaPluginDomAdapter.getPosition(get(ResourcesSP.eruda_plugin_dom_latest))
                        viewBinding.appResourcesErudaPluginDomVersion.setSelection(if (latest >= 0) latest else 0)
                    }
                }
            }
            name("apps_$pkg")
            get(AppSP.eruda_plugin_orientation).also {
                viewBinding.appResourcesErudaPluginOrientationCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesErudaPluginOrientationVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = erudaPluginOrientationAdapter.getPosition(get(AppSP.eruda_plugin_orientation_version))
                    if (last >= 0) viewBinding.appResourcesErudaPluginOrientationVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = erudaPluginOrientationAdapter.getPosition(get(ResourcesSP.eruda_plugin_orientation_latest))
                        viewBinding.appResourcesErudaPluginOrientationVersion.setSelection(if (latest >= 0) latest else 0)
                    }
                }
            }
            name("apps_$pkg")
            get(AppSP.eruda_plugin_touches).also {
                viewBinding.appResourcesErudaPluginTouchesCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesErudaPluginTouchesVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = erudaPluginTouchesAdapter.getPosition(get(AppSP.eruda_plugin_touches_version))
                    if (last >= 0) viewBinding.appResourcesErudaPluginTouchesVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = erudaPluginTouchesAdapter.getPosition(get(ResourcesSP.eruda_plugin_touches_latest))
                        viewBinding.appResourcesErudaPluginTouchesVersion.setSelection(if (latest >= 0) latest else 0)
                    }
                }
            }
            name("apps_$pkg")
            get(AppSP.nebulaUCSDK).also {
                viewBinding.appResourcesNebulaucsdkCard.setCardBackgroundColor((getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
                viewBinding.appResourcesNebulaucsdkVersion.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    val last = nebulaUCSDKAdapter.getPosition(get(AppSP.nebulaUCSDK_version))
                    if (last >= 0) viewBinding.appResourcesNebulaucsdkVersion.setSelection(last)
                    else {
                        toast?.cancel()
                        toast = Toast.makeText(this@App, getString(R.string.nothing_set_yet_a_default_will_be_set), Toast.LENGTH_SHORT)
                        toast!!.show()
                        name("resources")
                        val latest = nebulaUCSDKAdapter.getPosition(get(ResourcesSP.nebulaUCSDK_latest))
                        viewBinding.appResourcesNebulaucsdkVersion.setSelection(if (latest >= 0) latest else 0)
                    }
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
                    MaterialAlertDialogBuilder(this@App).apply {
                        setTitle(getString(R.string.delete))
                        setMessage(R.string.do_you_really_delete_this_rule)
                        setNegativeButton(R.string.cancel) { _, _ -> }
                        setPositiveButton(R.string.confirm) { _, _ ->
                            with(modulePrefs("apps_$pkg")) {
                                remove(AppSP.hooks, ruleName)
                                remove("hook_entry_$ruleName")
                            }
                            refresh()
                        }
                    }.show()
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