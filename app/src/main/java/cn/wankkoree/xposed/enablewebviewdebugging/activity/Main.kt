package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.wankkoree.xposed.enablewebviewdebugging.BuildConfig
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.data.AppsSP
import cn.wankkoree.xposed.enablewebviewdebugging.data.getSet
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.MainBinding
import com.highcapable.yukihookapi.hook.xposed.YukiHookModuleStatus
import com.highcapable.yukihookapi.hook.factory.isModuleActive
import com.highcapable.yukihookapi.hook.factory.isTaiChiModuleActive
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import java.net.URLEncoder


class Main: AppCompatActivity() {
    private lateinit var viewBinding: MainBinding
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = MainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.mainVersionText.text = getString(R.string.main_version_text).format(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
        if (isModuleActive) {
            viewBinding.mainStatusCard.setCardBackgroundColor(getColor(R.color.backgroundSuccess))
            viewBinding.mainStatusIcon.setImageResource(R.drawable.ic_round_check_circle_24)
            viewBinding.mainStatusIcon.contentDescription = getString(R.string.enabled)
            viewBinding.mainStatusText.text = getString(R.string.enabled)
            viewBinding.mainXposedText.visibility = View.VISIBLE
            when {
                YukiHookModuleStatus.executorVersion > 0 -> viewBinding.mainXposedText.text = getString(R.string.main_xposed_text).format("${YukiHookModuleStatus.executorName}(API ${YukiHookModuleStatus.executorVersion})")
                isTaiChiModuleActive -> viewBinding.mainXposedText.text = getString(R.string.main_xposed_text).format(YukiHookModuleStatus.executorName)
                else -> viewBinding.mainXposedText.text = getString(R.string.main_xposed_text).format(getString(R.string.unknown))
            }
        } else {
            viewBinding.mainStatusCard.setCardBackgroundColor(getColor(R.color.backgroundError))
            viewBinding.mainStatusIcon.setImageResource(R.drawable.ic_round_cancel_24)
            viewBinding.mainStatusIcon.contentDescription = getString(R.string.disabled)
            viewBinding.mainStatusText.text = getString(R.string.disabled)
            viewBinding.mainXposedText.visibility = View.GONE
        }
        viewBinding.mainAppsNum.text = getString(R.string.main_apps_num).format(modulePrefs("apps").getSet(AppsSP.enabled).size)

        viewBinding.mainToolbarMenu.setOnClickListener {
            if (!isModuleActive && !BuildConfig.DEBUG) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_enable_the_module_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            // TODO: 弹出菜单
        }
        viewBinding.mainAppsCard.setOnClickListener {
            if (!isModuleActive && !BuildConfig.DEBUG) {
                toast?.cancel()
                toast = Toast.makeText(this, getString(R.string.please_enable_the_module_first), Toast.LENGTH_SHORT)
                toast!!.show()
                return@setOnClickListener
            }
            val intent = Intent(this, Apps::class.java)
            startActivity(intent)
        }
        viewBinding.mainResourcesCard.setOnClickListener {
            if (!isModuleActive && !BuildConfig.DEBUG) {
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

}