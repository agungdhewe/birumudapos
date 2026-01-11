package com.birumuda.pos.ui.item

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.GridView
import android.widget.Spinner

import androidx.fragment.app.Fragment
import com.birumuda.pos.R
import com.birumuda.pos.ui.item.ItemAdapter
import com.birumuda.pos.data.db.AppDatabaseHelper
import com.birumuda.pos.data.model.Category
import com.birumuda.pos.data.model.Item
import com.birumuda.pos.data.repository.CategoryRepository
import com.birumuda.pos.data.repository.ItemRepository

class ItemListFragment : Fragment(R.layout.fragment_item_list) {

    interface Callback {
        fun onItemSelected(item: Item)
    }

    private lateinit var callback: Callback
    private lateinit var repo: ItemRepository
    private lateinit var adapter: ItemAdapter
    private val items = ArrayList<Item>()   // ✅ FIX: ArrayList


    private lateinit var etSearch: EditText
    private lateinit var spCategory: Spinner

    private lateinit var categoryRepo: CategoryRepository

    private var selectedCategoryId: Long? = null



    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSearch = view.findViewById(R.id.etSearch)
        spCategory = view.findViewById(R.id.spCategory)
        categoryRepo = CategoryRepository(AppDatabaseHelper(requireContext()))

        val grid = view.findViewById<GridView>(R.id.gridItem)
        repo = ItemRepository(AppDatabaseHelper(requireContext()))

        items.clear()
        items.addAll(repo.getAll())   // aman sekarang

        adapter = ItemAdapter(requireContext(), items)
        grid.adapter = adapter

        grid.setOnItemClickListener { _, _, pos, _ ->
            callback.onItemSelected(items[pos])
        }

        setupCategorySpinner()
        setupSearch()

        refreshData()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        items.clear()
        items.addAll(repo.getAll())
        adapter.notifyDataSetChanged()
    }

    private fun setupCategorySpinner() {
        val categories = ArrayList<Category>()
        categories.add(Category(0L, "Semua Category"))
        categories.addAll(categoryRepo.getAll())

        val adapterSpinner = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories.map { it.nama }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spCategory.adapter = adapterSpinner

        spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCategoryId = categories[position].categoryId
                applyFilter()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                applyFilter()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun applyFilter() {
        val keyword = etSearch.text.toString().trim()
        val result = repo.search(keyword, selectedCategoryId)

        items.clear()
        items.addAll(result)
        adapter.notifyDataSetChanged()
    }

}
