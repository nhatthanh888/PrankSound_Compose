package com.example.pranksound.data.dto.prank

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_custom_sound")
data class CustomSound(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uri: String="",
    val title: String="",
    var isSelected: Boolean = false,
    var isFavorite: Boolean = false
)
