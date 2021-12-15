package xyz.dean.androiddemos.demos.rating_bar

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import xyz.dean.androiddemos.R
import kotlin.math.abs
import kotlin.math.roundToInt


@Suppress("MemberVisibilityCanBePrivate")
class CusRatingBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    var mStarNormal: Drawable? = null
        set(starNormal) {
            field = starNormal
            invalidate()
        }
    var mStarChecked: Drawable? = null
        set(star) {
            field = star
            invalidate()
        }
    var mStarNum: Int = DEFAULT_STAR_NUM
        set(starNum) {
            field = starNum
            invalidate()
        }
    var mStarStep: Float = DEFAULT_STAR_STEP
        set(starStep) {
            field = starStep
            invalidate()
        }
    var mStarSpace: Int = DEFAULT_STAR_SPACE
        set(starGap) {
            field = starGap
            invalidate()
        }
    var mStarSize: Int = DEFAULT_STAR_SIZE
        set(starSize) {
            field = starSize
            invalidate()
        }
    var mRating: Float = 0f
        set(rating) {
            field = rating
            invalidate()
        }

    var onRatingBarChanged: ((rating: Float) -> Unit)? = null

    private var isIndicator: Boolean
    private val drawRect = Rect()
    private val starPaint: Paint by lazy { Paint().apply { isAntiAlias = true } }

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CusRatingBar)
        mStarNormal = typedArray.getDrawable(R.styleable.CusRatingBar_starNormal)
        mStarChecked = typedArray.getDrawable(R.styleable.CusRatingBar_starChecked)
        mStarNum = typedArray.getInteger(R.styleable.CusRatingBar_starNum, DEFAULT_STAR_NUM)
        mStarStep = typedArray.getFloat(R.styleable.CusRatingBar_starStep, DEFAULT_STAR_STEP)
        mStarSpace = typedArray.getDimensionPixelOffset(R.styleable.CusRatingBar_starSpace, DEFAULT_STAR_SPACE)
        mStarSize = typedArray.getDimensionPixelOffset(R.styleable.CusRatingBar_starSize, DEFAULT_STAR_SIZE)
        mRating = typedArray.getFloat(R.styleable.CusRatingBar_rating, 0f)
        isIndicator = typedArray.getBoolean(R.styleable.CusRatingBar_isIndicator, true)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)

        when (widthMode) {
            MeasureSpec.AT_MOST -> {
                widthSize = paddingLeft + paddingRight
                if (mStarNum > 0) {
                    widthSize += mStarNum * mStarSize + (mStarNum - 1) * mStarSpace
                }
            }
            MeasureSpec.UNSPECIFIED -> widthSize = suggestedMinimumWidth
            MeasureSpec.EXACTLY -> { }
        }

        when (heightMode) {
            MeasureSpec.AT_MOST -> heightSize = paddingTop + paddingBottom + mStarSize
            MeasureSpec.UNSPECIFIED -> heightSize = suggestedMinimumHeight
            MeasureSpec.EXACTLY -> { }
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radius = mStarSize / 2
        canvas.translate(radius.toFloat(), radius.toFloat())

        mStarNormal?.let { drawStarDrawable(canvas, it, mStarNum) }

        val size = mRating.roundToInt()
        var decimal = 0f
        // 根据步长获取小数位
        if (size > mRating) {
            decimal = mRating - size + 1
            val rate = (decimal / mStarStep).toInt()
            decimal = rate * mStarStep
        }
        val right = (mRating.toInt() * (mStarSize + mStarSpace) + decimal * mStarSize - radius).toInt()
        canvas.clipRect(-radius, -radius, right, mStarSize - radius)
        mStarChecked?.let { drawStarDrawable(canvas, it, size) }
    }

    private fun drawStarDrawable(canvas: Canvas, starDrawable: Drawable, starNum: Int) {
        val bitmap = (starDrawable as? BitmapDrawable)?.bitmap ?: return
        var spacing = 0
        val radius = mStarSize / 2
        for (i in 0 until starNum) {
            drawRect.apply {
                left = i * mStarSize - radius + spacing
                top = -radius
                right = (i + 1) * mStarSize - radius + spacing
                bottom = mStarSize - radius
            }
            canvas.drawBitmap(bitmap, null, drawRect, starPaint)
            spacing += mStarSpace
        }
    }

    private var lastX = 0f
    private var lastY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isIndicator) {
            return super.onTouchEvent(event)
        }

        mRating = (event.x / (mStarSize + mStarSpace))
            .nextUp(mStarStep)
            .limitRange()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastX
                val dy = event.y - lastY
                if (abs(dx) < abs(dy)) {
                    parent?.requestDisallowInterceptTouchEvent(false)
                }
            }
            MotionEvent.ACTION_UP -> {
                onRatingBarChanged?.invoke(mRating)
            }
        }
        lastX = event.x
        lastY = event.y
        return true
    }

    private fun Float.nextUp(step: Float): Float {
        val c = (this / step).toInt()
        return if (this - c * step < step * 0.3) c * step
        else (c + 1) * step
    }

    private fun Float.limitRange() = when {
        this > mStarNum -> mStarNum.toFloat()
        this < 0 -> 0f
        else -> this
    }

    companion object {
        private const val DEFAULT_STAR_NUM = 5
        private const val DEFAULT_STAR_STEP = 1f
        private const val DEFAULT_STAR_SPACE = 0
        private const val DEFAULT_STAR_SIZE = 80
    }
}