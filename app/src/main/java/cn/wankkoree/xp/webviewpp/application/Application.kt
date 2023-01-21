package cn.wankkoree.xp.webviewpp.application

import android.widget.Toast
import cn.wankkoree.xp.webviewpp.BuildConfig
import cn.wankkoree.xp.webviewpp.data.ModuleSP
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes

class Application : ModuleApplication() {
    private var toast : Toast? = null
    override fun onCreate() {
        super.onCreate()
        if (modulePrefs("module").get(ModuleSP.app_center))
            AppCenter.start(this@Application, BuildConfig.APP_CENTER_SECRET, Analytics::class.java, Crashes::class.java)
    }

    fun toast(text : CharSequence, longtime : Boolean) {
        toast?.cancel()
        toast = Toast.makeText(this@Application, text, if (longtime) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).also {
            it.show()
        }
    }
}