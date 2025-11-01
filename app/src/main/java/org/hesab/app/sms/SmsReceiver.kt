package org.hesab.app.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import androidx.preference.PreferenceManager
import org.hesab.app.db.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val smsEnabled = prefs.getBoolean("enable_sms", true)
            if (!smsEnabled) return

            val bundle: Bundle? = intent.extras
            val msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (msg in msgs) {
                val messageBody = msg.messageBody
                val sender = msg.originatingAddress ?: ""
                CoroutineScope(Dispatchers.IO).launch {
                    TransactionRepository(context).processIncomingSms(sender, messageBody)
                }
            }
        }
    }
}
