package com.birumuda.pos

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.birumuda.pos.utils.SessionManager

class LoginActivity : AppCompatActivity() {

	companion object {
		private const val DUMMY_USERNAME = "agung"
		private const val DUMMY_PASSWORD = "rahasia"
		private const val DUMMY_TOKEN = "232353453"
		private const val DUMMY_USERID = "1"
	}

	private lateinit var etUsername: EditText
	private lateinit var etPassword: EditText
	private lateinit var btnLogin: Button
	private lateinit var tvSetting: TextView

	private lateinit var sessionManager: SessionManager

	override fun onCreate(savedInstanceState: Bundle?) {
		AppCompatDelegate.setDefaultNightMode(
			AppCompatDelegate.MODE_NIGHT_NO
		)
		super.onCreate(savedInstanceState)

		// init sessionManager (punya BaseActivity)
		sessionManager = SessionManager(this)

		// Jika sudah login → langsung ke MainActivity
		if (sessionManager.isLoggedIn()) {
			navigateToMain()
			return
		}

		setContentView(R.layout.activity_login)

		initView()
		initAction()
	}

	private fun initView() {
		etUsername = findViewById(R.id.etUsername)
		etPassword = findViewById(R.id.etPassword)
		btnLogin = findViewById(R.id.btnLogin)
		tvSetting = findViewById(R.id.tvSetting)

		// Fokus awal ke Username
		etUsername.requestFocus()
	}

	private fun initAction() {

		// Enter di Username
		etUsername.setOnEditorActionListener { _, _, _ ->
			val username = etUsername.text.toString().trim()

			if (username.isEmpty()) {
				etUsername.error = "Username wajib diisi"
				etUsername.requestFocus()
			} else {
				etPassword.requestFocus()
			}
			true
		}

		// Enter di Password → Login
		etPassword.setOnEditorActionListener { _, _, _ ->
			val username = etUsername.text.toString().trim()
			val password = etPassword.text.toString().trim()

			if (validateInput(username, password)) {
				doLogin(username, password)
			}
			true
		}

		// Klik tombol Login
		btnLogin.setOnClickListener {
			val username = etUsername.text.toString().trim()
			val password = etPassword.text.toString().trim()

			if (validateInput(username, password)) {
				doLogin(username, password)
			}
		}


		// buka setting
		tvSetting.setOnClickListener {
			val intent = Intent(this, SettingActivity::class.java)
			startActivity(intent)
		}
	}

	private fun validateInput(username: String, password: String): Boolean {
		if (username.isEmpty()) {
			etUsername.error = "Username wajib diisi"
			etUsername.requestFocus()
			return false
		}

		if (password.isEmpty()) {
			etPassword.error = "Password wajib diisi"
			etPassword.requestFocus()
			return false
		}
		return true
	}

	private fun doLogin(username: String, password: String) {
		// TODO: Ganti dengan API login (Retrofit)
		if (username == DUMMY_USERNAME && password == DUMMY_PASSWORD) {

			// Dummy data (sementara)
			val userId = DUMMY_USERID
			val token = DUMMY_TOKEN

			// WAJIB pakai createLoginSession
			sessionManager.createLoginSession(
				userId = userId,
				username = username,
				token = token
			)

			Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
			navigateToMain()
		} else {
			Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show()
		}
	}

	private fun navigateToMain() {
		startActivity(Intent(this, MainActivity::class.java))
		finish()
	}
}