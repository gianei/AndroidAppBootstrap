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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidhuman.rxfirebase2.database.ChildAddEvent
import com.androidhuman.rxfirebase2.database.ChildChangeEvent
import com.androidhuman.rxfirebase2.database.ChildMoveEvent
import com.androidhuman.rxfirebase2.database.ChildRemoveEvent
import com.glsebastiany.appbootstrap.R
import com.glsebastiany.appbootstrap.databinding.FragmentHomeBinding
import com.glsebastiany.appbootstrap.model.ListenSampleData


class HomeFragment : Fragment() {

    val adapter = HomeAdapter()

    lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate<FragmentHomeBinding>(
                inflater, R.layout.fragment_home, container, false)

        binding.mainList?.adapter = adapter
        binding.mainList?.layoutManager = android.support.v7.widget.LinearLayoutManager(context)

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        homeViewModel.getLiveData().observe(this, Observer { event ->
            //TODO make proper binding using Data Binding
            when (event) {
                is ChildAddEvent -> {
                    val data = event.dataSnapshot().getValue(ListenSampleData::class.java)
                    data?.let { adapter.add(event.dataSnapshot().key, event.previousChildName(), it) }
                }

                is ChildChangeEvent -> {
                    val data = event.dataSnapshot().getValue(ListenSampleData::class.java)
                    data?.let { adapter.change(event.dataSnapshot().key, it) }
                }

                is ChildRemoveEvent -> {
                    adapter.remove(event.dataSnapshot().key)
                }

                is ChildMoveEvent -> {
                    event.previousChildName()?.let { adapter.move(event.dataSnapshot().key, it) }
                }
            }
        })

        return binding.root
    }

}