package xyz.dean.androiddemos.demos.blur_image

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_blur_image)
        binding.originIv.post {
            val bitmap = binding.originIv.drawToBitmap(top = this.dp2px(149f))
            val blurBitmap = blur(bitmap, 25f)
            binding.blurIv.setImageBitmap(blurBitmap)
        }
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
            drawARGB(150, 150, 150, 150)
        }
        val cropWidth = (right - left).takeIf { it >= 0 } ?: 0
        val cropHeight = (bottom - top).takeIf { it >= 0 } ?: 0
        return Bitmap.createBitmap(viewBitmap, left, top, cropWidth, cropHeight)
    }

    companion object {
        val demoItem = DemoItem("drag-list",
            R.string.blur_image_demo_name,
            R.string.blur_image_describe,
            BlurImageActivity::class.java, R.mipmap.img_practice)
    }
}