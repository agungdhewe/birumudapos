package com.birumuda.pos.ui.item

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import com.birumuda.pos.BaseDrawerActivity
import com.birumuda.pos.ui.main.MainActivity
import com.birumuda.pos.R
import com.birumuda.pos.data.model.Item
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ItemActivity : BaseDrawerActivity(),
    ItemFormFragment.FormCallback,
    ItemListFragment.Callback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupDrawer(toolbar)

        // Root fragment
        if (savedInstanceState == null) {
            showItemList()
        }

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            showItemForm(null)
        }

        // =========================
        // BACK HANDLER (FINAL)
        // Form -> List
        // List -> MainActivity (EXPLICIT)
        // =========================
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    val fm = supportFragmentManager

                    if (fm.backStackEntryCount > 0) {
                        // Sedang di Form
                        fm.popBackStack()
                    } else {
                        // Sedang di List -> kembali ke MainActivity
                        val intent = Intent(this@ItemActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        )
    }

    private fun showItemList() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ItemListFragment())
            .commit()
    }

    private fun showItemForm(item: Item?) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                ItemFormFragment.newInstance(item)
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onItemSelected(item: Item) {
        showItemForm(item)
    }

    override fun onFormSaved() {
        supportFragmentManager.popBackStack()
    }
}