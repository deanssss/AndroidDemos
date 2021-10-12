package xyz.dean.androiddemos.demos.blur_image

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.core.graphics.applyCanvas
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.databinding.ActivityBlurImageBinding
import xyz.dean.util.blur
import xyz.dean.util.dp2px

class BlurImageActivity : BaseActivity() {
    private lateinit var binding: ActivityBlurImageBinding

    override fun getDemoItem(): DemoItem= demoItem

    private lateinit var cropBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_blur_image)
        binding.lifecycleOwner = this
        binding.viewState = BlurImageViewState().apply {
            scale.observe(this@BlurImageActivity) {
                setBlurView(scaleValue, radiusValue, color.value!!)
            }
            radius.observe(this@BlurImageActivity) {
                setBlurView(scaleValue, radiusValue, color.value!!)
            }
            color.observe(this@BlurImageActivity) {
                setBlurView(scaleValue, radiusValue, it)
            }
        }

        binding.scaleSb.setOnSeekBarChangeListener { progress, _ ->
            binding.viewState?.scaleValue = (progress + 1) / 100f
        }
        binding.radiusSb.setOnSeekBarChangeListener { progress, _ ->
            binding.viewState?.radiusValue = (progress + 1) / 4f
        }
        binding.alphaSb.setOnSeekBarChangeListener { progress, _ ->
            binding.viewState?.alphaValue = progress
        }
        binding.redSb.setOnSeekBarChangeListener { progress, _ ->
            binding.viewState?.redValue = progress
        }
        binding.greenSb.setOnSeekBarChangeListener { progress, _ ->
            binding.viewState?.greenValue = progress
        }
        binding.blueSb.setOnSeekBarChangeListener { progress, _ ->
            binding.viewState?.blueValue = progress
        }

        binding.originIv.post {
            cropBitmap = binding.originIv.drawToBitmap(top = this.dp2px(149f))
            val blurBitmap = blur(cropBitmap, 25f, scale = 0.3f,
                filterColor = Color.argb(50, 100, 100, 100))
            binding.blurIv.setImageBitmap(blurBitmap)
        }
    }

    private fun setBlurView(
        scale: Float = 1f,
        blurRadius: Float = 10f,
        filterColor: Int = Color.TRANSPARENT
    ) {
        if (!::cropBitmap.isInitialized) return
        val blurBitmap = blur(cropBitmap, blurRadius, scale, filterColor)
        binding.blurIv.setImageBitmap(blurBitmap)
    }

    private fun View.drawToBitmap(
        config: Bitmap.Config = Bitmap.Config.ARGB_8888,
        left: Int = 0,
        top: Int = 0,
        right: Int = width,
        bottom: Int = height
    ): Bitmap {
        if (!ViewCompat.isLaidOut(this)) {
            error("View needs to be laid out before calling drawToBitmap()")
        }
        val viewBitmap = Bitmap.createBitmap(width, height, config).applyCanvas {
            translate(-scrollX.toFloat(), -scrollY.toFloat())
            draw(this)
        }
        val cropWidth = (right - left).takeIf { it >= 0 } ?: 0
        val cropHeight = (bottom - top).takeIf { it >= 0 } ?: 0
        return Bitmap.createBitmap(viewBitmap, left, top, cropWidth, cropHeight)
    }

    private inline fun SeekBar.setOnSeekBarChangeListener(
        crossinline onProgressChanged: (progress: Int, fromUser: Boolean) -> Unit
    ) {
        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                onProgressChanged(progress, fromUser)
            }
        })
    }

    companion object {
        val demoItem = DemoItem("drag-list",
            R.string.blur_image_demo_name,
            R.string.blur_image_describe,
            BlurImageActivity::class.java, R.mipmap.img_practice)
    }
}