package cn.wankkoree.xposed.enablewebviewdebugging.http

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object Http {
    suspend fun get(url: String): String = withContext(Dispatchers.IO) {
        (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Cache-Control", "no-cache")
            useCaches = false
            defaultUseCaches = false
            connectTimeout = 4000
            readTimeout = 4000
            connect()
        }.inputStream.bufferedReader().use { it.readText() }
    }

    suspend fun getBytes(url: String): ByteArray = withContext(Dispatchers.IO) {
        (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Cache-Control", "no-cache")
            useCaches = false
            defaultUseCaches = false
            connectTimeout = 4000
            readTimeout = 4000
            connect()
        }.inputStream.readBytes()
    }
}