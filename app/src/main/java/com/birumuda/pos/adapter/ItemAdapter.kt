package com.birumuda.pos.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.birumuda.pos.R
import com.birumuda.pos.data.model.Item
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class ItemAdapter(
	private val context: Context,
	private val items: ArrayList<Item>
) : BaseAdapter() {

	private val rupiahFormat = NumberFormat.getCurrencyInstance(
		Locale("in", "ID")
	).apply {
		maximumFractionDigits = 0
		val symbols = (this as java.text.DecimalFormat).decimalFormatSymbols
		symbols.currencySymbol = "Rp "
		decimalFormatSymbols = symbols
	}

	override fun getCount(): Int = items.size
	override fun getItem(position: Int): Item = items[position]
	override fun getItemId(position: Int): Long = items[position].itemId

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val view = convertView ?: LayoutInflater.from(context)
			.inflate(R.layout.item_grid, parent, false)

		val imgProduct = view.findViewById<ImageView>(R.id.imgProduct)
		val tvName = view.findViewById<TextView>(R.id.txtName)
		val tvPrice = view.findViewById<TextView>(R.id.txtPrice)

		val item = items[position]

		// ================= TEXT =================
		tvName.text = item.nama
		tvPrice.text = rupiahFormat.format(item.harga)

		// ================= IMAGE (FIXED) =================
		imgProduct.setImageResource(R.drawable.ic_image_placeholder) // reset dulu (PENTING)

		item.picture?.let { path ->
			val file = File(path)
			if (file.exists()) {
				imgProduct.setImageURI(Uri.fromFile(file))
			}
		}

		return view
	}

	/**
	 * Helper untuk refresh data (dipakai oleh Fragment)
	 */
	fun updateData(newItems: List<Item>) {
		items.clear()
		items.addAll(newItems)
		notifyDataSetChanged()
	}
}
