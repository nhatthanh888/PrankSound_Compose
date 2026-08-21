package com.example.pranksound.data.dto.prank

import androidx.room.Entity

data class Sound(
    val id: Int,
    val name: String,
    val group: String,
    val link: String,
    val thumb: String,
    var isSelected: Boolean = false,
    var isFavorite: Boolean = false

){

    val groupName: String
        get() = name.replace(Regex("\\s+\\d+$"), "")
}