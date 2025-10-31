package org.hesab.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import samanzamani.persiandate.PersianDate
import samanzamani.persiandate.PersianDateFormat

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            val bundle = intent.extras
            try {
                if (bundle != null) {
                    val pdus = bundle["pdus"] as Array<*>
                    for (pdu in pdus) {
                        val smsMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val format = bundle.getString("format")
                            SmsMessage.createFromPdu(pdu as ByteArray, format)
                        } else {
                            SmsMessage.createFromPdu(pdu as ByteArray)
                        }

                        val messageBody = smsMessage.messageBody ?: ""
                        val sender = smsMessage.displayOriginatingAddress ?: ""
                        Log.d("SmsReceiver", "پیامک از $sender : $messageBody")

                        // بررسی کلمات کلیدی و استخراج مبلغ
                        if (messageBody.contains("واریز") || messageBody.contains("برداشت") || messageBody.contains("برداشتی")) {
                            val isIncome = messageBody.contains("واریز")
                            val amount = extractAmount(messageBody)
                            val bankName = guessBank(sender, messageBody)

                            // تاریخ شمسی امروز
                            val persianDate = PersianDate()
                            val persianFormat = PersianDateFormat("Y/m/d")
                            val date = persianFormat.format(persianDate)

                            // افزودن به دیتابیس
                            val transaction = Transaction(
                                date = date,
                                amount = amount,
                                category = "سایر",
                                description = "ثبت خودکار از پیامک",
                                isIncome = isIncome,
                                orderIndex = (Date().time / 1000).toInt()
                            )

                            CoroutineScope(Dispatchers.IO).launch {
                                val db = AppDatabase.getDatabase(context)
                                db.transactionDao().insert(transaction)

                                showNotification(context, amount, isIncome)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SmsReceiver", "خطا در پردازش پیامک: ${e.message}")
            }
        }
    }

    private fun extractAmount(message: String): Long {
        val regex = Regex("([0-9,]+)")
        val match = regex.find(message)
        return match?.value?.replace(",", "")?.toLongOrNull() ?: 0L
    }

    private fun guessBank(sender: String, message: String): String {
        return when {
            sender.contains("Sepah", true) -> "سپه"
            sender.contains("Resalat", true) -> "رسالت"
            sender.contains("Saderat", true) -> "صادرات"
            message.contains("سپه") -> "سپه"
            message.contains("رسالت") -> "رسالت"
            message.contains("صادرات") -> "صادرات"
            else -> "صادرات"
        }
    }

    private fun showNotification(context: Context, amount: Long, isIncome: Boolean) {
        val channelId = "transaction_channel"
        val text = if (isIncome) "درآمد جدید افزوده شد" else "هزینه جدید افزوده شد"

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_add)
            .setContentTitle("تراکنش افزوده شد")
            .setContentText("$text (${String.format("%,d", amount)} ریال)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify((System.currentTimeMillis() % 10000).toInt(), builder.build())
        }
    }
}
