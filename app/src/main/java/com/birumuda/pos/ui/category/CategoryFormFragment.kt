package com.birumuda.pos.ui.category

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.birumuda.pos.ui.category.CategoryActivity
import com.birumuda.pos.R
import com.birumuda.pos.data.db.AppDatabaseHelper
import com.birumuda.pos.data.model.Category
import com.birumuda.pos.data.repository.CategoryRepository
import com.birumuda.pos.data.repository.ItemRepository

class CategoryFormFragment : Fragment() {

	private lateinit var repo: CategoryRepository
	private lateinit var itemRepo: ItemRepository

	private lateinit var tvTitle: TextView
	private lateinit var etName: EditText
	private lateinit var etDesc: EditText
	private lateinit var btnSave: Button
	private lateinit var btnDelete: Button

	private var categoryId: Long = 0L

	override fun onAttach(context: Context) {
		super.onAttach(context)
		val dbHelper = AppDatabaseHelper(context)
		repo = CategoryRepository(dbHelper)
		itemRepo = ItemRepository(dbHelper)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		categoryId = arguments?.getLong(ARG_ID) ?: 0L
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		val view = inflater.inflate(R.layout.fragment_category_form, container, false)

		tvTitle = view.findViewById(R.id.tvTitle)
		etName = view.findViewById(R.id.etCategoryName)
		etDesc = view.findViewById(R.id.etCategoryDesc)
		btnSave = view.findViewById(R.id.btnSave)
		btnDelete = view.findViewById(R.id.btnDelete)

		if (categoryId == 0L) {
			// MODE TAMBAH (default)
			tvTitle.setText(R.string.title_add_category)
			btnDelete.visibility = View.GONE
		} else {
			// MODE EDIT
			loadData()
		}

		btnSave.setOnClickListener { save() }
		btnDelete.setOnClickListener { confirmDelete() }

		return view
	}

	private fun loadData() {
		val category = repo.getAll().firstOrNull { it.categoryId == categoryId } ?: return

		tvTitle.setText(R.string.title_edit_category)
		etName.setText(category.nama)
		etDesc.setText(category.deskripsi)
		btnDelete.visibility = View.VISIBLE
	}

	private fun save() {
		val name = etName.text.toString().trim()
		if (name.isEmpty()) return

		val category = Category(
			categoryId = categoryId,
			nama = name,
			deskripsi = etDesc.text.toString()
		)

		if (categoryId == 0L) {
			repo.insert(category)
		} else {
			repo.update(category)
		}

		(activity as CategoryActivity).backToList()
	}

	private fun confirmDelete() {

		// Pencegahan: category sudah dipakai item
		if (itemRepo.isCategoryUsed(categoryId)) {
			AlertDialog.Builder(requireContext())
				.setTitle(R.string.msg_cannot_delete)
				.setMessage(R.string.msg_category_used)
				.setPositiveButton(android.R.string.ok, null)
				.show()
			return
		}

		AlertDialog.Builder(requireContext())
			.setTitle(R.string.title_delete_category)
			.setMessage(R.string.msg_delete_category_confirm)
			.setPositiveButton(android.R.string.ok) { _, _ ->
				repo.delete(categoryId)
				(activity as CategoryActivity).backToList()
			}
			.setNegativeButton(android.R.string.cancel, null)
			.show()
	}

	fun resetForm() {
		categoryId = 0L
		tvTitle.setText(R.string.title_add_category)
		etName.text.clear()
		etDesc.text.clear()
		btnDelete.visibility = View.GONE
	}

	companion object {
		private const val ARG_ID = "id"

		fun newInstance(id: Long = 0L): CategoryFormFragment {
			val f = CategoryFormFragment()
			val b = Bundle()
			b.putLong(ARG_ID, id)
			f.arguments = b
			return f
		}
	}
}
