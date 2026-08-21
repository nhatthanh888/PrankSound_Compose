package com.example.pranksound.data

import com.example.pranksound.data.dto.frames.DataFrames
import com.example.pranksound.data.dto.localprank.MyFolderAudio
import com.example.pranksound.data.dto.localprank.MyFolderImage
import com.example.pranksound.data.dto.localprank.MyFolderVideo
import com.example.pranksound.data.dto.localprank.MyVideo
import com.example.pranksound.data.dto.login.LoginRequest
import com.example.pranksound.data.dto.login.LoginResponse
import com.example.pranksound.data.dto.prank.CustomSound
import com.example.pranksound.data.dto.prank.FavoriteSound
import com.example.pranksound.data.dto.prank.PrankCall
import com.example.pranksound.data.dto.prank.Sound
import com.example.pranksound.data.dto.prank.SoundFolder
import com.example.pranksound.data.dto.recipes.Recipes
import com.example.pranksound.data.dto.response.*
import kotlinx.coroutines.flow.Flow

/**
 * Created by TruyenIT
 */

interface DataRepositorySource {
    suspend fun requestRecipes(): Flow<Resource<Recipes>>
    suspend fun doLogin(loginRequest: LoginRequest): Flow<Resource<LoginResponse>>
    suspend fun addToFavourite(id: String): Flow<Resource<Boolean>>
    suspend fun removeFromFavourite(id: String): Flow<Resource<Boolean>>
    suspend fun isFavourite(id: String): Flow<Resource<Boolean>>
    suspend fun requestFrames(): Flow<Resource<DataFrames>>
    suspend fun requestCategorySound(filter: String): Flow<Resource<ResponseCategorySound>>
    suspend fun requestVideo(filter: String): Flow<Resource<ResponseVideo>>
//    suspend fun requestCall(filter: String): Flow<Resource<ResponsePrankCall>>
    suspend fun getAllImage(): Flow<Resource<List<MyFolderImage>>>
    suspend fun getAllVideoFromFolder(path: String): Flow<Resource<List<MyVideo>>>
    suspend fun getAllAudio(): Flow<Resource<List<MyFolderAudio>>>
    suspend fun getAllVideo(): Flow<Resource<List<MyFolderVideo>>>
    suspend fun requestCategoryGif(filter: String): Flow<Resource<ResponsePrankRecordFolder>>
    suspend fun requestCategoryVideo(filter: String): Flow<Resource<ResponsePrankRecordFolder>>
    suspend fun requestItemGif(filter: String): Flow<Resource<ResponsePrankRecordItem>>
    suspend fun requestItemVideo(filter: String): Flow<Resource<ResponsePrankRecordItem>>


    // MinhTN
    suspend fun getSoundFolders(): Resource<List<SoundFolder>>

    suspend fun getSounds(): Flow<List<Sound>>

    suspend fun getPrankCall(): Flow<List<PrankCall>>


    // Local
    suspend fun insertFavoriteSound(sound: FavoriteSound)

    suspend fun deleteFavoriteSound(uniqueId: String)

    suspend fun getAllFavoriteSound(): Flow<List<FavoriteSound>>

    suspend fun updateSound(sound: Sound)

    suspend fun insertCustomSound(customSound: CustomSound)

    suspend fun getAllCustomSound(): Flow<List<CustomSound>>

    fun getMaxId(): Int



}
