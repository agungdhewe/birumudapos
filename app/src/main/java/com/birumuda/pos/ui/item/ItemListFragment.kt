package com.birumuda.pos.ui.item

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.birumuda.pos.R
import com.birumuda.pos.adapter.ItemAdapter
import com.birumuda.pos.data.db.AppDatabaseHelper
import com.birumuda.pos.data.model.Item
import com.birumuda.pos.data.repository.ItemRepository


class ItemListFragment(
    private val onItemClick: (Item) -> Unit
) : Fragment(R.layout.fragment_item_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val grid = view.findViewById<GridView>(R.id.gridItem)
        val repo = ItemRepository(AppDatabaseHelper(requireContext()))
        val items = repo.getAll()

        val adapter = ItemAdapter(requireContext(), items)
        grid.adapter = adapter

        grid.setOnItemClickListener { _, _, pos, _ ->
            onItemClick(items[pos])
        }
    }
}
