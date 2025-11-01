package org.hesab.app.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hesab.app.AppDatabase
import org.hesab.app.Transaction

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val bundle = intent.extras
        if (bundle != null) {
            val pdus = bundle.get("pdus") as? Array<*>
            if (pdus != null) {
                for (pdu in pdus) {
                    val sms = SmsMessage.createFromPdu(pdu as ByteArray)
                    val messageBody = sms.messageBody
                    val sender = sms.originatingAddress

                    // نمونه ساده ثبت تراکنش از پیامک
                    val db = AppDatabase.getInstance(context)
                    val transaction = Transaction(
                        date = System.currentTimeMillis(),
                        amount = 0L,
                        category = "پیامک",
                        note = "$sender: $messageBody",
                        orderIndex = 0
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        db.transactionDao().insert(transaction)
                    }
                }
            }
        }
    }
}
