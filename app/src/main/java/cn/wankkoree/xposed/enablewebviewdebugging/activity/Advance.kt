package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.wankkoree.xposed.enablewebviewdebugging.BuildConfig
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.data.ModuleSP
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.ActivityAdvanceBinding
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.DialogDataSourceBinding
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.modulePrefs

class Advance: AppCompatActivity() {
    private lateinit var viewBinding: ActivityAdvanceBinding
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAdvanceBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.advanceSettingDataSource.setOnClickListener {
            val dialogBinding = DialogDataSourceBinding.inflate(layoutInflater)
            MaterialAlertDialogBuilder(this@Advance).apply {
                setTitle(getString(R.string.data_source))
                setNeutralButton(getString(R.string.test), null)
                setNegativeButton(getString(R.string.cancel)) { _, _ -> }
                setPositiveButton(getString(R.string.save), null)

                val dataSource = modulePrefs("module").get(ModuleSP.data_source)
                when (dataSource) {
                    "https://raw.githubusercontent.com/WankkoRee/EnableWebViewDebugging-Rules/master" -> dialogBinding.dialogDataSource.check(dialogBinding.dialogDataSourceGithub.id)
                    "https://raw.fastgit.org/WankkoRee/EnableWebViewDebugging-Rules/master" -> dialogBinding.dialogDataSource.check(dialogBinding.dialogDataSourceFastgit.id)
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
                        dialogBinding.dialogDataSourceGithub.id -> "https://raw.githubusercontent.com/WankkoRee/EnableWebViewDebugging-Rules/master"
                        dialogBinding.dialogDataSourceFastgit.id -> "https://raw.fastgit.org/WankkoRee/EnableWebViewDebugging-Rules/master"
                        dialogBinding.dialogDataSourceCustom.id -> dialogBinding.dialogDataSourceCustomInputValue.text.let {
                            if (it == null || (!it.startsWith("http://", true) && !it.startsWith("https://", true))) {
                                toast?.cancel()
                                toast = Toast.makeText(this@Advance, getString(R.string.unavailable), Toast.LENGTH_SHORT)
                                toast!!.show()
                                null
                            } else {
                                it.toString()
                            }
                        }
                        else -> {
                            toast?.cancel()
                            toast = Toast.makeText(this@Advance, getString(R.string.unknown_checked_radio_button_id), Toast.LENGTH_SHORT)
                            toast!!.show()
                            null
                        }
                    } ?: return@TestEvent
                    Log.e(null, dataSource)
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
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    when (dialogBinding.dialogDataSource.checkedRadioButtonId) {
                        dialogBinding.dialogDataSourceGithub.id -> {
                            modulePrefs("module").put(ModuleSP.data_source, "https://raw.githubusercontent.com/WankkoRee/EnableWebViewDebugging-Rules/master")
                        }
                        dialogBinding.dialogDataSourceFastgit.id -> {
                            modulePrefs("module").put(ModuleSP.data_source, "https://raw.fastgit.org/WankkoRee/EnableWebViewDebugging-Rules/master")
                        }
                        dialogBinding.dialogDataSourceCustom.id -> {
                            modulePrefs("module").put(ModuleSP.data_source, dialogBinding.dialogDataSourceCustomInputValue.text.toString())
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
            modulePrefs("module").put(ModuleSP.auto_check_update, isChecked)
            refresh()
        }

        viewBinding.advanceLicenseAuthorCard.setCardBackgroundColor((getPrimaryColor(viewBinding.advanceLicenseAuthorIcon.drawable, this@Advance).first or 0xff000000.toInt()) and 0x33ffffff)
        viewBinding.advanceLicenseAuthorButtonGithub.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/WankkoRee")))
        }
        viewBinding.advanceLicenseAuthorButtonEmail.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mailto://wkr@wkr.moe")))
        }

        viewBinding.advanceLicenseYukiCard.setCardBackgroundColor((getPrimaryColor(viewBinding.advanceLicenseYukiIcon.drawable, this@Advance).first or 0xff000000.toInt()) and 0x33ffffff)
        viewBinding.advanceLicenseYukiDesc.text = getString(R.string.this_module_is_constructed_using_yukihookapi) + "\n" + getString(R.string.advance_yuki_version_text).format(YukiHookAPI.API_VERSION_NAME, YukiHookAPI.API_VERSION_CODE)
        viewBinding.advanceLicenseYukiButtonGithub.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fankes/YukiHookAPI")))
        }

        viewBinding.advanceLicenseModuleCard.setCardBackgroundColor((getPrimaryColor(viewBinding.advanceLicenseModuleIcon.drawable, this@Advance).first or 0xff000000.toInt()) and 0x33ffffff)
        viewBinding.advanceLicenseModuleIcon.setImageDrawable(packageManager.getApplicationIcon(BuildConfig.APPLICATION_ID))
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
            viewBinding.advanceSettingDataSourceValue.text = get(ModuleSP.data_source)
            viewBinding.advanceSettingAutoCheckUpdateValue.isChecked = get(ModuleSP.auto_check_update)
        }
    }
}