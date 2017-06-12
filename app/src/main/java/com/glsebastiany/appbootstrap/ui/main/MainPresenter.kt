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

package com.glsebastiany.appbootstrap.ui.main

import android.os.Bundle
import com.androidhuman.rxfirebase2.database.ChildAddEvent
import com.androidhuman.rxfirebase2.database.ChildChangeEvent
import com.androidhuman.rxfirebase2.database.ChildMoveEvent
import com.androidhuman.rxfirebase2.database.ChildRemoveEvent
import com.glsebastiany.appbootstrap.core.di.ApplicationComponent
import com.glsebastiany.appbootstrap.core.nucleus5.KotlinRxPresenter
import com.glsebastiany.appbootstrap.data.SimpleData
import com.glsebastiany.appbootstrap.domain.usecases.ListenToSimpleData
import io.reactivex.functions.Consumer
import nucleus5.presenter.Factory
import javax.inject.Inject


class MainPresenter : KotlinRxPresenter<MainActivity>() {

    private var name = DEFAULT_NAME

    @Inject
    lateinit  var listenToSimpleData: ListenToSimpleData

    override fun inject(injector: ApplicationComponent) {
        injector.inject(this)
    }

    public override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        if (savedState != null)
            name = savedState.getString(NAME_KEY)

        restartableWithView(
                REQUEST_ITEMS,
                Factory { listenToSimpleData.execute(null) },
                Consumer { delivery ->
                    val event = delivery.value
                    val view = delivery.view

                    if (view != null)
                        when (event) {
                            is ChildAddEvent -> {
                                val data = event.dataSnapshot().getValue(SimpleData::class.java)
                                data?.let { view.adapter.add(event.dataSnapshot().key, event.previousChildName(), it) }
                            }

                            is ChildChangeEvent -> {
                                val data = event.dataSnapshot().getValue(SimpleData::class.java)
                                data?.let { view.adapter.change(event.dataSnapshot().key, it) }
                            }

                            is ChildRemoveEvent -> {
                                view.adapter.remove(event.dataSnapshot().key)
                            }

                            is ChildMoveEvent -> {
                                event.previousChildName()?.let { view.adapter.move(event.dataSnapshot().key, it) }
                            }
                        }
                },
                Consumer { throwable -> println(throwable) })

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
