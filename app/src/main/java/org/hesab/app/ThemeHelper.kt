package org.hesab.app

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

object ThemeHelper {

    /**
     * ✅ اعمال تم و حالت شب بر اساس تنظیمات کاربر
     * این تابع باید قبل از setContentView در هر Activity فراخوانی شود.
     */
    fun applyTheme(context: Context) {
        val defaultPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val legacyPrefs = context.getSharedPreferences("org.hesab.app_preferences", Context.MODE_PRIVATE)

        // --- حالت شب ---
        val darkMode = defaultPrefs.getBoolean("dark_mode", legacyPrefs.getBoolean("dark_mode", false))
        AppCompatDelegate.setDefaultNightMode(
            if (darkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        // --- کلید تم ---
        val themeKey = defaultPrefs.getString(
            "app_theme",
            legacyPrefs.getString("theme_name", "light_blue")
        ) ?: "light_blue"

        val themeRes = when (themeKey) {
            "آبی روشن", "light_blue", "blue", "آبی" -> R.style.Theme_Hesab_LightBlue
            "سبز ملایم", "soft_green", "green", "سبز" -> R.style.Theme_Hesab_SoftGreen
            "تیره", "dark", "dark_mode", "تیره کلاسیک" -> R.style.Theme_Hesab_Dark
            "روشن", "light", "light_simple", "روشن ساده" -> R.style.Theme_Hesab_Light
            else -> R.style.Theme_Hesab_LightBlue
        }

        // --- اعمال تم انتخابی ---
        try {
            context.setTheme(themeRes)
        } catch (e: Exception) {
            // در صورت بروز خطا (مثلاً نبودن استایل در theme.xml)
            context.setTheme(R.style.Theme_Hesab_LightBlue)
        }
    }

    /**
     * ✅ بازخوانی تم در حال اجرا — برای اعمال زنده تغییرات.
     * (در SettingsActivity یا MainActivity فراخوانی شود)
     */
    fun refreshTheme(activity: Activity) {
        applyTheme(activity)
        activity.recreate() // بازسازی اکتیویتی با تم جدید
    }
}
