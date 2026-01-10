package com.birumuda.pos

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
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
