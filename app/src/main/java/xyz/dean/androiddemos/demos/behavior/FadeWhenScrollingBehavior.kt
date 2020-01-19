package xyz.dean.androiddemos.demos.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.common.log

//DEAN-TODO Data:2020-01-19 Todo:自定义Behavior实现view大小改变
class FadeWhenScrollingBehavior : CoordinatorLayout.Behavior<View> {
    constructor() : super()
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        log.d("Dean", "resolve dependency...")
        return dependency is ConstraintLayout
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        val dy = dependency.y / 147.0
        log.d("Dean", "dy: $dy")
        child.findViewById<View>(R.id.vi).alpha = dy.toFloat()

        return true
    }
}