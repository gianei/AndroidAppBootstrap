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

import com.glsebastiany.appbootstrap.domain.repository.retrofit.CriptoCompareApi
import com.glsebastiany.appbootstrap.domain.repository.retrofit.SampleJsonDataRepository
import com.glsebastiany.appbootstrap.domain.repository.retrofit.response.PriceResponse
import com.glsebastiany.appbootstrap.model.GetSampleData
import com.glsebastiany.appbootstrap.domain.repository.retrofit.response.RetrofitSampleData
import com.glsebastiany.appbootstrap.model.AppPriceResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CryptoCompareUseCase @Inject
internal constructor(): BaseUseCase<PriceResponse, AppPriceResponse, CryptoCompareUseCase.Params>(Schedulers.io(), AndroidSchedulers.mainThread()) {

    var params: Params? = null

    @Inject
    lateinit var repository: CriptoCompareApi


    override fun buildUseCaseObservable(params: Params): Observable<PriceResponse> {
        this.params = params
        return repository.getPrice(params.from, params.to)
    }

    override fun map(repoModel: PriceResponse): AppPriceResponse {
        val model = AppPriceResponse(params?.to ?: "", repoModel.priceMapping[params?.to ?: ""] ?: 0.0)
        return model
    }

    class Params(val from: String, val to: String)
}