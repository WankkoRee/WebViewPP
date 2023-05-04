package cn.wankkoree.xp.webviewpp.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.wankkoree.xp.webviewpp.BuildConfig
import cn.wankkoree.xp.webviewpp.R
import cn.wankkoree.xp.webviewpp.activity.fragment.Alipay
import cn.wankkoree.xp.webviewpp.activity.fragment.AlipayRedPacket
import cn.wankkoree.xp.webviewpp.activity.fragment.WeChat
import cn.wankkoree.xp.webviewpp.application.Application
import cn.wankkoree.xp.webviewpp.data.AppsSP
import cn.wankkoree.xp.webviewpp.data.ModuleSP
import cn.wankkoree.xp.webviewpp.data.ResourcesSP
import cn.wankkoree.xp.webviewpp.data.getSet
import cn.wankkoree.xp.webviewpp.http.bean.api.github.RepoRelease
import cn.wankkoree.xp.webviewpp.databinding.ActivityMainBinding
import cn.wankkoree.xp.webviewpp.databinding.DialogSupportBinding
import cn.wankkoree.xp.webviewpp.util.AppCenterTool
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.GsonBuilder
import com.highcapable.yukihookapi.YukiHookAPI.Status.Executor
import com.highcapable.yukihookapi.YukiHookAPI.Status.isXposedModuleActive
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import io.noties.markwon.Markwon

