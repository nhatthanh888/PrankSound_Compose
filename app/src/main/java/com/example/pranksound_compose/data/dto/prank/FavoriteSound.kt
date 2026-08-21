package com.example.pranksound.data.dto.prank

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_favorite_sound")
data class FavoriteSound(
    val id: Int = 0,

    @PrimaryKey
    val uniqueId: String,

    val name: String = "",
    val uri: String? = "",
    val link: String? = "",
    val thumb: String? = "",
    val isFavorite: Boolean = false,
    val isSelected: Boolean = false,
    val isCustom: Boolean = false,
    ){
    fun getCustomSound(
        favorite: FavoriteSound,
        customSoundList: List<CustomSound>
    ): CustomSound? {
        return customSoundList.find { it.uri == favorite.uri }
    }

    fun getSound(
        favorite: FavoriteSound,
        soundList: List<Sound>
    ): Sound? {
        return soundList.find { it.link == favorite.link }
    }

    fun getSoundFolder(
        sound: Sound,
        soundFolderList: List<SoundFolder>
    ): SoundFolder? {
        return soundFolderList.find { it.group == sound.group }
    }

}
