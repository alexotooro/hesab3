package org.hesab.app.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hesab.app.AppDatabase
import org.hesab.app.Transaction
import org.hesab.app.TransactionRepository
import org.hesab.app.utils.SmsParser

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
                val bundle: Bundle? = intent.extras
                if (bundle != null) {
                    val pdus = bundle["pdus"] as Array<*>
                    val messages = Array(pdus.size) { i ->
                        val format = bundle.getString("format")
                        SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
                    }

                    for (message in messages) {
                        val msgBody = message.messageBody
                        val sender = message.displayOriginatingAddress ?: ""

                        Log.d("SmsReceiver", "📩 Message from: $sender, text: $msgBody")

                        // بررسی اینکه آیا پیامک بانکی است یا نه
                        if (SmsParser.isBankMessage(msgBody)) {
                            val transaction = SmsParser.parseMessage(msgBody, sender)

                            // درج در دیتابیس با Repository
                            val db = AppDatabase.getDatabase(context)
                            val repo = TransactionRepository(db)

                            CoroutineScope(Dispatchers.IO).launch {
                                repo.insert(transaction)
                                Log.d("SmsReceiver", "✅ Transaction saved from SMS: $transaction")
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SmsReceiver", "❌ Error parsing SMS", e)
        }
    }
}
