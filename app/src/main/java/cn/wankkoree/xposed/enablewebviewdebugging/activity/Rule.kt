package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.transition.Slide
import android.util.Log
import android.view.View
import androidx.core.widget.doAfterTextChanged
import cn.wankkoree.xposed.enablewebviewdebugging.BuildConfig
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.ValueAlreadyExistedInSet
import cn.wankkoree.xposed.enablewebviewdebugging.activity.component.Code
import cn.wankkoree.xposed.enablewebviewdebugging.data.ModuleSP
import cn.wankkoree.xposed.enablewebviewdebugging.data.AppSP
import cn.wankkoree.xposed.enablewebviewdebugging.data.put
import cn.wankkoree.xposed.enablewebviewdebugging.data.remove
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.DialogCloudRulesBinding
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.ActivityRuleBinding
import cn.wankkoree.xposed.enablewebviewdebugging.http.bean.HookRules
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.highcapable.yukihookapi.hook.factory.modulePrefs

class Rule : AppCompatActivity() {
    private lateinit var viewBinding: ActivityRuleBinding
    private var toast: Toast? = null

    private lateinit var pkg: String
    private lateinit var version: String
    private var ruleName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.enterTransition = Slide()
        window.exitTransition = Slide()
        viewBinding = ActivityRuleBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        pkg = intent.getStringExtra("pkg")!!
        version = intent.getStringExtra("version")!!

        // TODO: 添加更多 hook 方法
        viewBinding.ruleHookMethod.setSimpleItems(arrayOf(
            "hookWebView",
            "hookWebViewClient",
            "replaceNebulaUCSDK",
            "hookCrossWalk",
            "hookXWebPreferences",
            "hookXWebView",
        ))

