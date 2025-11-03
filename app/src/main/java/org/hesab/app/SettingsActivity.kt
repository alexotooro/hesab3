package org.hesab.app

import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchEnableSms: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ قبل از setContentView تم فعال‌شده کاربر را اعمال کن
        applyUserTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        switchEnableSms = findViewById(R.id.switchEnableSms)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isEnabled = prefs.getBoolean("sms_enabled", false)
        switchEnableSms.isChecked = isEnabled

        switchEnableSms.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("sms_enabled", isChecked).apply()
        }
    }

    private fun applyUserTheme() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        when (prefs.getString("selected_theme", "LightBlue")) {
            "LightBlue" -> setTheme(R.style.Theme_Hesab_LightBlue)
            "SoftGreen" -> setTheme(R.style.Theme_Hesab_SoftGreen)
            "Dark" -> setTheme(R.style.Theme_Hesab_Dark)
            "Light" -> setTheme(R.style.Theme_Hesab_Light)
        }
    }
}
