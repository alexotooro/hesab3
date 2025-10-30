package org.hesab.app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "transactions_db"
                )
                    .fallbackToDestructiveMigration()
                    // عملیات دیتابیس در پس‌زمینه اجرا می‌شود تا UI کند نشود
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
