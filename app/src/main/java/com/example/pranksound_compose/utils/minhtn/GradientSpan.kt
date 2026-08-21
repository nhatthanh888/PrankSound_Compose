package com.example.plant.utils.minhtn

import android.graphics.Shader
import android.text.style.CharacterStyle

class GradientSpan(private val shader: Shader): CharacterStyle() {
    override fun updateDrawState(tp: android.text.TextPaint) {
        tp.shader = shader
    }
}

