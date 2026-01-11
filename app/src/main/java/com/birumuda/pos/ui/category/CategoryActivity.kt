package com.birumuda.pos.ui.category

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.birumuda.pos.BaseDrawerActivity
import com.birumuda.pos.ui.main.MainActivity
import com.birumuda.pos.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoryActivity : BaseDrawerActivity() {

	private lateinit var fabAdd: FloatingActionButton

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_category)

		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		setupDrawer(toolbar)

		fabAdd = findViewById(R.id.fabAdd)

		if (savedInstanceState == null) {
			supportFragmentManager.beginTransaction()
				.replace(R.id.fragmentContainer, CategoryListFragment())
				.commit()
		}

		fabAdd.setOnClickListener {
			val current =
				supportFragmentManager.findFragmentById(R.id.fragmentContainer)

			if (current is CategoryFormFragment) {
				current.resetForm()
			} else {
				openAddForm()
			}
		}

		// =========================
		// BACK HANDLER (FINAL FIX)
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
						val intent = Intent(this@CategoryActivity, MainActivity::class.java)
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
						startActivity(intent)
						finish()
					}
				}
			}
		)
	}

	private fun openAddForm() {
		openForm(CategoryFormFragment.newInstance())
	}

	fun openEditForm(categoryId: Long) {
		openForm(CategoryFormFragment.newInstance(categoryId))
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