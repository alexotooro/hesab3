package org.hesab.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import org.hesab.app.AddTransactionActivity
import org.hesab.app.Transaction

object NotificationHelper {
    private const val CHANNEL_ID = "hesab_channel"

    fun showTransactionAddedNotification(context: Context, insertedId: Long, tx: Transaction, bankGuess: String) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(CHANNEL_ID, "Hesab notifications", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(ch)
        }

        val intent = Intent(context, AddTransactionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("prefill_from_sms", true)
            putExtra("transaction_id_prefill", insertedId)
            putExtra("bank_guess", bankGuess)
        }
        val pi = PendingIntent.getActivity(context, insertedId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("تراکنش افزوده شد")
            .setContentText("مبلغ ${tx.amount} ریال اضافه شد")
            .setSmallIcon(android.R.drawable.stat_notify_more)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        nm.notify((1000 + insertedId % 1000).toInt(), notification)
    }
}
