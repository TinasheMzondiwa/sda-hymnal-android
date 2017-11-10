package com.tinashe.sdah.ui

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import com.tinashe.sdah.R
import com.tinashe.sdah.ui.base.BaseThemedActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseThemedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.blank, R.string.blank)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {

            }
        }

    }

    override fun onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }
}
