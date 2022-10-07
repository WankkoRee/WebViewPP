package cn.wankkoree.xp.webviewpp.hook.debug

import com.highcapable.yukihookapi.hook.log.loggerD

fun printStackTrace() {
    loggerD(msg = "---- ---- ---- ----")
    val stackElements = Throwable().stackTrace
    for (i in stackElements.indices) {
        val element = stackElements[i]
        loggerD(msg = "at ${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})")
    }
    loggerD(msg = "---- ---- ---- ----")
}
