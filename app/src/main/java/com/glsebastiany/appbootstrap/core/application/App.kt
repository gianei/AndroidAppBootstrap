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

package com.glsebastiany.appbootstrap.core.application

import android.app.Application
import android.content.Context
import com.glsebastiany.appbootstrap.BuildConfig
import com.glsebastiany.appbootstrap.core.di.ApplicationComponent
import com.glsebastiany.appbootstrap.core.di.ApplicationModule
import com.glsebastiany.appbootstrap.core.di.DaggerApplicationComponent
import timber.log.Timber

class App : Application(){

    lateinit var applicationComponent: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()

        startDependencyInjection()

        configureTimber()

    }

    private fun configureTimber() {
        // TODO add integration with logging tools
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun startDependencyInjection() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(applicationContext.applicationContext))
                .build()
    }
}

fun Context.getApplicationComponent() : ApplicationComponent =
        (this.applicationContext as App).applicationComponent
