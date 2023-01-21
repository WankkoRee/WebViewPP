package cn.wankkoree.xp.webviewpp.application

import android.widget.Toast
import cn.wankkoree.xp.webviewpp.util.AppCenterTool
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication

class Application : ModuleApplication() {
    private var toast : Toast? = null
    override fun onCreate() {
        super.onCreate()
        AppCenterTool.init()
    }

    fun toast(text : CharSequence, longtime : Boolean) {
        toast?.cancel()
        toast = Toast.makeText(this@Application, text, if (longtime) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).also {
            it.show()
        }
    }
}