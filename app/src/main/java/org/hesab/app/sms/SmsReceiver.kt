package org.hesab.app.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hesab.app.AppDatabase
import org.hesab.app.Transaction
import org.hesab.app.notification.NotificationHelper
import java.util.Date

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.provider.Telephony.SMS_RECEIVED") return
        val bundle: Bundle? = intent.extras
        val pdus = bundle?.get("pdus") as? Array<*>
        if (pdus == null) return
        for (pdu in pdus) {
            val format = bundle.getString("format")
            val sms = SmsMessage.createFromPdu(pdu as ByteArray, format)
            val body = sms.messageBody ?: continue
            val ts = sms.timestampMillis
            handleSms(context, body, ts)
        }
    }

    private fun handleSms(context: Context, body: String, timestampMillis: Long) {
        val parsed = SmsParser.parseBankSms(body)
        if (!parsed.isTransaction) return

        val dateShamsi = PersianDateHelper.toPersianDateString(java.util.Date(timestampMillis))
        val category = "سایر"
        val bankGuess = parsed.bankName ?: "صادرات"
        val tx = Transaction(date = dateShamsi, amount = parsed.amountRials, category = category, description = "پیامک خودکار", isIncome = parsed.isIncome)
        CoroutineScope(Dispatchers.IO).launch {
            val id = AppDatabase.getInstance(context).transactionDao().insert(tx)
            NotificationHelper.showTransactionAddedNotification(context, id, tx, bankGuess)
        }
    }
}
