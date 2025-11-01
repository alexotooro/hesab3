package org.hesab.app.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hesab.app.AppDatabase
import org.hesab.app.Transaction

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val smsEnabled = prefs.getBoolean("sms_enabled", false)
        if (!smsEnabled) return

        val db = AppDatabase.getDatabase(context)
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val pdus = bundle["pdus"] as? Array<*>
            pdus?.forEach { pdu ->
                val sms = SmsMessage.createFromPdu(pdu as ByteArray)
                val msgBody = sms.messageBody ?: ""
                Log.d("SmsReceiver", "SMS received: $msgBody")

                // نمونه‌ی ساده برای تشخیص تراکنش از متن پیام
                if (msgBody.contains("برداشت") || msgBody.contains("واریز")) {
                    val amount = extractAmount(msgBody)
                    val category = if (msgBody.contains("برداشت")) "برداشت" else "واریز"

                    CoroutineScope(Dispatchers.IO).launch {
                        db.transactionDao().insert(
                            Transaction(
                                date = java.time.LocalDate.now().toString(),
                                amount = amount,
                                category = category,
                                description = "از پیامک بانکی"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun extractAmount(message: String): Long {
        val regex = Regex("""\d[\d,]*""")
        val match = regex.find(message)?.value?.replace(",", "") ?: "0"
        return match.toLongOrNull() ?: 0L
    }
}
