package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.res.ColorStateList
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter

val grayColorFilter: ColorMatrixColorFilter = ColorMatrix().run {
    setSaturation(0f)
    ColorMatrixColorFilter(this)
}

fun colorStateSingle(color: Int): ColorStateList = ColorStateList(arrayOf(intArrayOf()), intArrayOf(color))
