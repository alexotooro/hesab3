package org.hesab.app

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = "تنظیمات"

        prefs = getSharedPreferences("hesab_settings", MODE_PRIVATE)

        val switchDarkMode = findViewById<Switch>(R.id.switchDarkMode)
        val switchShowLines = findViewById<Switch>(R.id.switchShowLines)
        val switchAlternateRows = findViewById<Switch>(R.id.switchAlternateRows)
        val switchShowInTomans = findViewById<Switch>(R.id.switchShowInTomans)
        val btnFontSizeUp = findViewById<Button>(R.id.btnFontSizeUp)
        val btnFontSizeDown = findViewById<Button>(R.id.btnFontSizeDown)
        val tvPreview = findViewById<TextView>(R.id.tvPreview)

        // بازیابی مقادیر قبلی
        switchDarkMode.isChecked = prefs.getBoolean("darkMode", false)
        switchShowLines.isChecked = prefs.getBoolean("showLines", true)
        switchAlternateRows.isChecked = prefs.getBoolean("alternateRows", false)
        switchShowInTomans.isChecked = prefs.getBoolean("showInTomans", false)

        val fontSize = prefs.getFloat("fontSize", 16f)
        tvPreview.textSize = fontSize

        // حالت شب
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("darkMode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // نمایش خطوط بین ردیف‌ها
        switchShowLines.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("showLines", isChecked).apply()
            Toast.makeText(this, "نمایش خطوط: ${if (isChecked) "فعال" else "غیرفعال"}", Toast.LENGTH_SHORT).show()
        }

        // ردیف‌های یکی‌درمیان رنگ متفاوت
        switchAlternateRows.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("alternateRows", isChecked).apply()
            Toast.makeText(this, "حالت اکسل: ${if (isChecked) "فعال" else "غیرفعال"}", Toast.LENGTH_SHORT).show()
        }

        // نمایش مبلغ به تومان
        switchShowInTomans.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("showInTomans", isChecked).apply()
            Toast.makeText(this, "واحد نمایش مبلغ: ${if (isChecked) "تومان" else "ریال"}", Toast.LENGTH_SHORT).show()
        }

        // سایز فونت
        btnFontSizeUp.setOnClickListener {
            val newSize = fontSize + 1
            prefs.edit().putFloat("fontSize", newSize).apply()
            tvPreview.textSize = newSize
        }

        btnFontSizeDown.setOnClickListener {
            val newSize = fontSize - 1
            prefs.edit().putFloat("fontSize", newSize).apply()
            tvPreview.textSize = newSize
        }

        // پیش‌نمایش رنگ و متن
        val colorPreview = findViewById<ImageView>(R.id.colorPreview)
        val btnColorGreen = findViewById<Button>(R.id.btnColorGreen)
        val btnColorRed = findViewById<Button>(R.id.btnColorRed)
        val btnColorGradient = findViewById<Button>(R.id.btnColorGradient)

        btnColorGreen.setOnClickListener {
            prefs.edit().putInt("textColor", ContextCompat.getColor(this, R.color.income_green)).apply()
            colorPreview.setBackgroundColor(ContextCompat.getColor(this, R.color.income_green))
        }

        btnColorRed.setOnClickListener {
            prefs.edit().putInt("textColor", ContextCompat.getColor(this, R.color.expense_red)).apply()
            colorPreview.setBackgroundColor(ContextCompat.getColor(this, R.color.expense_red))
        }

        btnColorGradient.setOnClickListener {
            Toast.makeText(this, "در نسخه بعدی فعال می‌شود", Toast.LENGTH_SHORT).show()
        }
    }
}
