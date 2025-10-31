package org.hesab.app.sms

import java.util.regex.Pattern

data class ParsedSms(val isTransaction: Boolean, val isIncome: Boolean, val amountRials: Long, val bankName: String?)

object SmsParser {
    private val incomeWords = listOf("واریز", "واریزی", "شارژ", "واریز شد")
    private val expenseWords = listOf("برداشت", "خرید", "کسر", "پرداخت", "کارت به کارت")
    private val amountPattern = Pattern.compile("(?:\\d{1,3}[,٬\\s])*\\d+")

    private val bankKeywords = mapOf(
        "صادرات" to listOf("صادرات"),
        "سپه" to listOf("سپه"),
        "رسالت" to listOf("رسالت"),
        "ملی" to listOf("ملی"),
        "ملت" to listOf("ملت"),
        "پاسارگاد" to listOf("پاسارگاد")
    )

    fun parseBankSms(body: String): ParsedSms {
        val lower = body.trim()
        val isIncome = incomeWords.any { lower.contains(it) }
        val isExpense = expenseWords.any { lower.contains(it) }
        val isTransaction = isIncome || isExpense

        var amount = 0L
        val m = amountPattern.matcher(lower.replace(".", "").replace("٬", ","))
        if (m.find()) {
            val num = m.group().replace(Regex("[^0-9]"), "")
            if (num.isNotEmpty()) {
                amount = try { num.toLong() } catch (e: Exception) { 0L }
            }
        }

        var bankGuess: String? = null
        for ((bank, keys) in bankKeywords) {
            if (keys.any { lower.contains(it) }) {
                bankGuess = bank
                break
            }
        }

        return ParsedSms(isTransaction = isTransaction, isIncome = isIncome && !isExpense, amountRials = amount, bankName = bankGuess)
    }
}
