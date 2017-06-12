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

import android.content.Context

abstract class RVListenerBaseAdapter<T> : RVBaseAdapter() {

    private val list = mutableListOf<Wrapper<T>>()

    override fun getModelObjForPosition(context: Context, position: Int): Any {
        return list[position].data as Any
    }

    override fun getItemCount(): Int {
        return list.count();
    }

    fun add(id: String, prevId: String?, data: T) {
        val newIndex = getNewIndex(prevId)

        list.add(newIndex, Wrapper(id, data))

        notifyItemInserted(newIndex)
    }

    fun change(id: String, data: T) {
        val changedIndex = indexOf(id)

        if (validIndex(changedIndex)) {
            val changedWrapper = list.getOrNull(changedIndex);
            if (changedWrapper != null) {
                changedWrapper.data = data
                notifyItemChanged(changedIndex)
            }
        }
    }

    fun move(id: String, prevId: String) {
        val oldIndex = indexOf(id)
        if (validIndex(oldIndex)) {
            val newIndex = getNewIndex(prevId)

            val removedWrapper = list.removeAt(oldIndex)

            list.add(newIndex, removedWrapper)

            notifyItemMoved(oldIndex, newIndex)
        }
    }

    fun remove(id: String) {
        val removedPos = indexOf(id)
        if (validIndex(removedPos)) {
            list.removeAt(removedPos)
            notifyItemRemoved(removedPos)
        }
    }

    private fun getNewIndex(prevId: String?) = getPreviousPosition(prevId) + 1

    private fun validIndex(index: Int) = index >= 0 && index < list.size

    private fun indexOf(id: String) = list.indexOfFirst { wrapper -> wrapper.id == id }

    fun getPreviousPosition(previousId: String?): Int {
        return list.indexOfFirst { wrapper -> wrapper.id == previousId }
    }

    private class Wrapper<T>(var id: String, var data: T)
}