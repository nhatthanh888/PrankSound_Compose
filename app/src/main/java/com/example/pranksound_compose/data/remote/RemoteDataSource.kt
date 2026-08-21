package com.example.pranksound.data.remote

import com.example.pranksound.data.Resource
import com.example.pranksound.data.dto.frames.DataFrames
import com.example.pranksound.data.dto.prank.PrankCall
import com.example.pranksound.data.dto.prank.Sound
import com.example.pranksound.data.dto.prank.SoundFolder
import com.example.pranksound.data.dto.recipes.Recipes
import com.example.pranksound.data.dto.response.*
import kotlinx.coroutines.flow.Flow

/**
 * Created by TruyenIT
 */

internal interface RemoteDataSource {
    suspend fun requestRecipes(): Resource<Recipes>
    suspend fun requestFrames(): Resource<DataFrames>
    suspend fun requestSoundCategory(filter: String): Resource<ResponseCategorySound>
    suspend fun requestVideo(filter: String): Resource<ResponseVideo>
//    suspend fun requestCall(filter: String): Resource<ResponsePrankCall>
    suspend fun requestCategoryGif(filter: String): Resource<ResponsePrankRecordFolder>
    suspend fun requestCategoryVideo(filter: String): Resource<ResponsePrankRecordFolder>
    suspend fun requestItemGif(filter: String): Resource<ResponsePrankRecordItem>
    suspend fun requestItemVideo(filter: String): Resource<ResponsePrankRecordItem>

    // MinhTN
    suspend fun getSoundFolders(): Resource<List<SoundFolder>>
    suspend fun getSounds(): Flow<List<Sound>>

    suspend fun getPrankCalls(): Flow<List<PrankCall>>

}
