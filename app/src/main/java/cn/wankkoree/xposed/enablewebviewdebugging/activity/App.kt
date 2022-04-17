package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
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
import cn.wankkoree.xposed.enablewebviewdebugging.ResourcesVersionNotExisted
import cn.wankkoree.xposed.enablewebviewdebugging.activity.component.Code
import cn.wankkoree.xposed.enablewebviewdebugging.data.*
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.AppBinding
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

        pkg = intent.getStringExtra("pkg")!!
        val app = packageManager.getPackageInfo(pkg, PackageManager.GET_META_DATA)
        icon = app.applicationInfo.loadIcon(packageManager)
        name = app.applicationInfo.loadLabel(packageManager) as String
        versionName = app.versionName
        versionCode = app.versionCode

        viewBinding.appToolbarName.text = name
        viewBinding.appIcon.setImageDrawable(icon)
        viewBinding.appIcon.transitionName = "${pkg}iconView"
        viewBinding.appIcon.contentDescription = name
        viewBinding.appText.text = name
        viewBinding.appText.transitionName = "${pkg}nameView"
        viewBinding.appPackage.text = pkg
        viewBinding.appPackage.transitionName = "${pkg}packageView"
        viewBinding.appVersion.text = getString(R.string.version_format).format(versionName, versionCode)
        viewBinding.appVersion.transitionName = "${pkg}versionView"
        refresh()

        viewBinding.appToolbarBack.setOnClickListener {
            finishAfterTransition()
        }
        viewBinding.appToolbarMenu.setOnClickListener {
            PopupMenu(this, it).run {
                menuInflater.inflate(R.menu.app_toolbar, menu)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.app_toolbar_menu_reset -> {
                            AlertDialog.Builder(this@App).run {
                                setMessage(R.string.do_you_really_reset_this_application_hooking_rules)
                                setPositiveButton(R.string.confirm) { _, _ ->
                                    reset()
                                    refresh()
                                }
                                setNegativeButton(R.string.cancel) { _, _ -> }
                                create()
                                show()
                            }
                        }
                    }
                    true
                }
                show()
            }
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
            ruleResultContract.launch(Intent(this@App, Rule::class.java).also {
                it.putExtra("pkg", pkg)
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
        modulePrefs.run {
            name("resources")
            val vConsoleAdapter = getSet(ResourcesSP.vConsole_versions).let {
                val adapter = ArrayAdapter(this@App, R.layout.component_spinneritem, it.toArray())
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.appResourcesVconsoleVersion.adapter = adapter
                adapter
            }
            val nebulaUCSDKAdapter = getSet(ResourcesSP.nebulaUCSDK_versions).let {
                val adapter = ArrayAdapter(this@App, R.layout.component_spinneritem, it.toArray())
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.appResourcesNebulaucsdkVersion.adapter = adapter
                adapter
            }
            name("apps_$pkg")
            get(AppSP.is_enabled).let {
                viewBinding.appCard.setCardBackgroundColor(getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) viewBinding.appCard.outlineSpotShadowColor = getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError)
                viewBinding.appIcon.drawable.mutate().colorFilter = if (it) null else grayColorFilter
            }
            get(AppSP.vConsole).let {
                viewBinding.appResourcesVconsoleCard.setCardBackgroundColor(getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError))
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
            get(AppSP.nebulaUCSDK).let {
                viewBinding.appResourcesNebulaucsdkCard.setCardBackgroundColor(getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError))
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
                val hookEntry = getList<String>("hook_entry_$ruleName")
                try {
                    v.code = when (hookEntry[0]) {
                        "hookWebView" -> getString(R.string.code_hookWebView).format(ruleName, hookEntry[1], hookEntry[2], hookEntry[3], hookEntry[4], hookEntry[5], hookEntry[6])
                        "hookWebViewClient" -> getString(R.string.code_hookWebViewClient).format(ruleName, hookEntry[1], hookEntry[2], hookEntry[3], hookEntry[4])
                        else -> getString(R.string.unknown_hook_method)
                    }
                    v.transitionName = ruleName
                } catch (e: Exception) {
                    Log.e(BuildConfig.APPLICATION_ID, getString(R.string.parse_failed), e)
                    toast?.cancel()
                    toast = Toast.makeText(this@App, getString(R.string.parse_failed), Toast.LENGTH_SHORT)
                    toast!!.show()
                    return@forEach // continue
                }
                v.isClickable = true
                v.setOnClickListener {
                    ruleResultContract.launch(Intent(this@App, Rule::class.java).also {
                        it.putExtra("pkg", pkg)
                        it.putExtra("rule_name", ruleName)
                    }, ActivityOptionsCompat.makeSceneTransitionAnimation(this@App, v, ruleName))
                }
                v.setOnLongClickListener {
                    AlertDialog.Builder(this@App).run {
                        setMessage(R.string.do_you_really_delete_this_rule)
                        setPositiveButton(R.string.confirm) { _, _ ->
                            modulePrefs("apps_$pkg").run {
                                remove(AppSP.hooks, ruleName)
                                remove("hook_entry_$ruleName")
                            }
                            refresh()
                        }
                        setNegativeButton(R.string.cancel) { _, _ -> }
                        create()
                        show()
                    }
                    true
                }
                viewBinding.appHooksList.addView(v)
            }
        }
    }

    private fun reset() {
        try { modulePrefs("apps").remove(AppsSP.enabled, pkg) } catch (_: ResourcesVersionNotExisted) { }
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