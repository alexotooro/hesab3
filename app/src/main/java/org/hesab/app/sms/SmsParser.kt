package org.hesab.app.sms

import java.util.regex.Pattern

data class ParsedSms(
    val isTransaction: Boolean,
    val isIncome: Boolean,
    val amountRials: Long,
    val bankName: String?
)

object SmsParser {

    // لیست کلیدواژه‌های بانکی برای تشخیص
    private val bankKeywords = mapOf(
        "صادرات" to listOf("صادرات"),
        "سپه" to listOf("سپه"),
        "رسالت" to listOf("رسالت"),
        "ملی" to listOf("ملی"),
        "ملت" to listOf("ملت"),
        "پاسارگاد" to listOf("پاسارگاد"),
        "پارس" to listOf("پارس"),
        "اقتصادنوین" to listOf("اقتصاد نوین","نوین"),
        "رفاه" to listOf("رفاه")
    )

    // regex برای پیدا کردن مبلغ (اعداد با یا بدون جداکننده)
    private val amountPattern = Pattern.compile("(?:(?:\\d{1,3}[,٬\\s])*(?:\\d{1,3}))+(?:\\.\\d+)?")

    fun parseBankSms(body: String): ParsedSms {
        val lower = body.trim()
        // تشخیص واریز/برداشت با کلمات کلیدی معمول
        val incomeWords = listOf("واریز", "شارژ", "واریزی", "درآمد")
        val expenseWords = listOf("برداشت", "خرج", "کسر", "کسر مبلغ", "برداشت وجه", "خرید")

        val isIncome = incomeWords.any { lower.contains(it) }
        val isExpense = expenseWords.any { lower.contains(it) }
        val isTransaction = isIncome || isExpense

        // مقدار را بیرون بکش
        val m = amountPattern.matcher(lower)
        var amount = 0L
        if (m.find()) {
            val raw = m.group().replace(Regex("[^0-9]"), "")
            if (raw.isNotEmpty()) {
                // بعضی پیامک‌ها مبالغ را به تومان می‌فرستند؛ اینجا فرض می‌کنیم پیام بانک ریال می‌دهد
                amount = try { raw.toLong() } catch (e: Exception) { 0L }
            }
        }

        // حدس نام بانک
        var bankGuess: String? = null
        for ((bankName, keys) in bankKeywords) {
            if (keys.any { lower.contains(it) }) {
                bankGuess = bankName
                break
            }
        }

        // اگر هیچ کلمه‌ی خاصی نبود اما متن شامل 'بانک' بود، سعی کنیم نام بعدی را بگیریم (ساده)
        if (bankGuess == null) {
            val bankRegex = Pattern.compile("بانک\\s+([\\p{L}0-9]+)")
            val m2 = bankRegex.matcher(lower)
            if (m2.find()) bankGuess = m2.group(1)
        }

        return ParsedSms(
            isTransaction = isTransaction,
            isIncome = isIncome && !isExpense,
            amountRials = amount,
            bankName = bankGuess
        )
    }
}
