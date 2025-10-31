package org.hesab.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SettingsActivity : AppCompatActivity() {

    private val REQUEST_SMS_PERMISSION = 101
    private val REQUEST_PICK_SMS = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val addFromSmsLayout = findViewById<LinearLayout>(R.id.layoutAddFromSms)

        addFromSmsLayout.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_SMS),
                    REQUEST_SMS_PERMISSION
                )
            } else {
                openSmsList()
            }
        }
    }

    private fun openSmsList() {
        val intent = Intent(Intent.ACTION_PICK, Uri.parse("content://sms/inbox"))
        startActivityForResult(intent, REQUEST_PICK_SMS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_SMS && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val bodyIndex = it.getColumnIndexOrThrow(Telephony.Sms.BODY)
                    val body = it.getString(bodyIndex)
                    parseSmsAndOpenAddTransaction(body)
                }
            }
        }
    }

    private fun parseSmsAndOpenAddTransaction(body: String) {
        if (!body.contains("برداشت") && !body.contains("واریز")) {
            Toast.makeText(this, "پیامک بانکی معتبر یافت نشد", Toast.LENGTH_SHORT).show()
            return
        }

        val amountRegex = Regex("([\\d,]+) ?ریال")
        val amount = amountRegex.find(body)?.groupValues?.get(1)?.replace(",", "") ?: "0"
        val isIncome = body.contains("واریز")

        val intent = Intent(this, AddTransactionActivity::class.java).apply {
            putExtra("amount", amount)
            putExtra("date", JalaliDate.today())
            putExtra("category", "سایر")
            putExtra("description", "افزوده از پیامک بانکی")
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_SMS_PERMISSION &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            openSmsList()
        } else {
            Toast.makeText(this, "اجازه دسترسی به پیامک داده نشد", Toast.LENGTH_SHORT).show()
        }
    }
}
