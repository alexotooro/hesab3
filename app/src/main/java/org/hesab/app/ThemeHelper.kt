package org.hesab.app

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

/**
 * ThemeHelper — کنترل تم و حالت شب برنامه "حساب"
 *
 * این کلاس وظیفه دارد تم انتخاب‌شده‌ی کاربر را از SharedPreferences خوانده و قبل از
 * setContentView در هر Activity اعمال کند.
 *
 * کلیدها:
 * - app_theme  (مقادیر: "light_blue", "soft_green", "dark", "light")
 * - theme_name (مقادیر فارسی نسخه‌های قدیمی: "آبی روشن", "سبز ملایم", "تیره کلاسیک", "روشن ساده")
 * - dark_mode  (Boolean) — اگر true باشد، حالت شب فعال می‌شود.
 *
 * توجه: باید در کلاس Application یا ابتدای هر Activity فراخوانی شود:
 *      ThemeHelper.applyTheme(this)
 */
object ThemeHelper {

    fun applyTheme(context: Context) {
        // SharedPreferences پیش‌فرض و قدیمی برای سازگاری
        val defaultPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val legacyPrefs = context.getSharedPreferences("org.hesab.app_preferences", Context.MODE_PRIVATE)

        // فعال‌سازی حالت شب در صورت نیاز
        val darkMode = defaultPrefs.getBoolean("dark_mode", legacyPrefs.getBoolean("dark_mode", false))
        AppCompatDelegate.setDefaultNightMode(
            if (darkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        // خواندن کلید تم (نسخه جدید یا قدیمی)
        val themeKey = defaultPrefs.getString("app_theme",
            legacyPrefs.getString("theme_name", "light_blue")
        ) ?: "light_blue"

        // انتخاب تم براساس کلید
        val themeRes = when (themeKey) {
            "آبی روشن", "light_blue", "blue", "آبی" -> R.style.Theme_Hesab_LightBlue
            "سبز ملایم", "soft_green", "green", "سبز" -> R.style.Theme_Hesab_SoftGreen
            "تیره", "dark", "dark_mode", "تیره کلاسیک" -> R.style.Theme_Hesab_Dark
            "روشن", "light", "light_simple", "روشن ساده" -> R.style.Theme_Hesab_Light
            else -> R.style.Theme_Hesab_LightBlue
        }

        // اعمال تم
        context.setTheme(themeRes)
    }
}
