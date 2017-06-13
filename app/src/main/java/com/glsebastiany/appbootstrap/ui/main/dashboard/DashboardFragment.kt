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

package com.glsebastiany.appbootstrap.ui.main.dashboard

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.glsebastiany.appbootstrap.R
import com.glsebastiany.appbootstrap.databinding.FragmentDashboardBinding
import nucleus5.factory.RequiresPresenter
import nucleus5.view.NucleusFragment

@RequiresPresenter(DashboardPresenter::class)
class DashboardFragment : NucleusFragment<DashboardPresenter>() {

    lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate<FragmentDashboardBinding>(
                inflater, R.layout.fragment_dashboard, container, false)

        presenter.request()

        return binding.root
    }

    fun updateText(value: String?){
        binding.textView?.text = "Hello " + (value ?: " none")
    }
}