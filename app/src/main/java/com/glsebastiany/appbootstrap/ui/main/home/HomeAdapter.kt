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

package com.glsebastiany.appbootstrap.ui.main.home

import com.glsebastiany.appbootstrap.model.ListenSampleData
import com.glsebastiany.appbootstrap.ui.widgets.RVListenerBaseAdapter

class HomeAdapter : RVListenerBaseAdapter<ListenSampleData>() {

    override fun getLayoutIdForPosition(position: Int): Int {
        return com.glsebastiany.appbootstrap.R.layout.home_card
    }

}
