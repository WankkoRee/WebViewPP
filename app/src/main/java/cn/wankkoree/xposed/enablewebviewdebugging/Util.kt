package cn.wankkoree.xposed.enablewebviewdebugging

import java.io.InputStream
import android.app.AndroidAppHelper
import android.app.Application
import android.content.Context

import de.robv.android.xposed.XposedBridge

object Util {
    private const val modulePackage = "cn.wankkoree.xposed.enablewebviewdebugging"
    private const val vConsole = "v3.12.0"
    private const val libUC7zSo = "libWebViewCore_3.21.0.174.200825145737_7z_uc.so"

    fun getApplication(): Application {
        return AndroidAppHelper.currentApplication()
    }

    fun getModuleContext(): Context {
        return this.getApplication().createPackageContext(modulePackage, Context.CONTEXT_IGNORE_SECURITY)
    }

    fun getVConsole(): String {
        return this.getModuleContext().assets.open("vConsole/$vConsole/vconsole.min.js").readBytes().decodeToString()
    }

    fun getLibUC7zSo(cpuArch: String): InputStream {
        return this.getModuleContext().assets.open("libUC7zSo/$cpuArch/$libUC7zSo")
    }

    fun getClassString(clazz: Class<*>): String {
        return "${clazz.classLoader.javaClass.name}=>${clazz.name}"
    }

    fun getClassStringWithHash(clazz: Class<*>): String {
        return "${clazz.classLoader.javaClass.name}@${clazz.classLoader!!.hashCode()}=>${clazz.name}@${clazz.hashCode()}"
    }

    fun log(level: String, packageName: String, message: String) {
        if ((level == "debug" && BuildConfig.DEBUG) || (level == "info"))
            XposedBridge.log("[EnableWebViewDebugging]<$packageName>[$level]: $message")
    }

    fun printStackTrace(level: String, packageName: String) {
        this.log(level, packageName, "---- ---- ---- ----")
        val stackElements = Throwable().stackTrace
        for (i in stackElements.indices) {
            val element = stackElements[i]
            this.log(level, packageName, "at ${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})")
        }
        this.log(level, packageName, "---- ---- ---- ----")
    }
}