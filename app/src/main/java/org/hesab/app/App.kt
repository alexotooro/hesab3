package org.hesab.app

import android.app.Application

/**
 * App.kt — نقطه‌ی شروع برنامه "حساب"
 * در این کلاس تم، حالت شب و فونت سفارشی اعمال می‌شود.
 *
 * نکته: حتماً در AndroidManifest.xml به عنوان android:name=".App" تعریف شده باشد.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // ✅ اعمال تم (رنگ‌ها و حالت شب)
        ThemeHelper.applyTheme(this)

        // ✅ اعمال فونت انتخاب‌شده توسط کاربر
        FontHelper.applyFont(this)
    }
}
