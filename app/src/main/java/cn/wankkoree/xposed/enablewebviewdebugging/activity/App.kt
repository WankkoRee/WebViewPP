package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.data.AppSP
import cn.wankkoree.xposed.enablewebviewdebugging.data.AppsSP
import cn.wankkoree.xposed.enablewebviewdebugging.data.put
import cn.wankkoree.xposed.enablewebviewdebugging.data.remove
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.AppBinding
import com.highcapable.yukihookapi.hook.factory.modulePrefs

class App : AppCompatActivity() {
    private lateinit var viewBinding: AppBinding
    private var toast: Toast? = null

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
        modulePrefs("apps_$pkg").run {
            val state = get(AppSP.is_enabled)
            viewBinding.appCard.setCardBackgroundColor(getColor(if (state) R.color.backgroundSuccess else R.color.backgroundError))
            viewBinding.appIcon.drawable.mutate().colorFilter = if (state) null else grayColorFilter
        }

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
                viewBinding.appCard.setCardBackgroundColor(getColor(if (state) R.color.backgroundSuccess else R.color.backgroundError))
                viewBinding.appIcon.drawable.mutate().colorFilter = if (state) null else grayColorFilter
                state
            }
            if (state)
                modulePrefs("apps").put(AppsSP.enabled, pkg)
            else
                modulePrefs("apps").remove(AppsSP.enabled, pkg)
            toast?.cancel()
            toast = Toast.makeText(this, getString(if (state) R.string.enabled else R.string.disabled), Toast.LENGTH_SHORT)
            toast!!.show()
        }
    }

    private fun reset() {
        toast?.cancel()
        toast = Toast.makeText(this, getString(R.string.reset_completed), Toast.LENGTH_SHORT)
        toast!!.show()
    }
}