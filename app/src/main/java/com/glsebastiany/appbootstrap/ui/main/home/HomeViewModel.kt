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

package com.glsebastiany.appbootstrap.ui.main.home

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.androidhuman.rxfirebase2.database.ChildEvent
import com.glsebastiany.appbootstrap.core.application.getApplicationComponent
import com.glsebastiany.appbootstrap.domain.usecases.SampleListenUseCase
import timber.log.Timber
import javax.inject.Inject


class HomeViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var listenSampleData: SampleListenUseCase

    private val useCaseChildEvents: MutableLiveData<ChildEvent> = MutableLiveData()

    init {
        application.getApplicationComponent().inject(this)
    }

    fun getLiveData(): LiveData<ChildEvent> {
        if (useCaseChildEvents.value == null) load()
        return useCaseChildEvents
    }

    private fun load() {

        listenSampleData
                .execute(Any())
                .subscribe(
                        { result ->
                            useCaseChildEvents.value = result
                        },
                        Timber::e //TODO add generic error handler for Connectivity error
                )

    }

}
