package com.example.pranksound.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pranksound.data.db.dao.CustomSoundDao
import com.example.pranksound.data.db.dao.FavoriteSoundDao
import com.example.pranksound.data.dto.prank.CustomSound
import com.example.pranksound.data.dto.prank.FavoriteSound
import com.example.pranksound.data.dto.prank.Sound

@Database(
    entities = [FavoriteSound::class, CustomSound::class],
    version = 1,
    exportSchema = false
)

abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun favoriteSound(): FavoriteSoundDao

    abstract fun customSound(): CustomSoundDao

}