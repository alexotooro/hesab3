package org.hesab.app

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat

object ThemeHelper {

    fun applyTheme(context: Context) {
        val prefs = context.getSharedPreferences("org.hesab.app_preferences", Context.MODE_PRIVATE)
        val darkMode = prefs.getBoolean("dark_mode", false)
        val themeName = prefs.getString("theme_name", "آبی روشن")

        // حالت تیره/روشن
        AppCompatDelegate.setDefaultNightMode(
            if (darkMode)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )

        // انتخاب رنگ اصلی بر اساس تم
        when (themeName) {
            "آبی روشن" -> context.setTheme(R.style.Theme_Hesab_LightBlue)
            "سبز ملایم" -> context.setTheme(R.style.Theme_Hesab_SoftGreen)
            "تیره کلاسیک" -> context.setTheme(R.style.Theme_Hesab_Dark)
            "روشن ساده" -> context.setTheme(R.style.Theme_Hesab_Light)
            else -> context.setTheme(R.style.Theme_Hesab_LightBlue)
        }
    }

    fun getThemeColor(context: Context, colorResId: Int): Int {
        return ContextCompat.getColor(context, colorResId)
    }
}
