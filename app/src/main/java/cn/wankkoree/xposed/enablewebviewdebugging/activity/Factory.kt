package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.palette.graphics.Palette
import cn.wankkoree.xposed.enablewebviewdebugging.R

val grayColorFilter: ColorMatrixColorFilter = ColorMatrix().run {
    setSaturation(0f)
    ColorMatrixColorFilter(this)
}

fun colorStateSingle(color: Int): ColorStateList = ColorStateList(arrayOf(intArrayOf()), intArrayOf(color))

fun getPrimaryColor(d: Drawable, context: Context): Triple<Int, Int, Int> {
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

fun Context.getDrawableCompat(resId: Int) = AppCompatResources.getDrawable(this@getDrawableCompat, resId)
