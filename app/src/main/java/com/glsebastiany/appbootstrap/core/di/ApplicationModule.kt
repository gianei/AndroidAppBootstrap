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

package com.glsebastiany.appbootstrap.core.di

import android.content.Context
import com.glsebastiany.appbootstrap.domain.repository.SimpleDataFirebaseRepository
import com.glsebastiany.appbootstrap.domain.repository.SimpleDataRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Dagger module that provides objects which will live during the application lifecycle.
 */
@Module
class ApplicationModule(context: Context) {
    private val context: Context

    init {
        this.context = context.applicationContext
    }

    @Provides
    @Singleton
    internal fun provideApplicationContext(): Context {
        return this.context
    }

    @Provides
    @Singleton
    internal fun simpleDataRepository(repository: SimpleDataFirebaseRepository): SimpleDataRepository {
        return repository
    }


}
