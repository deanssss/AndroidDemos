package xyz.dean.androiddemos.demos.scaleview

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.common.log
import xyz.dean.androiddemos.databinding.ActivityScaleViewBinding
import kotlin.math.max
import kotlin.math.min

class ScaleViewActivity : BaseActivity() {
    private lateinit var binding: ActivityScaleViewBinding
    private lateinit var touchListener: CustomTouchListener

    override fun getDemoItem(): DemoItem? = demoItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scale_view)
        touchListener = CustomTouchListener(
            context = this,
            onScale = {
                applyScale(it)
            },
            onDoubleTapMove = { dx, dy ->
                applyTransformation(dx, dy)
            },
            onDoubleTapCanceled = {
                reformat()
            }
        )
        binding.containerRl.setOnTouchListener(touchListener)
        binding.resetBt.setOnClickListener {
            binding.containerFl.scaleX = 1f
            binding.containerFl.scaleY = 1f
            binding.containerFl.translationX = 0f
            binding.containerFl.translationY = 0f
            binding.resetBt.isVisible = false

            offsetX = 0f
            offsetY = 0f
            scaleFactor = 1f
        }
    }

    private fun applyScale(scaleFactor: Float) {
        this.scaleFactor *= scaleFactor
        // 限制缩放比例
        this.scaleFactor = max(0.5, min(this.scaleFactor.toDouble(), 3.0)).toFloat()
        log.d("DDDD", "缩放比例：${this.scaleFactor}")

        binding.containerFl.scaleX = this.scaleFactor
        binding.containerFl.scaleY = this.scaleFactor
    }

    private var offsetX: Float = 0f
    private var offsetY: Float = 0f
    private var scaleFactor = 1.0f

    private fun applyTransformation(dx: Float, dy: Float) {
        offsetX += dx
        offsetY += dy
        binding.containerFl.translationX = offsetX
        binding.containerFl.translationY = offsetY

        if (
            !binding.resetBt.isVisible
            && (binding.containerFl.translationX != 0f
                    || binding.containerFl.translationY != 0f
                    || binding.containerFl.scaleX != 1f
                    || binding.containerFl.scaleY != 1f)
        ) {
            binding.resetBt.isVisible = true
        } else if (
            binding.resetBt.isVisible
            && (binding.containerFl.translationX == 0f
                    && binding.containerFl.translationY == 0f
                    && binding.containerFl.scaleX == 1f
                    && binding.containerFl.scaleY == 1f)
        ) {
            binding.resetBt.isVisible = false
        }
    }

    private fun reformat() {
        if (scaleFactor < 1f) {
            applyTransformation(-offsetX, -offsetY)
        } else {
            val parentLeft = binding.containerRl.left
            val parentRight = binding.containerRl.right
            val parentTop = binding.containerRl.top
            val parentBottom = binding.containerRl.bottom

            log.d("DDDD", "pl: $parentLeft, pr: $parentRight, pt: $parentTop, pb: $parentBottom")

            val realLeft = binding.containerFl.left
            val realRight = binding.containerFl.right
            val realTop = binding.containerFl.top
            val realBottom = binding.containerFl.bottom

            log.d("DDDD", "rl: $realLeft, rr: $realRight, rt: $realTop, rb: $realBottom")

            val pointLt = floatArrayOf(realLeft.toFloat(), realTop.toFloat())
            val pointRb = floatArrayOf(realRight.toFloat(), realBottom.toFloat())
            binding.containerFl.matrix.mapPoints(pointLt)
            binding.containerFl.matrix.mapPoints(pointRb)

            val (tl, tt) = pointLt
            val (tr, tb) = pointRb

            log.d("DDDD", "tl: $tl, tr: $tr, tt: $tt, tb: $tb")

            var dx = 0f
            var dy = 0f
            if (tl > parentLeft) {
                log.d("DDDD", "out off left")
                dx = (parentLeft - tl)
            }
            if (tr < parentRight) {
                log.d("DDDD", "out off right")
                dx = (parentRight - tr)
            }
            if (tt > parentTop) {
                dy = (parentTop - tt)
                log.d("DDDD", "out off top")
            }
            if (tb < parentBottom) {
                log.d("DDDD", "out off borrom")
                dy = (parentBottom - tb)
            }
            applyTransformation(dx, dy)
        }
    }

    companion object {
        val demoItem = DemoItem("scale-view",
            "ScaleView",
            "ScaleView",
            ScaleViewActivity::class.java, R.mipmap.img_practice)
    }
}

class CustomTouchListener(
    context: Context,
    private val onScale: (scaleFactor: Float) -> Unit = {},
    private val onDoubleTapMove: (dx: Float, dy: Float) -> Unit = { _, _ -> },
    private val onDoubleTapCanceled: () -> Unit = {},
) : OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {
    private val mScaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(context, this)
    private var lastFocusX = 0f
    private var lastFocusY = 0f

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        var isHandled = mScaleGestureDetector.onTouchEvent(event)

        var sumX = 0f
        var sumY = 0f
        var focusX = 0f
        var focusY = 0f
        val pCount = event.pointerCount
        if (pCount == 2) {
            for (i in (0 until 2)) {
                sumX += event.getX(i)
                sumY += event.getY(i)
            }
            focusX = sumX / 2
            focusY = sumY / 2
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (pCount == 2) {
                    lastFocusX = focusX
                    lastFocusY = focusY
                    isHandled = true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (!mScaleGestureDetector.isInProgress && pCount == 2) {
                    val dx = focusX - lastFocusX
                    val dy = focusY - lastFocusY

                    log.d("DDDD", "update offset, ACTION_MOVE")
                    onDoubleTapMove(dx, dy)

                    lastFocusX = focusX
                    lastFocusY = focusY
                    isHandled = true
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                onDoubleTapCanceled()
            }

            MotionEvent.ACTION_UP -> {
            }
        }
        return if (isHandled) true else v.onTouchEvent(event)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        onScale(detector.scaleFactor)

        val focusX = detector.focusX
        val focusY = detector.focusY
        val dx = focusX - lastFocusX
        val dy = focusY - lastFocusY

        log.d("DDDD", "update offset, onScale")
        onDoubleTapMove(dx, dy)

        lastFocusX = focusX
        lastFocusY = focusY
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
    }
}

