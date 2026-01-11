package com.birumuda.pos.ui.category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.birumuda.pos.R
import com.birumuda.pos.data.model.Category

class CategoryAdapter(
	private val context: Context,
	private val categories: List<Category>
) : BaseAdapter() {

	override fun getCount(): Int = categories.size
	override fun getItem(position: Int): Category = categories[position]
	override fun getItemId(position: Int): Long = categories[position].categoryId

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val view = convertView ?: LayoutInflater.from(context)
			.inflate(R.layout.row_category, parent, false)

		val tvName = view.findViewById<TextView>(R.id.txtCategoryName)
		val tvDesc = view.findViewById<TextView>(R.id.txtCategoryDesc)

		val category = categories[position]

		// Nama (wajib)
		tvName.text = category.nama

		// Deskripsi (optional)
		if (!category.deskripsi.isNullOrBlank()) {
			tvDesc.text = category.deskripsi
			tvDesc.visibility = View.VISIBLE
		} else {
			tvDesc.visibility = View.GONE
		}

		return view
	}
}