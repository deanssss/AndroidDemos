package xyz.dean.framework.common.event

import androidx.annotation.Keep

@Keep
interface EventResultCombiner<T> {
    fun combine(result: List<T>): T
}