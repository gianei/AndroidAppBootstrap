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

package com.glsebastiany.appbootstrap.ui.widgets

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import com.glsebastiany.appbootstrap.BR

class RVGenericViewHolder(val binding: android.databinding.ViewDataBinding) : android.support.v7.widget.RecyclerView.ViewHolder(binding.root) {

    fun bindModel(model: Any) {
        binding.setVariable(com.glsebastiany.appbootstrap.BR.obj, model)
        binding.executePendingBindings()
    }

    fun bindHandler(handler: Any) {
        binding.setVariable(com.glsebastiany.appbootstrap.BR.handler, handler)
    }
}