package org.hesab.app.utils

import android.util.Log
import org.hesab.app.Transaction
import java.util.Date
import java.util.regex.Pattern

object SmsParser {

    /**
     * تشخیص اینکه پیامک بانکی است یا نه
     */
    fun isBankMessage(message: String): Boolean {
        val bankKeywords = listOf("برداشت", "واریز", "خرید", "انتقال", "deposit", "withdraw", "purchase")
        return bankKeywords.any { message.contains(it, ignoreCase = true) }
    }

    /**
     * تبدیل متن پیامک به یک تراکنش
     */
    fun parseMessage(message: String, sender: String): Transaction {
        var amount = 0L
        var category = "بانک"
        var note = message.take(100)
        var isIncome = false

        try {
            // الگو برای پیدا کردن مبلغ
            val amountPattern = Pattern.compile("(\\d{1,3}(,\\d{3})*|\\d+)")
            val matcher = amountPattern.matcher(message)
            if (matcher.find()) {
                val raw = matcher.group(1)?.replace(",", "") ?: "0"
                amount = raw.toLong()
            }

            // تشخیص نوع تراکنش
            if (message.contains("واریز") || message.contains("deposit", true))
                isIncome = true
            else if (message.contains("برداشت") || message.contains("خرید") || message.contains("withdraw", true))
                isIncome = false

            category = when {
                message.contains("خرید", true) -> "خرید"
                message.contains("انتقال", true) -> "انتقال"
                message.contains("برداشت", true) -> "برداشت"
                message.contains("واریز", true) -> "واریز"
                else -> "بانک"
            }

        } catch (e: Exception) {
            Log.e("SmsParser", "خطا در تجزیه پیامک: ${e.message}")
        }

        return Transaction(
            id = 0,
            date = Date(),
            amount = amount,
            category = category,
            note = note,
            isIncome = isIncome,
            orderIndex = 0
        )
    }
}
