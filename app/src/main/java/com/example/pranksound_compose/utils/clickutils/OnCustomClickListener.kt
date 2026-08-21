package com.example.pranksound.utils.clickutils

import android.view.MotionEvent
import android.view.View

interface OnCustomClickListener {
    fun onCustomClick(v: View?, event: MotionEvent?)
}