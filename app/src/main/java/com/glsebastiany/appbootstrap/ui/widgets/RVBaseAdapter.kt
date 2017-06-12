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

abstract class RVBaseAdapter : android.support.v7.widget.RecyclerView.Adapter<RVGenericViewHolder>() {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): RVGenericViewHolder {
        val layoutInflater = android.view.LayoutInflater.from(parent.context)

        val binding = android.databinding.DataBindingUtil.inflate<android.databinding.ViewDataBinding>(
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

    protected abstract fun getModelObjForPosition(context: android.content.Context, position: Int): Any

    protected fun getHandlerObjForPosition(position: Int): Any? {
        return null
    }

    protected abstract fun getLayoutIdForPosition(position: Int): Int
}