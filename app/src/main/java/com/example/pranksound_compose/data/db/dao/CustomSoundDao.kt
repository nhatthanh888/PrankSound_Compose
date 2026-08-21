package com.example.pranksound.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pranksound.data.dto.prank.CustomSound
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomSoundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customSound: CustomSound)

    @Query("SELECT * FROM my_custom_sound")
    fun getAll(): Flow<List<CustomSound>>

    @Query("SELECT MAX(id) FROM my_custom_sound")
    fun getMaxId(): Int?

}