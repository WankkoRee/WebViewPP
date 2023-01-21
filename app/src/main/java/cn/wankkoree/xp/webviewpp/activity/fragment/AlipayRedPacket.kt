package cn.wankkoree.xp.webviewpp.activity.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wankkoree.xp.webviewpp.R
import cn.wankkoree.xp.webviewpp.application.Application
import cn.wankkoree.xp.webviewpp.databinding.FragmentSupportAlipayRedPacketBinding
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication

class AlipayRedPacket : Fragment() {
    private val application = ModuleApplication.appContext as Application
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentSupportAlipayBinding = FragmentSupportAlipayRedPacketBinding.inflate(inflater, container, false)
        fragmentSupportAlipayBinding.fragmentSupportAlipayRedPacketCopy.setOnClickListener {
            val clipboard = application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("alipay_red_packet_key", "810737536")
            clipboard.setPrimaryClip(clip)
            application.toast(getString(R.string.copied_just_go_to_alipay_and_search_it), false)
        }
        return fragmentSupportAlipayBinding.root
    }
}