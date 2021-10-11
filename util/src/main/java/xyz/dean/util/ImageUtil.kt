package xyz.dean.util

import android.content.Context
import android.graphics.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import kotlin.math.round

private const val TAG = "ImageUtil"

fun Context.blur(
    bitmap: Bitmap,
    @FloatRange(from = 0.0, to = 25.0) blurRadius: Float,
    @FloatRange(from = 0.0) scale: Float = 1.0f,
    @ColorInt filterColor: Int = Color.TRANSPARENT,
): Bitmap {
    val width = round(bitmap.width * scale).toInt()
    val height = round(bitmap.height * scale).toInt()
    val tempBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(tempBitmap).apply {
        scale(scale, scale)
    }
    val paint = Paint().apply {
        flags = Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG
        colorFilter = PorterDuffColorFilter(filterColor, PorterDuff.Mode.SRC_ATOP)
    }
    canvas.drawBitmap(bitmap, 0f, 0f, paint)
    val outputBitmap = Bitmap.createBitmap(tempBitmap)
    val rs = RenderScript.create(this)
    val tempIn = Allocation.createFromBitmap(rs, tempBitmap)
    val tempOut = Allocation.createFromBitmap(rs, outputBitmap)
    ScriptIntrinsicBlur.create(rs, Element.U8_4(rs)).apply {
        setRadius(blurRadius)
        setInput(tempIn)
        forEach(tempOut)
    }
    tempOut.copyTo(outputBitmap)
    return outputBitmap
}