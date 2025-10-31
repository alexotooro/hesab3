package org.hesab.app

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

object ThemeHelper {

    /**
     * خواندن تنظیمات تم از SharedPreferences و اعمال تم/حالت شب.
     *
     * کلیدها (سازگاری):
     * - app_theme    (values: "light_blue", "soft_green", "dark", "light")
     * - theme_name   (legacy / alternative: "آبی روشن", "سبز ملایم", "تیره کلاسیک", "روشن ساده")
     * - dark_mode    (boolean) -- اگر true باشد حالت شب فعال می‌شود
     *
     * فراخوانی: قبل از setContentView در Activity
     */
    fun applyTheme(context: Context) {
        // تلاش برای خواندن از default prefs و در صورت نبودن از prefs اختصاصی
        val defaultPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val legacyPrefs = context.getSharedPreferences("org.hesab.app_preferences", Context.MODE_PRIVATE)

        // dark mode
        val darkMode = defaultPrefs.getBoolean("dark_mode", legacyPrefs.getBoolean("dark_mode", false))
        AppCompatDelegate.setDefaultNightMode(
            if (darkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        // theme key: اول app_theme (new), بعد theme_name (legacy فارسی) -> تبدیل به مقادیر استاندارد
        val themeKey = defaultPrefs.getString("app_theme",
            legacyPrefs.getString("theme_name", "light_blue")
        ) ?: "light_blue"

        val themeRes = when (themeKey) {
            // ممکنه کاربر فارسی مقدار گذاشته باشه (legacy)، لذا چند حالت را پشتیبانی می‌کنیم
            "آبی روشن", "light_blue", "blue", "آبی" -> R.style.Theme_Hesab_LightBlue
            "سبز ملایم", "soft_green", "green", "سبز" -> R.style.Theme_Hesab_SoftGreen
            "تیره", "dark", "dark_mode", "تیره کلاسیک" -> R.style.Theme_Hesab_Dark
            "روشن", "light", "light_simple", "روشن ساده" -> R.style.Theme_Hesab_Light
            else -> R.style.Theme_Hesab_LightBlue
        }

        // اعمال تم روی کانتکست
        context.setTheme(themeRes)
    }
}
