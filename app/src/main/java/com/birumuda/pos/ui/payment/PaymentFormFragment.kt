package com.birumuda.pos.ui.payment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.birumuda.pos.R
import com.birumuda.pos.data.db.AppDatabaseHelper
import com.birumuda.pos.data.model.Payment
import com.birumuda.pos.data.model.PaymentType
import com.birumuda.pos.data.repository.PaymentRepository

class PaymentFormFragment : Fragment() {

	private lateinit var repo: PaymentRepository

	private lateinit var etName: EditText
	private lateinit var etDesc: EditText
	private lateinit var spType: Spinner
	private lateinit var cbNonActive: CheckBox
	private lateinit var btnSave: Button
	private lateinit var btnDelete: Button   // ✅ NEW

	private var editPayment: Payment? = null

	override fun onAttach(context: Context) {
		super.onAttach(context)
		repo = PaymentRepository(AppDatabaseHelper(context))
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {

		val view = inflater.inflate(R.layout.fragment_payment_form, container, false)

		etName = view.findViewById(R.id.etPaymentName)
		etDesc = view.findViewById(R.id.etPaymentDesc)
		spType = view.findViewById(R.id.spPaymentType)
		cbNonActive = view.findViewById(R.id.cbNonActive)
		btnSave = view.findViewById(R.id.btnSave)
		btnDelete = view.findViewById(R.id.btnDelete) // ✅ bind

		setupSpinner()
		bindEditData()
		setupSaveAction()
		setupDeleteAction()   // ✅ NEW

		return view
	}

	// =========================
	// Spinner setup
	// =========================
	private fun setupSpinner() {
		val items = mutableListOf("Pilih Tipe Payment")
		items.addAll(PaymentType.entries.map { it.displayName })

//		val adapter = ArrayAdapter(
//			requireContext(),
//			android.R.layout.simple_spinner_item,
//			items
//		).also {
//			it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//		}

		val adapter = ArrayAdapter(
			requireContext(),
			R.layout.option_spinner_payment,
			items
		).also {
			it.setDropDownViewResource(R.layout.option_spinner_payment)
		}

		spType.adapter = adapter
	}

	// =========================
	// Edit mode
	// =========================
	private fun bindEditData() {
		editPayment = arguments?.getParcelable(ARG_PAYMENT)

		editPayment?.let { payment ->
			etName.setText(payment.name)
			etName.isEnabled = false

			etDesc.setText(payment.description)
			cbNonActive.isChecked = payment.isNonActive

			val index = PaymentType.entries.indexOf(payment.type) + 1
			spType.setSelection(index)

			// ✅ TAMPILKAN DELETE SAAT EDIT
			btnDelete.visibility = View.VISIBLE
		} ?: run {
			// ADD MODE
			btnDelete.visibility = View.GONE
		}
	}

	// =========================
	// Save
	// =========================
	private fun setupSaveAction() {
		btnSave.setOnClickListener {

			val name = etName.text.toString().trim()
			val desc = etDesc.text.toString().trim()
			val selectedIndex = spType.selectedItemPosition
			val isNonActive = cbNonActive.isChecked

			if (name.isEmpty()) {
				etName.error = "Nama wajib diisi"
				return@setOnClickListener
			}

			if (selectedIndex == 0) {
				Toast.makeText(
					requireContext(),
					"Silakan pilih tipe payment",
					Toast.LENGTH_SHORT
				).show()
				return@setOnClickListener
			}

			val type = PaymentType.entries[selectedIndex - 1]

			val payment = Payment(
				name = name,
				description = if (desc.isEmpty()) null else desc,
				type = type,
				isNonActive = isNonActive
			)

			val success = if (editPayment == null) {
				if (repo.exists(name)) {
					etName.error = "Payment sudah ada"
					return@setOnClickListener
				}
				repo.insert(payment)
			} else {
				repo.update(payment)
			}

			if (success) {
				(activity as PaymentActivity).backToList()
			} else {
				Toast.makeText(
					requireContext(),
					"Gagal menyimpan",
					Toast.LENGTH_SHORT
				).show()
			}
		}
	}

	// =========================
	// Delete (EDIT ONLY)
	// =========================
	private fun setupDeleteAction() {
		btnDelete.setOnClickListener {
			editPayment?.let { payment ->

				AlertDialog.Builder(requireContext())
					.setTitle("Hapus Payment")
					.setMessage("Apakah Anda yakin ingin menghapus payment \"${payment.name}\"?")
					.setPositiveButton("Hapus") { _, _ ->
						repo.delete(payment.name)
						(activity as PaymentActivity).backToList()
					}
					.setNegativeButton("Batal", null)
					.show()
			}
		}
	}


	// =========================
	// Dipanggil FAB (Add ulang)
	// =========================
	fun resetForm() {
		editPayment = null
		etName.setText("")
		etName.isEnabled = true
		etDesc.setText("")
		spType.setSelection(0)
		cbNonActive.isChecked = false
		btnDelete.visibility = View.GONE
	}

	companion object {

		private const val ARG_PAYMENT = "payment"

		fun newInstance(): PaymentFormFragment {
			return PaymentFormFragment()
		}

		fun newInstance(payment: Payment): PaymentFormFragment {
			return PaymentFormFragment().apply {
				arguments = Bundle().apply {
					putParcelable(ARG_PAYMENT, payment)
				}
			}
		}
	}
}
