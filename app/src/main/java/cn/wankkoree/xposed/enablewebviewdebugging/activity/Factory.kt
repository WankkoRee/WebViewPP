package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter

val grayColorFilter: ColorMatrixColorFilter = ColorMatrix().run {
    setSaturation(0f)
    ColorMatrixColorFilter(this)
}