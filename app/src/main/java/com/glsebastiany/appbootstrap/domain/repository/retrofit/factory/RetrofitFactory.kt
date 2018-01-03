/*
 * Copyright (c) 2018 Gianei Leandro Sebastiany
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

package com.glsebastiany.appbootstrap.domain.repository.retrofit.factory

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.glsebastiany.appbootstrap.BuildConfig
import com.glsebastiany.appbootstrap.domain.repository.retrofit.interceptor.ConnectivityInterceptor
import com.glsebastiany.appbootstrap.domain.repository.retrofit.interceptor.HeaderInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RetrofitFactory @Inject constructor(
        connectivityInterceptor: ConnectivityInterceptor,
        headerInterceptor: HeaderInterceptor) {

    private val rxAdapter: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

    val retrofit: Retrofit

    init {
        val clientBuilder = OkHttpClient.Builder()
                .addInterceptor(connectivityInterceptor)
                .addInterceptor(headerInterceptor)

        if (BuildConfig.DEBUG) {
            clientBuilder.addNetworkInterceptor(StethoInterceptor())

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            clientBuilder.addNetworkInterceptor(logging)
        }

        val client = clientBuilder.build()


        val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ") // TODO define in a constant the api date format
                .create()

        retrofit = Retrofit.Builder()
                .baseUrl("https://appbootsrap-516ea.firebaseio.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(rxAdapter)
                .build()

        //TODO Usually apps have one api, but it might have more. "https://min-api.cryptocompare.com"
    }

    fun <T> createRetrofitService(lifekeeApiClass: Class<T>): T = retrofit.create(lifekeeApiClass)
}