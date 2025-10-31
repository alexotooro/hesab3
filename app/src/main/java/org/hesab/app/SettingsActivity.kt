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

        switchDarkMode.isChecked = prefs.getBoolean("darkMode", false)
        switchShowLines.isChecked = prefs.getBoolean("showLines", true)
        switchAlternateRows.isChecked = prefs.getBoolean("alternateRows", false)
        switchShowInTomans.isChecked = prefs.getBoolean("showInTomans", false)

        val fontSize = prefs.getFloat("fontSize", 16f)
        tvPreview.textSize = fontSize

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("darkMode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        switchShowLines.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("showLines", isChecked).apply()
        }

        switchAlternateRows.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("alternateRows", isChecked).apply()
        }

        switchShowInTomans.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("showInTomans", isChecked).apply()
        }

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
    }
}
