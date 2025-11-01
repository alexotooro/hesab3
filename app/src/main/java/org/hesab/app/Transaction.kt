package org.hesab.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: Long = System.currentTimeMillis(),   // تاریخ به صورت timestamp
    val amount: Long,                              // مبلغ
    val category: String,                          // بابت
    val note: String? = null,                      // توضیحات
    val isIncome: Boolean = false,                 // درآمد یا هزینه
    val orderIndex: Int = 0                        // ترتیب نمایش
)
