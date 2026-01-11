package com.birumuda.pos

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.birumuda.pos.ui.category.CategoryActivity
import com.birumuda.pos.ui.item.ItemActivity
import com.birumuda.pos.ui.main.MainActivity
import com.birumuda.pos.ui.payment.PaymentActivity
import com.birumuda.pos.ui.setting.SettingActivity
import com.google.android.material.navigation.NavigationView

abstract class BaseDrawerActivity : BaseActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var navigationView: NavigationView

    protected open fun drawerIconColor(): Int {
        return android.R.color.white   // DEFAULT PUTIH
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // child activity WAJIB memanggil setContentView lebih dulu

        // =========================
        // BACK HANDLER UNTUK DRAWER
        // =========================
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (::drawerLayout.isInitialized &&
                        drawerLayout.isDrawerOpen(GravityCompat.START)
                    ) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    } else {
                        // Lepas callback → biarkan Activity anak yang handle
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )
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

        toggle.drawerArrowDrawable.color =
            ContextCompat.getColor(this, drawerIconColor())

        setupHeader()
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupHeader() {
        val headerView = navigationView.getHeaderView(0)
        val tvUsername =
            headerView.findViewById<android.widget.TextView>(R.id.tvUsername)
        tvUsername.text = sessionManager.username ?: "Unknown User"
    }

    override fun onNavigationItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> navigate(MainActivity::class.java)
            R.id.menu_item -> navigate(ItemActivity::class.java)
            R.id.menu_category -> navigate(CategoryActivity::class.java)
            R.id.menu_payment -> navigate(PaymentActivity::class.java)
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
            val intent = Intent(this, target)

            // Jika target MainActivity → reset ke root
            if (target == MainActivity::class.java) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            startActivity(intent)
            finish()
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
