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

package com.glsebastiany.appbootstrap.widgets

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

abstract class RVBaseAdapter : RecyclerView.Adapter<RVGenericViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVGenericViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = DataBindingUtil.inflate<ViewDataBinding>(
                layoutInflater, viewType, parent, false)

        return RVGenericViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RVGenericViewHolder,
                                  position: Int) {
        val model = getModelObjForPosition(holder.binding.root.context, position)
        holder.bindModel(model)

        val handler = getHandlerObjForPosition(position)
        if (handler != null) {
            holder.bindHandler(handler)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutIdForPosition(position)
    }

    protected abstract fun getModelObjForPosition(context: Context, position: Int): Any

    protected fun getHandlerObjForPosition(position: Int): Any? {
        return null
    }

    protected abstract fun getLayoutIdForPosition(position: Int): Int
}