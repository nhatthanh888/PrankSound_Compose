package com.example.pranksound.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pranksound.data.dto.prank.FavoriteSound
import com.example.pranksound.data.dto.prank.Sound
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteSoundDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fav: FavoriteSound)

    @Query("SELECT * FROM my_favorite_sound")
    fun getAll(): Flow<List<FavoriteSound>>

    @Query("DELETE FROM my_favorite_sound WHERE uniqueId = :uniqueId")
    suspend fun delete(uniqueId: String)
}