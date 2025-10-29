package org.hesab.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,        // "income" یا "expense"
    val date: String,
    val amount: Double,
    val category: String,
    val description: String?
)
