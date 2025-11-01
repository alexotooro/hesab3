package org.hesab.app

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: Date = Date(),              // تاریخ تراکنش
    val amount: Long,                     // مبلغ
    val category: String,                 // بابت (مثلاً خرید، واریز و...)
    val note: String? = null,             // توضیحات
    val isIncome: Boolean = false,        // درآمد یا هزینه
    val orderIndex: Int = 0               // برای مرتب‌سازی در RecyclerView
)
