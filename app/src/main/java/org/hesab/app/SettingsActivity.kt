package org.hesab.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import org.hesab.app.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        binding.switchEnableSms.isChecked = prefs.getBoolean("enable_sms", true)

        binding.switchEnableSms.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("enable_sms", isChecked).apply()
        }

        FontHelper.refreshFont(binding.root)
    }
}
