package com.birumuda.pos

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.birumuda.pos.data.model.Item
import com.birumuda.pos.ui.item.ItemFormFragment
import com.birumuda.pos.ui.item.ItemListFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ItemActivity : BaseDrawerActivity(),
    ItemFormFragment.FormCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupDrawer(toolbar)
        showItemList()

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            showItemForm(null) // ADD MODE
        }
    }



    /**
     * Tampilkan Grid Item (default)
     */
    private fun showItemList() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                ItemListFragment { item ->
                    showItemForm(item) // UPDATE MODE
                }
            )
            .commit()
    }

    /**
     * Tampilkan Form Item
     * @param item null = ADD, not null = UPDATE
     */
    private fun showItemForm(item: Item?) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                ItemFormFragment.newInstance(item)
            )
            .addToBackStack(null)
            .commit()
    }

    /**
     * Callback dari ItemFormFragment
     * Dipanggil setelah data berhasil disimpan
     */
    override fun onFormSaved() {
        supportFragmentManager.popBackStack()
        showItemList()
    }
}
