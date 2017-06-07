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

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class KotlinTransformer<View, T>(private val view: Observable<View?>) : ObservableTransformer<T, KotlinDelivery<View?, T>> {

    override fun apply(observable: Observable<T>): ObservableSource<KotlinDelivery<View?, T>> {
        return observable.map {
            data -> KotlinDelivery<View?, T>(view.blockingFirst(), data)
        }
    }

}
