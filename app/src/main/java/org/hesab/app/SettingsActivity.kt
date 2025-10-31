package org.hesab.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var fontSizeSpinner: Spinner
    private lateinit var colorSpinner: Spinner
    private lateinit var themeSpinner: Spinner
    private lateinit var lineSwitch: Switch
    private lateinit var excelSwitch: Switch
    private lateinit var currencySwitch: Switch
    private lateinit var saveButton: Button
    private lateinit var smsButton: Button

    private val smsPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(this, "دسترسی پیامک فعال شد", Toast.LENGTH_SHORT).show()
            openSmsList()
        } else {
            Toast.makeText(this, "برای افزودن از پیامک، اجازه لازم است", Toast.LENGTH_SHORT).show()
        }
    }

    private val prefs by lazy {
        getSharedPreferences("hesab_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        fontSizeSpinner = findViewById(R.id.spinner_font_size)
        colorSpinner = findViewById(R.id.spinner_color)
        themeSpinner = findViewById(R.id.spinner_theme)
        lineSwitch = findViewById(R.id.switch_lines)
        excelSwitch = findViewById(R.id.switch_excel)
        currencySwitch = findViewById(R.id.switch_currency)
        saveButton = findViewById(R.id.btn_save_settings)
        smsButton = findViewById(R.id.btn_add_from_sms)

        setupSmsButton()
        loadPreferences()

        saveButton.setOnClickListener { savePreferences() }
    }

    private fun setupSmsButton() {
        if (Build.VERSION.SDK_INT <= 25) {
            smsButton.isEnabled = true
            smsButton.setOnClickListener {
                checkSmsPermissionAndOpen()
            }
        } else {
            smsButton.isEnabled = false
            smsButton.text = "غیرفعال در نسخه‌های جدید اندروید"
        }
    }

    private fun checkSmsPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_GRANTED -> openSmsList()

            shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS) -> {
                Toast.makeText(this, "برای خواندن پیامک، اجازه بدهید", Toast.LENGTH_LONG).show()
                smsPermission.launch(Manifest.permission.READ_SMS)
            }

            else -> smsPermission.launch(Manifest.permission.READ_SMS)
        }
    }

    private fun openSmsList() {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_MESSAGING)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "امکان باز کردن پیام‌ها وجود ندارد", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePreferences() {
        prefs.edit()
            .putString("font_size", fontSizeSpinner.selectedItem.toString())
            .putString("color", colorSpinner.selectedItem.toString())
            .putString("theme", themeSpinner.selectedItem.toString())
            .putBoolean("show_lines", lineSwitch.isChecked)
            .putBoolean("excel_mode", excelSwitch.isChecked)
            .putBoolean("use_toman", currencySwitch.isChecked)
            .apply()

        Toast.makeText(this, "تنظیمات ذخیره شد", Toast.LENGTH_SHORT).show()
    }

    private fun loadPreferences() {
        val fontSize = prefs.getString("font_size", "متوسط")
        val color = prefs.getString("color", "پیش‌فرض")
        val theme = prefs.getString("theme", "روشن")
        val showLines = prefs.getBoolean("show_lines", false)
        val excelMode = prefs.getBoolean("excel_mode", false)
        val useToman = prefs.getBoolean("use_toman", false)

        lineSwitch.isChecked = showLines
        excelSwitch.isChecked = excelMode
        currencySwitch.isChecked = useToman

        // انتخاب آیتم‌ها (برای simplicity فرض بر اینه که spinnerها داده‌های ساده دارن)
        fontSizeSpinner.setSelection(getSpinnerIndex(fontSizeSpinner, fontSize))
        colorSpinner.setSelection(getSpinnerIndex(colorSpinner, color))
        themeSpinner.setSelection(getSpinnerIndex(themeSpinner, theme))
    }

    private fun getSpinnerIndex(spinner: Spinner, value: String?): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == value) return i
        }
        return 0
    }
}
