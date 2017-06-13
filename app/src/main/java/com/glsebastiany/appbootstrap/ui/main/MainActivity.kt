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

package com.glsebastiany.appbootstrap.ui.main

import com.glsebastiany.appbootstrap.R
import com.glsebastiany.appbootstrap.databinding.ActivityMainBinding
import com.glsebastiany.appbootstrap.ui.main.dashboard.DashboardFragment
import nucleus5.factory.RequiresPresenter
import nucleus5.view.NucleusActivity

@RequiresPresenter(MainPresenter::class)
class MainActivity : NucleusActivity<MainPresenter>() {

    var binding: ActivityMainBinding? = null

    val adapter = MainAdapter()

    private val mOnNavigationItemSelectedListener = android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            com.glsebastiany.appbootstrap.R.id.navigation_home -> {
                binding?.message?.setText(com.glsebastiany.appbootstrap.R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            com.glsebastiany.appbootstrap.R.id.navigation_dashboard -> {
                binding?.message?.setText(com.glsebastiany.appbootstrap.R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            com.glsebastiany.appbootstrap.R.id.navigation_notifications -> {
                binding?.message?.setText(com.glsebastiany.appbootstrap.R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.glsebastiany.appbootstrap.R.layout.activity_main)
        binding = android.databinding.DataBindingUtil.setContentView(this, com.glsebastiany.appbootstrap.R.layout.activity_main);

        binding?.navigation?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        binding?.mainList?.adapter = adapter
        binding?.mainList?.layoutManager = android.support.v7.widget.LinearLayoutManager(this)
//        binding?.mainList?.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

        presenter.request(MainPresenter.Companion.NAME_1)

        if (savedInstanceState == null) {
            val fragment = DashboardFragment()
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit()
        }
    }

    fun onNetworkError(throwable: Throwable) {
        android.widget.Toast.makeText(this, throwable.message, android.widget.Toast.LENGTH_LONG).show()
    }

    fun retrieveAdapter(): MainAdapter {
        return adapter
    }


}
