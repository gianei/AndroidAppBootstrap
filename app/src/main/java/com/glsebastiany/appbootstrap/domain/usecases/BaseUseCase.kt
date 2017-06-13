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
package com.glsebastiany.appbootstrap.domain.usecases

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.observers.DisposableObserver

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This interface represents a execution unit for different use cases (this means any use case
 * in the application should implement this contract).

 * By convention each BaseUseCase implementation will return the result using a [DisposableObserver]
 * that will execute its job in a background thread and will post the result in the UI thread.
 */
abstract class BaseUseCase<T, Params>
//  private final CompositeDisposable disposables;

internal constructor(private val threadExecutor: Scheduler, private val postExecutionThread: Scheduler)//    this.disposables = new CompositeDisposable();
{

    /**
     * Builds an [Observable] which will be used when executing the current [BaseUseCase].
     */
    internal abstract fun buildUseCaseObservable(params: Params): Observable<T>

    fun execute(params: Params): Observable<T> {
        return this.buildUseCaseObservable(params)
                .subscribeOn(threadExecutor)
                .observeOn(postExecutionThread)
    }

}
