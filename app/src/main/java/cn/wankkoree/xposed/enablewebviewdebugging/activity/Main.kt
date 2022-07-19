package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import cn.wankkoree.xposed.enablewebviewdebugging.BuildConfig
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.data.AppsSP
import cn.wankkoree.xposed.enablewebviewdebugging.data.ResourcesSP
import cn.wankkoree.xposed.enablewebviewdebugging.data.getSet
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.ActivityMainBinding
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.highcapable.yukihookapi.YukiHookAPI.Status.executorName
import com.highcapable.yukihookapi.YukiHookAPI.Status.executorVersion
import com.highcapable.yukihookapi.YukiHookAPI.Status.isXposedModuleActive
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import io.noties.markwon.Markwon
import java.net.URLEncoder

class Main: AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    private var toast: Toast? = null
    private val appsResultContract = registerForActivityResult(AppsResultContract()) {
        refresh()
    }
    private lateinit var markdown: Markwon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        markdown = Markwon.create(this@Main)

        viewBinding.mainToolbarIcon.setImageDrawable(packageManager.getApplicationIcon(BuildConfig.APPLICATION_ID))
        viewBinding.mainVersionText.text = getString(R.string.main_version_text).format("${BuildConfig.VERSION_NAME}-${BuildConfig.BUILD_TYPE}", BuildConfig.VERSION_CODE)
        if (isXposedModuleActive) {
            viewBinding.mainStatusCard.setCardBackgroundColor((getColor(R.color.backgroundSuccess) or 0xff000000.toInt()) and 0x77ffffff)
            viewBinding.mainStatusIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawableCompat(R.drawable.ic_round_check_circle_24), null, null, null)
            viewBinding.mainStatusIcon.contentDescription = getString(R.string.enabled)
            viewBinding.mainStatusText.text = getString(R.string.enabled)
            viewBinding.mainXposedText.visibility = View.VISIBLE
            when {
                executorVersion != -1 -> viewBinding.mainXposedText.text = getString(R.string.main_xposed_text).format("${executorName}(API ${executorVersion})")
                else -> viewBinding.mainXposedText.text = getString(R.string.main_xposed_text).format(executorName)
            }
        } else {
            viewBinding.mainStatusCard.setCardBackgroundColor((getColor(R.color.backgroundError) or 0xff000000.toInt()) and 0x77ffffff)
            viewBinding.mainStatusIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawableCompat(R.drawable.ic_round_cancel_24), null, null, null)
            viewBinding.mainStatusIcon.contentDescription = getString(R.string.disabled)
            viewBinding.mainStatusText.text = getString(R.string.disabled)
            viewBinding.mainXposedText.visibility = View.GONE
        }
        refresh()
        checkUpdate()

        viewBinding.mainToolbarMenu.setOnClickListener {
            if (!isXposedModuleActive && !BuildConfig.DEBUG) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_enable_the_module_first), Toast.LENGTH_SHORT)
                toast!!.show()
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
                                    with(modulePrefs) {
                                        name("apps").getSet(AppsSP.enabled).forEach { pkg ->
                                            name("apps_$pkg").clear()
                                        }
                                        name("apps").clear()
                                        name("resources").getSet(ResourcesSP.vConsole_versions).forEach { version ->
                                            name("resources_vConsole_$version").clear()
                                        }
                                        name("resources").getSet(ResourcesSP.nebulaUCSDK_versions).forEach { version ->
                                            name("resources_nebulaUCSDK_$version").clear()
                                        }
                                        name("resources").clear()
                                    }
                                    toast?.cancel()
                                    toast = Toast.makeText(this@Main, getString(R.string.reset_completed), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    refresh()
                                }
                            }.show()
                        }
                        R.id.main_toolbar_menu_advance -> {
                            val intent = Intent(this@Main, Advance::class.java)
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
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_enable_the_module_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            appsResultContract.launch(Unit)
        }
        viewBinding.mainResourcesCard.setOnClickListener {
            if (!isXposedModuleActive && !BuildConfig.DEBUG) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_enable_the_module_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val intent = Intent(this, Resources::class.java)
            startActivity(intent)
        }
        viewBinding.mainDonateCard.setOnClickListener {
            val intent = Intent.parseUri("intent://platformapi/startapp" +
                    "?saId=10000007" + // 扫二维码
                    "&qrcode=${URLEncoder.encode("https://qr.alipay.com/tsx03240ll1s0gcvv1qd924", "UTF-8")}" + // 钦定扫码结果以跳过扫码
                    "#Intent;scheme=alipayqr;package=com.eg.android.AlipayGphone;end", Intent.URI_INTENT_SCHEME)
            try {
                startActivity(intent)
            } catch (e: android.content.ActivityNotFoundException) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.alipay_is_not_found_please_install_it_first), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        }
    }

    private fun refresh() {
        viewBinding.mainAppsNum.text = getString(R.string.main_apps_num).format(modulePrefs("apps").getSet(AppsSP.enabled).size)
    }

    private fun checkUpdate() {
        toast?.cancel()
        toast = Toast.makeText(this@Main, getString(R.string.checking_for_updates), Toast.LENGTH_SHORT)
        toast!!.show()
        Fuel.get("https://api.github.com/repos/WankkoRee/EnableWebViewDebugging/releases/latest")
            .responseObject<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.github.RepoRelease>(GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()) { _, _, result ->
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
                                markdown.setMarkdown(
                                    dialog.findViewById(android.R.id.message)!!,
                                    """## ${getString(R.string.latest_version)}
                                        |
                                        |`${getString(R.string.version_format).format(latestName, latestCode)}`
                                        |
                                        |${latest.body}
                                    """.trimMargin()
                                )
                            }
                        } else if (latestCode == BuildConfig.VERSION_CODE) {
                            toast?.cancel()
                            toast = Toast.makeText(this@Main, getString(R.string.is_the_latest_version), Toast.LENGTH_SHORT)
                            toast!!.show()
                        } else {
                            toast?.cancel()
                            toast = Toast.makeText(this@Main, getString(R.string.your_version_is_higher_than_the_latest_version_it_may_be_a_withdrawn_version_or_a_prerelease_version), Toast.LENGTH_SHORT)
                            toast!!.show()
                        }
                    }
                }, { e ->
                    Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.latest_version)), e)
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