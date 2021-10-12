package xyz.dean.androiddemos.demos.blur_image

import android.graphics.Color
import androidx.lifecycle.MutableLiveData

class BlurImageViewState(
    initScale: Float = 0.3f,
    initRadius: Float = 25.0f,
    initAlpha: Int = 50,
    initRed: Int = 100,
    initGreen: Int = 100,
    initBlue: Int = 100,
) {
    var scaleValue: Float = initScale
        set(value) {
            scale.value = String.format("%.2f", value)
            field = value
        }
    val scaleProgressValue: Int
        get() = (scaleValue * 100 - 1).toInt()

    var radiusValue: Float = initRadius
        set(value) {
            radius.value = String.format("%3.1f", value)
            field = value
        }
    val radiusProgressValue: Int
        get() = (radiusValue * 4).toInt()

    var alphaValue: Int = initAlpha
        set(value) {
            alpha.value = String.format("%d", value)
            setColor(value, redValue, greenValue, blueValue)
            field = value
        }
    var redValue: Int = initRed
        set(value) {
            red.value = String.format("%d", value)
            setColor(alphaValue, value, greenValue, blueValue)
            field = value
        }
    var greenValue: Int = initGreen
        set(value) {
            green.value = String.format("%d", value)
            setColor(alphaValue, redValue, value, blueValue)
            field = value
        }
    var blueValue: Int = initBlue
        set(value) {
            blue.value = String.format("%d", value)
            setColor(alphaValue, redValue, greenValue, value)
            field = value
        }

    private fun setColor(alpha: Int, red: Int, green: Int, blue: Int) {
        val colorInt = Color.argb(alpha, red, green, blue)
        color.value = colorInt
        colorString.value = String.format("#%08X", 0xFFFFFFFF and colorInt.toLong())
    }

    val scale = MutableLiveData<String>().apply { value = String.format("%.2f", initScale) }
    val radius = MutableLiveData<String>().apply { value = String.format("%3.1f", initRadius) }
    val alpha = MutableLiveData<String>().apply { value = "$initAlpha" }
    val red = MutableLiveData<String>().apply { value = "$initRed" }
    val green = MutableLiveData<String>().apply { value = "$initGreen" }
    val blue = MutableLiveData<String>().apply { value = "$initBlue" }
    val color = MutableLiveData<Int>().apply {
        value = Color.argb(initAlpha, initRed, initGreen, initBlue)
    }
    val colorString = MutableLiveData<String>().apply {
        value = String.format("#%08X",
            0xFFFFFFFF and Color.argb(initAlpha, initRed, initGreen, initBlue).toLong())
    }
}