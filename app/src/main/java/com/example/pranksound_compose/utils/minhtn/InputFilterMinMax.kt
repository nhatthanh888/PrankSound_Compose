package com.example.pranksound.utils.minhtn

import android.text.InputFilter
import android.text.Spanned

class InputFilterMinMax(private val min: Int, private val max: Int) : InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val newVal = (dest.substring(0, dstart) + source + dest.substring(dend))
            val input = newVal.toInt()
            if (input in min..max) return null
        } catch (_: NumberFormatException) {}
        return ""
    }
}
