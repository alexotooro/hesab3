package org.hesab.app.utils

import org.hesab.app.Transaction
import java.text.SimpleDateFormat
import java.util.*

object SmsParser {

    fun parse(bankSms: String): Transaction? {
        return try {
            val amount = Regex("([0-9,]+)").find(bankSms)?.value?.replace(",", "")?.toLong() ?: 0L
            val isIncome = bankSms.contains("واریز") || bankSms.contains("دریافت")
            val category = if (isIncome) "واریز بانکی" else "برداشت بانکی"
            val note = bankSms.take(50)
            val date = Date() // الان تاریخ لحظه دریافت پیام

            Transaction(
                date = date,
                amount = amount,
                category = category,
                note = note,
                isIncome = isIncome
            )
        } catch (e: Exception) {
            null
        }
    }
}
