package org.hesab.app

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        prefs = getSharedPreferences("hesab_prefs", MODE_PRIVATE)

        // فونت و سایز
        val fontSizeSeekBar = findViewById<SeekBar>(R.id.seekFontSize)
        val txtFontSizeValue = findViewById<TextView>(R.id.txtFontSizeValue)

        // رنگ متن
        val colorSpinner = findViewById<Spinner>(R.id.spinnerTextColor)

        // تم
        val themeSpinner = findViewById<Spinner>(R.id.spinnerTheme)

        // خطوط بین ردیف‌ها
        val chkLines = findViewById<CheckBox>(R.id.chkLines)
        val chkExcelMode = findViewById<CheckBox>(R.id.chkExcelMode)

        // گزینه نمایش مبلغ به تومان
        val chkShowToman = findViewById<CheckBox>(R.id.chkShowToman)

        // دکمه ذخیره
        val btnSave = findViewById<Button>(R.id.btnSaveSettings)

        // ---------- مقداردهی اولیه ----------
        val savedFontSize = prefs.getInt("fontSize", 16)
        fontSizeSeekBar.progress = savedFontSize
        txtFontSizeValue.text = "$savedFontSize sp"

        val savedColorIndex = prefs.getInt("colorIndex", 0)
        colorSpinner.setSelection(savedColorIndex)

        val savedThemeIndex = prefs.getInt("themeIndex", 0)
        themeSpinner.setSelection(savedThemeIndex)

        chkLines.isChecked = prefs.getBoolean("lines", true)
        chkExcelMode.isChecked = prefs.getBoolean("excelMode", false)
        chkShowToman.isChecked = prefs.getBoolean("showToman", false)

        // ---------- رفتار کنترل‌ها ----------
        fontSizeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                txtFontSizeValue.text = "$progress sp"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // ---------- دکمه ذخیره ----------
        btnSave.setOnClickListener {
            prefs.edit()
                .putInt("fontSize", fontSizeSeekBar.progress)
                .putInt("colorIndex", colorSpinner.selectedItemPosition)
                .putInt("themeIndex", themeSpinner.selectedItemPosition)
                .putBoolean("lines", chkLines.isChecked)
                .putBoolean("excelMode", chkExcelMode.isChecked)
                .putBoolean("showToman", chkShowToman.isChecked)
                .apply()

            Toast.makeText(this, "تنظیمات ذخیره شد ✅", Toast.LENGTH_SHORT).show()
            finish()
        }

        // ---------- رنگ‌ها ----------
        val colors = arrayOf("پیش‌فرض (درآمد سبز، هزینه قرمز)", "آبی", "نارنجی", "خاکستری", "گرادینت")
        val colorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, colors)
        colorSpinner.adapter = colorAdapter

        // ---------- تم‌ها ----------
        val themes = arrayOf("پیش‌فرض (آبی روشن)", "تیره", "سبز", "طلایی")
        val themeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, themes)
        themeSpinner.adapter = themeAdapter
    }
}