        viewBinding.ruleToolbarBack.setOnClickListener {
            finishAfterTransition()
        }
        viewBinding.ruleToolbarCloud.setOnClickListener {
            val dialogBinding = DialogCloudRulesBinding.inflate(layoutInflater)
            MaterialAlertDialogBuilder(this@Rule).apply {
                setTitle(getString(R.string.cloud_rules))
                setView(dialogBinding.root)
            }.show().also { dialog ->
                dialogBinding.dialogCloudRulesVersions.doAfterTextChanged { version ->
                    Fuel.get("${modulePrefs("module").get(ModuleSP.data_source)}/rules/$pkg/$version.json")
                        .responseObject< HookRules> { _, _, result ->
                            result.fold({ rules ->
                                dialogBinding.dialogCloudRulesRules.removeAllViews()

                                // TODO: 添加更多 hook 方法
                                if (rules.hookWebView != null) for (hookRule in rules.hookWebView) {
                                    val v = Code(this@Rule)
                                    v.code = getString(R.string.code_hookFunction, if (hookRule.remark.trim() != "") getString(R.string.code_hookRemark, hookRule.remark.trim()) else "", hookRule.name, "hookWebView", arrayOf(
                                        getString(R.string.code_hookParam, "Class_WebView", hookRule.Class_WebView),
                                        getString(R.string.code_hookParam, "Method_getSettings", hookRule.Method_getSettings),
                                        getString(R.string.code_hookParam, "Method_setWebContentsDebuggingEnabled", hookRule.Method_setWebContentsDebuggingEnabled),
                                        getString(R.string.code_hookParam, "Method_setJavaScriptEnabled", hookRule.Method_setJavaScriptEnabled),
                                        getString(R.string.code_hookParam, "Method_loadUrl", hookRule.Method_loadUrl),
                                        getString(R.string.code_hookParam, "Method_setWebViewClient", hookRule.Method_setWebViewClient),
                                    ).joinToString(""))
                                    v.isClickable = true
                                    v.setOnClickListener {
                                        viewBinding.ruleHookMethod.setText("hookWebView", false)
                                        viewBinding.ruleName.setText(hookRule.name)
                                        viewBinding.ruleRemark.setText((getString(R.string.from, version) + "\n" + hookRule.remark.trim()).trim())
                                        viewBinding.ruleHookWebViewClassWebView.setText(hookRule.Class_WebView)
                                        viewBinding.ruleHookWebViewMethodGetSettings.setText(hookRule.Method_getSettings)
                                        viewBinding.ruleHookWebViewMethodSetWebContentsDebuggingEnabled.setText(hookRule.Method_setWebContentsDebuggingEnabled)
                                        viewBinding.ruleHookWebViewMethodSetJavaScriptEnabled.setText(hookRule.Method_setJavaScriptEnabled)
                                        viewBinding.ruleHookWebViewMethodLoadUrl.setText(hookRule.Method_loadUrl)
                                        viewBinding.ruleHookWebViewMethodSetWebViewClient.setText(hookRule.Method_setWebViewClient)
                                        dialog.cancel()
                                    }
                                    dialogBinding.dialogCloudRulesRules.addView(v)
                                }
                                if (rules.hookWebViewClient != null) for (hookRule in rules.hookWebViewClient) {
                                    val v = Code(this@Rule)
                                    v.code = getString(R.string.code_hookFunction, if (hookRule.remark.trim() != "") getString(R.string.code_hookRemark, hookRule.remark.trim()) else "", hookRule.name, "hookWebViewClient", arrayOf(
                                        getString(R.string.code_hookParam, "Class_WebViewClient", hookRule.Class_WebViewClient),
                                        getString(R.string.code_hookParam, "Method_onPageFinished", hookRule.Method_onPageFinished),
                                        getString(R.string.code_hookParam, "Class_WebView", hookRule.Class_WebView),
                                        getString(R.string.code_hookParam, "Method_evaluateJavascript", hookRule.Method_evaluateJavascript),
                                    ).joinToString(""))
                                    v.isClickable = true
                                    v.setOnClickListener {
                                        viewBinding.ruleHookMethod.setText("hookWebViewClient", false)
                                        viewBinding.ruleName.setText(hookRule.name)
                                        viewBinding.ruleRemark.setText((getString(R.string.from, version) + "\n" + hookRule.remark.trim()).trim())
                                        viewBinding.ruleHookWebViewClientClassWebViewClient.setText(hookRule.Class_WebViewClient)
                                        viewBinding.ruleHookWebViewClientMethodOnPageFinished.setText(hookRule.Method_onPageFinished)
                                        viewBinding.ruleHookWebViewClientClassWebView.setText(hookRule.Class_WebView)
                                        viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.setText(hookRule.Method_evaluateJavascript)
                                        dialog.cancel()
                                    }
                                    dialogBinding.dialogCloudRulesRules.addView(v)
                                }
                                if (rules.replaceNebulaUCSDK != null) for (hookRule in rules.replaceNebulaUCSDK) {
                                    val v = Code(this@Rule)
                                    v.code = getString(R.string.code_hookFunction, if (hookRule.remark.trim() != "") getString(R.string.code_hookRemark, hookRule.remark.trim()) else "", hookRule.name, "replaceNebulaUCSDK", arrayOf(
                                        getString(R.string.code_hookParam, "Class_UcServiceSetup", hookRule.Class_UcServiceSetup),
                                        getString(R.string.code_hookParam, "Method_updateUCVersionAndSdcardPath", hookRule.Method_updateUCVersionAndSdcardPath),
                                        getString(R.string.code_hookParam, "Field_sInitUcFromSdcardPath", hookRule.Field_sInitUcFromSdcardPath),
                                    ).joinToString(""))
                                    v.isClickable = true
                                    v.setOnClickListener {
                                        viewBinding.ruleHookMethod.setText("replaceNebulaUCSDK", false)
                                        viewBinding.ruleName.setText(hookRule.name)
                                        viewBinding.ruleRemark.setText((getString(R.string.from, version) + "\n" + hookRule.remark.trim()).trim())
                                        viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.setText(hookRule.Class_UcServiceSetup)
                                        viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.setText(hookRule.Method_updateUCVersionAndSdcardPath)
                                        viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.setText(hookRule.Field_sInitUcFromSdcardPath)
                                        dialog.cancel()
                                    }
                                    dialogBinding.dialogCloudRulesRules.addView(v)
                                }
                                if (rules.hookCrossWalk != null) for (hookRule in rules.hookCrossWalk) {
                                    val v = Code(this@Rule)
                                    v.code = getString(R.string.code_hookFunction, if (hookRule.remark.trim() != "") getString(R.string.code_hookRemark, hookRule.remark.trim()) else "", hookRule.name, "hookCrossWalk", arrayOf(
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
                                        viewBinding.ruleHookMethod.setText("hookCrossWalk", false)
                                        viewBinding.ruleName.setText(hookRule.name)
                                        viewBinding.ruleRemark.setText((getString(R.string.from, version) + "\n" + hookRule.remark.trim()).trim())
                                        viewBinding.ruleHookCrossWalkClassXWalkView.setText(hookRule.Class_XWalkView)
                                        viewBinding.ruleHookCrossWalkMethodGetSettings.setText(hookRule.Method_getSettings)
                                        viewBinding.ruleHookCrossWalkMethodSetJavaScriptEnabled.setText(hookRule.Method_setJavaScriptEnabled)
                                        viewBinding.ruleHookCrossWalkMethodLoadUrl.setText(hookRule.Method_loadUrl)
                                        viewBinding.ruleHookCrossWalkMethodSetResourceClient.setText(hookRule.Method_setResourceClient)
                                        viewBinding.ruleHookCrossWalkClassXWalkPreferences.setText(hookRule.Class_XWalkPreferences)
                                        viewBinding.ruleHookCrossWalkMethodSetValue.setText(hookRule.Method_setValue)
                                        dialog.cancel()
                                    }
                                    dialogBinding.dialogCloudRulesRules.addView(v)
                                }
                                if (rules.hookXWebPreferences != null) for (hookRule in rules.hookXWebPreferences) {
                                    val v = Code(this@Rule)
                                    v.code = getString(R.string.code_hookFunction, if (hookRule.remark.trim() != "") getString(R.string.code_hookRemark, hookRule.remark.trim()) else "", hookRule.name, "hookXWebPreferences", arrayOf(
                                        getString(R.string.code_hookParam, "Class_XWebPreferences",  hookRule.Class_XWebPreferences),
                                        getString(R.string.code_hookParam, "Method_setValue",  hookRule.Method_setValue),
                                    ).joinToString(""))
                                    v.isClickable = true
                                    v.setOnClickListener {
                                        viewBinding.ruleHookMethod.setText("hookXWebPreferences", false)
                                        viewBinding.ruleName.setText(hookRule.name)
                                        viewBinding.ruleRemark.setText((getString(R.string.from, version) + "\n" + hookRule.remark.trim()).trim())
                                        viewBinding.ruleHookXWebPreferencesClassXWebPreferences.setText(hookRule.Class_XWebPreferences)
                                        viewBinding.ruleHookXWebPreferencesMethodSetValue.setText(hookRule.Method_setValue)
                                        dialog.cancel()
                                    }
                                    dialogBinding.dialogCloudRulesRules.addView(v)
                                }
                                if (rules.hookXWebView != null) for (hookRule in rules.hookXWebView) {
                                    val v = Code(this@Rule)
                                    v.code = getString(R.string.code_hookFunction, if (hookRule.remark.trim() != "") getString(R.string.code_hookRemark, hookRule.remark.trim()) else "", hookRule.name, "hookXWebView", arrayOf(
                                        getString(R.string.code_hookParam, "Class_XWebView", hookRule.Class_XWebView),
                                        getString(R.string.code_hookParam, "Method_initWebviewCore", hookRule.Method_initWebviewCore),
                                        getString(R.string.code_hookParam, "Method_isXWalk", hookRule.Method_isXWalk),
                                        getString(R.string.code_hookParam, "Method_isPinus", hookRule.Method_isPinus),
                                        getString(R.string.code_hookParam, "Method_isX5", hookRule.Method_isX5),
                                        getString(R.string.code_hookParam, "Method_isSys", hookRule.Method_isSys),
                                    ).joinToString(""))
                                    v.isClickable = true
                                    v.setOnClickListener {
                                        viewBinding.ruleHookMethod.setText("hookXWebView", false)
                                        viewBinding.ruleName.setText(hookRule.name)
                                        viewBinding.ruleRemark.setText((getString(R.string.from, version) + "\n" + hookRule.remark.trim()).trim())
                                        viewBinding.ruleHookXWebViewClassClassXWebView.setText(hookRule.Class_XWebView)
                                        viewBinding.ruleHookXWebViewMethodInitWebviewCore.setText(hookRule.Method_initWebviewCore)
                                        viewBinding.ruleHookXWebViewMethodIsXWalk.setText(hookRule.Method_isXWalk)
                                        viewBinding.ruleHookXWebViewMethodIsPinus.setText(hookRule.Method_isPinus)
                                        viewBinding.ruleHookXWebViewMethodIsX5.setText(hookRule.Method_isX5)
                                        viewBinding.ruleHookXWebViewMethodIsSys.setText(hookRule.Method_isSys)
                                        dialog.cancel()
                                    }
                                    dialogBinding.dialogCloudRulesRules.addView(v)
                                }
                            }, { e ->
                                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed, pkg+' '+getString(R.string.cloud_rules)), e)
                                toast?.cancel()
                                toast = Toast.makeText(this@Rule, getString(R.string.pull_failed, pkg+' '+version+' '+getString(R.string.cloud_rules))+'\n'+getString(R.string.please_set_custom_hook_rules_then_push_rules_to_rules_repos), Toast.LENGTH_SHORT)
                                toast!!.show()
                                dialog.cancel()
                            })
                        }
                }
                Fuel.get("${modulePrefs("module").get(ModuleSP.data_source)}/rules/$pkg/metadata.json")
                    .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.Metadata> { _, _, result ->
                        result.fold({ metadata ->
                            dialogBinding.dialogCloudRulesVersions.setSimpleItems(metadata.versions.toTypedArray())
                            val p = metadata.versions.indexOf(version)
                            if (p >= 0)
                                dialogBinding.dialogCloudRulesVersions.setText(version, false)
                            else {
                                toast?.cancel()
                                toast = Toast.makeText(this@Rule, getString(R.string.no_matching_version), Toast.LENGTH_SHORT)
                                toast!!.show()
                            }
                        }, { e ->
                            Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed, pkg+' '+getString(R.string.cloud_rules)), e)
                            toast?.cancel()
                            toast = Toast.makeText(this@Rule, getString(R.string.pull_failed, pkg+' '+getString(R.string.cloud_rules))+'\n'+getString(R.string.please_set_custom_hook_rules_then_push_rules_to_rules_repos), Toast.LENGTH_SHORT)
                            toast!!.show()
                            dialog.cancel()
                        })
                    }
            }
        }
        viewBinding.ruleToolbarSave.setOnClickListener {
            val name = viewBinding.ruleName.text.toString()
            val type = viewBinding.ruleHookMethod.text.toString()
            if (name.isEmpty()) {
                toast?.cancel()
                toast = Toast.makeText(this@Rule, getString(R.string.s_cannot_be_empty, getString(R.string.rule_name)), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            } else if (name.contains('|')) {
                toast?.cancel()
                toast = Toast.makeText(this@Rule, getString(R.string.s_cannot_contains_vertical, getString(R.string.rule_name)), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            } else if (type.isEmpty()) {
                toast?.cancel()
                toast = Toast.makeText(this@Rule, getString(R.string.s_cannot_be_empty, getString(R.string.rule_type)), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            } else {
                with(modulePrefs("apps_$pkg")) {
                    try {
                        put(AppSP.hooks, name)
                    } catch (_: ValueAlreadyExistedInSet) {
                        if (ruleName == null || ruleName != name) { // 新建 or 修改名称
                            toast?.cancel()
                            toast = Toast.makeText(this@Rule, getString(R.string.s_already_exists, getString(R.string.rule_name) + """ "$name" """), Toast.LENGTH_SHORT)
                            toast!!.show()
                            return@setOnClickListener
                        }
                    }
                    if (ruleName != null && ruleName != name) { // 修改名称
                        remove(AppSP.hooks, ruleName!!)
                        remove("hook_entry_${ruleName!!}")
                    }
                    putString("hook_entry_$name", when (type) {
                        // TODO: 添加更多 hook 方法
                        "hookWebView" -> Gson().toJson(
                            HookRules.HookRuleWebView(
                                type,
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
                                type,
                                viewBinding.ruleRemark.text.toString(),
                                viewBinding.ruleHookWebViewClientClassWebViewClient.text.toString(),
                                viewBinding.ruleHookWebViewClientMethodOnPageFinished.text.toString(),
                                viewBinding.ruleHookWebViewClientClassWebView.text.toString(),
                                viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.text.toString(),
                            )
                        )
                        "replaceNebulaUCSDK" -> Gson().toJson(
                            HookRules.ReplaceNebulaUCSDK(
                                type,
                                viewBinding.ruleRemark.text.toString(),
                                viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.text.toString(),
                                viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.text.toString(),
                                viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.text.toString(),
                            )
                        )
                        "hookCrossWalk" -> Gson().toJson(
                            HookRules.HookCrossWalk(
                                type,
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
                                type,
                                viewBinding.ruleRemark.text.toString(),
                                viewBinding.ruleHookXWebPreferencesClassXWebPreferences.text.toString(),
                                viewBinding.ruleHookXWebPreferencesMethodSetValue.text.toString(),
                            )
                        )
                        "hookXWebView" -> Gson().toJson(
                            HookRules.HookXWebView(
                                type,
                                viewBinding.ruleRemark.text.toString(),
                                viewBinding.ruleHookXWebViewClassClassXWebView.text.toString(),
                                viewBinding.ruleHookXWebViewMethodInitWebviewCore.text.toString(),
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
        viewBinding.ruleHookMethod.doAfterTextChanged { hookMethod ->
            when(hookMethod!!.toString()) {
                // TODO: 添加更多 hook 方法
                "hookWebView" -> {
                    viewBinding.ruleHookWebView.visibility = View.VISIBLE
                    viewBinding.ruleHookWebViewClient.visibility = View.GONE
                    viewBinding.ruleReplaceNebulaUCSDK.visibility = View.GONE
                    viewBinding.ruleHookCrossWalk.visibility = View.GONE
                    viewBinding.ruleHookXWebPreferences.visibility = View.GONE
                    viewBinding.ruleHookXWebView.visibility = View.GONE

                    HookRules.HookRuleWebView().also {
                        if (viewBinding.ruleHookWebViewClassWebView.text!!.isEmpty()) viewBinding.ruleHookWebViewClassWebView.setText(it.Class_WebView)
                        if (viewBinding.ruleHookWebViewMethodGetSettings.text!!.isEmpty()) viewBinding.ruleHookWebViewMethodGetSettings.setText(it.Method_getSettings)
                        if (viewBinding.ruleHookWebViewMethodSetWebContentsDebuggingEnabled.text!!.isEmpty()) viewBinding.ruleHookWebViewMethodSetWebContentsDebuggingEnabled.setText(it.Method_setWebContentsDebuggingEnabled)
                        if (viewBinding.ruleHookWebViewMethodSetJavaScriptEnabled.text!!.isEmpty()) viewBinding.ruleHookWebViewMethodSetJavaScriptEnabled.setText(it.Method_setJavaScriptEnabled)
                        if (viewBinding.ruleHookWebViewMethodLoadUrl.text!!.isEmpty()) viewBinding.ruleHookWebViewMethodLoadUrl.setText(it.Method_loadUrl)
                        if (viewBinding.ruleHookWebViewMethodSetWebViewClient.text!!.isEmpty()) viewBinding.ruleHookWebViewMethodSetWebViewClient.setText(it.Method_setWebViewClient)
                    }
                }
                "hookWebViewClient" -> {
                    viewBinding.ruleHookWebView.visibility = View.GONE
                    viewBinding.ruleHookWebViewClient.visibility = View.VISIBLE
                    viewBinding.ruleReplaceNebulaUCSDK.visibility = View.GONE
                    viewBinding.ruleHookCrossWalk.visibility = View.GONE
                    viewBinding.ruleHookXWebPreferences.visibility = View.GONE
                    viewBinding.ruleHookXWebView.visibility = View.GONE

                    HookRules.HookRuleWebViewClient().also {
                        if (viewBinding.ruleHookWebViewClientClassWebViewClient.text!!.isEmpty()) viewBinding.ruleHookWebViewClientClassWebViewClient.setText(it.Class_WebViewClient)
                        if (viewBinding.ruleHookWebViewClientMethodOnPageFinished.text!!.isEmpty()) viewBinding.ruleHookWebViewClientMethodOnPageFinished.setText(it.Method_onPageFinished)
                        if (viewBinding.ruleHookWebViewClientClassWebView.text!!.isEmpty()) viewBinding.ruleHookWebViewClientClassWebView.setText(it.Class_WebView)
                        if (viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.text!!.isEmpty()) viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.setText(it.Method_evaluateJavascript)
                    }
                }
                "replaceNebulaUCSDK" -> {
                    viewBinding.ruleHookWebView.visibility = View.GONE
                    viewBinding.ruleHookWebViewClient.visibility = View.GONE
                    viewBinding.ruleReplaceNebulaUCSDK.visibility = View.VISIBLE
                    viewBinding.ruleHookCrossWalk.visibility = View.GONE
                    viewBinding.ruleHookXWebPreferences.visibility = View.GONE
                    viewBinding.ruleHookXWebView.visibility = View.GONE

                    HookRules.ReplaceNebulaUCSDK().also {
                        if (viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.text!!.isEmpty()) viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.setText(it.Class_UcServiceSetup)
                        if (viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.text!!.isEmpty()) viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.setText(it.Method_updateUCVersionAndSdcardPath)
                        if (viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.text!!.isEmpty()) viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.setText(it.Field_sInitUcFromSdcardPath)
                    }
                }
                "hookCrossWalk" -> {
                    viewBinding.ruleHookWebView.visibility = View.GONE
                    viewBinding.ruleHookWebViewClient.visibility = View.GONE
                    viewBinding.ruleReplaceNebulaUCSDK.visibility = View.GONE
                    viewBinding.ruleHookCrossWalk.visibility = View.VISIBLE
                    viewBinding.ruleHookXWebPreferences.visibility = View.GONE
                    viewBinding.ruleHookXWebView.visibility = View.GONE

                    HookRules.HookCrossWalk().also {
                        if (viewBinding.ruleHookCrossWalkClassXWalkView.text!!.isEmpty()) viewBinding.ruleHookCrossWalkClassXWalkView.setText(it.Class_XWalkView)
                        if (viewBinding.ruleHookCrossWalkMethodGetSettings.text!!.isEmpty()) viewBinding.ruleHookCrossWalkMethodGetSettings.setText(it.Method_getSettings)
                        if (viewBinding.ruleHookCrossWalkMethodSetJavaScriptEnabled.text!!.isEmpty()) viewBinding.ruleHookCrossWalkMethodSetJavaScriptEnabled.setText(it.Method_setJavaScriptEnabled)
                        if (viewBinding.ruleHookCrossWalkMethodLoadUrl.text!!.isEmpty()) viewBinding.ruleHookCrossWalkMethodLoadUrl.setText(it.Method_loadUrl)
                        if (viewBinding.ruleHookCrossWalkMethodSetResourceClient.text!!.isEmpty()) viewBinding.ruleHookCrossWalkMethodSetResourceClient.setText(it.Method_setResourceClient)
                        if (viewBinding.ruleHookCrossWalkClassXWalkPreferences.text!!.isEmpty()) viewBinding.ruleHookCrossWalkClassXWalkPreferences.setText(it.Class_XWalkPreferences)
                        if (viewBinding.ruleHookCrossWalkMethodSetValue.text!!.isEmpty()) viewBinding.ruleHookCrossWalkMethodSetValue.setText(it.Method_setValue)
                    }
                }
                "hookXWebPreferences" -> {
                    viewBinding.ruleHookWebView.visibility = View.GONE
                    viewBinding.ruleHookWebViewClient.visibility = View.GONE
                    viewBinding.ruleReplaceNebulaUCSDK.visibility = View.GONE
                    viewBinding.ruleHookCrossWalk.visibility = View.GONE
                    viewBinding.ruleHookXWebPreferences.visibility = View.VISIBLE
                    viewBinding.ruleHookXWebView.visibility = View.GONE

                    HookRules.HookXWebPreferences().also {
                        if (viewBinding.ruleHookXWebPreferencesClassXWebPreferences.text!!.isEmpty()) viewBinding.ruleHookXWebPreferencesClassXWebPreferences.setText(it.Class_XWebPreferences)
                        if (viewBinding.ruleHookXWebPreferencesMethodSetValue.text!!.isEmpty()) viewBinding.ruleHookXWebPreferencesMethodSetValue.setText(it.Method_setValue)
                    }
                }
                "hookXWebView" -> {
                    viewBinding.ruleHookWebView.visibility = View.GONE
                    viewBinding.ruleHookWebViewClient.visibility = View.GONE
                    viewBinding.ruleReplaceNebulaUCSDK.visibility = View.GONE
                    viewBinding.ruleHookCrossWalk.visibility = View.GONE
                    viewBinding.ruleHookXWebPreferences.visibility = View.GONE
                    viewBinding.ruleHookXWebView.visibility = View.VISIBLE

                    HookRules.HookXWebView().also {
                        if (viewBinding.ruleHookXWebViewClassClassXWebView.text!!.isEmpty()) viewBinding.ruleHookXWebViewClassClassXWebView.setText(it.Class_XWebView)
                        if (viewBinding.ruleHookXWebViewMethodInitWebviewCore.text!!.isEmpty()) viewBinding.ruleHookXWebViewMethodInitWebviewCore.setText(it.Method_initWebviewCore)
                        if (viewBinding.ruleHookXWebViewMethodIsXWalk.text!!.isEmpty()) viewBinding.ruleHookXWebViewMethodIsXWalk.setText(it.Method_isXWalk)
                        if (viewBinding.ruleHookXWebViewMethodIsPinus.text!!.isEmpty()) viewBinding.ruleHookXWebViewMethodIsPinus.setText(it.Method_isPinus)
                        if (viewBinding.ruleHookXWebViewMethodIsX5.text!!.isEmpty()) viewBinding.ruleHookXWebViewMethodIsX5.setText(it.Method_isX5)
                        if (viewBinding.ruleHookXWebViewMethodIsSys.text!!.isEmpty()) viewBinding.ruleHookXWebViewMethodIsSys.setText(it.Method_isSys)
                    }
                }
                else -> {
                    viewBinding.ruleHookWebView.visibility = View.GONE
                    viewBinding.ruleHookWebViewClient.visibility = View.GONE
                    viewBinding.ruleReplaceNebulaUCSDK.visibility = View.GONE
                    viewBinding.ruleHookCrossWalk.visibility = View.GONE
                    viewBinding.ruleHookXWebPreferences.visibility = View.GONE
                    viewBinding.ruleHookXWebView.visibility = View.GONE
                    Log.e(BuildConfig.APPLICATION_ID, getString(R.string.unknown_hook_method))
                }
            }
            refreshCode()
        }
        viewBinding.ruleName.doAfterTextChanged { refreshCode() }
        viewBinding.ruleRemark.doAfterTextChanged { refreshCode() }
        // TODO: 添加更多 hook 方法
        viewBinding.ruleHookWebViewClassWebView.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookWebViewMethodGetSettings.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookWebViewMethodSetWebContentsDebuggingEnabled.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookWebViewMethodSetJavaScriptEnabled.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookWebViewMethodLoadUrl.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookWebViewMethodSetWebViewClient.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookWebViewClientClassWebViewClient.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookWebViewClientMethodOnPageFinished.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookWebViewClientClassWebView.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.doAfterTextChanged { refreshCode() }
        viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.doAfterTextChanged { refreshCode() }
        viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.doAfterTextChanged { refreshCode() }
        viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookCrossWalkClassXWalkView.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookCrossWalkMethodGetSettings.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookCrossWalkMethodSetJavaScriptEnabled.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookCrossWalkMethodLoadUrl.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookCrossWalkMethodSetResourceClient.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookCrossWalkClassXWalkPreferences.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookCrossWalkMethodSetValue.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookXWebPreferencesClassXWebPreferences.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookXWebPreferencesMethodSetValue.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookXWebViewClassClassXWebView.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookXWebViewMethodInitWebviewCore.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookXWebViewMethodIsXWalk.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookXWebViewMethodIsPinus.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookXWebViewMethodIsX5.doAfterTextChanged { refreshCode() }
        viewBinding.ruleHookXWebViewMethodIsSys.doAfterTextChanged { refreshCode() }
        refresh()
    }

    private fun refresh() {
        ruleName = intent.getStringExtra("rule_name")
        if (ruleName == null) {
            viewBinding.ruleHookMethod.setText("", false)
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
                        viewBinding.ruleHookMethod.setText("hookWebView", false)
                    }
                    "hookWebViewClient" -> {
                        val hookEntry = Gson().fromJson(hookJson, HookRules.HookRuleWebViewClient::class.java)
                        viewBinding.ruleRemark.setText(hookEntry.remark)
                        viewBinding.ruleHookWebViewClientClassWebViewClient.setText(hookEntry.Class_WebViewClient)
                        viewBinding.ruleHookWebViewClientMethodOnPageFinished.setText(hookEntry.Method_onPageFinished)
                        viewBinding.ruleHookWebViewClientClassWebView.setText(hookEntry.Class_WebView)
                        viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.setText(hookEntry.Method_evaluateJavascript)
                        viewBinding.ruleHookMethod.setText("hookWebViewClient", false)
                    }
                    "replaceNebulaUCSDK" -> {
                        val hookEntry = Gson().fromJson(hookJson, HookRules.ReplaceNebulaUCSDK::class.java)
                        viewBinding.ruleRemark.setText(hookEntry.remark)
                        viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.setText(hookEntry.Class_UcServiceSetup)
                        viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.setText(hookEntry.Method_updateUCVersionAndSdcardPath)
                        viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.setText(hookEntry.Field_sInitUcFromSdcardPath)
                        viewBinding.ruleHookMethod.setText("replaceNebulaUCSDK", false)
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
                        viewBinding.ruleHookMethod.setText("hookCrossWalk", false)
                    }
                    "hookXWebPreferences" -> {
                        val hookEntry = Gson().fromJson(hookJson, HookRules.HookXWebPreferences::class.java)
                        viewBinding.ruleRemark.setText(hookEntry.remark)
                        viewBinding.ruleHookXWebPreferencesClassXWebPreferences.setText(hookEntry.Class_XWebPreferences)
                        viewBinding.ruleHookXWebPreferencesMethodSetValue.setText(hookEntry.Method_setValue)
                        viewBinding.ruleHookMethod.setText("hookXWebPreferences", false)
                    }
                    "hookXWebView" -> {
                        val hookEntry = Gson().fromJson(hookJson, HookRules.HookXWebView::class.java)
                        viewBinding.ruleRemark.setText(hookEntry.remark)
                        viewBinding.ruleHookXWebViewClassClassXWebView.setText(hookEntry.Class_XWebView)
                        viewBinding.ruleHookXWebViewMethodInitWebviewCore.setText(hookEntry.Method_initWebviewCore)
                        viewBinding.ruleHookXWebViewMethodIsXWalk.setText(hookEntry.Method_isXWalk)
                        viewBinding.ruleHookXWebViewMethodIsPinus.setText(hookEntry.Method_isPinus)
                        viewBinding.ruleHookXWebViewMethodIsX5.setText(hookEntry.Method_isX5)
                        viewBinding.ruleHookXWebViewMethodIsSys.setText(hookEntry.Method_isSys)
                        viewBinding.ruleHookMethod.setText("hookXWebView", false)
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
        viewBinding.ruleCode.code = when (viewBinding.ruleHookMethod.text.toString()) {
            // TODO: 添加更多 hook 方法
            "hookWebView" -> getString(R.string.code_hookFunction,
                viewBinding.ruleRemark.text.toString().trim().let {if (it != "") getString(R.string.code_hookRemark, it) else ""},
                viewBinding.ruleName.text.toString().trim(),
                "hookWebView",
                arrayOf(
                    getString(R.string.code_hookParam, "Class_WebView", viewBinding.ruleHookWebViewClassWebView.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_getSettings", viewBinding.ruleHookWebViewMethodGetSettings.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_setWebContentsDebuggingEnabled", viewBinding.ruleHookWebViewMethodSetWebContentsDebuggingEnabled.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_setJavaScriptEnabled", viewBinding.ruleHookWebViewMethodSetJavaScriptEnabled.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_loadUrl", viewBinding.ruleHookWebViewMethodLoadUrl.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_setWebViewClient", viewBinding.ruleHookWebViewMethodSetWebViewClient.text.toString().trim()),
                ).joinToString("")
            )
            "hookWebViewClient" -> getString(R.string.code_hookFunction,
                viewBinding.ruleRemark.text.toString().trim().let {if (it != "") getString(R.string.code_hookRemark, it) else ""},
                viewBinding.ruleName.text.toString().trim(),
                "hookWebViewClient",
                arrayOf(
                    getString(R.string.code_hookParam, "Class_WebViewClient", viewBinding.ruleHookWebViewClientClassWebViewClient.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_onPageFinished", viewBinding.ruleHookWebViewClientMethodOnPageFinished.text.toString().trim()),
                    getString(R.string.code_hookParam, "Class_WebView", viewBinding.ruleHookWebViewClientClassWebView.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_evaluateJavascript", viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.text.toString().trim()),
                ).joinToString("")
            )
            "replaceNebulaUCSDK" -> getString(R.string.code_hookFunction,
                viewBinding.ruleRemark.text.toString().trim().let {if (it != "") getString(R.string.code_hookRemark, it) else ""},
                viewBinding.ruleName.text.toString().trim(),
                "replaceNebulaUCSDK",
                arrayOf(
                    getString(R.string.code_hookParam, "Class_UcServiceSetup", viewBinding.ruleReplaceNebulaUCSDKClassUcServiceSetup.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_updateUCVersionAndSdcardPath", viewBinding.ruleReplaceNebulaUCSDKMethodUpdateUCVersionAndSdcardPath.text.toString().trim()),
                    getString(R.string.code_hookParam, "Field_sInitUcFromSdcardPath", viewBinding.ruleReplaceNebulaUCSDKFieldSInitUcFromSdcardPath.text.toString().trim()),
                ).joinToString("")
            )
            "hookCrossWalk" -> getString(R.string.code_hookFunction,
                viewBinding.ruleRemark.text.toString().trim().let {if (it != "") getString(R.string.code_hookRemark, it) else ""},
                viewBinding.ruleName.text.toString().trim(),
                "hookCrossWalk",
                arrayOf(
                    getString(R.string.code_hookParam, "Class_XWalkView", viewBinding.ruleHookCrossWalkClassXWalkView.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_getSettings", viewBinding.ruleHookCrossWalkMethodGetSettings.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_setJavaScriptEnabled", viewBinding.ruleHookCrossWalkMethodSetJavaScriptEnabled.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_loadUrl", viewBinding.ruleHookCrossWalkMethodLoadUrl.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_setResourceClient", viewBinding.ruleHookCrossWalkMethodSetResourceClient.text.toString().trim()),
                    getString(R.string.code_hookParam, "Class_XWalkPreferences", viewBinding.ruleHookCrossWalkClassXWalkPreferences.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_setValue", viewBinding.ruleHookCrossWalkMethodSetValue.text.toString().trim()),
                ).joinToString("")
            )
            "hookXWebPreferences" -> getString(R.string.code_hookFunction,
                viewBinding.ruleRemark.text.toString().trim().let {if (it != "") getString(R.string.code_hookRemark, it) else ""},
                viewBinding.ruleName.text.toString().trim(),
                "hookXWebPreferences",
                arrayOf(
                    getString(R.string.code_hookParam, "Class_XWebPreferences", viewBinding.ruleHookXWebPreferencesClassXWebPreferences.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_setValue", viewBinding.ruleHookXWebPreferencesMethodSetValue.text.toString().trim()),
                ).joinToString("")
            )
            "hookXWebView" -> getString(R.string.code_hookFunction,
                viewBinding.ruleRemark.text.toString().trim().let {if (it != "") getString(R.string.code_hookRemark, it) else ""},
                viewBinding.ruleName.text.toString().trim(),
                "hookXWebView",
                arrayOf(
                    getString(R.string.code_hookParam, "Class_XWebView", viewBinding.ruleHookXWebViewClassClassXWebView.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_initWebviewCore", viewBinding.ruleHookXWebViewMethodInitWebviewCore.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_isXWalk", viewBinding.ruleHookXWebViewMethodIsXWalk.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_isPinus", viewBinding.ruleHookXWebViewMethodIsPinus.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_isX5", viewBinding.ruleHookXWebViewMethodIsX5.text.toString().trim()),
                    getString(R.string.code_hookParam, "Method_isSys", viewBinding.ruleHookXWebViewMethodIsSys.text.toString().trim()),
                ).joinToString("")
            )
            else -> getString(R.string.unknown_hook_method)
        }
    }
}