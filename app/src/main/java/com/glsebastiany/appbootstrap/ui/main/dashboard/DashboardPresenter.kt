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

package com.glsebastiany.appbootstrap.ui.main.dashboard

import android.os.Bundle
import com.glsebastiany.appbootstrap.core.di.ApplicationComponent
import com.glsebastiany.appbootstrap.core.nucleus5.KotlinRxPresenter
import com.glsebastiany.appbootstrap.domain.usecases.GetSampleJson
import io.reactivex.functions.Consumer
import nucleus5.presenter.Factory
import javax.inject.Inject


class DashboardPresenter : KotlinRxPresenter<DashboardFragment>() {

    @Inject
    lateinit var useCase : GetSampleJson

    override fun inject(injector: ApplicationComponent) {
        injector.inject(this)
    }

    public override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        restartableWithView(
                OBSERVABLE_ID,
                Factory { useCase.execute(GetSampleJson.Params("id0")) },
                Consumer { delivery ->
                    val value = delivery.value
                    val view = delivery.view

                    view?.updateText(value.name)
                },
                Consumer { throwable -> println(throwable) })

        if (savedState == null)
            start(OBSERVABLE_ID)
    }

    fun request(){
        start(OBSERVABLE_ID)
    }


    companion object {
        private val OBSERVABLE_ID = 1
    }


}