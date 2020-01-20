package xyz.dean.androiddemos.demos.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.common.log

/* 解决方案二：自定义Behavior，建立内容视图与AppBar之间的依赖，根据AppBar的位置改变内容视图的高度。
 */

class AdjustViewSizeWhenScrollingBehavior : AppBarLayout.ScrollingViewBehavior {
    @Suppress("unused")
    constructor() : super()
    @Suppress("unused")
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var childOriginalHeight = 0
    private val tag = this.javaClass.simpleName

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        if (childOriginalHeight <= 0) {
            val header = parent.findViewById<AppBarLayout>(R.id.behavior_bar_layout)
            childOriginalHeight = child.measuredHeight - header.measuredHeight
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        if (childOriginalHeight > 0) {
            val offset = dependency.y.toInt()
            log.d(tag, "rv.height: ${child.height} ==> rvHeight: $childOriginalHeight - offset: $offset")
            child.let {
                it.layoutParams.height = childOriginalHeight - offset
                it.requestLayout()
            }
        }

        return super.onDependentViewChanged(parent, child, dependency)
    }
}