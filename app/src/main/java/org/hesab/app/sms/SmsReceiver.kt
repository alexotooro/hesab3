package org.hesab.app.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hesab.app.AppDatabase
import org.hesab.app.R
import org.hesab.app.Transaction
import org.hesab.app.AddTransactionActivity
import java.text.SimpleDateFormat
import java.util.*

class SMSReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT > 25) return // فقط برای اندروید 7 و پایین فعال است

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            for (sms in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                val msgBody = sms.messageBody
                val bankGuess = guessBank(msgBody)
                val amount = extractAmount(msgBody)
                val type = detectType(msgBody)

                if (amount > 0) {
                    val transaction = Transaction(
                        id = 0,
                        date = getTodayJalali(),
                        amount = amount,
                        category = type,
                        description = "ثبت خودکار از پیامک بانکی",
                        bank = bankGuess,
                        orderIndex = 0
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = AppDatabase.getInstance(context).transactionDao()
                        dao.insert(transaction)
                    }

                    showNotification(context, transaction)
                }
            }
        }
    }

    private fun detectType(text: String): String {
        return when {
            text.contains("واریز") || text.contains("deposit", true) -> "درآمد"
            text.contains("برداشت") || text.contains("withdraw", true) -> "هزینه"
            else -> "سایر"
        }
    }

    private fun extractAmount(text: String): Long {
        val regex = Regex("(\\d{1,3}(,\\d{3})*)\\s*ریال")
        val match = regex.find(text)
        return match?.groupValues?.get(1)?.replace(",", "")?.toLongOrNull() ?: 0L
    }

    private fun guessBank(text: String): String {
        return when {
            text.contains("ملی") -> "ملی"
            text.contains("ملت") -> "ملت"
            text.contains("صادرات") -> "صادرات"
            text.contains("تجارت") -> "تجارت"
            text.contains("رفاه") -> "رفاه"
            else -> "صادرات"
        }
    }

    private fun getTodayJalali(): String {
        val f = SimpleDateFormat("yyyy/MM/dd", Locale("fa"))
        return f.format(Date())
    }

    private fun showNotification(context: Context, t: Transaction) {
        val channelId = "transaction_added"
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_add)
            .setContentTitle("تراکنش جدید افزوده شد")
            .setContentText("${t.amount} ریال - ${t.category}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val intent = Intent(context, AddTransactionActivity::class.java).apply {
            putExtra("id", t.id)
        }

        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            0,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(context)) {
            notify((0..9999).random(), builder.build())
        }
    }
}
