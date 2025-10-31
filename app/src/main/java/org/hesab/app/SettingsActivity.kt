package org.hesab.app

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ اعمال تم و فونت قبل از inflate layout
        ThemeHelper.applyTheme(this)
        FontHelper.applyFont(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // --- سوئیچ‌ها ---
        val switchDarkMode = findViewById<SwitchMaterial>(R.id.switchDarkMode)
        val switchShowTotal = findViewById<SwitchMaterial>(R.id.switchShowTotal)
        val switchSortOrder = findViewById<SwitchMaterial>(R.id.switchSortOrder)
        val switchEnableSms = findViewById<SwitchMaterial>(R.id.switchEnableSms)

        switchDarkMode.isChecked = prefs.getBoolean("dark_mode", false)
        switchShowTotal.isChecked = prefs.getBoolean("show_total", true)
        switchSortOrder.isChecked = prefs.getBoolean("sort_descending", false)
        switchEnableSms.isChecked = prefs.getBoolean("enable_sms", true)

        switchDarkMode.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            ThemeHelper.refreshTheme(this)
        }

        switchShowTotal.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("show_total", isChecked).apply()
        }

        switchSortOrder.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("sort_descending", isChecked).apply()
        }

        switchEnableSms.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("enable_sms", isChecked).apply()
        }

        // --- انتخاب تم ---
        val themeSelector = findViewById<MaterialAutoCompleteTextView>(R.id.selectTheme)
        val themes = listOf("آبی روشن", "سبز ملایم", "تیره کلاسیک", "روشن ساده")
        themeSelector.setSimpleItems(themes.toTypedArray())

        val currentTheme = when (prefs.getString("app_theme", "light_blue")) {
            "soft_green" -> "سبز ملایم"
            "dark" -> "تیره کلاسیک"
            "light" -> "روشن ساده"
            else -> "آبی روشن"
        }
        themeSelector.setText(currentTheme, false)

        themeSelector.setOnItemClickListener { _, _, position, _ ->
            val value = when (themes[position]) {
                "آبی روشن" -> "light_blue"
                "سبز ملایم" -> "soft_green"
                "تیره کلاسیک" -> "dark"
                "روشن ساده" -> "light"
                else -> "light_blue"
            }
            prefs.edit().putString("app_theme", value).apply()
            ThemeHelper.refreshTheme(this)
        }

        // --- انتخاب فونت ---
        val fontSelector = findViewById<MaterialAutoCompleteTextView>(R.id.selectFont)
        val fonts = listOf("سیستم", "ایران‌سنس", "وزیر", "دیانا")
        fontSelector.setSimpleItems(fonts.toTypedArray())

        val currentFont = when (prefs.getString("app_font", "vazir")) {
            "system" -> "سیستم"
            "iransans" -> "ایران‌سنس"
            "diana" -> "دیانا"
            else -> "وزیر"
        }
        fontSelector.setText(currentFont, false)

        fontSelector.setOnItemClickListener { _, _, position, _ ->
            val value = when (fonts[position]) {
                "سیستم" -> "system"
                "ایران‌سنس" -> "iransans"
                "وزیر" -> "vazir"
                "دیانا" -> "diana"
                else -> "vazir"
            }
            prefs.edit().putString("app_font", value).apply()
            FontHelper.applyFont(this)
            FontHelper.refreshFont(window.decorView.rootView) // ✅ بدون ری‌استارت
        }

        // --- نوار بالا ---
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "تنظیمات"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
