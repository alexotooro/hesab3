package org.hesab.app

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // اعمال تم و فونت قبل از نمایش صفحه
        ThemeHelper.applyTheme(this)
        FontHelper.applyFont(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // --- سوییچ‌ها ---
        val switchDarkMode = findViewById<SwitchMaterial>(R.id.switchDarkMode)
        val switchShowTotal = findViewById<SwitchMaterial>(R.id.switchShowTotal)
        val switchSortOrder = findViewById<SwitchMaterial>(R.id.switchSortOrder)

        switchDarkMode.isChecked = prefs.getBoolean("dark_mode", false)
        switchShowTotal.isChecked = prefs.getBoolean("show_total", true)
        switchSortOrder.isChecked = prefs.getBoolean("sort_descending", false)

        switchDarkMode.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        switchShowTotal.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("show_total", isChecked).apply()
        }

        switchSortOrder.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("sort_descending", isChecked).apply()
        }

        // --- انتخاب تم ---
        val themeSelector = findViewById<MaterialAutoCompleteTextView>(R.id.selectTheme)
        val themes = listOf("روشن", "تیره", "آبی روشن", "سبز ملایم")
        themeSelector.setSimpleItems(themes.toTypedArray())
        val currentTheme = prefs.getString("app_theme", "روشن")
        themeSelector.setText(currentTheme, false)

        themeSelector.setOnItemClickListener { _, _, position, _ ->
            val value = when (themes[position]) {
                "روشن" -> "light"
                "تیره" -> "dark"
                "آبی روشن" -> "blue"
                "سبز ملایم" -> "green"
                else -> "light"
            }
            prefs.edit().putString("app_theme", value).apply()
            recreate()
        }

        // --- انتخاب فونت ---
        val fontSelector = findViewById<MaterialAutoCompleteTextView>(R.id.selectFont)
        val fonts = listOf("سیستم", "ایران‌سنس", "وزیر", "دیانا")
        fontSelector.setSimpleItems(fonts.toTypedArray())
        val currentFont = prefs.getString("app_font", "سیستم")
        fontSelector.setText(currentFont, false)

        fontSelector.setOnItemClickListener { _, _, position, _ ->
            val value = when (fonts[position]) {
                "سیستم" -> "system"
                "ایران‌سنس" -> "iransans"
                "وزیر" -> "vazir"
                "دیانا" -> "diana"
                else -> "system"
            }
            prefs.edit().putString("app_font", value).apply()
            recreate()
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
