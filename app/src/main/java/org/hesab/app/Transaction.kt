package org.hesab.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: Long = System.currentTimeMillis(),  // ✅ زمان به صورت timestamp
    val amount: Long,                             // مبلغ
    val category: String,                         // بابت (مثل خرید یا واریز)
    val note: String? = null,                     // توضیحات
    val isIncome: Boolean = false,                // درآمد یا هزینه
    val orderIndex: Int = 0                       // ترتیب در RecyclerView
)
