package org.hesab.app

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.preference.PreferenceManager
import java.lang.reflect.Field

object FontHelper {
    private const val TAG = "FontHelper"
    private var currentTypeface: Typeface? = null

    /**
     * اعمال فونت انتخاب‌شده برای کل برنامه.
     * (در App.onCreate و هر زمان که تنظیمات تغییر کند.)
     */
    fun applyFont(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val legacyPrefs = context.getSharedPreferences("org.hesab.app_preferences", Context.MODE_PRIVATE)

        val fontKey = prefs.getString("app_font",
            legacyPrefs.getString("font_name", "vazir")
        ) ?: "vazir"

        val assetName = when (fontKey) {
            "سیستم", "system" -> null
            "ایران‌سنس", "iransans" -> "fonts/iransans.ttf"
            "وزیر", "vazir" -> "fonts/vazir.ttf"
            "دیانا", "diana" -> "fonts/diana.ttf"
            else -> "fonts/vazir.ttf"
        }

        if (assetName == null) return

        try {
            currentTypeface = Typeface.createFromAsset(context.assets, assetName)
            replaceDefaultFont(currentTypeface!!)
        } catch (e: Exception) {
            Log.e(TAG, "applyFont: failed to load font $assetName", e)
        }
    }

    private fun replaceDefaultFont(newTypeface: Typeface) {
        try {
            val staticFields = arrayOf("DEFAULT", "DEFAULT_BOLD", "SANS_SERIF", "SERIF", "MONOSPACE")
            for (fieldName in staticFields) {
                try {
                    val field: Field = Typeface::class.java.getDeclaredField(fieldName)
                    field.isAccessible = true
                    field.set(null, newTypeface)
                } catch (t: Throwable) {
                    Log.w(TAG, "replaceDefaultFont: couldn't replace $fieldName", t)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "replaceDefaultFont: error", e)
        }
    }

    /** 🔹 بازخوانی فونت روی ویوهای موجود بدون ری‌استارت Activity */
    fun refreshFont(root: View) {
        currentTypeface?.let {
            applyToAll(root)
        }
    }

    fun applyToAll(root: View) {
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                val child = root.getChildAt(i)
                if (child is ViewGroup) applyToAll(child)
                else if (child is TextView) child.typeface = currentTypeface
            }
        } else if (root is TextView) {
            root.typeface = currentTypeface
        }
    }
}
