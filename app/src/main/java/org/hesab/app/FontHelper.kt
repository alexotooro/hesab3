package org.hesab.app

import android.content.Context
import android.graphics.Typeface
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.lang.reflect.Field

object FontHelper {
    private const val TAG = "FontHelper"
    private var currentTypeface: Typeface? = null

    fun applyFont(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val legacyPrefs = context.getSharedPreferences("org.hesab.app_preferences", Context.MODE_PRIVATE)

        val fontKey = prefs.getString("app_font",
            legacyPrefs.getString("font_name", "vazir")
        ) ?: "vazir"

        val assetName = when (fontKey) {
            "system", "سیستم" -> null
            "iransans", "ایران‌سنس" -> "fonts/iransans.ttf"
            "vazir", "وزیر" -> "fonts/vazir.ttf"
            "diana", "دیانا" -> "fonts/diana.ttf"
            else -> "fonts/vazir.ttf"
        }

        try {
            val tf = assetName?.let { Typeface.createFromAsset(context.assets, it) }

            currentTypeface = tf ?: when (fontKey) {
                "iransans" -> Typeface.create(context, R.font.iransans)
                "vazir" -> Typeface.create(context, R.font.vazir)
                "diana" -> Typeface.create(context, R.font.diana)
                else -> Typeface.create(context, R.font.vazir)
            }

            currentTypeface?.let { replaceDefaultFont(it) }

        } catch (e: Exception) {
            Log.e(TAG, "applyFont: failed to load font", e)
        }
    }

    private fun replaceDefaultFont(newTypeface: Typeface) {
        try {
            val fields = arrayOf("DEFAULT", "DEFAULT_BOLD", "SANS_SERIF", "SERIF", "MONOSPACE")
            for (fieldName in fields) {
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

    fun refreshFont(root: View) {
        currentTypeface?.let { applyToAll(root) }
    }

    fun applyToAll(root: View) {
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                applyToAll(root.getChildAt(i))
            }
        } else if (root is TextView) {
            root.typeface = currentTypeface
        }
    }
}
