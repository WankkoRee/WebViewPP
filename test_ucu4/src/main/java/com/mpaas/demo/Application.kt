package com.mpaas.demo

import android.app.Application
import android.content.Context
import com.alipay.mobile.framework.quinoxless.QuinoxlessFramework
import androidx.multidex.MultiDex

class Application : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        QuinoxlessFramework.setup(this) { }
    }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        QuinoxlessFramework.init()
    }
}