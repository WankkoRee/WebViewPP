package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import cn.wankkoree.xposed.enablewebviewdebugging.BuildConfig
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.data.ModuleSP.data_source
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.AdvanceBinding
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.DialogDataSourceBinding
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.modulePrefs

class Advance: AppCompatActivity() {
    private lateinit var viewBinding: AdvanceBinding
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = AdvanceBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.advanceSettingDataSource.setOnClickListener {
            val dialogBinding = DialogDataSourceBinding.inflate(layoutInflater)
            AlertDialog.Builder(this@Advance).apply {
                val dataSource = modulePrefs("module").get(data_source)
                when (dataSource) {
                    "https://raw.githubusercontent.com/WankkoRee/EnableWebViewDebugging-Rules/master" -> dialogBinding.dialogDataSource.check(dialogBinding.dialogDataSourceGithub.id)
                    "https://raw.fastgit.org/WankkoRee/EnableWebViewDebugging-Rules/master" -> dialogBinding.dialogDataSource.check(dialogBinding.dialogDataSourceFastgit.id)
                    else -> {
                        dialogBinding.dialogDataSource.check(dialogBinding.dialogDataSourceCustom.id)
                        dialogBinding.dialogDataSourceCustomValue.visibility = View.VISIBLE
                    }
                }
                dialogBinding.dialogDataSourceCustomValue.setText(dataSource)
                setView(dialogBinding.root)
            }.show().also { dialog ->
                dialogBinding.dialogDataSource.setOnCheckedChangeListener { _, checkedId ->
                    when (checkedId) {
                        dialogBinding.dialogDataSourceGithub.id -> {
                            dialogBinding.dialogDataSourceCustomValue.visibility = View.GONE
                        }
                        dialogBinding.dialogDataSourceFastgit.id -> {
                            dialogBinding.dialogDataSourceCustomValue.visibility = View.GONE
                        }
                        dialogBinding.dialogDataSourceCustom.id -> {
                            dialogBinding.dialogDataSourceCustomValue.visibility = View.VISIBLE
                        }
                    }
                }
                dialogBinding.dialogDataSourceTest.setOnClickListener TestEvent@ {
                    val dataSource = when (dialogBinding.dialogDataSource.checkedRadioButtonId) {
                        dialogBinding.dialogDataSourceGithub.id -> "https://raw.githubusercontent.com/WankkoRee/EnableWebViewDebugging-Rules/master"
                        dialogBinding.dialogDataSourceFastgit.id -> "https://raw.fastgit.org/WankkoRee/EnableWebViewDebugging-Rules/master"
                        dialogBinding.dialogDataSourceCustom.id -> dialogBinding.dialogDataSourceCustomValue.text.toString()
                        else -> {
                            toast?.cancel()
                            toast = Toast.makeText(this@Advance, getString(R.string.unknown_checked_radio_button_id), Toast.LENGTH_SHORT)
                            toast!!.show()
                            null
                        }
                    } ?: return@TestEvent
                    Fuel.get("$dataSource/rules/rules.json")
                        .responseObject<List<String>> { _, _, result ->
                            result.fold({
                                toast?.cancel()
                                toast = Toast.makeText(this@Advance, getString(R.string.available), Toast.LENGTH_SHORT)
                                toast!!.show()
                            }, {
                                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.unavailable), it)
                                toast?.cancel()
                                toast = Toast.makeText(this@Advance, getString(R.string.unavailable), Toast.LENGTH_SHORT)
                                toast!!.show()
                            })
                        }
                }
                dialogBinding.dialogDataSourceCancel.setOnClickListener {
                    dialog.cancel()
                }
                dialogBinding.dialogDataSourceSave.setOnClickListener {
                    when (dialogBinding.dialogDataSource.checkedRadioButtonId) {
                        dialogBinding.dialogDataSourceGithub.id -> {
                            modulePrefs("module").put(data_source, "https://raw.githubusercontent.com/WankkoRee/EnableWebViewDebugging-Rules/master")
                        }
                        dialogBinding.dialogDataSourceFastgit.id -> {
                            modulePrefs("module").put(data_source, "https://raw.fastgit.org/WankkoRee/EnableWebViewDebugging-Rules/master")
                        }
                        dialogBinding.dialogDataSourceCustom.id -> {
                            modulePrefs("module").put(data_source, dialogBinding.dialogDataSourceCustomValue.text.toString())
                        }
                    }
                    refresh()
                    dialog.cancel()
                }
            }
        }

        getPrimaryColor(viewBinding.advanceLicenseAuthorIcon.drawable, this@Advance).first.also {
            viewBinding.advanceLicenseAuthorCard.backgroundTintList = colorStateSingle((it or 0xff000000.toInt()) and 0x33ffffff)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) viewBinding.advanceLicenseAuthorCard.outlineSpotShadowColor = it
        }
        viewBinding.advanceLicenseAuthorButtonGithub.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/WankkoRee")))
        }
        viewBinding.advanceLicenseAuthorButtonEmail.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mailto://wkr@wkr.moe")))
        }

        viewBinding.advanceLicenseYukiDesc.text = getString(R.string.this_module_is_constructed_using_yukihookapi) + "\n" + getString(R.string.advance_yuki_version_text).format(YukiHookAPI.API_VERSION_NAME, YukiHookAPI.API_VERSION_CODE)
        getPrimaryColor(viewBinding.advanceLicenseYukiIcon.drawable, this@Advance).first.also {
            viewBinding.advanceLicenseYukiCard.backgroundTintList = colorStateSingle((it or 0xff000000.toInt()) and 0x33ffffff)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) viewBinding.advanceLicenseYukiCard.outlineSpotShadowColor = it
        }
        viewBinding.advanceLicenseYukiButtonGithub.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fankes/YukiHookAPI")))
        }

        viewBinding.advanceLicenseModuleIcon.setImageDrawable(packageManager.getApplicationIcon(BuildConfig.APPLICATION_ID))
        getPrimaryColor(viewBinding.advanceLicenseModuleIcon.drawable, this@Advance).first.also {
            viewBinding.advanceLicenseModuleCard.backgroundTintList = colorStateSingle((it or 0xff000000.toInt()) and 0x33ffffff)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) viewBinding.advanceLicenseModuleCard.outlineSpotShadowColor = it
        }
        viewBinding.advanceLicenseModuleButtonGithub.setOnClickListener {
            toast?.cancel()
            toast = Toast.makeText(this@Advance, getString(R.string.if_you_find_this_project_useful_please_star_this_project), Toast.LENGTH_SHORT)
            toast!!.show()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/WankkoRee/EnableWebViewDebugging")))
        }
        viewBinding.advanceLicenseModuleButtonTelegram.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/EnableWebViewDebugging")))
        }

        viewBinding.advanceToolbarBack.setOnClickListener {
            finish()
        }

        refresh()
    }

    private fun refresh() {
        with(modulePrefs("module")) {
            viewBinding.advanceSettingDataSourceValue.text = get(data_source)
        }
    }

    class LicenseAdapter(private val items: ArrayList<View>) : PagerAdapter() {
        override fun getCount(): Int = items.size

        override fun isViewFromObject(view: View, any: Any): Boolean {
            return view == any
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return items[position].also {
                container.addView(it)
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
            container.removeView(items[position])
        }
    }
}