package com.birumuda.pos

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.birumuda.pos.data.model.Item
import com.birumuda.pos.ui.item.ItemFormFragment
import com.birumuda.pos.ui.item.ItemListFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ItemActivity : BaseDrawerActivity(),
    ItemFormFragment.FormCallback,
    ItemListFragment.Callback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupDrawer(toolbar)

        // ✅ PENTING: JANGAN replace fragment saat rotate
        if (savedInstanceState == null) {
            showItemList()
        }

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            showItemForm(null)
        }
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
