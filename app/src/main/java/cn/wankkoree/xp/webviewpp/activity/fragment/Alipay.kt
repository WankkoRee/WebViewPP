package cn.wankkoree.xp.webviewpp.activity.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wankkoree.xp.webviewpp.R
import cn.wankkoree.xp.webviewpp.application.Application
import cn.wankkoree.xp.webviewpp.databinding.FragmentSupportAlipayBinding
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import java.net.URLEncoder

class Alipay : Fragment() {
    private val application = ModuleApplication.appContext as Application
    private lateinit var viewBinding : FragmentSupportAlipayBinding

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View {
        viewBinding = FragmentSupportAlipayBinding.inflate(inflater, container, false)
        viewBinding.fragmentSupportAlipayOpen.setOnClickListener {
            val intent = Intent.parseUri("intent://platformapi/startapp" +
                    "?saId=10000007" + // 扫二维码
                    "&qrcode=${URLEncoder.encode("https://qr.alipay.com/tsx03240ll1s0gcvv1qd924", "UTF-8")}" + // 钦定扫码结果以跳过扫码
                    "#Intent;scheme=alipayqr;package=com.eg.android.AlipayGphone;end", Intent.URI_INTENT_SCHEME)
            try {
                startActivity(intent)
            } catch (e : android.content.ActivityNotFoundException) {
                application.toast(getString(R.string.alipay_is_not_found_please_install_it_first), false)
            }
        }
        return viewBinding.root
    }
}