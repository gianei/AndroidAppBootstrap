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

package com.glsebastiany.appbootstrap.domain.repository

import android.content.Context
import com.glsebastiany.appbootstrap.data.SampleJsonData
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


class SampleJsonDataRetrofitRepository @Inject
constructor(private val context: Context) : SampleJsonDataRepository {

    var rxAdapter = RxJava2CallAdapterFactory.create()

    val BASE_URL = "https://appbootsrap-516ea.firebaseio.com"
    var retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(rxAdapter)
            .build()

    override fun getData(): Observable<SampleJsonData> {
        val apiService = retrofit.create(MyApiEndpointInterface::class.java)
        return apiService.getUser("id0")

    }


}
