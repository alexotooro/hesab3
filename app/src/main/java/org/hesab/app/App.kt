package org.hesab.app

import android.app.Application
import androidx.room.Room

class App : Application() {
    companion object {
        lateinit var db: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "hesab_db"
        ).fallbackToDestructiveMigration()
         .build()
    }
}
