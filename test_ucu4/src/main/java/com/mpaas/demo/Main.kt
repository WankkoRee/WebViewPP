package com.mpaas.demo

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.alipay.mobile.h5container.api.H5Param
import com.mpaas.nebula.adapter.api.MPNebula

class Main : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val button = findViewById<Button>(R.id.btn_scan_open_page)
        button.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(H5Param.LONG_SHOW_PROGRESS, true)
            // 开启 LONG_PULL_REFRESH 后，可自定义下拉刷新
            //bundle.putBoolean(H5Param.LONG_PULL_REFRESH, true)
            MPNebula.startUrl("https://liulanmi.com/core", bundle)
        }
    }
}