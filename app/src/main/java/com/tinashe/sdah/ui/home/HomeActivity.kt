/*
 * Copyright (c) 2017. Tinashe Mzondiwa (www.tinashemzondiwa.co.za)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.tinashe.sdah.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tinashe.sdah.R
import com.tinashe.sdah.model.constants.DateType
import com.tinashe.sdah.ui.base.BaseDrawerFragment
import com.tinashe.sdah.ui.base.BaseThemedActivity
import com.tinashe.sdah.ui.custom.extensions.*
import com.tinashe.sdah.ui.home.favorites.FavoritesFragment
import com.tinashe.sdah.ui.home.featured.FeaturedFragment
import com.tinashe.sdah.ui.home.hymns.HymnsFragment
import com.tinashe.sdah.ui.home.index.IndexListFragment
import com.tinashe.sdah.util.DateUtils
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_layout.*
import java.util.*
import javax.inject.Inject

class HomeActivity : BaseThemedActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val RQ_LOCATION: Int = 23

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: HomeViewModel

    private var currentFragment: BaseDrawerFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.blank, R.string.blank)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        appBar.addOnOffsetChangedListener { _, verticalOffset ->
            run {
                if (verticalOffset == 0) {
                    window.visible()
                } else {
                    if (currentFragment is HymnsFragment) {
                        window.lowProfile()
                    }
                }
            }
        }

        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                window.visible()
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    window.lowProfile()
                }
            }
        })

        navigationView.setNavigationItemSelectedListener(this)

        fab.setOnClickListener({ currentFragment?.fabClicked(fab) })

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(HomeViewModel::class.java)

        viewModel.urlData.observe(this, Observer { loadBackdrop(it.orEmpty()) })
        viewModel.sabbathDate.observe(this, Observer { setSabbathTime(it) })
        viewModel.navigation.observe(this, Observer { switchToFragment(it) })

        if (savedInstanceState == null) {
            viewModel.navigationSelected(Navigation.HYMNS)
        }

    }

    private fun switchToFragment(navigation: Int?) {

        currentFragment = when (navigation) {
            Navigation.HYMNS -> HymnsFragment()
            Navigation.FAVORITES -> FavoritesFragment()
            Navigation.INDEX -> IndexListFragment()
            Navigation.FEATURED -> FeaturedFragment()
            else -> return
        }

        val item = navigationView.menu.findItem(navigation)
        item?.isChecked = true

        if (currentFragment is HymnsFragment) {
            fab.show()
        } else if (fab.isShown) {
            fab.hide()
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, currentFragment)
                .commit()
    }

    override fun onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawers()

        if (item.isChecked) {
            return false
        }

        when (item.itemId) {
            Navigation.HYMNS,
            Navigation.FAVORITES,
            Navigation.INDEX,
            Navigation.FEATURED -> viewModel.navigationSelected(item.itemId)
            R.id.nav_settings -> {
                //TODO Implement
            }
            R.id.nav_feedback -> {
                //TODO Implement
            }
            R.id.nav_donate -> {
                //TODO Implement
            }
            else -> return false
        }

        return true
    }

    private fun loadBackdrop(url: String) {

        var view: ImageView? = headerBackdrop

        if (view == null) { //Nullable during orientation changes
            view = navigationView.getHeaderView(0)
                    .findViewById(R.id.headerBackdrop)
        }

        val outValue = TypedValue()
        var res: Int = R.color.windowBackgroundDark

        if (theme.resolveAttribute(R.attr.colorPrimary, outValue, true)) {
            res = outValue.resourceId
        }
        view?.loadFromUrl(url, res)
    }

    private fun setSabbathTime(date: Calendar?) {
        if (date == null) {
            return
        }

        headerAccountImg?.setOnClickListener { revealLayout?.circularReveal(it.getCenter()) }
        revealClose?.setOnClickListener { revealLayout?.circularConceal(headerAccountImg.getCenter()) }

        var view: TextView? = headerSabbathText
        if (view == null) {
            view = navigationView.getHeaderView(0)
                    .findViewById(R.id.headerSabbathText)
        }

        if (date.before(Calendar.getInstance())) {
            //Its Sabbath Day
            view?.setText(R.string.sabbath_greeting)
            return
        }

        val day = DateUtils.getFormattedDate(date.time, DateType.DATE)
        val time = DateUtils.getFormattedDate(date.time, DateType.TIME)
        view?.renderHtml(resources.getString(R.string.sabbath_date_time, day, time))

    }

    override fun onStart() {
        super.onStart()

        val locPerm = Manifest.permission.ACCESS_FINE_LOCATION

        if (ActivityCompat.checkSelfPermission(this, locPerm) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, locPerm)) {

                Snackbar.make(toolbar, R.string.permission_location_rationale, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, {
                            ActivityCompat.requestPermissions(this, arrayOf(locPerm), RQ_LOCATION)
                        })

            } else {
                ActivityCompat.requestPermissions(this, arrayOf(locPerm), RQ_LOCATION)
            }

        } else {
            fetchLocation()
        }

    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {

        val fusedLocationClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            viewModel.calculateSunset(it.latitude, it.longitude)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {

        if (requestCode == RQ_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission has been granted
                fetchLocation()
            }
        }
    }
}
