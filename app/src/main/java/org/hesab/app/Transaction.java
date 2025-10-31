package org.hesab.app;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String date;
    public long amount;
    public String category;
    public String description;
    public boolean isIncome;
    public String bankName;
    public int orderIndex;

    public Transaction(String date, long amount, String category, String description,
                       boolean isIncome, String bankName, int orderIndex) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.isIncome = isIncome;
        this.bankName = bankName;
        this.orderIndex = orderIndex;
    }
}
