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

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.glsebastiany.appbootstrap.R
import com.glsebastiany.appbootstrap.databinding.FragmentHomeBinding
import nucleus5.factory.RequiresPresenter
import nucleus5.view.NucleusFragment

@RequiresPresenter(HomePresenter::class)
class HomeFragment : NucleusFragment<HomePresenter>() {

    val adapter = HomeAdapter()

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate<FragmentHomeBinding>(
                inflater, R.layout.fragment_home, container, false)

        presenter.request(HomePresenter.NAME_1)

        binding?.mainList?.adapter = adapter
        binding?.mainList?.layoutManager = android.support.v7.widget.LinearLayoutManager(context)
//        binding?.mainList?.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

        return binding.root
    }

    fun onNetworkError(throwable: Throwable) {
        android.widget.Toast.makeText(context, throwable.message, android.widget.Toast.LENGTH_LONG).show()
    }

    fun retrieveAdapter(): HomeAdapter {
        return adapter
    }

}