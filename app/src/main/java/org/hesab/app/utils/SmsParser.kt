package org.hesab.app.utils

import org.hesab.app.Transaction

object SmsParser {

    fun isBankMessage(sender: String?, body: String?): Boolean {
        if (sender == null || body == null) return false
        return sender.contains("BANK", ignoreCase = true)
    }

    fun parseMessage(body: String): Transaction? {
        try {
            val amountRegex = Regex("([0-9,]+)")
            val match = amountRegex.find(body) ?: return null
            val amount = match.value.replace(",", "").toLongOrNull() ?: return null

            val isIncome = body.contains("واریز", ignoreCase = true)
            val category = if (isIncome) "واریز بانکی" else "برداشت بانکی"

            return Transaction(
                amount = amount,
                category = category,
                note = body,
                isIncome = isIncome,
                date = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
