package org.hesab.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val amount: Long,
    val category: String,
    val description: String,
    val type: String = "هزینه", // 🆕 اضافه شد
    val orderIndex: Int = 0
)
