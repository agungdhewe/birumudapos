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

		// Nama Item
		tvName.text = item.nama

		// Harga (Rp 19.800)
		tvPrice.text = rupiahFormat.format(item.harga)

		// Gambar Produk
		if (item.picture.isNullOrEmpty()) {
			imgProduct.setImageResource(R.drawable.ic_image_placeholder)
		} else {
			imgProduct.setImageURI(Uri.parse(item.picture))
		}

		return view
	}
}
