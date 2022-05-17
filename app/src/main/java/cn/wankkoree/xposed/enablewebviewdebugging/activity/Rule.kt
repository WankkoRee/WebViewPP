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
import cn.wankkoree.xposed.enablewebviewdebugging.http.bean.HookRules
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
        viewBinding.ruleHookMethod.adapter = ArrayAdapter(this@Rule, R.layout.component_spinneritem, arrayOf(
            "hookWebView",
            "hookWebViewClient",
            "replaceNebulaUCSDK",
            "hookCrossWalk",
            "hookXWebPreferences",
            "hookXWebView",
        )).apply {
            setDropDownViewResource(R.layout.component_spinneritem)
        }

        viewBinding.ruleToolbarBack.setOnClickListener {
            finishAfterTransition()
        }
        viewBinding.ruleToolbarCloud.setOnClickListener {
            val dialogBinding = DialogCloudrulesBinding.inflate(layoutInflater)
            AlertDialog.Builder(this@Rule).apply {
                setView(dialogBinding.root)
            }.show().also { dialog ->
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
                                val rules = Gson().fromJson(rulesStr, HookRules::class.java)
                                // TODO: 添加更多 hook 方法
                                if (rules.hookWebView != null) for (hookRule in rules.hookWebView) {
                                    val v = Code(this@Rule)
                                    v.code = getString(R.string.code_hookFunction, if (hookRule.remark != null && hookRule.remark != "") getString(R.string.code_hookRemark, hookRule.remark) else "", hookRule.name, "hookWebView", arrayOf(
                                        getString(R.string.code_hookParam, "Class_WebView", hookRule.Class_WebView),
                                        getString(R.string.code_hookParam, "Method_getSettings", hookRule.Method_getSettings),
                                        getString(R.string.code_hookParam, "Method_setWebContentsDebuggingEnabled", hookRule.Method_setWebContentsDebuggingEnabled),
                                        getString(R.string.code_hookParam, "Method_setJavaScriptEnabled", hookRule.Method_setJavaScriptEnabled),
                                        getString(R.string.code_hookParam, "Method_loadUrl", hookRule.Method_loadUrl),
                                        getString(R.string.code_hookParam, "Method_setWebViewClient", hookRule.Method_setWebViewClient),
                                    ).joinToString(""))
                                    v.isClickable = true
                                    v.setOnClickListener {
                                        viewBinding.ruleHookMethod.setSelection(0)
                                        viewBinding.ruleName.setText(hookRule.name)
                                        viewBinding.ruleRemark.setText(hookRule.remark ?: "")
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
                                    v.code = getString(R.string.code_hookFunction, if (hookRule.remark != null && hookRule.remark != "") getString(R.string.code_hookRemark, hookRule.remark) else "", hookRule.name, "hookWebViewClient", arrayOf(
                                        getString(R.string.code_hookParam, "Class_WebView", hookRule.Class_WebView),
                                        getString(R.string.code_hookParam, "Class_WebViewClient", hookRule.Class_WebViewClient),
                                        getString(R.string.code_hookParam, "Method_onPageFinished", hookRule.Method_onPageFinished),
                                        getString(R.string.code_hookParam, "Method_evaluateJavascript", hookRule.Method_evaluateJavascript),
                                        getString(R.string.code_hookParam, "Class_ValueCallback", hookRule.Class_ValueCallback),
                                    ).joinToString(""))
                                    v.isClickable = true
                                    v.setOnClickListener {
                                        viewBinding.ruleHookMethod.setSelection(1)
                                        viewBinding.ruleName.setText(hookRule.name)
                                        viewBinding.ruleRemark.setText(hookRule.remark ?: "")
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
                                    v.code = getString(R.string.code_hookFunction, if (hookRule.remark != null && hookRule.remark != "") getString(R.string.code_hookRemark, hookRule.remark) else "", hookRule.name, "replaceNebulaUCSDK", arrayOf(
                                        getString(R.string.code_hookParam, "Class_UcServiceSetup", hookRule.Class_UcServiceSetup),
                                        getString(R.string.code_hookParam, "Method_updateUCVersionAndSdcardPath", hookRule.Method_updateUCVersionAndSdcardPath),
                                        getString(R.string.code_hookParam, "Field_sInitUcFromSdcardPath", hookRule.Field_sInitUcFromSdcardPath),
                                    ).joinToString(""))
                                    v.isClickable = true
                                    v.setOnClickListener {
                                        viewBinding.ruleHookMethod.setSelection(2)
                                        viewBinding.ruleName.setText(hookRule.name)
                                        viewBinding.ruleRemark.setText(hookRule.remark ?: "")
                                        viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.setText(hookRule.Class_UcServiceSetup)
                                        viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.setText(hookRule.Method_updateUCVersionAndSdcardPath)
                                        viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.setText(hookRule.Field_sInitUcFromSdcardPath)
                                        dialog.cancel()
                                    }
                                    dialogBinding.dialogCloudrulesRules.addView(v)
                                }
                                if (rules.hookCrossWalk != null) for (hookRule in rules.hookCrossWalk) {
                                    val v = Code(this@Rule)
                                    v.code = getString(R.string.code_hookFunction, if (hookRule.remark != null && hookRule.remark != "") getString(R.string.code_hookRemark, hookRule.remark) else "", hookRule.name, "hookCrossWalk", arrayOf(
                                        getString(R.string.code_hookParam, "Class_XWalkView", hookRule.Class_XWalkView),
                                        getString(R.string.code_hookParam, "Method_getSettings", hookRule.Method_getSettings),
                                        getString(R.string.code_hookParam, "Method_setJavaScriptEnabled", hookRule.Method_setJavaScriptEnabled),
                                        getString(R.string.code_hookParam, "Method_loadUrl", hookRule.Method_loadUrl),
                                        getString(R.string.code_hookParam, "Method_setResourceClient", hookRule.Method_setResourceClient),
                                        getString(R.string.code_hookParam, "Class_XWalkPreferences", hookRule.Class_XWalkPreferences),
                                        getString(R.string.code_hookParam, "Method_setValue", hookRule.Method_setValue),
                                    ).joinToString(""))
                                    v.isClickable = true
                                    v.setOnClickListener {
                                        viewBinding.ruleHookMethod.setSelection(3)
                                        viewBinding.ruleName.setText(hookRule.name)
                                        viewBinding.ruleRemark.setText(hookRule.remark ?: "")
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
                                    v.code = getString(R.string.code_hookFunction, if (hookRule.remark != null && hookRule.remark != "") getString(R.string.code_hookRemark, hookRule.remark) else "", hookRule.name, "hookXWebPreferences", arrayOf(
                                        getString(R.string.code_hookParam, "Class_XWebPreferences",  hookRule.Class_XWebPreferences),
                                        getString(R.string.code_hookParam, "Method_setValue",  hookRule.Method_setValue),
                                    ).joinToString(""))
                                    v.isClickable = true
                                    v.setOnClickListener {
                                        viewBinding.ruleHookMethod.setSelection(4)
                                        viewBinding.ruleName.setText(hookRule.name)
                                        viewBinding.ruleRemark.setText(hookRule.remark ?: "")
                                        viewBinding.ruleHookXWebPreferencesClassXWebPreferences.setText(hookRule.Class_XWebPreferences)
                                        viewBinding.ruleHookXWebPreferencesMethodSetValue.setText(hookRule.Method_setValue)
                                        dialog.cancel()
                                    }
                                    dialogBinding.dialogCloudrulesRules.addView(v)
                                }
                                if (rules.hookXWebView != null) for (hookRule in rules.hookXWebView) {
                                    val v = Code(this@Rule)
                                    v.code = getString(R.string.code_hookFunction, if (hookRule.remark != null && hookRule.remark != "") getString(R.string.code_hookRemark, hookRule.remark) else "", hookRule.name, "hookXWebView", arrayOf(
                                        getString(R.string.code_hookParam, "Class_XWebView", hookRule.Class_XWebView),
                                        getString(R.string.code_hookParam, "Method_initWebviewCoreInternal", hookRule.Method_initWebviewCoreInternal),
                                        getString(R.string.code_hookParam, "Method_isXWalk", hookRule.Method_isXWalk),
                                        getString(R.string.code_hookParam, "Method_isPinus", hookRule.Method_isPinus),
                                        getString(R.string.code_hookParam, "Method_isX5", hookRule.Method_isX5),
                                        getString(R.string.code_hookParam, "Method_isSys", hookRule.Method_isSys),
                                    ).joinToString(""))
                                    v.isClickable = true
                                    v.setOnClickListener {
                                        viewBinding.ruleHookMethod.setSelection(5)
                                        viewBinding.ruleName.setText(hookRule.name)
                                        viewBinding.ruleRemark.setText(hookRule.remark ?: "")
                                        viewBinding.ruleHookXWebViewClassClassXWebView.setText(hookRule.Class_XWebView)
                                        viewBinding.ruleHookXWebViewMethodInitWebviewCoreInternal.setText(hookRule.Method_initWebviewCoreInternal)
                                        viewBinding.ruleHookXWebViewMethodIsXWalk.setText(hookRule.Method_isXWalk)
                                        viewBinding.ruleHookXWebViewMethodIsPinus.setText(hookRule.Method_isPinus)
                                        viewBinding.ruleHookXWebViewMethodIsX5.setText(hookRule.Method_isX5)
                                        viewBinding.ruleHookXWebViewMethodIsSys.setText(hookRule.Method_isSys)
                                        dialog.cancel()
                                    }
                                    dialogBinding.dialogCloudrulesRules.addView(v)
                                }
                            } else {
                                toast?.cancel()
                                toast = Toast.makeText(this@Rule, getString(R.string.pull_failed).format(pkg+' '+version+' '+getString(R.string.cloud_rules))+'\n'+getString(R.string.please_set_custom_hook_rules_then_push_rules_to_rules_repos), Toast.LENGTH_SHORT)
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
                        val adapter = ArrayAdapter(dialog.context, R.layout.component_spinneritem, versions.map{ it.name.substringBeforeLast(".json") })
                        adapter.setDropDownViewResource(R.layout.component_spinneritem)
                        dialogBinding.dialogCloudrulesVersions.adapter = adapter
                        val p = adapter.getPosition(version)
                        if (p >= 0)
                            dialogBinding.dialogCloudrulesVersions.setSelection(p)
                        else {
                            dialogBinding.dialogCloudrulesVersions.setSelection(0)
                            toast?.cancel()
                            toast = Toast.makeText(this@Rule, getString(R.string.no_matching_version), Toast.LENGTH_SHORT)
                            toast!!.show()
                        }
                    } else {
                        toast?.cancel()
                        toast = Toast.makeText(this@Rule, getString(R.string.pull_failed).format(pkg+' '+getString(R.string.cloud_rules))+'\n'+getString(R.string.please_set_custom_hook_rules_then_push_rules_to_rules_repos), Toast.LENGTH_SHORT)
                        toast!!.show()
                        dialog.cancel()
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
            } else if (name.contains('|')) {
                toast?.cancel()
                toast = Toast.makeText(this@Rule, getString(R.string.s_cannot_contains_vertical).format(getString(R.string.rule_name)), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            } else {
                with(modulePrefs("apps_$pkg")) {
                    try {
                        put(AppSP.hooks, name)
                    } catch (_: ValueAlreadyExistedInSet) {
                        if (ruleName == null || ruleName != name) { // 新建 or 修改名称
                            toast?.cancel()
                            toast = Toast.makeText(this@Rule, getString(R.string.s_already_exists).format(getString(R.string.rule_name) + """ "$name" """), Toast.LENGTH_SHORT)
                            toast!!.show()
                            return@setOnClickListener
                        }
                    }
                    if (ruleName != null && ruleName != name) { // 修改名称
                        remove(AppSP.hooks, ruleName!!)
                        remove("hook_entry_${ruleName!!}")
                    }
                    putString("hook_entry_$name", when (viewBinding.ruleHookMethod.selectedItem as String) {
                        // TODO: 添加更多 hook 方法
                        "hookWebView" -> Gson().toJson(
                            HookRules.HookRuleWebView(
                                "hookWebView",
                                viewBinding.ruleRemark.text.toString(),
                                viewBinding.ruleHookWebViewClassWebView.text.toString(),
                                viewBinding.ruleHookWebViewMethodGetSettings.text.toString(),
                                viewBinding.ruleHookWebViewMethodSetWebContentsDebuggingEnabled.text.toString(),
                                viewBinding.ruleHookWebViewMethodSetJavaScriptEnabled.text.toString(),
                                viewBinding.ruleHookWebViewMethodLoadUrl.text.toString(),
                                viewBinding.ruleHookWebViewMethodSetWebViewClient.text.toString(),
                            )
                        )
                        "hookWebViewClient" -> Gson().toJson(
                            HookRules.HookRuleWebViewClient(
                                "hookWebViewClient",
                                viewBinding.ruleRemark.text.toString(),
                                viewBinding.ruleHookWebViewClientClassWebView.text.toString(),
                                viewBinding.ruleHookWebViewClientClassWebViewClient.text.toString(),
                                viewBinding.ruleHookWebViewClientMethodOnPageFinished.text.toString(),
                                viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.text.toString(),
                                viewBinding.ruleHookWebViewClientClassValueCallback.text.toString(),
                            )
                        )
                        "replaceNebulaUCSDK" -> Gson().toJson(
                            HookRules.ReplaceNebulaUCSDK(
                                "replaceNebulaUCSDK",
                                viewBinding.ruleRemark.text.toString(),
                                viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.text.toString(),
                                viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.text.toString(),
                                viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.text.toString(),
                            )
                        )
                        "hookCrossWalk" -> Gson().toJson(
                            HookRules.HookCrossWalk(
                                "hookCrossWalk",
                                viewBinding.ruleRemark.text.toString(),
                                viewBinding.ruleHookCrossWalkClassXWalkView.text.toString(),
                                viewBinding.ruleHookCrossWalkMethodGetSettings.text.toString(),
                                viewBinding.ruleHookCrossWalkMethodSetJavaScriptEnabled.text.toString(),
                                viewBinding.ruleHookCrossWalkMethodLoadUrl.text.toString(),
                                viewBinding.ruleHookCrossWalkMethodSetResourceClient.text.toString(),
                                viewBinding.ruleHookCrossWalkClassXWalkPreferences.text.toString(),
                                viewBinding.ruleHookCrossWalkMethodSetValue.text.toString(),
                            )
                        )
                        "hookXWebPreferences" -> Gson().toJson(
                            HookRules.HookXWebPreferences(
                                "hookXWebPreferences",
                                viewBinding.ruleRemark.text.toString(),
                                viewBinding.ruleHookXWebPreferencesClassXWebPreferences.text.toString(),
                                viewBinding.ruleHookXWebPreferencesMethodSetValue.text.toString(),
                            )
                        )
                        "hookXWebView" -> Gson().toJson(
                            HookRules.HookXWebView(
                                "hookXWebView",
                                viewBinding.ruleRemark.text.toString(),
                                viewBinding.ruleHookXWebViewClassClassXWebView.text.toString(),
                                viewBinding.ruleHookXWebViewMethodInitWebviewCoreInternal.text.toString(),
                                viewBinding.ruleHookXWebViewMethodIsXWalk.text.toString(),
                                viewBinding.ruleHookXWebViewMethodIsPinus.text.toString(),
                                viewBinding.ruleHookXWebViewMethodIsX5.text.toString(),
                                viewBinding.ruleHookXWebViewMethodIsSys.text.toString(),
                            )
                        )
                        else -> {
                            Log.e(BuildConfig.APPLICATION_ID, getString(R.string.unknown_hook_method))
                            "{}"
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
                        viewBinding.ruleHookXWebView.visibility = View.GONE
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
                        viewBinding.ruleHookXWebView.visibility = View.GONE
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
                        viewBinding.ruleHookXWebView.visibility = View.GONE
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
                        viewBinding.ruleHookXWebView.visibility = View.GONE
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
                        viewBinding.ruleHookXWebView.visibility = View.GONE
                        if (viewBinding.ruleHookXWebPreferencesClassXWebPreferences.text!!.isEmpty()) viewBinding.ruleHookXWebPreferencesClassXWebPreferences.setText("org.xwalk.core.XWalkPreferences")
                        if (viewBinding.ruleHookXWebPreferencesMethodSetValue.text!!.isEmpty()) viewBinding.ruleHookXWebPreferencesMethodSetValue.setText("setValue")
                    }
                    "hookXWebView" -> {
                        viewBinding.ruleHookWebView.visibility = View.GONE
                        viewBinding.ruleHookWebViewClient.visibility = View.GONE
                        viewBinding.ruleReplaceNebulaUCSDK.visibility = View.GONE
                        viewBinding.ruleHookCrossWalk.visibility = View.GONE
                        viewBinding.ruleHookXWebPreferences.visibility = View.GONE
                        viewBinding.ruleHookXWebView.visibility = View.VISIBLE
                        if (viewBinding.ruleHookXWebViewClassClassXWebView.text!!.isEmpty()) viewBinding.ruleHookXWebViewClassClassXWebView.setText("com.tencent.xweb.WebView")
                        if (viewBinding.ruleHookXWebViewMethodInitWebviewCoreInternal.text!!.isEmpty()) viewBinding.ruleHookXWebViewMethodInitWebviewCoreInternal.setText("initWebviewCoreInternal")
                        if (viewBinding.ruleHookXWebViewMethodIsXWalk.text!!.isEmpty()) viewBinding.ruleHookXWebViewMethodIsXWalk.setText("isXWalk")
                        if (viewBinding.ruleHookXWebViewMethodIsPinus.text!!.isEmpty()) viewBinding.ruleHookXWebViewMethodIsPinus.setText("isPinus")
                        if (viewBinding.ruleHookXWebViewMethodIsX5.text!!.isEmpty()) viewBinding.ruleHookXWebViewMethodIsX5.setText("isX5")
                        if (viewBinding.ruleHookXWebViewMethodIsSys.text!!.isEmpty()) viewBinding.ruleHookXWebViewMethodIsSys.setText("isSys")
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
        viewBinding.ruleRemark.doAfterTextChanged {
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
        viewBinding.ruleHookXWebViewClassClassXWebView.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookXWebViewMethodInitWebviewCoreInternal.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookXWebViewMethodIsXWalk.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookXWebViewMethodIsPinus.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookXWebViewMethodIsX5.doAfterTextChanged {
            refreshCode()
        }
        viewBinding.ruleHookXWebViewMethodIsSys.doAfterTextChanged {
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
            val hookJson = modulePrefs("apps_$pkg").getString("hook_entry_$ruleName", "{}")
            try {
                when(Gson().fromJson(hookJson, HookRules.HookRule::class.java).name) {
                    // TODO: 添加更多 hook 方法
                    "hookWebView" -> {
                        val hookEntry = Gson().fromJson(hookJson, HookRules.HookRuleWebView::class.java)
                        viewBinding.ruleRemark.setText(hookEntry.remark)
                        viewBinding.ruleHookWebViewClassWebView.setText(hookEntry.Class_WebView)
                        viewBinding.ruleHookWebViewMethodGetSettings.setText(hookEntry.Method_getSettings)
                        viewBinding.ruleHookWebViewMethodSetWebContentsDebuggingEnabled.setText(hookEntry.Method_setWebContentsDebuggingEnabled)
                        viewBinding.ruleHookWebViewMethodSetJavaScriptEnabled.setText(hookEntry.Method_setJavaScriptEnabled)
                        viewBinding.ruleHookWebViewMethodLoadUrl.setText(hookEntry.Method_loadUrl)
                        viewBinding.ruleHookWebViewMethodSetWebViewClient.setText(hookEntry.Method_setWebViewClient)
                        viewBinding.ruleHookMethod.setSelection(0)
                    }
                    "hookWebViewClient" -> {
                        val hookEntry = Gson().fromJson(hookJson, HookRules.HookRuleWebViewClient::class.java)
                        viewBinding.ruleRemark.setText(hookEntry.remark)
                        viewBinding.ruleHookWebViewClientClassWebView.setText(hookEntry.Class_WebView)
                        viewBinding.ruleHookWebViewClientClassWebViewClient.setText(hookEntry.Class_WebViewClient)
                        viewBinding.ruleHookWebViewClientMethodOnPageFinished.setText(hookEntry.Method_onPageFinished)
                        viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.setText(hookEntry.Method_evaluateJavascript)
                        viewBinding.ruleHookWebViewClientClassValueCallback.setText(hookEntry.Class_ValueCallback)
                        viewBinding.ruleHookMethod.setSelection(1)
                    }
                    "replaceNebulaUCSDK" -> {
                        val hookEntry = Gson().fromJson(hookJson, HookRules.ReplaceNebulaUCSDK::class.java)
                        viewBinding.ruleRemark.setText(hookEntry.remark)
                        viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.setText(hookEntry.Class_UcServiceSetup)
                        viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.setText(hookEntry.Method_updateUCVersionAndSdcardPath)
                        viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.setText(hookEntry.Field_sInitUcFromSdcardPath)
                        viewBinding.ruleHookMethod.setSelection(2)
                    }
                    "hookCrossWalk" -> {
                        val hookEntry = Gson().fromJson(hookJson, HookRules.HookCrossWalk::class.java)
                        viewBinding.ruleRemark.setText(hookEntry.remark)
                        viewBinding.ruleHookCrossWalkClassXWalkView.setText(hookEntry.Class_XWalkView)
                        viewBinding.ruleHookCrossWalkMethodGetSettings.setText(hookEntry.Method_getSettings)
                        viewBinding.ruleHookCrossWalkMethodSetJavaScriptEnabled.setText(hookEntry.Method_setJavaScriptEnabled)
                        viewBinding.ruleHookCrossWalkMethodLoadUrl.setText(hookEntry.Method_loadUrl)
                        viewBinding.ruleHookCrossWalkMethodSetResourceClient.setText(hookEntry.Method_setResourceClient)
                        viewBinding.ruleHookCrossWalkClassXWalkPreferences.setText(hookEntry.Class_XWalkPreferences)
                        viewBinding.ruleHookCrossWalkMethodSetValue.setText(hookEntry.Method_setValue)
                        viewBinding.ruleHookMethod.setSelection(3)
                    }
                    "hookXWebPreferences" -> {
                        val hookEntry = Gson().fromJson(hookJson, HookRules.HookXWebPreferences::class.java)
                        viewBinding.ruleRemark.setText(hookEntry.remark)
                        viewBinding.ruleHookXWebPreferencesClassXWebPreferences.setText(hookEntry.Class_XWebPreferences)
                        viewBinding.ruleHookXWebPreferencesMethodSetValue.setText(hookEntry.Method_setValue)
                        viewBinding.ruleHookMethod.setSelection(4)
                    }
                    "hookXWebView" -> {
                        val hookEntry = Gson().fromJson(hookJson, HookRules.HookXWebView::class.java)
                        viewBinding.ruleRemark.setText(hookEntry.remark)
                        viewBinding.ruleHookXWebViewClassClassXWebView.setText(hookEntry.Class_XWebView)
                        viewBinding.ruleHookXWebViewMethodInitWebviewCoreInternal.setText(hookEntry.Method_initWebviewCoreInternal)
                        viewBinding.ruleHookXWebViewMethodIsXWalk.setText(hookEntry.Method_isXWalk)
                        viewBinding.ruleHookXWebViewMethodIsPinus.setText(hookEntry.Method_isPinus)
                        viewBinding.ruleHookXWebViewMethodIsX5.setText(hookEntry.Method_isX5)
                        viewBinding.ruleHookXWebViewMethodIsSys.setText(hookEntry.Method_isSys)
                        viewBinding.ruleHookMethod.setSelection(5)
                    }
                }
            } catch (e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.parse_failed), e)
                toast?.cancel()
                toast = Toast.makeText(this@Rule, getString(R.string.parse_failed), Toast.LENGTH_SHORT)
                toast!!.show()
                return
            }
        }
    }

    private fun refreshCode() {
        viewBinding.ruleCode.code = when (viewBinding.ruleHookMethod.selectedItem as String) {
            // TODO: 添加更多 hook 方法
            "hookWebView" -> getString(R.string.code_hookFunction, if (viewBinding.ruleRemark.text.toString() != "") getString(R.string.code_hookRemark, viewBinding.ruleRemark.text.toString()) else "", viewBinding.ruleName.text.toString(), "hookWebView", arrayOf(
                getString(R.string.code_hookParam, "Class_WebView", viewBinding.ruleHookWebViewClassWebView.text.toString()),
                getString(R.string.code_hookParam, "Method_getSettings", viewBinding.ruleHookWebViewMethodGetSettings.text.toString()),
                getString(R.string.code_hookParam, "Method_setWebContentsDebuggingEnabled", viewBinding.ruleHookWebViewMethodSetWebContentsDebuggingEnabled.text.toString()),
                getString(R.string.code_hookParam, "Method_setJavaScriptEnabled", viewBinding.ruleHookWebViewMethodSetJavaScriptEnabled.text.toString()),
                getString(R.string.code_hookParam, "Method_loadUrl", viewBinding.ruleHookWebViewMethodLoadUrl.text.toString()),
                getString(R.string.code_hookParam, "Method_setWebViewClient", viewBinding.ruleHookWebViewMethodSetWebViewClient.text.toString()),
            ).joinToString(""))
            "hookWebViewClient" -> getString(R.string.code_hookFunction, if (viewBinding.ruleRemark.text.toString() != "") getString(R.string.code_hookRemark, viewBinding.ruleRemark.text.toString()) else "", viewBinding.ruleName.text.toString(), "hookWebViewClient", arrayOf(
                getString(R.string.code_hookParam, "Class_WebView", viewBinding.ruleHookWebViewClientClassWebView.text.toString()),
                getString(R.string.code_hookParam, "Class_WebViewClient", viewBinding.ruleHookWebViewClientClassWebViewClient.text.toString()),
                getString(R.string.code_hookParam, "Method_onPageFinished", viewBinding.ruleHookWebViewClientMethodOnPageFinished.text.toString()),
                getString(R.string.code_hookParam, "Method_evaluateJavascript", viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.text.toString()),
                getString(R.string.code_hookParam, "Class_ValueCallback", viewBinding.ruleHookWebViewClientClassValueCallback.text.toString()),
            ).joinToString(""))
            "replaceNebulaUCSDK" -> getString(R.string.code_hookFunction, if (viewBinding.ruleRemark.text.toString() != "") getString(R.string.code_hookRemark, viewBinding.ruleRemark.text.toString()) else "", viewBinding.ruleName.text.toString(), "replaceNebulaUCSDK", arrayOf(
                getString(R.string.code_hookParam, "Class_UcServiceSetup", viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.text.toString()),
                getString(R.string.code_hookParam, "Method_updateUCVersionAndSdcardPath", viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.text.toString()),
                getString(R.string.code_hookParam, "Field_sInitUcFromSdcardPath", viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.text.toString()),
            ).joinToString(""))
            "hookCrossWalk" -> getString(R.string.code_hookFunction, if (viewBinding.ruleRemark.text.toString() != "") getString(R.string.code_hookRemark, viewBinding.ruleRemark.text.toString()) else "", viewBinding.ruleName.text.toString(), "hookCrossWalk", arrayOf(
                getString(R.string.code_hookParam, "Class_XWalkView", viewBinding.ruleHookCrossWalkClassXWalkView.text.toString()),
                getString(R.string.code_hookParam, "Method_getSettings", viewBinding.ruleHookCrossWalkMethodGetSettings.text.toString()),
                getString(R.string.code_hookParam, "Method_setJavaScriptEnabled", viewBinding.ruleHookCrossWalkMethodSetJavaScriptEnabled.text.toString()),
                getString(R.string.code_hookParam, "Method_loadUrl", viewBinding.ruleHookCrossWalkMethodLoadUrl.text.toString()),
                getString(R.string.code_hookParam, "Method_setResourceClient", viewBinding.ruleHookCrossWalkMethodSetResourceClient.text.toString()),
                getString(R.string.code_hookParam, "Class_XWalkPreferences", viewBinding.ruleHookCrossWalkClassXWalkPreferences.text.toString()),
                getString(R.string.code_hookParam, "Method_setValue", viewBinding.ruleHookCrossWalkMethodSetValue.text.toString()),
            ).joinToString(""))
            "hookXWebPreferences" -> getString(R.string.code_hookFunction, if (viewBinding.ruleRemark.text.toString() != "") getString(R.string.code_hookRemark, viewBinding.ruleRemark.text.toString()) else "", viewBinding.ruleName.text.toString(), "hookXWebPreferences", arrayOf(
                getString(R.string.code_hookParam, "Class_XWebPreferences", viewBinding.ruleHookXWebPreferencesClassXWebPreferences.text.toString()),
                getString(R.string.code_hookParam, "Method_setValue", viewBinding.ruleHookXWebPreferencesMethodSetValue.text.toString()),
            ).joinToString(""))
            "hookXWebView" -> getString(R.string.code_hookFunction, if (viewBinding.ruleRemark.text.toString() != "") getString(R.string.code_hookRemark, viewBinding.ruleRemark.text.toString()) else "", viewBinding.ruleName.text.toString(), "hookXWebView", arrayOf(
                getString(R.string.code_hookParam, "Class_XWebView", viewBinding.ruleHookXWebViewClassClassXWebView.text.toString()),
                getString(R.string.code_hookParam, "Method_initWebviewCoreInternal", viewBinding.ruleHookXWebViewMethodInitWebviewCoreInternal.text.toString()),
                getString(R.string.code_hookParam, "Method_isXWalk", viewBinding.ruleHookXWebViewMethodIsXWalk.text.toString()),
                getString(R.string.code_hookParam, "Method_isPinus", viewBinding.ruleHookXWebViewMethodIsPinus.text.toString()),
                getString(R.string.code_hookParam, "Method_isX5", viewBinding.ruleHookXWebViewMethodIsX5.text.toString()),
                getString(R.string.code_hookParam, "Method_isSys", viewBinding.ruleHookXWebViewMethodIsSys.text.toString()),
            ).joinToString(""))
            else -> getString(R.string.unknown_hook_method)
        }
    }
}