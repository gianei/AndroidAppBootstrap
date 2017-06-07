/*
 * Copyright (c) 2017 Gianei Leandro Sebastiany
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.glsebastiany.appbootstrap.nucleus5

import android.os.Bundle
import android.support.annotation.CallSuper

import java.util.ArrayList
import java.util.HashMap

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiConsumer
import io.reactivex.functions.Consumer
import io.reactivex.subjects.BehaviorSubject
import nucleus5.presenter.Factory
import nucleus5.presenter.Presenter
import nucleus5.presenter.delivery.Delivery

/**
 * This is an extension of [Presenter] which provides RxJava functionality.

 * @param <View> a type of view.
</View> */
open class KotlinRxPresenter<View> : Presenter<View?>() {

    private val views = BehaviorSubject.create<View?>()
    private val disposables = CompositeDisposable()

    private val restartables = HashMap<Int, Factory<Disposable>>()
    private val restartableDisposables = HashMap<Int, Disposable>()
    private val requested = ArrayList<Int>()

    /**
     * Returns an [Observable] that emits the current attached view or null.
     * See [BehaviorSubject] for more information.

     * @return an observable that emits the current attached view or null.
     */
    fun view(): Observable<View?> {
        return views
    }

    /**
     * Registers a disposable to automatically dispose it during onDestroy.
     * See [CompositeDisposable.add] for details.}

     * @param disposable a disposable to add.
     */
    fun add(disposable: Disposable) {
        this.disposables.add(disposable)
    }

    /**
     * Removes and unsubscribes a disposable that has been registered with [.add] previously.
     * See [CompositeDisposable.remove] for details.

     * @param disposable a disposable to remove.
     */
    fun remove(disposable: Disposable) {
        this.disposables.remove(disposable)
    }

    /**
     * A restartable is any RxJava observable that can be started (subscribed) and
     * should be automatically restarted (re-subscribed) after a process restart if
     * it was still subscribed at the moment of saving presenter's state.

     * Registers a factory. Re-subscribes the restartable after the process restart.

     * @param restartableId id of the restartable
     * *
     * @param factory       factory of the restartable
     */
    fun restartable(restartableId: Int, factory: Factory<Disposable>) {
        restartables.put(restartableId, factory)
        if (requested.contains(restartableId))
            start(restartableId)
    }

    /**
     * Starts the given restartable.

     * @param restartableId id of the restartable
     */
    fun start(restartableId: Int) {
        stop(restartableId)
        requested.add(restartableId)
        restartableDisposables.put(restartableId, restartables[restartableId]!!.create())
    }

    /**
     * Disposes a restartable

     * @param restartableId id of a restartable.
     */
    fun stop(restartableId: Int) {
        requested.remove(restartableId.toInt())
        val disposable = restartableDisposables[restartableId]
        disposable?.dispose()
    }

    /**
     * Checks if a restartable is disposed.

     * @param restartableId id of the restartable.
     * *
     * @return true if the disposable is null or disposed, false otherwise.
     */
    fun isDisposed(restartableId: Int): Boolean {
        val disposable = restartableDisposables[restartableId]
        return disposable == null || disposable.isDisposed
    }

    fun <T> restartableWithView(restartableId: Int, observableFactory: Factory<Observable<T>>,
                                onNext: BiConsumer<View, T>, onError: BiConsumer<View, Throwable>?){
        restartable(restartableId, Factory {
            observableFactory.create()
                    .compose (KotlinTransformer<View, T>(view()))
                    .subscribe { split(onNext, onError)}
        })
    }

//    fun <T> restartableReplay(restartableId: Int, observableFactory: Factory<Observable<T>>,
//                              onNext: BiConsumer<View, T>, onError: BiConsumer<View, Throwable>?) {
//
//        restartable(restartableId, Factory {
//            observableFactory.create()
//                    .compose(this@KotlinRxPresenter.deliverReplay<T>())
//                    .subscribe(split(onNext, onError))
//        })
//    }


    /**
     * Returns an [io.reactivex.ObservableTransformer] that couples views with data that has been emitted by
     * the source [Observable].

     * [.deliverReplay] keeps all onNext values and emits them each time a new view gets attached.
     * If a new onNext value appears while a view is attached, it will be delivered immediately.

     * @param <T> the type of source observable emissions
    </T> */
//    fun <T> deliverReplay(): DeliverReplay<View?, T> {
//        return DeliverReplay(views)
//    }

    /**
     * Returns a method that can be used for manual restartable chain build. It returns an Action1 that splits
     * a received [Delivery] into two [BiConsumer] onNext and onError calls.

     * @param onNext  a method that will be called if the delivery contains an emitted onNext value.
     * *
     * @param onError a method that will be called if the delivery contains an onError throwable.
     * *
     * @param <T>     a type on onNext value.
     * *
     * @return an Action1 that splits a received [Delivery] into two [BiConsumer] onNext and onError calls.
    </T> */
    fun <T> split(onNext: BiConsumer<View, T>, onError: BiConsumer<View, Throwable>?): Consumer<Delivery<View, T>> {
        return Consumer { delivery -> delivery.split(onNext, onError) }
    }

    /**
     * This is a shortcut for calling [.split] when the second parameter is null.
     */
    fun <T> split(onNext: BiConsumer<View, T>): Consumer<Delivery<View, T>> {
        return split(onNext, null)
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    override fun onCreate(savedState: Bundle?) {
        if (savedState != null)
            requested.addAll(savedState.getIntegerArrayList(REQUESTED_KEY))
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    override fun onDestroy() {
        views.onComplete()
        disposables.dispose()
        for ((_, value) in restartableDisposables)
            value.dispose()
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    override fun onSave(state: Bundle) {
        for (i in requested.indices.reversed()) {
            val restartableId = requested[i]
            val disposable = restartableDisposables[restartableId]
            if (disposable != null && disposable.isDisposed)
                requested.removeAt(i)
        }
        state.putIntegerArrayList(REQUESTED_KEY, requested)
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    override fun onTakeView(view: View?) {
        views.onNext(view)
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    override fun onDropView() {
//        views.onNext(OptionalView<View>(null))
    }

    /**
     * Please, use restartableXX and deliverXX methods for pushing data from RxPresenter into View.
     */
    @Deprecated("")
    override fun getView(): View? {
        return super.getView()
    }

    companion object {

        private val REQUESTED_KEY = KotlinRxPresenter::class.java.name + "#requested"
    }
}
