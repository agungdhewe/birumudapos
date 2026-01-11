package com.birumuda.pos.ui.item

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.birumuda.pos.R
import com.birumuda.pos.data.db.AppDatabaseHelper
import com.birumuda.pos.data.model.Category
import com.birumuda.pos.data.model.Item
import com.birumuda.pos.data.repository.CategoryRepository
import com.birumuda.pos.data.repository.ItemRepository
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class ItemFormFragment : Fragment(R.layout.fragment_item_form) {

	interface FormCallback {
		fun onFormSaved()
	}

	interface Callback {
		fun onItemSelected(item: Item)
	}


	private lateinit var callback: FormCallback

	private var selectedItem: Item? = null
	private var selectedCategory: Category? = null

	private var imageUri: Uri? = null
	private var isImageChanged = false

	private val PLACEHOLDER_CATEGORY = "Pilih Category"
	private val ADD_CATEGORY_LABEL = "+ Tambah Kategori"

	/* ================= IMAGE PICKER ================= */

	private val pickImageLauncher =
		registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
			uri?.let {
				imageUri = it
				isImageChanged = true
				view?.findViewById<ImageView>(R.id.imgProduct)
					?.setImageURI(it)
			}
		}

	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = context as FormCallback
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		selectedItem = arguments?.getParcelable(ARG_ITEM)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		val etName = view.findViewById<EditText>(R.id.etName)
		val etPrice = view.findViewById<EditText>(R.id.etPrice)
		val etCogs = view.findViewById<EditText>(R.id.etCogs)
		val tvCategory = view.findViewById<TextView>(R.id.tvCategory)
		val btnSave = view.findViewById<Button>(R.id.btnSave)
		val btnDelete = view.findViewById<Button>(R.id.btnDelete)
		val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
		val imgProduct = view.findViewById<ImageView>(R.id.imgProduct)

		val dbHelper = AppDatabaseHelper(requireContext())
		val itemRepo = ItemRepository(dbHelper)
		val categoryRepo = CategoryRepository(dbHelper)

		setupCurrencyFormatter(etPrice)
		setupCurrencyFormatter(etCogs)

		/* ================= MODE EDIT / ADD ================= */

		selectedItem?.let { item ->
			tvTitle.text = getString(R.string.title_edit_item)
			btnDelete.visibility = View.VISIBLE

			etName.setText(item.nama)
			etPrice.setText(formatNumber(item.harga))
			etCogs.setText(formatNumber(item.cogs))

			selectedCategory =
				categoryRepo.getAll().firstOrNull { it.categoryId == item.categoryId }

			tvCategory.text = selectedCategory?.nama ?: PLACEHOLDER_CATEGORY

			item.picture?.let {
				val file = File(it)
				if (file.exists()) {
					imageUri = Uri.fromFile(file)
					imgProduct.setImageURI(imageUri)
				}
			}
		} ?: run {
			tvCategory.text = PLACEHOLDER_CATEGORY
			imgProduct.setImageResource(R.drawable.ic_image_placeholder)
		}

		/* ================= PILIH IMAGE ================= */

		imgProduct.setOnClickListener {
			pickImageLauncher.launch("image/*")
		}

		/* ================= PILIH CATEGORY ================= */

		tvCategory.setOnClickListener {
			showCategorySearchDialog(categoryRepo) { category ->
				selectedCategory = category
				tvCategory.text = category.nama
			}
		}

		/* ================= SIMPAN ================= */

		btnSave.setOnClickListener {

			if (etName.text.isNullOrBlank()
				|| etPrice.text.isNullOrBlank()
				|| etCogs.text.isNullOrBlank()
				|| selectedCategory == null
				|| selectedCategory!!.categoryId <= 0L
			) {
				Toast.makeText(
					requireContext(),
					"Lengkapi semua field",
					Toast.LENGTH_SHORT
				).show()
				return@setOnClickListener
			}

			val nama = etName.text.toString()
			val harga = etPrice.text.toString().replace(".", "").toLong()
			val cogs = etCogs.text.toString().replace(".", "").toLong()

			val picturePath =
				if (isImageChanged) imageUri?.let { copyImageToInternal(it) }
				else null

			if (selectedItem == null) {
				itemRepo.insert(
					Item(
						nama = nama,
						harga = harga,
						cogs = cogs,
						categoryId = selectedCategory!!.categoryId,
						picture = picturePath
					)
				)
			} else {
				itemRepo.update(
					selectedItem!!.copy(
						nama = nama,
						harga = harga,
						cogs = cogs,
						categoryId = selectedCategory!!.categoryId,
						picture = picturePath ?: selectedItem!!.picture
					)
				)
			}

			callback.onFormSaved()
		}

		/* ================= DELETE ================= */

		btnDelete.setOnClickListener {
			AlertDialog.Builder(requireContext())
				.setTitle("Hapus Item")
				.setMessage("Yakin ingin menghapus item ini?")
				.setPositiveButton("Hapus") { _, _ ->
					selectedItem?.let {
						it.picture?.let { path ->
							val file = File(path)
							if (file.exists()) file.delete()
						}
						itemRepo.delete(it.itemId)
						callback.onFormSaved()
					}
				}
				.setNegativeButton("Batal", null)
				.show()
		}
	}

	/* ================= CATEGORY DIALOG ================= */

	private fun showCategorySearchDialog(
		categoryRepo: CategoryRepository,
		onSelected: (Category) -> Unit
	) {
		val dialogView = layoutInflater.inflate(
			R.layout.dialog_category_search,
			null
		)

		val etSearch = dialogView.findViewById<EditText>(R.id.etSearch)
		val listView = dialogView.findViewById<ListView>(R.id.listCategory)

		val categories = mutableListOf<Category>().apply {
			add(Category(0L, PLACEHOLDER_CATEGORY))
			addAll(categoryRepo.getAll())
			add(Category(-1L, ADD_CATEGORY_LABEL))
		}

		val adapter = ArrayAdapter(
			requireContext(),
			android.R.layout.simple_list_item_1,
			categories.map { it.nama }
		)

		listView.adapter = adapter

		etSearch.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable?) {
				adapter.filter.filter(s.toString())
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
		})

		val dialog = AlertDialog.Builder(requireContext())
			.setTitle("Pilih Category")
			.setView(dialogView)
			.create()

		listView.setOnItemClickListener { _, _, position, _ ->
			val selected = categories[position]
			when (selected.categoryId) {
				0L -> Unit
				-1L -> {
					dialog.dismiss()
					showAddCategoryDialog(categoryRepo, onSelected)
				}
				else -> {
					onSelected(selected)
					dialog.dismiss()
				}
			}
		}

		dialog.show()
	}

	private fun showAddCategoryDialog(
		categoryRepo: CategoryRepository,
		onSelected: (Category) -> Unit
	) {
		val etCategory = EditText(requireContext()).apply {
			hint = "Nama category"
		}

		AlertDialog.Builder(requireContext())
			.setTitle("Tambah Category")
			.setView(etCategory)
			.setPositiveButton("Simpan") { _, _ ->
				val name = etCategory.text.toString().trim()
				if (name.isNotEmpty()) {
					categoryRepo.insertIfNotExists(name)
					val newId = categoryRepo.getCategoryIdByName(name)
					onSelected(Category(newId, name))
				}
			}
			.setNegativeButton("Batal", null)
			.show()
	}

	/* ================= FORMAT ================= */

	private fun setupCurrencyFormatter(editText: EditText) {
		val symbols = DecimalFormatSymbols().apply {
			groupingSeparator = '.'
			decimalSeparator = ','
		}

		val formatter = DecimalFormat("#,###", symbols)

		editText.addTextChangedListener(object : TextWatcher {
			private var current = ""

			override fun afterTextChanged(s: Editable?) {
				if (s.toString() != current) {
					editText.removeTextChangedListener(this)

					val clean = s.toString().replace(".", "")
					if (clean.isNotEmpty()) {
						val formatted = formatter.format(clean.toLong())
						current = formatted
						editText.setText(formatted)
						editText.setSelection(formatted.length)
					}

					editText.addTextChangedListener(this)
				}
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
		})
	}

	private fun formatNumber(value: Long): String {
		val symbols = DecimalFormatSymbols().apply {
			groupingSeparator = '.'
		}
		return DecimalFormat("#,###", symbols).format(value)
	}

	/* ================= IMAGE SAVE ================= */

	private fun copyImageToInternal(uri: Uri): String {
		val input = requireContext().contentResolver.openInputStream(uri)
		val file = File(
			requireContext().filesDir,
			"item_${System.currentTimeMillis()}.jpg"
		)
		val output = FileOutputStream(file)

		input?.copyTo(output)
		input?.close()
		output.close()

		return file.absolutePath
	}

	companion object {
		private const val ARG_ITEM = "arg_item"

		fun newInstance(item: Item?): ItemFormFragment {
			return ItemFormFragment().apply {
				arguments = Bundle().apply {
					putParcelable(ARG_ITEM, item)
				}
			}
		}
	}
}
