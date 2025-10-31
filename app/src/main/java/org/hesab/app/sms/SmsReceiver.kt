class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val enableSms = prefs.getBoolean("enable_sms", true) // دریافت وضعیت فعال/غیرفعال بودن

        if (!enableSms) return // اگر غیرفعال باشد، هیچ چیزی پردازش نکنیم

        val extras = intent.extras ?: return
        val pdus = extras.get("pdus") as? Array<*> ?: return

        for (pdu in pdus) {
            val message = SmsMessage.createFromPdu(pdu as ByteArray)
            val msgBody = message.messageBody ?: continue

            if (msgBody.contains("برداشت") || msgBody.contains("واریز")) {
                val amountRegex = Regex("([\\d,]+) ?ریال")
                val amount = amountRegex.find(msgBody)?.groupValues?.get(1)?.replace(",", "")?.toLongOrNull() ?: 0L
                val isIncome = msgBody.contains("واریز")

                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val date = sdf.format(Date())

                val transaction = Transaction(
                    date = date,
                    amount = amount,
                    category = "سایر",
                    description = "افزوده از پیامک بانکی",
                    isIncome = isIncome,
                    orderIndex = 0
                )

                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getDatabase(context)
                    db.transactionDao().insert(transaction)
                }

                showNotification(context, transaction)
            }
        }
    }

    private fun showNotification(context: Context, transaction: Transaction) {
        val channelId = "hesab_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "تراکنش‌ها", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(context, AddTransactionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("amount", transaction.amount.toString())
            putExtra("date", transaction.date)
            putExtra("category", transaction.category)
            putExtra("description", transaction.description)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("تراکنش افزوده شد")
            .setContentText("${if (transaction.isIncome) "واریز" else "برداشت"} ${transaction.amount} ریال")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
