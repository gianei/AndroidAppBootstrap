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

package com.glsebastiany.appbootstrap.core.di

import com.glsebastiany.appbootstrap.domain.repository.firebase.SampleDataFirebaseRepository
import com.glsebastiany.appbootstrap.domain.repository.firebase.SampleDataRepository
import com.glsebastiany.appbootstrap.domain.repository.retrofit.CriptoCompareApi
import com.glsebastiany.appbootstrap.domain.repository.retrofit.SampleJsonDataRepository
import com.glsebastiany.appbootstrap.domain.repository.retrofit.factory.RetrofitFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Dagger module that provides objects which will live during the application lifecycle.
 */
@Module
class RepositoryModule() {

    @Provides
    @Singleton
    internal fun simpleDataRepository(repository: SampleDataFirebaseRepository): SampleDataRepository =
            repository

    @Provides
    @Singleton
    internal fun jsonDataRepository(retrofitFactory: RetrofitFactory): SampleJsonDataRepository = retrofitFactory.createRetrofitService(SampleJsonDataRepository::class.java)

    @Provides
    @Singleton
    internal fun cryptoCompareRepository(retrofitFactory: RetrofitFactory): CriptoCompareApi = retrofitFactory.createRetrofitService(CriptoCompareApi::class.java)

}
