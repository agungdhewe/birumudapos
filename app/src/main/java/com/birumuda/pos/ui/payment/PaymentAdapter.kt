package com.birumuda.pos.ui.payment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.birumuda.pos.R
import com.birumuda.pos.data.model.Payment

class PaymentAdapter(
	private val context: Context,
	private val payments: List<Payment>
) : BaseAdapter() {

	override fun getCount(): Int = payments.size
	override fun getItem(position: Int): Payment = payments[position]

	// Tidak pakai id numerik → pakai position
	override fun getItemId(position: Int): Long = position.toLong()

	override fun getView(
		position: Int,
		convertView: View?,
		parent: ViewGroup?
	): View {

		val view = convertView ?: LayoutInflater.from(context)
			.inflate(R.layout.row_payment, parent, false)

		val tvName = view.findViewById<TextView>(R.id.tvPaymentName)
		val tvType = view.findViewById<TextView>(R.id.tvPaymentType)
//		val tvDesc = view.findViewById<TextView>(R.id.tvPaymentDesc)
		val tvNonActive = view.findViewById<TextView>(R.id.tvNonActive)

		val payment = payments[position]

		// Nama (wajib)
		tvName.text = payment.name

		// Tipe Payment (wajib)
		tvType.text = payment.type.displayName

		// Deskripsi (optional)
//		if (!payment.description.isNullOrBlank()) {
//			tvDesc.text = payment.description
//			tvDesc.visibility = View.VISIBLE
//		} else {
//			tvDesc.visibility = View.GONE
//		}

		// Status Nonaktif
		tvNonActive.visibility =
			if (payment.isNonActive) View.VISIBLE else View.GONE

		return view
	}
}
