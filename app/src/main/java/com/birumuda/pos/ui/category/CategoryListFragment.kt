package com.birumuda.pos.ui.category

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.birumuda.pos.ui.category.CategoryActivity
import com.birumuda.pos.R
import com.birumuda.pos.ui.category.CategoryAdapter
import com.birumuda.pos.data.db.AppDatabaseHelper
import com.birumuda.pos.data.repository.CategoryRepository

class CategoryListFragment : Fragment() {

	private lateinit var repo: CategoryRepository
	private lateinit var adapter: CategoryAdapter
	private lateinit var listView: ListView

	override fun onAttach(context: Context) {
		super.onAttach(context)
		repo = CategoryRepository(AppDatabaseHelper(context))
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		val view = inflater.inflate(R.layout.fragment_category_list, container, false)

		val etSearch = view.findViewById<EditText>(R.id.etSearchCategory)
		listView = view.findViewById(R.id.listCategory)

		loadData()

		listView.setOnItemClickListener { _, _, position, _ ->
			val category = adapter.getItem(position)
			(activity as CategoryActivity).openEditForm(category.categoryId)
		}

		etSearch.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable?) {
				filter(s.toString())
			}
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
		})

		return view
	}

	private fun loadData() {
		val list = repo.getAll()
		adapter = CategoryAdapter(requireContext(), list)
		listView.adapter = adapter
	}

	private fun filter(keyword: String) {
		val filtered = repo.getAll()
			.filter { it.nama.contains(keyword, ignoreCase = true) }

		adapter = CategoryAdapter(requireContext(), filtered)
		listView.adapter = adapter
	}
}
