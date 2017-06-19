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
import com.glsebastiany.appbootstrap.ui.main.home.HomePresenter
import com.glsebastiany.appbootstrap.ui.main.dashboard.DashboardPresenter
import dagger.Component
import javax.inject.Singleton

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {
    // Exposed to sub-graphs. Calls like this must be inserted if it is necessary to expose Context
    // for sub components
    fun context(): Context

    // Calls like this are needed for every point of injection
    fun inject(mainPresenter: DashboardPresenter)
    fun inject(homePresenter: HomePresenter)
}
