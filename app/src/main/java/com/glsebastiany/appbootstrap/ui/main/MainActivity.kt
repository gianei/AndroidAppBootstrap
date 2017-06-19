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

import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.glsebastiany.appbootstrap.R
import com.glsebastiany.appbootstrap.databinding.ActivityMainBinding
import com.glsebastiany.appbootstrap.ui.main.dashboard.DashboardFragment
import com.glsebastiany.appbootstrap.ui.main.home.HomeFragment
import com.glsebastiany.appbootstrap.ui.main.dashboard.NotificationsFragment

class MainActivity : AppCompatActivity() {

    var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.glsebastiany.appbootstrap.R.layout.activity_main)
        binding = android.databinding.DataBindingUtil.setContentView(this, com.glsebastiany.appbootstrap.R.layout.activity_main);

        setupBottomBarNavigation()
    }

    private fun setupBottomBarNavigation() {
        binding?.navigation?.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        binding?.viewPager?.adapter = MyPagerAdapter(supportFragmentManager)
        binding?.viewPager?.addOnPageChangeListener(MyPageChangeListener(binding?.navigation))
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                binding?.viewPager?.setCurrentItem(0, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                binding?.viewPager?.setCurrentItem(1, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                binding?.viewPager?.setCurrentItem(2, true)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private class MyPageChangeListener(val navigation: BottomNavigationView?) : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            when(position){
                0 -> {
                    navigation?.selectedItemId = R.id.navigation_home
                }
                1 -> {
                    navigation?.selectedItemId = R.id.navigation_dashboard
                }
                2 -> {
                    navigation?.selectedItemId = R.id.navigation_notifications
                }
            }
        }
    }

    private class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        val fragments = arrayListOf(
                DashboardFragment(),
                HomeFragment(),
                NotificationsFragment()
        )

        override fun getItem(position: Int): Fragment {
            return fragments.get(position)
        }

        override fun getCount(): Int {
            return fragments.count()
        }

    }

}
