package cn.wankkoree.xp.webviewpp.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.wankkoree.xp.webviewpp.BuildConfig
import cn.wankkoree.xp.webviewpp.R
import cn.wankkoree.xp.webviewpp.application.Application
import cn.wankkoree.xp.webviewpp.data.ModuleSP
import cn.wankkoree.xp.webviewpp.databinding.ActivityAdvanceBinding
import cn.wankkoree.xp.webviewpp.databinding.DialogDataSourceBinding
import cn.wankkoree.xp.webviewpp.util.AppCenterTool
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication

class Advance : AppCompatActivity() {
    private val application = ModuleApplication.appContext as Application
    private lateinit var viewBinding : ActivityAdvanceBinding

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAdvanceBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        AppCenterTool.trackEvent("activity", hashMapOf("activity" to "advance"))

        viewBinding.advanceSettingDataSource.setOnClickListener {
            val dialogBinding = DialogDataSourceBinding.inflate(layoutInflater)
            MaterialAlertDialogBuilder(this@Advance).apply {
                setTitle(getString(R.string.data_source))
                setNeutralButton(getString(R.string.test), null)
                setNegativeButton(getString(R.string.cancel)) { _, _ -> }
                setPositiveButton(getString(R.string.save), null)

                val dataSource = prefs("module").get(ModuleSP.data_source)
                when (dataSource) {
                    "https://raw.githubusercontent.com/WankkoRee/WebViewPP-Rules/master" -> dialogBinding.dialogDataSource.check(dialogBinding.dialogDataSourceGithub.id)
                    "https://raw.fastgit.org/WankkoRee/WebViewPP-Rules/master" -> dialogBinding.dialogDataSource.check(dialogBinding.dialogDataSourceFastgit.id)
                    else -> {
                        dialogBinding.dialogDataSource.check(dialogBinding.dialogDataSourceCustom.id)
                        dialogBinding.dialogDataSourceCustomInput.visibility = View.VISIBLE
                    }
                }
                dialogBinding.dialogDataSourceCustomInputValue.setText(dataSource)
                setView(dialogBinding.root)
            }.show().also { dialog ->
                dialogBinding.dialogDataSource.setOnCheckedChangeListener { _, checkedId ->
                    when (checkedId) {
                        dialogBinding.dialogDataSourceGithub.id -> {
                            dialogBinding.dialogDataSourceCustomInput.visibility = View.GONE
                        }
                        dialogBinding.dialogDataSourceFastgit.id -> {
                            dialogBinding.dialogDataSourceCustomInput.visibility = View.GONE
                        }
                        dialogBinding.dialogDataSourceCustom.id -> {
                            dialogBinding.dialogDataSourceCustomInput.visibility = View.VISIBLE
                        }
                    }
                }
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener TestEvent@ {
                    val dataSource = when (dialogBinding.dialogDataSource.checkedRadioButtonId) {
                        dialogBinding.dialogDataSourceGithub.id -> "https://raw.githubusercontent.com/WankkoRee/WebViewPP-Rules/master"
                        dialogBinding.dialogDataSourceFastgit.id -> "https://raw.fastgit.org/WankkoRee/WebViewPP-Rules/master"
                        dialogBinding.dialogDataSourceCustom.id -> dialogBinding.dialogDataSourceCustomInputValue.text.let {
                            if (it == null || (!it.startsWith("http://", true) && !it.startsWith("https://", true))) {
                                application.toast(getString(R.string.unavailable), false)
                                null
                            } else {
                                it.toString()
                            }
                        }
                        else -> {
                            application.toast(getString(R.string.unknown_checked_radio_button_id), false)
                            null
                        }
                    } ?: return@TestEvent
                    Fuel.get("$dataSource/rules/rules.json")
                        .responseObject<List<String>> { _, _, result ->
                            result.fold({
                                application.toast(getString(R.string.available), false)
                            }, {
                                loggerE(BuildConfig.APPLICATION_ID, getString(R.string.unavailable), it)
                                AppCenterTool.trackError(it, mapOf("msg" to getString(R.string.unavailable)), null)
                                application.toast(getString(R.string.unavailable), false)
                            })
                        }
                }
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    when (dialogBinding.dialogDataSource.checkedRadioButtonId) {
                        dialogBinding.dialogDataSourceGithub.id -> {
                            prefs("module").edit {put(ModuleSP.data_source, "https://raw.githubusercontent.com/WankkoRee/WebViewPP-Rules/master")}
                        }
                        dialogBinding.dialogDataSourceFastgit.id -> {
                            prefs("module").edit {put(ModuleSP.data_source, "https://raw.fastgit.org/WankkoRee/WebViewPP-Rules/master")}
                        }
                        dialogBinding.dialogDataSourceCustom.id -> {
                            prefs("module").edit {put(ModuleSP.data_source, dialogBinding.dialogDataSourceCustomInputValue.text.toString())}
                        }
                    }
                    refresh()
                    dialog.cancel()
                }
            }
        }

        viewBinding.advanceSettingAutoCheckUpdate.setOnClickListener {
            viewBinding.advanceSettingAutoCheckUpdateValue.isChecked = !viewBinding.advanceSettingAutoCheckUpdateValue.isChecked
        }
        viewBinding.advanceSettingAutoCheckUpdateValue.setOnCheckedChangeListener { _, isChecked ->
            prefs("module").edit { put(ModuleSP.auto_check_update, isChecked) }
            refresh()
        }

        viewBinding.advanceSettingAppCenter.setOnClickListener {
            viewBinding.advanceSettingAppCenterValue.isChecked = !viewBinding.advanceSettingAppCenterValue.isChecked
        }
        viewBinding.advanceSettingAppCenterValue.setOnCheckedChangeListener { _, isChecked ->
            prefs("module").edit { put(ModuleSP.app_center, isChecked) }
            AppCenterTool.init()
            refresh()
        }

        viewBinding.advanceLicenseAuthorCard.setCardBackgroundColor((getPrimaryColor(viewBinding.advanceLicenseAuthorIcon.drawable, application).first or 0xff000000.toInt()) and 0x33ffffff)
        viewBinding.advanceLicenseAuthorButtonGithub.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/WankkoRee")))
        }
        viewBinding.advanceLicenseAuthorButtonEmail.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mailto://wkr@wkr.moe")))
        }

        viewBinding.advanceLicenseYukiCard.setCardBackgroundColor((getPrimaryColor(viewBinding.advanceLicenseYukiIcon.drawable, application).first or 0xff000000.toInt()) and 0x33ffffff)
        viewBinding.advanceLicenseYukiDesc.text = getString(R.string.this_module_is_constructed_using_yukihookapi) + "\n" + getString(R.string.advance_yuki_version_text, YukiHookAPI.API_VERSION_NAME, YukiHookAPI.API_VERSION_CODE)
        viewBinding.advanceLicenseYukiButtonGithub.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fankes/YukiHookAPI")))
        }

        viewBinding.advanceLicenseModuleCard.setCardBackgroundColor((getPrimaryColor(viewBinding.advanceLicenseModuleIcon.drawable, application).first or 0xff000000.toInt()) and 0x33ffffff)
        viewBinding.advanceLicenseModuleIcon.setImageDrawable(packageManager.getApplicationIcon(BuildConfig.APPLICATION_ID))
        viewBinding.advanceLicenseModuleButtonGithub.setOnClickListener {
            application.toast(getString(R.string.if_you_find_this_project_useful_please_star_this_project), false)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/WankkoRee/WebViewPP")))
        }
        viewBinding.advanceLicenseModuleButtonTelegram.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/WebViewPP")))
        }

        viewBinding.advanceToolbarBack.setOnClickListener {
            finish()
        }

        refresh()
    }

    private fun refresh() {
        with(prefs("module")) {
            viewBinding.advanceSettingDataSourceValue.text = get(ModuleSP.data_source)
            viewBinding.advanceSettingAutoCheckUpdateValue.isChecked = get(ModuleSP.auto_check_update)
            viewBinding.advanceSettingAppCenterValue.isChecked = get(ModuleSP.app_center)
        }
    }
}