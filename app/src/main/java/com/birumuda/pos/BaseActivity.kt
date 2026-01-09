package com.birumuda.pos


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.birumuda.pos.utils.SessionManager

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
    }

    /**
     * Digunakan oleh activity yang WAJIB login
     */
    protected fun checkLogin() {
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin()
        }
    }

    /**
     * Redirect aman ke LoginActivity
     */
    protected fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Logout global
     */
    protected fun logout() {
        sessionManager.logout()
        redirectToLogin()
    }

    protected fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
