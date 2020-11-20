package xyz.dean.androiddemos.utils.reactivex

import io.reactivex.*
import org.reactivestreams.Publisher

class SwitchTransformer<T> internal constructor(
    private val subscribeOn: Scheduler,
    private val observableOn: Scheduler
) : ObservableTransformer<T, T>,
    FlowableTransformer<T, T>,
    SingleTransformer<T, T>,
    MaybeTransformer<T, T>,
    CompletableTransformer
{
    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.subscribeOn(subscribeOn)
            .observeOn(observableOn)
    }

    override fun apply(upstream: Flowable<T>): Publisher<T> {
        return upstream.subscribeOn(subscribeOn)
            .observeOn(observableOn)
    }

    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.subscribeOn(subscribeOn)
            .observeOn(observableOn)
    }

    override fun apply(upstream: Maybe<T>): MaybeSource<T> {
        return upstream.subscribeOn(subscribeOn)
            .observeOn(observableOn)
    }

    override fun apply(upstream: Completable): CompletableSource {
        return upstream.subscribeOn(subscribeOn)
            .observeOn(observableOn)
    }
}