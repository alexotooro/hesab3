package org.hesab.app.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.lang.ref.WeakReference
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

class ThousandsTextWatcher(editText: EditText) : TextWatcher {
    private val ref = WeakReference(editText)
    private var current = ""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable?) {
        val edt = ref.get() ?: return
        if (s == null) return
        if (s.toString() == current) return
        edt.removeTextChangedListener(this)
        val clean = s.toString().replace(Regex("[^0-9]"), "")
        val nf = NumberFormat.getInstance(Locale.getDefault()) as DecimalFormat
        nf.applyPattern("#,###")
        val formatted = if (clean.isBlank()) "" else nf.format(clean.toLong())
        current = formatted
        edt.setText(formatted)
        edt.setSelection(formatted.length)
        edt.addTextChangedListener(this)
    }
}
