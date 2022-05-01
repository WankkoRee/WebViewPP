package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.transition.Slide
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import cn.wankkoree.xposed.enablewebviewdebugging.BuildConfig
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.ValueAlreadyExistedInSet
import cn.wankkoree.xposed.enablewebviewdebugging.activity.component.Code
import cn.wankkoree.xposed.enablewebviewdebugging.data.*
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.DialogCloudrulesBinding
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.RuleBinding
import cn.wankkoree.xposed.enablewebviewdebugging.http.Http
import com.google.gson.Gson
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Rule : AppCompatActivity() {
    private lateinit var viewBinding: RuleBinding
    private var toast: Toast? = null

    private lateinit var pkg: String
    private lateinit var version: String
    private var ruleName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.enterTransition = Slide()
        window.exitTransition = Slide()
        viewBinding = RuleBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        pkg = intent.getStringExtra("pkg")!!
        version = intent.getStringExtra("version")!!

        // TODO: 添加更多 hook 方法
        ArrayAdapter(this@Rule, R.layout.component_spinneritem, arrayOf("hookWebView", "hookWebViewClient", "replaceNebulaUCSDK", "hookCrossWalk", "hookXWebPreferences")).also { adapter ->
            adapter.setDropDownViewResource(R.layout.component_spinneritem)
            viewBinding.ruleHookMethod.adapter = adapter
        }

        viewBinding.ruleToolbarBack.setOnClickListener {
            finishAfterTransition()
        }
        viewBinding.ruleToolbarCloud.setOnClickListener {
            AlertDialog.Builder(this@Rule).run {
                val dialogBinding = DialogCloudrulesBinding.inflate(layoutInflater)
                setView(dialogBinding.root)
                show().let { dialog ->
                    dialogBinding.dialogCloudrulesVersions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) { }
                        override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                            val version = dialogBinding.dialogCloudrulesVersions.adapter.getItem(p) as String
                            lifecycleScope.launch(Dispatchers.Main) {
                                val rulesStr = try {
                                    Http.get("https://raw.githubusercontent.com/WankkoRee/EnableWebViewDebugging-Rules/master/rules/$pkg/$version.json")
                                } catch(e: Exception) {
                                    Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(pkg+' '+getString(R.string.cloud_rules)), e)
                                    null
                                }
                                if (rulesStr != null) {
                                    dialogBinding.dialogCloudrulesRules.removeAllViews()
                                    val rules = Gson().fromJson(rulesStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.HookRules::class.java)
                                    // TODO: 添加更多 hook 方法
                                    if (rules.hookWebView != null) for (hookRule in rules.hookWebView) {
                                        val v = Code(this@Rule)
                                        v.code = getString(R.string.code_hookWebView).format(
                                            hookRule.name,
                                            hookRule.Class_WebView,
                                            hookRule.Method_getSettings,
                                            hookRule.Method_setWebContentsDebuggingEnabled,
                                            hookRule.Method_setJavaScriptEnabled,
                                            hookRule.Method_loadUrl,
                                            hookRule.Method_setWebViewClient,
                                        )
                                        v.isClickable = true
                                        v.setOnClickListener {
                                            viewBinding.ruleHookMethod.setSelection(0)
                                            viewBinding.ruleName.setText(hookRule.name)
                                            viewBinding.ruleHookWebViewClassWebView.setText(hookRule.Class_WebView)
                                            viewBinding.ruleHookWebViewMethodGetSettings.setText(hookRule.Method_getSettings)
                                            viewBinding.ruleHookWebViewMethodSetWebContentsDebuggingEnabled.setText(hookRule.Method_setWebContentsDebuggingEnabled)
                                            viewBinding.ruleHookWebViewMethodSetJavaScriptEnabled.setText(hookRule.Method_setJavaScriptEnabled)
                                            viewBinding.ruleHookWebViewMethodLoadUrl.setText(hookRule.Method_loadUrl)
                                            viewBinding.ruleHookWebViewMethodSetWebViewClient.setText(hookRule.Method_setWebViewClient)
                                            dialog.cancel()
                                        }
                                        dialogBinding.dialogCloudrulesRules.addView(v)
                                    }
                                    if (rules.hookWebViewClient != null) for (hookRule in rules.hookWebViewClient) {
                                        val v = Code(this@Rule)
                                        v.code = getString(R.string.code_hookWebViewClient).format(
                                            hookRule.name,
                                            hookRule.Class_WebView,
                                            hookRule.Class_WebViewClient,
                                            hookRule.Method_onPageFinished,
                                            hookRule.Method_evaluateJavascript,
                                            hookRule.Class_ValueCallback,
                                        )
                                        v.isClickable = true
                                        v.setOnClickListener {
                                            viewBinding.ruleHookMethod.setSelection(1)
                                            viewBinding.ruleName.setText(hookRule.name)
                                            viewBinding.ruleHookWebViewClientClassWebView.setText(hookRule.Class_WebView)
                                            viewBinding.ruleHookWebViewClientClassWebViewClient.setText(hookRule.Class_WebViewClient)
                                            viewBinding.ruleHookWebViewClientMethodOnPageFinished.setText(hookRule.Method_onPageFinished)
                                            viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.setText(hookRule.Method_evaluateJavascript)
                                            viewBinding.ruleHookWebViewClientClassValueCallback.setText(hookRule.Class_ValueCallback)
                                            dialog.cancel()
                                        }
                                        dialogBinding.dialogCloudrulesRules.addView(v)
                                    }
                                    if (rules.replaceNebulaUCSDK != null) for (hookRule in rules.replaceNebulaUCSDK) {
                                        val v = Code(this@Rule)
                                        v.code = getString(R.string.code_replaceNebulaUCSDK).format(
                                            hookRule.name,
                                            hookRule.Class_UcServiceSetup,
                                            hookRule.Method_updateUCVersionAndSdcardPath,
                                            hookRule.Field_sInitUcFromSdcardPath,
                                        )
                                        v.isClickable = true
                                        v.setOnClickListener {
                                            viewBinding.ruleHookMethod.setSelection(2)
                                            viewBinding.ruleName.setText(hookRule.name)
                                            viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.setText(hookRule.Class_UcServiceSetup)
                                            viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.setText(hookRule.Method_updateUCVersionAndSdcardPath)
                                            viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.setText(hookRule.Field_sInitUcFromSdcardPath)
                                            dialog.cancel()
                                        }
                                        dialogBinding.dialogCloudrulesRules.addView(v)
                                    }
                                    if (rules.hookCrossWalk != null) for (hookRule in rules.hookCrossWalk) {
                                        val v = Code(this@Rule)
                                        v.code = getString(R.string.code_hookCrossWalk).format(
                                            hookRule.name,
                                            hookRule.Class_XWalkView,
                                            hookRule.Method_getSettings,
                                            hookRule.Method_setJavaScriptEnabled,
                                            hookRule.Method_loadUrl,
                                            hookRule.Method_setResourceClient,
                                            hookRule.Class_XWalkPreferences,
                                            hookRule.Method_setValue,
                                        )
                                        v.isClickable = true
                                        v.setOnClickListener {
                                            viewBinding.ruleHookMethod.setSelection(3)
                                            viewBinding.ruleName.setText(hookRule.name)
                                            viewBinding.ruleHookCrossWalkClassXWalkView.setText(hookRule.Class_XWalkView)
                                            viewBinding.ruleHookCrossWalkMethodGetSettings.setText(hookRule.Method_getSettings)
                                            viewBinding.ruleHookCrossWalkMethodSetJavaScriptEnabled.setText(hookRule.Method_setJavaScriptEnabled)
                                            viewBinding.ruleHookCrossWalkMethodLoadUrl.setText(hookRule.Method_loadUrl)
                                            viewBinding.ruleHookCrossWalkMethodSetResourceClient.setText(hookRule.Method_setResourceClient)
                                            viewBinding.ruleHookCrossWalkClassXWalkPreferences.setText(hookRule.Class_XWalkPreferences)
                                            viewBinding.ruleHookCrossWalkMethodSetValue.setText(hookRule.Method_setValue)
                                            dialog.cancel()
                                        }
                                        dialogBinding.dialogCloudrulesRules.addView(v)
                                    }
                                    if (rules.hookXWebPreferences != null) for (hookRule in rules.hookXWebPreferences) {
                                        val v = Code(this@Rule)
                                        v.code = getString(R.string.code_hookXWebPreferences).format(
                                            hookRule.name,
                                            hookRule.Class_XWebPreferences,
                                            hookRule.Method_setValue,
                                        )
                                        v.isClickable = true
                                        v.setOnClickListener {
                                            viewBinding.ruleHookMethod.setSelection(4)
                                            viewBinding.ruleName.setText(hookRule.name)
                                            viewBinding.ruleHookXWebPreferencesClassXWebPreferences.setText(hookRule.Class_XWebPreferences)
                                            viewBinding.ruleHookXWebPreferencesMethodSetValue.setText(hookRule.Method_setValue)
                                            dialog.cancel()
                                        }
                                        dialogBinding.dialogCloudrulesRules.addView(v)
                                    }
                                } else {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.pull_failed).format(pkg+' '+version+' '+getString(R.string.cloud_rules))+'\n'+getString(R.string.please_set_custom_hook_rules_then_push_rules_to_rules_repos), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    dialog.cancel()
                                }
                            }
                        }
                    }
                    lifecycleScope.launch(Dispatchers.Main) {
                        val versionsStr = try {
                            Http.get("https://api.github.com/repos/WankkoRee/EnableWebViewDebugging-Rules/contents/rules/$pkg")
                        } catch(e: Exception) {
                            Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(pkg+' '+getString(R.string.cloud_rules)), e)
                            null
                        }
                        if (versionsStr != null) {
                            val versions = Gson().fromJson(versionsStr, Array<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.github.RepoContent>::class.java)
                            val adapter = ArrayAdapter(context, R.layout.component_spinneritem, versions.map{ it.name.substringBeforeLast(".json") })
                            adapter.setDropDownViewResource(R.layout.component_spinneritem)
                            dialogBinding.dialogCloudrulesVersions.adapter = adapter
                            val p = adapter.getPosition(version)
                            if (p >= 0)
                                dialogBinding.dialogCloudrulesVersions.setSelection(p)
                            else {
                                dialogBinding.dialogCloudrulesVersions.setSelection(0)
                                toast?.cancel()
                                toast = Toast.makeText(context, getString(R.string.no_matching_version), Toast.LENGTH_SHORT)
                                toast!!.show()
                            }
                        } else {
                            toast?.cancel()
                            toast = Toast.makeText(context, getString(R.string.pull_failed).format(pkg+' '+getString(R.string.cloud_rules))+'\n'+getString(R.string.please_set_custom_hook_rules_then_push_rules_to_rules_repos), Toast.LENGTH_SHORT)
                            toast!!.show()
                            dialog.cancel()
                        }
                    }
                }
            }
        }
        viewBinding.ruleToolbarSave.setOnClickListener {
            val name = viewBinding.ruleName.text.toString()
            if (name.isEmpty()) {
                toast?.cancel()
                toast = Toast.makeText(this@Rule, getString(R.string.s_cannot_be_empty).format(getString(R.string.rule_name)), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            } else {
                modulePrefs("apps_$pkg").run {
                    try {
                        put(AppSP.hooks, name)
                    } catch (_: ValueAlreadyExistedInSet) {
                        if (ruleName == null || ruleName != name) { // 新建 or 修改名称
                            toast?.cancel()
                            toast = Toast.makeText(this@Rule, getString(R.string.s_already_exists).format(getString(R.string.rule_name)), Toast.LENGTH_SHORT)
                            toast!!.show()
                            return@setOnClickListener
                        }
                    }
                    if (ruleName != null && ruleName != name) { // 修改名称
                        remove(AppSP.hooks, ruleName!!)
                        remove("hook_entry_${ruleName!!}")
                    }
                    putList("hook_entry_$name", when (viewBinding.ruleHookMethod.selectedItem as String) {
                        // TODO: 添加更多 hook 方法
                        "hookWebView" -> listOf(
                            "hookWebView",
                            viewBinding.ruleHookWebViewClassWebView.text.toString(),
                            viewBinding.ruleHookWebViewMethodGetSettings.text.toString(),
                            viewBinding.ruleHookWebViewMethodSetWebContentsDebuggingEnabled.text.toString(),
                            viewBinding.ruleHookWebViewMethodSetJavaScriptEnabled.text.toString(),
                            viewBinding.ruleHookWebViewMethodLoadUrl.text.toString(),
                            viewBinding.ruleHookWebViewMethodSetWebViewClient.text.toString(),
                        )
                        "hookWebViewClient" -> listOf(
                            "hookWebViewClient",
                            viewBinding.ruleHookWebViewClientClassWebView.text.toString(),
                            viewBinding.ruleHookWebViewClientClassWebViewClient.text.toString(),
                            viewBinding.ruleHookWebViewClientMethodOnPageFinished.text.toString(),
                            viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.text.toString(),
                            viewBinding.ruleHookWebViewClientClassValueCallback.text.toString(),
                        )
                        "replaceNebulaUCSDK" -> listOf(
                            "replaceNebulaUCSDK",
                            viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.text.toString(),
                            viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.text.toString(),
                            viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.text.toString(),
                        )
                        "hookCrossWalk" -> listOf(
                            "hookCrossWalk",
                            viewBinding.ruleHookCrossWalkClassXWalkView.text.toString(),
                            viewBinding.ruleHookCrossWalkMethodGetSettings.text.toString(),
                            viewBinding.ruleHookCrossWalkMethodSetJavaScriptEnabled.text.toString(),
                            viewBinding.ruleHookCrossWalkMethodLoadUrl.text.toString(),
                            viewBinding.ruleHookCrossWalkMethodSetResourceClient.text.toString(),
                            viewBinding.ruleHookCrossWalkClassXWalkPreferences.text.toString(),
                            viewBinding.ruleHookCrossWalkMethodSetValue.text.toString(),
                        )
                        "hookXWebPreferences" -> listOf(
                            "hookXWebPreferences",
                            viewBinding.ruleHookXWebPreferencesClassXWebPreferences.text.toString(),
                            viewBinding.ruleHookXWebPreferencesMethodSetValue.text.toString(),
                        )
                        else -> {
                            Log.e(BuildConfig.APPLICATION_ID, getString(R.string.unknown_hook_method))
                            emptyList()
                        }
                    })
                }
                finishAfterTransition()
            }
        }
        viewBinding.ruleHookMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, it: View?, p: Int, id: Long) {
                when(viewBinding.ruleHookMethod.adapter.getItem(p) as String) {
                    // TODO: 添加更多 hook 方法
                    "hookWebView" -> {
                        viewBinding.ruleHookWebView.visibility = View.VISIBLE
                        viewBinding.ruleHookWebViewClient.visibility = View.GONE
                        viewBinding.ruleReplaceNebulaUCSDK.visibility = View.GONE
                        viewBinding.ruleHookCrossWalk.visibility = View.GONE
                        viewBinding.ruleHookXWebPreferences.visibility = View.GONE
                        if (viewBinding.ruleHookWebViewClassWebView.text!!.isEmpty()) viewBinding.ruleHookWebViewClassWebView.setText("android.webkit.WebView")
                        if (viewBinding.ruleHookWebViewMethodGetSettings.text!!.isEmpty()) viewBinding.ruleHookWebViewMethodGetSettings.setText("getSettings")
                        if (viewBinding.ruleHookWebViewMethodSetWebContentsDebuggingEnabled.text!!.isEmpty()) viewBinding.ruleHookWebViewMethodSetWebContentsDebuggingEnabled.setText("setWebContentsDebuggingEnabled")
                        if (viewBinding.ruleHookWebViewMethodSetJavaScriptEnabled.text!!.isEmpty()) viewBinding.ruleHookWebViewMethodSetJavaScriptEnabled.setText("setJavaScriptEnabled")
                        if (viewBinding.ruleHookWebViewMethodLoadUrl.text!!.isEmpty()) viewBinding.ruleHookWebViewMethodLoadUrl.setText("loadUrl")
                        if (viewBinding.ruleHookWebViewMethodSetWebViewClient.text!!.isEmpty()) viewBinding.ruleHookWebViewMethodSetWebViewClient.setText("setWebViewClient")
                    }
                    "hookWebViewClient" -> {
                        viewBinding.ruleHookWebView.visibility = View.GONE
                        viewBinding.ruleHookWebViewClient.visibility = View.VISIBLE
                        viewBinding.ruleReplaceNebulaUCSDK.visibility = View.GONE
                        viewBinding.ruleHookCrossWalk.visibility = View.GONE
                        viewBinding.ruleHookXWebPreferences.visibility = View.GONE
                        if (viewBinding.ruleHookWebViewClientClassWebView.text!!.isEmpty()) viewBinding.ruleHookWebViewClientClassWebView.setText("android.webkit.WebView")
                        if (viewBinding.ruleHookWebViewClientClassWebViewClient.text!!.isEmpty()) viewBinding.ruleHookWebViewClientClassWebViewClient.setText("android.webkit.WebViewClient")
                        if (viewBinding.ruleHookWebViewClientMethodOnPageFinished.text!!.isEmpty()) viewBinding.ruleHookWebViewClientMethodOnPageFinished.setText("onPageFinished")
                        if (viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.text!!.isEmpty()) viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.setText("evaluateJavascript")
                        if (viewBinding.ruleHookWebViewClientClassValueCallback.text!!.isEmpty()) viewBinding.ruleHookWebViewClientClassValueCallback.setText("android.webkit.ValueCallback")
                    }
                    "replaceNebulaUCSDK" -> {
                        viewBinding.ruleHookWebView.visibility = View.GONE
                        viewBinding.ruleHookWebViewClient.visibility = View.GONE
                        viewBinding.ruleReplaceNebulaUCSDK.visibility = View.VISIBLE
                        viewBinding.ruleHookCrossWalk.visibility = View.GONE
                        viewBinding.ruleHookXWebPreferences.visibility = View.GONE
                        if (viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.text!!.isEmpty()) viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.setText("com.alipay.mobile.nebulauc.impl.UcServiceSetup")
                        if (viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.text!!.isEmpty()) viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.setText("updateUCVersionAndSdcardPath")
                        if (viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.text!!.isEmpty()) viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.setText("sInitUcFromSdcardPath")
                    }
                    "hookCrossWalk" -> {
                        viewBinding.ruleHookWebView.visibility = View.GONE
                        viewBinding.ruleHookWebViewClient.visibility = View.GONE
                        viewBinding.ruleReplaceNebulaUCSDK.visibility = View.GONE
                        viewBinding.ruleHookCrossWalk.visibility = View.VISIBLE
                        viewBinding.ruleHookXWebPreferences.visibility = View.GONE
                        if (viewBinding.ruleHookCrossWalkClassXWalkView.text!!.isEmpty()) viewBinding.ruleHookCrossWalkClassXWalkView.setText("org.xwalk.core.XWalkView")
                        if (viewBinding.ruleHookCrossWalkMethodGetSettings.text!!.isEmpty()) viewBinding.ruleHookCrossWalkMethodGetSettings.setText("getSettings")
                        if (viewBinding.ruleHookCrossWalkMethodSetJavaScriptEnabled.text!!.isEmpty()) viewBinding.ruleHookCrossWalkMethodSetJavaScriptEnabled.setText("setJavaScriptEnabled")
                        if (viewBinding.ruleHookCrossWalkMethodLoadUrl.text!!.isEmpty()) viewBinding.ruleHookCrossWalkMethodLoadUrl.setText("loadUrl")
                        if (viewBinding.ruleHookCrossWalkMethodSetResourceClient.text!!.isEmpty()) viewBinding.ruleHookCrossWalkMethodSetResourceClient.setText("setResourceClient")
                        if (viewBinding.ruleHookCrossWalkClassXWalkPreferences.text!!.isEmpty()) viewBinding.ruleHookCrossWalkClassXWalkPreferences.setText("org.xwalk.core.XWalkPreferences")
                        if (viewBinding.ruleHookCrossWalkMethodSetValue.text!!.isEmpty()) viewBinding.ruleHookCrossWalkMethodSetValue.setText("setValue")
                    }
                    "hookXWebPreferences" -> {
                        viewBinding.ruleHookWebView.visibility = View.GONE
                        viewBinding.ruleHookWebViewClient.visibility = View.GONE
                        viewBinding.ruleReplaceNebulaUCSDK.visibility = View.GONE
                        viewBinding.ruleHookCrossWalk.visibility = View.GONE
                        viewBinding.ruleHookXWebPreferences.visibility = View.VISIBLE
                        if (viewBinding.ruleHookXWebPreferencesClassXWebPreferences.text!!.isEmpty()) viewBinding.ruleHookXWebPreferencesClassXWebPreferences.setText("org.xwalk.core.XWalkPreferences")
                        if (viewBinding.ruleHookXWebPreferencesMethodSetValue.text!!.isEmpty()) viewBinding.ruleHookXWebPreferencesMethodSetValue.setText("setValue")
                    }
                    else -> {
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.unknown_hook_method))
                    }
                }
                refreshCode()
            }
        }
        viewBinding.ruleName.doAfterTextChanged {
            refreshCode()
        }
        // TODO: 添加更多 hook 方法
        viewBinding.ruleHookWebViewClassWebView.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookWebViewMethodGetSettings.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookWebViewMethodSetWebContentsDebuggingEnabled.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookWebViewMethodSetJavaScriptEnabled.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookWebViewMethodLoadUrl.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookWebViewMethodSetWebViewClient.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookWebViewClientClassWebView.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookWebViewClientClassWebViewClient.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookWebViewClientMethodOnPageFinished.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookWebViewClientClassValueCallback.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookCrossWalkClassXWalkView.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookCrossWalkMethodGetSettings.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookCrossWalkMethodSetJavaScriptEnabled.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookCrossWalkMethodLoadUrl.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookCrossWalkMethodSetResourceClient.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookCrossWalkClassXWalkPreferences.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookCrossWalkMethodSetValue.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookXWebPreferencesClassXWebPreferences.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookXWebPreferencesMethodSetValue.doAfterTextChanged {
            refreshCode()
        }
        refresh()
    }

    private fun refresh() {
        ruleName = intent.getStringExtra("rule_name")
        if (ruleName == null) {
            viewBinding.ruleHookMethod.setSelection(0)
        } else {
            viewBinding.ruleName.setText(ruleName)
            modulePrefs("apps_$pkg").run {
                val hookEntry = getList<String>("hook_entry_$ruleName")
                when (hookEntry[0]) {
                    // TODO: 添加更多 hook 方法
                    "hookWebView" -> {
                        viewBinding.ruleHookWebViewClassWebView.setText(hookEntry[1])
                        viewBinding.ruleHookWebViewMethodGetSettings.setText(hookEntry[2])
                        viewBinding.ruleHookWebViewMethodSetWebContentsDebuggingEnabled.setText(hookEntry[3])
                        viewBinding.ruleHookWebViewMethodSetJavaScriptEnabled.setText(hookEntry[4])
                        viewBinding.ruleHookWebViewMethodLoadUrl.setText(hookEntry[5])
                        viewBinding.ruleHookWebViewMethodSetWebViewClient.setText(hookEntry[6])
                        viewBinding.ruleHookMethod.setSelection(0)
                    }
                    "hookWebViewClient" -> {
                        viewBinding.ruleHookWebViewClientClassWebView.setText(hookEntry[1])
                        viewBinding.ruleHookWebViewClientClassWebViewClient.setText(hookEntry[2])
                        viewBinding.ruleHookWebViewClientMethodOnPageFinished.setText(hookEntry[3])
                        viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.setText(hookEntry[4])
                        viewBinding.ruleHookWebViewClientClassValueCallback.setText(hookEntry[5])
                        viewBinding.ruleHookMethod.setSelection(1)
                    }
                    "replaceNebulaUCSDK" -> {
                        viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.setText(hookEntry[1])
                        viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.setText(hookEntry[2])
                        viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.setText(hookEntry[3])
                        viewBinding.ruleHookMethod.setSelection(2)
                    }
                    "hookCrossWalk" -> {
                        viewBinding.ruleHookCrossWalkClassXWalkView.setText(hookEntry[1])
                        viewBinding.ruleHookCrossWalkMethodGetSettings.setText(hookEntry[2])
                        viewBinding.ruleHookCrossWalkMethodSetJavaScriptEnabled.setText(hookEntry[3])
                        viewBinding.ruleHookCrossWalkMethodLoadUrl.setText(hookEntry[4])
                        viewBinding.ruleHookCrossWalkMethodSetResourceClient.setText(hookEntry[5])
                        viewBinding.ruleHookCrossWalkClassXWalkPreferences.setText(hookEntry[6])
                        viewBinding.ruleHookCrossWalkMethodSetValue.setText(hookEntry[7])
                        viewBinding.ruleHookMethod.setSelection(3)
                    }
                    "hookXWebPreferences" -> {
                        viewBinding.ruleHookXWebPreferencesClassXWebPreferences.setText(hookEntry[1])
                        viewBinding.ruleHookXWebPreferencesMethodSetValue.setText(hookEntry[2])
                        viewBinding.ruleHookMethod.setSelection(4)
                    }
                }
            }
        }
    }

    private fun refreshCode() {
        viewBinding.ruleCode.code = when (viewBinding.ruleHookMethod.selectedItem as String) {
            // TODO: 添加更多 hook 方法
            "hookWebView" -> getString(R.string.code_hookWebView).format(
                viewBinding.ruleName.text.toString(),
                viewBinding.ruleHookWebViewClassWebView.text.toString(),
                viewBinding.ruleHookWebViewMethodGetSettings.text.toString(),
                viewBinding.ruleHookWebViewMethodSetWebContentsDebuggingEnabled.text.toString(),
                viewBinding.ruleHookWebViewMethodSetJavaScriptEnabled.text.toString(),
                viewBinding.ruleHookWebViewMethodLoadUrl.text.toString(),
                viewBinding.ruleHookWebViewMethodSetWebViewClient.text.toString(),
            )
            "hookWebViewClient" -> getString(R.string.code_hookWebViewClient).format(
                viewBinding.ruleName.text.toString(),
                viewBinding.ruleHookWebViewClientClassWebView.text.toString(),
                viewBinding.ruleHookWebViewClientClassWebViewClient.text.toString(),
                viewBinding.ruleHookWebViewClientMethodOnPageFinished.text.toString(),
                viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.text.toString(),
                viewBinding.ruleHookWebViewClientClassValueCallback.text.toString(),
            )
            "replaceNebulaUCSDK" -> getString(R.string.code_replaceNebulaUCSDK).format(
                viewBinding.ruleName.text.toString(),
                viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.text.toString(),
                viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.text.toString(),
                viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.text.toString(),
            )
            "hookCrossWalk" -> getString(R.string.code_hookCrossWalk).format(
                viewBinding.ruleName.text.toString(),
                viewBinding.ruleHookCrossWalkClassXWalkView.text.toString(),
                viewBinding.ruleHookCrossWalkMethodGetSettings.text.toString(),
                viewBinding.ruleHookCrossWalkMethodSetJavaScriptEnabled.text.toString(),
                viewBinding.ruleHookCrossWalkMethodLoadUrl.text.toString(),
                viewBinding.ruleHookCrossWalkMethodSetResourceClient.text.toString(),
                viewBinding.ruleHookCrossWalkClassXWalkPreferences.text.toString(),
                viewBinding.ruleHookCrossWalkMethodSetValue.text.toString(),
            )
            "hookXWebPreferences" -> getString(R.string.code_hookXWebPreferences).format(
                viewBinding.ruleName.text.toString(),
                viewBinding.ruleHookXWebPreferencesClassXWebPreferences.text.toString(),
                viewBinding.ruleHookXWebPreferencesMethodSetValue.text.toString(),
            )
            else -> getString(R.string.unknown_hook_method)
        }
    }
}