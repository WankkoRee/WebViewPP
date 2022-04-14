package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.AppBinding

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

        viewBinding.appToolbarBack.setOnClickListener {
            finish()
        }
        viewBinding.appToolbarMenu.setOnClickListener {
            PopupMenu(this, it).run {
                menuInflater.inflate(R.menu.app_toolbar, menu)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.app_toolbar_menu_reset -> {
                            // TODO: 对话框，是否重置
                        }
                    }
                    true
                }
                show()
            }
        }
    }
}