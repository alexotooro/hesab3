package org.hesab.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var smsButton: Button
    private val smsPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(this, "دسترسی پیامک فعال شد", Toast.LENGTH_SHORT).show()
            openSmsList()
        } else {
            Toast.makeText(this, "برای افزودن از پیامک، اجازه لازم است", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        smsButton = findViewById(R.id.btn_add_from_sms)

        // فقط برای اندروید 7 و پایین فعال می‌ماند
        if (Build.VERSION.SDK_INT <= 25) {
            smsButton.isEnabled = true
            smsButton.setOnClickListener {
                checkSmsPermissionAndOpen()
            }
        } else {
            smsButton.isEnabled = false
            smsButton.text = "غیرفعال در نسخه‌های جدید اندروید"
        }
    }

    private fun checkSmsPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_GRANTED -> openSmsList()

            shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS) -> {
                Toast.makeText(this, "برای خواندن پیامک، اجازه بدهید", Toast.LENGTH_LONG).show()
                smsPermission.launch(Manifest.permission.READ_SMS)
            }

            else -> smsPermission.launch(Manifest.permission.READ_SMS)
        }
    }

    private fun openSmsList() {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_MESSAGING)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "امکان باز کردن پیام‌ها وجود ندارد", Toast.LENGTH_SHORT).show()
        }
    }
}
