package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.transition.Slide
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import cn.wankkoree.xposed.enablewebviewdebugging.BuildConfig
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.ResourcesVersionAlreadyExisted
import cn.wankkoree.xposed.enablewebviewdebugging.data.*
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.RuleBinding
import com.highcapable.yukihookapi.hook.factory.modulePrefs

class Rule : AppCompatActivity() {
    private lateinit var viewBinding: RuleBinding
    private var toast: Toast? = null

    private lateinit var pkg: String
    private var ruleName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.enterTransition = Slide()
        window.exitTransition = Slide()
        viewBinding = RuleBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        pkg = intent.getStringExtra("pkg")!!

        val adapter = ArrayAdapter(this@Rule, R.layout.component_spinneritem, arrayOf("hookWebView", "hookWebViewClient"))
        adapter.setDropDownViewResource(R.layout.component_spinneritem)
        viewBinding.ruleHookMethod.adapter = adapter

        viewBinding.ruleToolbarBack.setOnClickListener {
            finishAfterTransition()
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
                    } catch (_: ResourcesVersionAlreadyExisted) {
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
                            viewBinding.ruleHookWebViewClientClassWebViewClient.text.toString(),
                            viewBinding.ruleHookWebViewClientMethodOnPageFinished.text.toString(),
                            viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.text.toString(),
                            viewBinding.ruleHookWebViewClientClassValueCallback.text.toString(),
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
                    "hookWebView" -> {
                        viewBinding.ruleHookWebView.visibility = View.VISIBLE
                        viewBinding.ruleHookWebViewClient.visibility = View.GONE
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
                        if (viewBinding.ruleHookWebViewClientClassWebViewClient.text!!.isEmpty()) viewBinding.ruleHookWebViewClientClassWebViewClient.setText("android.webkit.WebViewClient")
                        if (viewBinding.ruleHookWebViewClientMethodOnPageFinished.text!!.isEmpty()) viewBinding.ruleHookWebViewClientMethodOnPageFinished.setText("onPageFinished")
                        if (viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.text!!.isEmpty()) viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.setText("evaluateJavascript")
                        if (viewBinding.ruleHookWebViewClientClassValueCallback.text!!.isEmpty()) viewBinding.ruleHookWebViewClientClassValueCallback.setText("android.webkit.ValueCallback")
                    }
                    else -> {
                        Log.e(BuildConfig.APPLICATION_ID, getString(R.string.unknown_hook_method))
                    }
                }
            }
        }
        viewBinding.ruleName.doAfterTextChanged {
            viewBinding.ruleCode.transitionName = viewBinding.ruleName.text.toString()
            refreshCode()
        }
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
                        viewBinding.ruleHookWebViewClientClassWebViewClient.setText(hookEntry[1])
                        viewBinding.ruleHookWebViewClientMethodOnPageFinished.setText(hookEntry[2])
                        viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.setText(hookEntry[3])
                        viewBinding.ruleHookWebViewClientClassValueCallback.setText(hookEntry[4])
                        viewBinding.ruleHookMethod.setSelection(1)
                    }
                }
            }
        }
    }

    private fun refreshCode() {
        viewBinding.ruleCode.code = when (viewBinding.ruleHookMethod.selectedItem as String) {
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
                viewBinding.ruleHookWebViewClientClassWebViewClient.text.toString(),
                viewBinding.ruleHookWebViewClientMethodOnPageFinished.text.toString(),
                viewBinding.ruleHookWebViewClientMethodEvaluateJavascript.text.toString(),
                viewBinding.ruleHookWebViewClientClassValueCallback.text.toString(),
            )
            else -> getString(R.string.unknown_hook_method)
        }
    }
}