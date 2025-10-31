package org.hesab.app

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val switchDarkMode = findViewById<SwitchMaterial>(R.id.switchDarkMode)
        val switchShowTotal = findViewById<SwitchMaterial>(R.id.switchShowTotal)
        val switchSortOrder = findViewById<SwitchMaterial>(R.id.switchSortOrder)

        // مقداردهی اولیه از تنظیمات ذخیره‌شده
        switchDarkMode.isChecked = prefs.getBoolean("dark_mode", false)
        switchShowTotal.isChecked = prefs.getBoolean("show_total", true)
        switchSortOrder.isChecked = prefs.getBoolean("sort_descending", false)

        // رویدادها
        switchDarkMode.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked)
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        switchShowTotal.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("show_total", isChecked).apply()
        }

        switchSortOrder.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("sort_descending", isChecked).apply()
        }

        // دکمه بازگشت در نوار بالا
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "تنظیمات"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
