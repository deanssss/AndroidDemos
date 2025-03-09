package xyz.dean.util

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

inline val @receiver:StringRes Int.str get() = ApplicationHolder.application.getString(this)
fun @receiver:StringRes Int.str(vararg formatArgs: Any?) = ApplicationHolder.application.getString(this, *formatArgs)

@get:ColorInt inline val @receiver:ColorRes Int.color get() = ContextCompat.getColor(ApplicationHolder.application, this)
inline val @receiver:ColorRes Int.colorStateList get() = ContextCompat.getColorStateList(ApplicationHolder.application, this)

inline val @receiver:DrawableRes Int.drawable get() = ContextCompat.getDrawable(ApplicationHolder.application, this)

inline val @receiver:DimenRes Int.dimen get() = ApplicationHolder.application.resources.getDimensionPixelSize(this)