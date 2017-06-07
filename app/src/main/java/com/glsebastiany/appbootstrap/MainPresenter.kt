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

package com.glsebastiany.appbootstrap

import android.os.Bundle
import com.glsebastiany.appbootstrap.domain.interactor.ListenToSimpleData
import com.glsebastiany.appbootstrap.nucleus5.KotlinRxPresenter
import com.glsebastiany.appbootstrap.splash.Singletons
import io.reactivex.functions.BiConsumer
import nucleus5.presenter.Factory
import javax.inject.Inject


class MainPresenter : KotlinRxPresenter<MainActivity>() {

    private var name = DEFAULT_NAME

    @Inject
    lateinit  var listenToSimpleData: ListenToSimpleData

    public override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        //TODO pass to abstract
        Singletons.instance.applicationComponent.inject(this)

        if (savedState != null)
            name = savedState.getString(NAME_KEY)

        restartableWithView(
                REQUEST_ITEMS,
                Factory { listenToSimpleData.execute(null) },
                BiConsumer { v, d -> v?.onItems(d, name) },
                BiConsumer { t1, t2 -> println(t2) })

        if (savedState == null)
            start(REQUEST_ITEMS)
    }

    public override fun onSave(state: Bundle) {
        super.onSave(state)
        state.putString(NAME_KEY, name)
    }

    fun request(name: String) {
        this.name = name
        start(REQUEST_ITEMS)
    }

    companion object {

        val NAME_1 = "Chuck Norris"
        val NAME_2 = "Jackie Chan"
        val DEFAULT_NAME = NAME_1

        private val REQUEST_ITEMS = 1

        private val NAME_KEY = MainPresenter::class.java.name + "#name"
    }
}
