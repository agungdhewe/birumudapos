package com.birumuda.pos.ui.item

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.birumuda.pos.R
import com.birumuda.pos.data.db.AppDatabaseHelper
import com.birumuda.pos.data.model.Category
import com.birumuda.pos.data.model.Item
import com.birumuda.pos.data.repository.CategoryRepository
import com.birumuda.pos.data.repository.ItemRepository
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class ItemFormFragment : Fragment(R.layout.fragment_item_form) {

    interface FormCallback {
        fun onFormSaved()
    }

    private lateinit var callback: FormCallback

    private var selectedItem: Item? = null
    private var selectedCategory: Category? = null

    private val PLACEHOLDER_CATEGORY = "Pilih Category"
    private val ADD_CATEGORY_LABEL = "+ Tambah Kategori"

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

        val dbHelper = AppDatabaseHelper(requireContext())
        val itemRepo = ItemRepository(dbHelper)
        val categoryRepo = CategoryRepository(dbHelper)

        // Formatter harga & COGS
        setupCurrencyFormatter(etPrice)
        setupCurrencyFormatter(etCogs)

        /* ================= MODE EDIT ================= */
        selectedItem?.let { item ->
            tvTitle.text = "Edit Item"
            btnDelete.visibility = View.VISIBLE

            etName.setText(item.nama)
            etPrice.setText(formatNumber(item.harga))
            etCogs.setText(formatNumber(item.cogs))

            selectedCategory =
                categoryRepo.getAll().firstOrNull { it.categoryId == item.categoryId }

            tvCategory.text = selectedCategory?.nama ?: PLACEHOLDER_CATEGORY
        } ?: run {
            // MODE ADD
            tvCategory.text = PLACEHOLDER_CATEGORY
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

            if (selectedItem == null) {
                // ADD
                itemRepo.insert(
                    Item(
                        nama = nama,
                        harga = harga,
                        cogs = cogs,
                        categoryId = selectedCategory!!.categoryId
                    )
                )
            } else {
                // UPDATE
                itemRepo.update(
                    selectedItem!!.copy(
                        nama = nama,
                        harga = harga,
                        cogs = cogs,
                        categoryId = selectedCategory!!.categoryId
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
                        itemRepo.delete(it.itemId)
                        callback.onFormSaved()
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    /* =========================================================
       SEARCHABLE CATEGORY DIALOG
       ========================================================= */

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
                0L -> {
                    // placeholder → abaikan
                }
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

    /* =========================================================
       TAMBAH CATEGORY
       ========================================================= */

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

    /* =========================================================
       FORMAT ANGKA
       ========================================================= */

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
