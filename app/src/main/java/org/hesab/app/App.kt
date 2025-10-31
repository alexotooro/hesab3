package org.hesab.app

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // ✅ تمام تنظیمات تم و حالت شب از ThemeHelper خوانده می‌شود
        ThemeHelper.applyTheme(this)
    }
}
