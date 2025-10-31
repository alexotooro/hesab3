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
            val timestampMillis = sms.timestampMillis ?: System.currentTimeMillis()
            handleSms(context, body, timestampMillis)
        }
    }

    private fun handleSms(context: Context, body: String, timestampMillis: Long) {
        val parsed = SmsParser.parseBankSms(body)
        if (!parsed.isTransaction) return

        val category = "سایر" // default as user requested
        val bankGuess = parsed.bankName ?: "صادرات"
        val dateShamsi = PersianDateHelper.toPersianDateString(Date(timestampMillis))
        val amountRials = parsed.amountRials

        val transaction = Transaction(
            date = dateShamsi,
            amount = amountRials,
            category = category,
            description = "پیامک خودکار",
            isIncome = parsed.isIncome,
            orderIndex = 0 // will fix order after insert
        )

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(context)
            val insertedId = db.transactionDao().insert(transaction) // add insert in DAO
            // update ordering (place newly inserted at top as required elsewhere)
            NotificationHelper.showTransactionAddedNotification(context, insertedId, transaction, bankGuess)
        }
    }
}
