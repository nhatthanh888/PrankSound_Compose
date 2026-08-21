package com.example.plant.utils.minhtn

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

class ColoredStrikeSpan(
    private val strikeColor: Int,
    private val textColor: Int? = null
) : ReplacementSpan() {

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        return paint.measureText(text, start, end).toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val originalColor = paint.color
        val originalStyle = paint.style
        val originalStrokeWidth = paint.strokeWidth
        val originalTypeface = paint.typeface

        val textToDraw = text.subSequence(start, end).toString()

        // Vẽ text với màu custom (nếu có)
        textColor?.let { paint.color = it }
        canvas.drawText(textToDraw, x, y.toFloat(), paint)

        // Vẽ gạch ngang
        val width = paint.measureText(textToDraw)
        val centerY = (top + bottom) / 2f

        paint.color = strikeColor
        paint.strokeWidth = paint.textSize / 15f
        paint.style = Paint.Style.STROKE
        canvas.drawLine(x, centerY, x + width, centerY, paint)

        // Restore lại paint
        paint.color = originalColor
        paint.strokeWidth = originalStrokeWidth
        paint.style = originalStyle
        paint.typeface = originalTypeface
    }
}

