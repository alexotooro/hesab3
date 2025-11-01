package org.hesab.app

import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchEnableSms: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        switchEnableSms = findViewById(R.id.switchEnableSms)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isEnabled = prefs.getBoolean("sms_enabled", false)
        switchEnableSms.isChecked = isEnabled

        switchEnableSms.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            prefs.edit().putBoolean("sms_enabled", isChecked).apply()
        }
    }
}
