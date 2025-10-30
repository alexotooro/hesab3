package org.hesab.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: String,          // تاریخ تراکنش
    val amount: Long,          // مبلغ تراکنش
    val category: String,      // بابت
    val description: String,   // توضیحات
    val type: String,          // نوع: "درآمد" یا "هزینه"
    var orderIndex: Int = 0    // ترتیب ردیف برای نمایش در RecyclerView
)
