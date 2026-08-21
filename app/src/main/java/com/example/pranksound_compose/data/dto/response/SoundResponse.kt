package com.example.pranksound.data.dto.response

import com.example.pranksound.data.dto.prank.Sound

data class SoundResponse(
    val code: Int,
    val `data`: List<Sound>
)