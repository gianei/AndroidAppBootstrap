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

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.glsebastiany.appbootstrap.core.application.AppSingletons
import com.glsebastiany.appbootstrap.domain.usecases.CryptoCompareUseCase
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject


class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var cryptoApi: CryptoCompareUseCase

    init {
        AppSingletons.injector.inject(this)
    }

    private var subscription: Disposable? = null

    private val sampleData: MutableLiveData<String> = MutableLiveData()

    fun getSampleData(): LiveData<String> {
        if (sampleData.value == null) loadSampleData()
        return sampleData
    }

    private fun loadSampleData() {
        subscription = cryptoApi
                .execute(CryptoCompareUseCase.Params("BTC", "LTC"))
                .subscribe(
                        { result ->
                            sampleData.value = result.amount.toString()
                        },
                        Timber::e //TODO configure timber logging
                )
    }

    override fun onCleared() {
        if (subscription?.isDisposed == false) subscription?.dispose()
    }

}