class Main : AppCompatActivity() {
    private val application = ModuleApplication.appContext as Application
    private lateinit var viewBinding : ActivityMainBinding
    private val appsResultContract = registerForActivityResult(AppsResultContract()) {
        refresh()
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        AppCenterTool.trackEvent("activity", hashMapOf("activity" to "main"))

        viewBinding.mainToolbarIcon.setImageDrawable(packageManager.getApplicationIcon(BuildConfig.APPLICATION_ID))
        viewBinding.mainVersionText.text = getString(R.string.main_version_text, "${BuildConfig.VERSION_NAME}-${BuildConfig.BUILD_TYPE}", BuildConfig.VERSION_CODE)
        if (isXposedModuleActive) {
            viewBinding.mainStatusCard.setCardBackgroundColor((getColor(R.color.backgroundSuccess) or 0xff000000.toInt()) and 0x77ffffff)
            viewBinding.mainStatusIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawableCompat(R.drawable.ic_round_check_circle_24), null, null, null)
            viewBinding.mainStatusIcon.contentDescription = getString(R.string.enabled)
            viewBinding.mainStatusText.text = getString(R.string.enabled)
            viewBinding.mainXposedText.visibility = View.VISIBLE
            when {
                Executor.apiLevel != -1 -> viewBinding.mainXposedText.text = getString(R.string.main_xposed_text, "${Executor.name}(API ${Executor.apiLevel})")
                else -> viewBinding.mainXposedText.text = getString(R.string.main_xposed_text, Executor.name)
            }
        } else {
            viewBinding.mainStatusCard.setCardBackgroundColor((getColor(R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
            viewBinding.mainStatusIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawableCompat(R.drawable.ic_round_cancel_24), null, null, null)
            viewBinding.mainStatusIcon.contentDescription = getString(R.string.disabled)
            viewBinding.mainStatusText.text = getString(R.string.disabled)
            viewBinding.mainXposedText.visibility = View.GONE
        }
        refresh()
        if (prefs("module").get(ModuleSP.auto_check_update)) checkUpdate()

        viewBinding.mainToolbarMenu.setOnClickListener {
            if (!isXposedModuleActive && !BuildConfig.DEBUG) {
                application.toast(getString(R.string.please_enable_the_module_first), false)
                return@setOnClickListener
            }
            PopupMenu(this@Main, it).apply {
                menuInflater.inflate(R.menu.main_toolbar_menu, menu)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.main_toolbar_menu_reset -> {
                            MaterialAlertDialogBuilder(this@Main).apply {
                                setTitle(R.string.reset)
                                setMessage(R.string.do_you_really_reset_all_configurations)
                                setNegativeButton(R.string.cancel) { _, _ -> }
                                setPositiveButton(R.string.confirm) { _, _ ->
                                    with(prefs()) {
                                        name("apps").getSet(AppsSP.enabled).forEach { pkg ->
                                            name("apps_$pkg").edit { clear() }
                                        }
                                        name("apps").edit { clear() }
                                        name("resources").getSet(ResourcesSP.vConsole_versions).forEach { version ->
                                            name("resources_vConsole_$version").edit { clear() }
                                        }
                                        name("resources").getSet(ResourcesSP.nebulaUCSDK_versions).forEach { version ->
                                            name("resources_nebulaUCSDK_$version").edit { clear() }
                                        }
                                        name("resources").edit { clear() }
                                    }
                                    application.toast(getString(R.string.reset_completed), false)
                                    refresh()
                                }
                            }.show()
                        }
                        R.id.main_toolbar_menu_advance -> {
                            val intent = Intent(application, Advance::class.java)
                            startActivity(intent)
                        }
                    }
                    true
                }
            }.show()
        }
        viewBinding.mainStatusCard.setOnClickListener {
            checkUpdate()
        }
        viewBinding.mainAppsCard.setOnClickListener {
            if (!isXposedModuleActive && !BuildConfig.DEBUG) {
                application.toast(getString(R.string.please_enable_the_module_first), false)
                return@setOnClickListener
            }
            appsResultContract.launch(Unit)
        }
        viewBinding.mainResourcesCard.setOnClickListener {
            if (!isXposedModuleActive && !BuildConfig.DEBUG) {
                application.toast(getString(R.string.please_enable_the_module_first), false)
                return@setOnClickListener
            }
            val intent = Intent(application, Resources::class.java)
            startActivity(intent)
        }
        viewBinding.mainSupportCard.setOnClickListener {
            val dialogBinding = DialogSupportBinding.inflate(layoutInflater)
            BottomSheetDialog(this@Main).apply {
                dialogBinding.dialogSupportPager.adapter = object : FragmentStateAdapter(this@Main) {
                    override fun getItemCount() : Int = 3
                    override fun createFragment(position : Int): Fragment = when (position) {
                        0 -> AlipayRedPacket()
                        1 -> Alipay()
                        2 -> WeChat()
                        else -> Alipay()
                    }
                }
                dialogBinding.dialogSupportPager.children.find { it is RecyclerView }?.let {
                    (it as RecyclerView).isNestedScrollingEnabled = false // 禁用自身滚动以启用底部对话框拖动
                }
                TabLayoutMediator(dialogBinding.dialogSupportTab, dialogBinding.dialogSupportPager) { tab, position ->
                    when (position) {
                        0 -> {
                            tab.text = getString(R.string.alipay_red_packet)
                        }
                        1 -> {
                            tab.text = getString(R.string.alipay)
                        }
                        2 -> {
                            tab.text = getString(R.string.wechat)
                        }
                    }
                }.attach()
                setContentView(dialogBinding.root)
            }.show()
        }
    }

    private fun refresh() {
        viewBinding.mainAppsNum.text = getString(R.string.main_apps_num, prefs("apps").getSet(AppsSP.enabled).size)
    }

    private fun checkUpdate() {
        application.toast(getString(R.string.checking_for_updates), false)
        Fuel.get("https://api.github.com/repos/WankkoRee/WebViewPP/releases/latest")
            .responseObject<RepoRelease>(GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()) { _, _, result ->
                result.fold({ latest ->
                    Regex("^([0-9]+?)-(.+)\$").matchEntire(latest.tag_name)!!.groupValues.also {
                        val latestCode = it[1].toInt()
                        val latestName = it[2]
                        if (latestCode > BuildConfig.VERSION_CODE) {
                            MaterialAlertDialogBuilder(this@Main).apply {
                                setTitle(R.string.it_is_checked_that_there_is_a_new_version_do_you_want_to_download_it)
                                setMessage("123")
                                setNegativeButton(R.string.cancel) { _, _ -> }
                                setPositiveButton(R.string.confirm) { _, _ ->
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(latest.html_url)))
                                }
                            }.show().also { dialog ->
                                Markwon.create(this@Main).setMarkdown(
                                    dialog.findViewById(android.R.id.message)!!,
                                    """## ${getString(R.string.latest_version)}
                                        |
                                        |`${getString(R.string.version_format, latestName, latestCode)}`
                                        |
                                        |${latest.body}
                                    """.trimMargin()
                                )
                            }
                        } else if (latestCode == BuildConfig.VERSION_CODE) {
                            application.toast(getString(R.string.is_the_latest_version), false)
                        } else {
                            application.toast(getString(R.string.your_version_is_higher_than_the_latest_version_it_may_be_a_withdrawn_version_or_a_prerelease_version), false)
                        }
                    }
                }, { e ->
                    loggerE(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed, getString(R.string.latest_version)), e)
                    AppCenterTool.trackError(e, mapOf("msg" to getString(R.string.pull_failed, getString(R.string.latest_version))), null)
                })
            }
    }

    class AppsResultContract : ActivityResultContract<Unit, Unit>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(context, Apps::class.java)
        }

        override fun parseResult(resultCode: Int, intent: Intent?) {
        }
    }
}