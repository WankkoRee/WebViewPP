package cn.wankkoree.xp.webviewpp.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wankkoree.xp.webviewpp.databinding.FragmentSupportWechatBinding

class WeChat : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentSupportWechatBinding = FragmentSupportWechatBinding.inflate(inflater, container, false)
        return fragmentSupportWechatBinding.root
    }
}