package xyz.dean.androiddemos.utils.reactivex

import io.reactivex.*
import org.reactivestreams.Publisher
import java.util.concurrent.atomic.AtomicInteger

class RetryTransformer<T> internal constructor(
    private val maxRetryTimes: Int,
    private val onPreRetry: (Throwable) -> Completable,
    private val tag: String
) : ObservableTransformer<T, T>,
    FlowableTransformer<T, T>,
    SingleTransformer<T, T>,
    MaybeTransformer<T, T>,
    CompletableTransformer
{
    private val retryCount: AtomicInteger = AtomicInteger()

    private fun handleRetry(error: Throwable): Single<Unit> {
        return if (retryCount.incrementAndGet() <= maxRetryTimes) {
            onPreRetry(error)
                .doOnComplete { log.w(tag, "Retry stream with exception: ${error.message}") }
                .andThen(Single.just(Unit))
        } else {
            Single.error(error)
        }
    }

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.retryWhen { errFlow ->
            errFlow.flatMapSingle(this::handleRetry)
        }
    }

    override fun apply(upstream: Flowable<T>): Publisher<T> {
        return upstream.retryWhen { errFlow ->
            errFlow.flatMapSingle(this::handleRetry)
        }
    }

    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.retryWhen { errFlow ->
            errFlow.flatMapSingle(this::handleRetry)
        }
    }

    override fun apply(upstream: Maybe<T>): MaybeSource<T> {
        return upstream.retryWhen { errFlow ->
            errFlow.flatMapSingle(this::handleRetry)
        }
    }

    override fun apply(upstream: Completable): CompletableSource {
        return upstream.retryWhen { errFlow ->
            errFlow.flatMapSingle(this::handleRetry)
        }
    }
}