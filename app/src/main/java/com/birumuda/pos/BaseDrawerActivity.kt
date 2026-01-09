package com.birumuda.pos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

abstract class BaseDrawerActivity : BaseActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // child activity WAJIB memanggil setContentView lebih dulu
    }

    protected fun setupDrawer(toolbar: androidx.appcompat.widget.Toolbar) {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            0,
            0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupHeader()
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupHeader() {
        val headerView = navigationView.getHeaderView(0)
        val tvUsername = headerView.findViewById<android.widget.TextView>(R.id.tvUsername)
        tvUsername.text = sessionManager.username ?: "Unknown User"
    }

    override fun onNavigationItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> navigate(MainActivity::class.java)
//			R.id.menu_opname -> navigate(OpnameActivity::class.java)
//			R.id.menu_receiving -> navigate(ReceivingActivity::class.java)
//            R.id.menu_print_label -> navigate(PrintlabelActivity::class.java)
            R.id.menu_setting -> navigate(SettingActivity::class.java)
            R.id.menu_logout -> showLogoutDialog()
        }
        drawerLayout.closeDrawers()
        return true
    }

    override fun onResume() {
        super.onResume()
        checkLogin()
    }

    protected fun navigate(target: Class<*>) {
        if (this::class.java != target) {
            startActivity(Intent(this, target))
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("Ya") { _, _ ->
                sessionManager.logout()
                redirectToLogin()
                finishAffinity()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }
}