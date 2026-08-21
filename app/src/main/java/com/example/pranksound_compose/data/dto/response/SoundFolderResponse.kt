package com.example.pranksound.data.dto.response

import com.example.pranksound.data.dto.prank.SoundFolder

data class SoundFolderResponse(
    val code: Int,
    val `data`: List<SoundFolder>
) {
}