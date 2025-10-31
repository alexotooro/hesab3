package org.hesab.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.hesab.app.databinding.ActivitySettingsBinding
import java.util.regex.Pattern

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val PICK_SMS_REQUEST = 2001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // دکمه‌ی افزودن تراکنش از پیامک دلخواه
        binding.btnAddFromSms.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_PICK, Telephony.Sms.Inbox.CONTENT_URI)
                startActivityForResult(intent, PICK_SMS_REQUEST)
            } catch (e: Exception) {
                Toast.makeText(this, "امکان دسترسی به پیامک‌ها وجود ندارد", Toast.LENGTH_SHORT).show()
            }
        }

        // سایر گزینه‌های تنظیمات فعلی شما (در صورت وجود) پایین همین خط می‌مانند
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_SMS_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val uri: Uri? = data.data
            if (uri != null) {
                val cursor = contentResolver.query(uri, arrayOf("body"), null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val smsBody = it.getString(it.getColumnIndexOrThrow("body"))
                        processSmsText(smsBody)
                    }
                }
            }
        }
    }

    private fun processSmsText(sms: String) {
        val amountRegex = Pattern.compile("(\\d{1,3}(,\\d{3})*)\\s*ریال")
        val depositKeywords = listOf("واریز", "deposit", "credited")
        val withdrawKeywords = listOf("برداشت", "withdraw", "debited")

        val matcher = amountRegex.matcher(sms)
        var amount = 0L
        if (matcher.find()) {
            val raw = matcher.group(1).replace(",", "")
            amount = raw.toLongOrNull() ?: 0L
        }

        val isDeposit = depositKeywords.any { sms.contains(it, ignoreCase = true) }
        val isWithdraw = withdrawKeywords.any { sms.contains(it, ignoreCase = true) }

        val type = when {
            isDeposit -> "درآمد"
            isWithdraw -> "هزینه"
            else -> "سایر"
        }

        val bankGuess = when {
            sms.contains("ملی") -> "ملی"
            sms.contains("ملت") -> "ملت"
            sms.contains("صادرات") -> "صادرات"
            sms.contains("تجارت") -> "تجارت"
            sms.contains("رفاه") -> "رفاه"
            sms.contains("کشاورزی") -> "کشاورزی"
            else -> "صادرات"
        }

        val intent = Intent(this, AddTransactionActivity::class.java).apply {
            putExtra("amount", amount)
            putExtra("type", type)
            putExtra("description", sms.take(80))
            putExtra("bank", bankGuess)
            putExtra("autoDate", true)
        }
        startActivity(intent)
    }
}
