package com.birumuda.pos.ui.payment

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.birumuda.pos.BaseDrawerActivity
import com.birumuda.pos.R
import com.birumuda.pos.data.model.Payment
import com.birumuda.pos.ui.main.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PaymentActivity : BaseDrawerActivity() {

	private lateinit var fabAdd: FloatingActionButton

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_payment)

		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		setupDrawer(toolbar)

		fabAdd = findViewById(R.id.fabAdd)

		if (savedInstanceState == null) {
			supportFragmentManager.beginTransaction()
				.replace(R.id.fragmentContainer, PaymentListFragment())
				.commit()
		}

		fabAdd.setOnClickListener {
			val current =
				supportFragmentManager.findFragmentById(R.id.fragmentContainer)

			if (current is PaymentFormFragment) {
				current.resetForm()
			} else {
				openAddForm()
			}
		}

		// =========================
		// BACK HANDLER (SAMA PERSIS DENGAN CATEGORY)
		// =========================
		onBackPressedDispatcher.addCallback(
			this,
			object : OnBackPressedCallback(true) {
				override fun handleOnBackPressed() {

					val fm = supportFragmentManager

					if (fm.backStackEntryCount > 0) {
						// Form -> List
						fm.popBackStack()
					} else {
						// List -> MainActivity (EXPLICIT)
						val intent =
							Intent(this@PaymentActivity, MainActivity::class.java)
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
						startActivity(intent)
						finish()
					}
				}
			}
		)
	}

	private fun openAddForm() {
		openForm(PaymentFormFragment.newInstance())
	}

	fun openEditForm(payment: Payment) {
		openForm(PaymentFormFragment.newInstance(payment))
	}

	private fun openForm(fragment: Fragment) {
		supportFragmentManager.beginTransaction()
			.replace(R.id.fragmentContainer, fragment)
			.addToBackStack(null)
			.commit()
	}

	fun backToList() {
		supportFragmentManager.popBackStack()
	}
}
