@file:Suppress("unused")

package xyz.dean.androiddemos.utils.reactivex

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.schedulers.Schedulers
import xyz.dean.androiddemos.utils.Log

internal val log: Log by lazy { xyz.dean.androiddemos.common.log }

const val MAIN = "io.reactivex.android:main"
const val IO = SchedulerSupport.IO
const val COMPUTATION = SchedulerSupport.COMPUTATION
const val CUSTOM = SchedulerSupport.CUSTOM
const val NONE = SchedulerSupport.NONE

@SchedulerSupport(CUSTOM)
fun <T> switchThread(
    subscribeOn: Scheduler = Schedulers.io(),
    observableOn: Scheduler = AndroidSchedulers.mainThread()
): SwitchTransformer<T> {
    return SwitchTransformer(subscribeOn, observableOn)
}

@SchedulerSupport(NONE)
fun <T> rxRetry(
    maxRetryTimes: Int = 3,
    onPreRetry: (Throwable) -> Completable = { Completable.complete() },
    tag: String = "RxRetry"
): RetryTransformer<T> {
    return RetryTransformer(maxRetryTimes, onPreRetry, tag)
}