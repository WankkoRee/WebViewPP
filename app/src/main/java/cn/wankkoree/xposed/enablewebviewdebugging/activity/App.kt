package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.data.*
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.AppBinding
import com.highcapable.yukihookapi.hook.factory.modulePrefs

class App : AppCompatActivity() {
    private lateinit var viewBinding: AppBinding
    private var toast: Toast? = null
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
        viewBinding.appIcon.contentDescription = name
        viewBinding.appText.text = name
        viewBinding.appPackage.text = pkg
        viewBinding.appVersion.text = "$versionName($versionCode)"
        refresh()

        viewBinding.appToolbarBack.setOnClickListener {
            finish()
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
                val adapter = ArrayAdapter(this@App, R.layout.spinner_item, it.toArray())
                adapter.setDropDownViewResource(R.layout.spinner_item)
                viewBinding.appResourcesVconsoleVersion.adapter = adapter
                adapter
            }
            val nebulaUCSDKAdapter = getSet(ResourcesSP.nebulaUCSDK_versions).let {
                val adapter = ArrayAdapter(this@App, R.layout.spinner_item, it.toArray())
                adapter.setDropDownViewResource(R.layout.spinner_item)
                viewBinding.appResourcesNebulaucsdkVersion.adapter = adapter
                adapter
            }
            name("apps_$pkg")
            get(AppSP.is_enabled).let {
                viewBinding.appCard.setCardBackgroundColor(getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError))
                viewBinding.appIcon.drawable.mutate().colorFilter = if (it) null else grayColorFilter
            }
            get(AppSP.vConsole).let {
                viewBinding.appResourcesVconsoleCard.setCardBackgroundColor(getColor(if (it) R.color.backgroundSuccess else R.color.backgroundError))
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
        }
    }

    private fun reset() {
        toast?.cancel()
        toast = Toast.makeText(this@App, getString(R.string.reset_completed), Toast.LENGTH_SHORT)
        toast!!.show()
    }

    class ResourcesResultContract : ActivityResultContract<Unit, Unit>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(context, Resources::class.java)
        }
        override fun parseResult(resultCode: Int, intent: Intent?) { }
    }
}