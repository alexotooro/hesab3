// app/src/main/java/org/hesab/app/SettingsActivity.kt
package org.hesab.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val switchNotifications = findViewById<SwitchCompat>(R.id.switchNotifications)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        switchNotifications.isChecked = prefs.getBoolean("notifications", true)

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notifications", isChecked).apply()
        }
    }
}
