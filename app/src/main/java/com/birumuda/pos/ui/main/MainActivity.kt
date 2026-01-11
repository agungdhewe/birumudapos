package com.birumuda.pos.ui.main

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.birumuda.pos.BaseDrawerActivity
import com.birumuda.pos.R
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : BaseDrawerActivity() {
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupDrawer(toolbar)

    }

    override fun drawerIconColor(): Int {
        return android.R.color.black
    }
}