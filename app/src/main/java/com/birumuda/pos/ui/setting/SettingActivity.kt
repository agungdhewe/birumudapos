package com.birumuda.pos.ui.setting

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.birumuda.pos.R
import com.birumuda.pos.data.model.PrinterOption
import com.birumuda.pos.utils.SessionManager

class SettingActivity : AppCompatActivity() {

    protected lateinit var sessionManager: SessionManager

    private lateinit var etSiteCode: EditText
    private lateinit var etBrandCode: EditText
    private lateinit var spBarcodeReader: Spinner
    private lateinit var spPrinter: Spinner

    private val prefs by lazy {
        getSharedPreferences("app_setting", MODE_PRIVATE)
    }

    private val printerOptions = listOf(
		PrinterOption("Pilih Printer", ""),
		PrinterOption("Epson TM180", "TM180"),
		PrinterOption("Kassen RPP02N", "RPP02N")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Setting"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        sessionManager = SessionManager(this)

        bindView()
        setupEditorState()
        setupBarcodeReaderSpinner()
        setupPrinterSpinner()
        loadSetting()

        toolbar.navigationIcon?.setTint(Color.WHITE)

    }

    private fun bindView() {
        etSiteCode = findViewById(R.id.etSiteCode)
        etBrandCode = findViewById(R.id.etBrandCode)
        spBarcodeReader = findViewById(R.id.spBarcodeReader)
        spPrinter = findViewById(R.id.spPrinter)
    }

    private fun setupEditorState() {
        if (sessionManager.isLoggedIn()) {
            etSiteCode.isEnabled = false
        } else {
            etSiteCode.isEnabled = true
        }
    }

    private fun setupBarcodeReaderSpinner() {
        val options = listOf("Scanner", "Camera")

        val adapter = ArrayAdapter(
			this,
			android.R.layout.simple_spinner_item,
			options
		)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spBarcodeReader.adapter = adapter
    }

    private fun setupPrinterSpinner() {
        val displayList = printerOptions.map { it.display }

        val adapter = ArrayAdapter(
			this,
			android.R.layout.simple_spinner_item,
			displayList
		)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spPrinter.adapter = adapter
    }

    /**
     * Load setting saat Activity dibuka
     */
    private fun loadSetting() {
        etSiteCode.setText(prefs.getString("site_code", ""))
        etBrandCode.setText(prefs.getString("brand_code", ""))

        val barcodeReader = prefs.getString("barcode_reader", "Scanner")
        val barcodeIndex =
            if (barcodeReader == "Camera") 1 else 0
        spBarcodeReader.setSelection(barcodeIndex)

        val printerPrefix = prefs.getString("printer_prefix", "")
        val printerIndex = printerOptions.indexOfFirst {
            it.prefix == printerPrefix
        }.takeIf { it >= 0 } ?: 0

        spPrinter.setSelection(printerIndex)
    }

    /**
     * Simpan setting otomatis
     */
    private fun saveSetting() {
        val selectedPrinter = printerOptions[spPrinter.selectedItemPosition]

        prefs.edit().apply {
            putString("site_code", etSiteCode.text.toString())
            putString("brand_code", etBrandCode.text.toString())
            putString("barcode_reader", spBarcodeReader.selectedItem.toString())
            putString("printer_prefix", selectedPrinter.prefix)
            apply()
        }
    }

    override fun onPause() {
        super.onPause()
        saveSetting() // AUTO SAVE saat Back / Home
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}