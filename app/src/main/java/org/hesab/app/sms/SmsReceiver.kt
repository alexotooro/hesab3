package org.hesab.app.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hesab.app.AppDatabase
import org.hesab.app.Transaction
import org.hesab.app.TransactionRepository

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION != intent.action) return

        val bundle: Bundle? = intent.extras
        val msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        for (msg in msgs) {
            val messageBody = msg.messageBody ?: continue

            // بررسی وجود کلمات کلیدی تراکنش بانکی
            if (messageBody.contains("برداشت") || messageBody.contains("واریز")) {
                val amount = extractAmount(messageBody)
                val type = if (messageBody.contains("برداشت")) "هزینه" else "درآمد"

                val transaction = Transaction(
                    date = java.text.SimpleDateFormat("yyyy/MM/dd HH:mm").format(System.currentTimeMillis()),
                    amount = amount,
                    category = type,
                    note = "از پیامک بانکی"
                )

                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getDatabase(context)
                    val repo = TransactionRepository(db)
                    repo.insert(transaction)
                }
            }
        }
    }

    private fun extractAmount(message: String): Long {
        val regex = Regex("[\\d,]+")
        val match = regex.find(message)
        return match?.value?.replace(",", "")?.toLongOrNull() ?: 0L
    }
}
