package cn.wankkoree.xp.webviewpp.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wankkoree.xp.webviewpp.application.Application
import cn.wankkoree.xp.webviewpp.databinding.FragmentSupportWechatBinding
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication

class WeChat : Fragment() {
    private val application = ModuleApplication.appContext as Application
    private lateinit var viewBinding : FragmentSupportWechatBinding

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View {
        viewBinding = FragmentSupportWechatBinding.inflate(inflater, container, false)
        return viewBinding.root
    }
}