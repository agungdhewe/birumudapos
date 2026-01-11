package com.birumuda.pos.ui.payment

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
import com.birumuda.pos.R
import com.birumuda.pos.data.db.AppDatabaseHelper
import com.birumuda.pos.data.repository.PaymentRepository

class PaymentListFragment : Fragment() {

	private lateinit var repo: PaymentRepository
	private lateinit var adapter: PaymentAdapter
	private lateinit var listView: ListView

	override fun onAttach(context: Context) {
		super.onAttach(context)
		repo = PaymentRepository(AppDatabaseHelper(context))
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		val view = inflater.inflate(R.layout.fragment_payment_list, container, false)

		val etSearch = view.findViewById<EditText>(R.id.etSearch)
		listView = view.findViewById(R.id.listPayment)

		loadData()

		listView.setOnItemClickListener { _, _, position, _ ->
			val payment = adapter.getItem(position)
			(activity as PaymentActivity).openEditForm(payment)
		}

		etSearch.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable?) {
				filter(s.toString())
			}

			override fun beforeTextChanged(
				s: CharSequence?,
				start: Int,
				count: Int,
				after: Int
			) {}

			override fun onTextChanged(
				s: CharSequence?,
				start: Int,
				before: Int,
				count: Int
			) {}
		})

		return view
	}

	private fun loadData() {
		val list = repo.getAll()
		adapter = PaymentAdapter(requireContext(), list)
		listView.adapter = adapter
	}

	private fun filter(keyword: String) {
		val filtered = repo.getAll()
			.filter {
				it.name.contains(keyword, ignoreCase = true) ||
						it.type.displayName.contains(keyword, ignoreCase = true)
			}

		adapter = PaymentAdapter(requireContext(), filtered)
		listView.adapter = adapter
	}
}
