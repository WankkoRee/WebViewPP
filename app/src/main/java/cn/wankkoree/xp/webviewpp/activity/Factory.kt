package cn.wankkoree.xp.webviewpp.activity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.palette.graphics.Palette
import cn.wankkoree.xp.webviewpp.R

val grayColorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
    setSaturation(0f)
})

fun getPrimaryColor(d : Drawable, context : Context) : Triple<Int, Int, Int> {
    // https://stackoverflow.com/a/55852660/15603001
    d.state = intArrayOf(android.R.attr.state_enabled)
    val bitmap = Bitmap.createBitmap(d.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    d.setBounds(0, 0, canvas.width, canvas.height)
    d.draw(canvas)
    return Palette.from(bitmap).generate().let {
        Triple(
            it.getVibrantColor(context.getColor(R.color.textPrimary)),
            it.getMutedColor(context.getColor(R.color.textSecondary)),
            it.getDominantColor(context.getColor(R.color.background)),
        )
    }
}

fun Context.getDrawableCompat(resId : Int) = AppCompatResources.getDrawable(this@getDrawableCompat, resId)

fun Long.autoUnitByte() : String = when (this@autoUnitByte) {
    in 0 until 1024 -> "${this@autoUnitByte} B"
    in 1024 until 1024*1024 -> "${(this@autoUnitByte / 1024.0).round(2)} KB"
    in 1024*1024 until 1024*1024*1024 -> "${(this@autoUnitByte / 1048576.0).round(2)} MB"
    else -> "${(this@autoUnitByte / 1073741824.0).round(2)} GB"
}

fun Number.round(scale : Int) : String = String.format("%.${scale}f", this@round)
