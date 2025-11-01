package org.hesab.app.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hesab.app.AppDatabase
import org.hesab.app.utils.SmsParser

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            for (sms in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                val sender = sms.displayOriginatingAddress
                val messageBody = sms.messageBody

                if (SmsParser.isBankMessage(sender, messageBody)) {
                    val transaction = SmsParser.parseMessage(messageBody)
                    if (transaction != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val db = AppDatabase.getDatabase(context)
                            db.transactionDao().insert(transaction)
                        }
                    }
                }
            }
        }
    }
}
