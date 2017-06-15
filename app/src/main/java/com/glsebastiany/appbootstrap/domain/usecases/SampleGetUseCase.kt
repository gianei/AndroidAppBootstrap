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

import com.glsebastiany.appbootstrap.domain.repository.retrofit.SampleJsonDataRepository
import com.glsebastiany.appbootstrap.model.GetSampleData
import com.glsebastiany.appbootstrap.domain.repository.retrofit.response.RetrofitSampleData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SampleGetUseCase @Inject
internal constructor(): BaseUseCase<RetrofitSampleData, GetSampleData, SampleGetUseCase.Params>(Schedulers.io(), AndroidSchedulers.mainThread()) {

    @Inject
    lateinit var repository: SampleJsonDataRepository

    override fun buildUseCaseObservable(params: Params): Observable<RetrofitSampleData> {
        return repository.getData(params.id)
    }

    override fun map(repoModel: RetrofitSampleData): GetSampleData {
        val model = GetSampleData("App Model")
        model.name = repoModel.name
        return model
    }

    class Params(val id:String)
}