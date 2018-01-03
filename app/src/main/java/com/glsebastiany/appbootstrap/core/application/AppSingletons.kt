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

import android.content.Context

import com.glsebastiany.appbootstrap.core.di.ApplicationComponent
import com.glsebastiany.appbootstrap.core.di.ApplicationModule
import com.glsebastiany.appbootstrap.core.di.DaggerApplicationComponent


class AppSingletons {

    lateinit var applicationComponent: ApplicationComponent
        private set

    companion object {

        private val MUTEX = Any()

        private var myInstance: AppSingletons? = null

        fun start(applicationContext: Context): AppSingletons? {
            if (myInstance == null) {
                synchronized(MUTEX) {
                    if (myInstance == null) {
                        myInstance = AppSingletons()
                        myInstance!!.applicationComponent = DaggerApplicationComponent.builder()
                                .applicationModule(ApplicationModule(applicationContext.applicationContext))
                                .build()
                    }
                }
            }
            return myInstance
        }

        val instance: AppSingletons
            get() {
                if (myInstance == null) {
                    throw IllegalStateException("It has not been injected")
                }

                return myInstance!!
            }

        val injector: ApplicationComponent
            get() {
                return instance.applicationComponent
            }

    }

}
