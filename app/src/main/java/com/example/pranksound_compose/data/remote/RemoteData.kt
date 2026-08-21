package com.example.pranksound.data.remote

import android.util.Log
import com.example.pranksound.data.Resource
import com.example.pranksound.data.dto.frames.DataFrames
import com.example.pranksound.data.dto.prank.PrankCall
import com.example.pranksound.data.dto.prank.Sound
import com.example.pranksound.data.dto.prank.SoundFolder
import com.example.pranksound.data.dto.recipes.Recipes
import com.example.pranksound.data.dto.recipes.RecipesItem
import com.example.pranksound.data.dto.response.*
import com.example.pranksound.data.error.NETWORK_ERROR
import com.example.pranksound.data.error.NO_INTERNET_CONNECTION
import com.example.pranksound.data.remote.service.*
import com.example.pranksound.utils.NetworkConnectivity
import com.facebook.appevents.FlushResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject


/**
 * Created by TruyenIT
 */

class RemoteData @Inject constructor(
    private val serviceGenerator: ServiceGenerator,
    private val networkConnectivity: NetworkConnectivity
) : RemoteDataSource {

    // MinhTN
    override suspend fun getSoundFolders(): Resource<List<SoundFolder>> {
        val soundFolderService = serviceGenerator.createService(SoundFolderService::class.java)
        return when (val result = processCall(soundFolderService::getSoundFolder)) {
            is SoundFolderResponse  -> {
                Resource.Success(result.data)
            }
            is Int -> {
                Resource.DataError(errorCode = result)
            }
            else -> {
                Resource.DataError(errorCode = -1)
            }
        }
    }

    override suspend fun getSounds(): Flow<List<Sound>> = flow {
        val trendingSoundService = serviceGenerator.createService(SoundService::class.java)
        when (val result = processCall(trendingSoundService::getSound)) {
            is SoundResponse -> {
                emit(result.data)
            }
            else -> {
                emit(emptyList())
            }
        }
    }

    override suspend fun getPrankCalls(): Flow<List<PrankCall>> {
        val callContactService = serviceGenerator.createService(CallContactService::class.java)
        when(val result = processCall(callContactService::getPrankCall)){
            is PrankCallResponse -> {
                return flow {
                    emit(result.data)
                }
            }
            else -> {
                return flow {
                    emit(emptyList())
                }
            }
        }

    }


    override suspend fun requestRecipes(): Resource<Recipes> {
        val recipesService = serviceGenerator.createService(RecipesService::class.java)
        return when (val response = processCall(recipesService::fetchRecipes)) {
            is List<*> -> {
                Resource.Success(data = Recipes(response as ArrayList<RecipesItem>))
            }
            else -> {
                Resource.DataError(errorCode = response as Int)
            }
        }
    }

    override suspend fun requestFrames(): Resource<DataFrames> {
        val framesService = serviceGenerator.createService(FramesService::class.java)
        return when (val response = processCall(framesService::fetchFrames)) {
            is DataFrames -> {
                Resource.Success(data = response as DataFrames)
            }
            else -> {
                Resource.DataError(errorCode = response as Int)
            }
        }
    }

    override suspend fun requestSoundCategory(filter: String): Resource<ResponseCategorySound> {
        val categorySoundService = serviceGenerator.createService(SoundCategoryService::class.java)
        return when (val response = processCall { categorySoundService.fetchCategorySoynd(filter, 0, 100) }) {
            is ResponseCategorySound -> {
                Resource.Success(data = response as ResponseCategorySound)
            }
            else -> {
                Resource.DataError(errorCode = response as Int)
            }
        }
    }


    override suspend fun requestVideo(filter: String): Resource<ResponseVideo> {
        val videoService = serviceGenerator.createService(VideoService::class.java)
        val response = processCall { videoService.fetchVideo(filter, 0, 100) }
        return when (response) {
            is ResponseVideo -> {
                Resource.Success(data = response as ResponseVideo)
            }
            else -> {
                Resource.DataError(errorCode = response as Int)
            }
        }
    }

//    override suspend fun requestCall(filter: String): Resource<ResponsePrankCall> {
//        val callContactService = serviceGenerator.createService(CallContactService::class.java)
//        return Resource<ResponsePrankCall()>
//    }

    override suspend fun requestCategoryGif(filter: String): Resource<ResponsePrankRecordFolder> {
        val categoryGifService = serviceGenerator.createService(PrankFolderGifService::class.java)
        return when (val response = processCall { categoryGifService.fetchCategoryGif(filter, 0, 100) }) {
            is ResponsePrankRecordFolder -> {
                Resource.Success(data = response as ResponsePrankRecordFolder)
            }
            else -> {
                Resource.DataError(errorCode = response as Int)
            }
        }
    }

    override suspend fun requestCategoryVideo(filter: String): Resource<ResponsePrankRecordFolder> {
        val categoryVideoService = serviceGenerator.createService(PrankFolderVideoService::class.java)
        return when (val response = processCall { categoryVideoService.fetchCategoryVideo(filter, 0, 100) }) {
            is ResponsePrankRecordFolder -> {
                Resource.Success(data = response as ResponsePrankRecordFolder)
            }
            else -> {
                Resource.DataError(errorCode = response as Int)
            }
        }
    }

    override suspend fun requestItemGif(filter: String): Resource<ResponsePrankRecordItem> {
        val itemGifService = serviceGenerator.createService(PrankItemGifService::class.java)
        return when (val response = processCall { itemGifService.fetchItemGif(filter, 0, 1000, "name") }) {
            is ResponsePrankRecordItem -> {
                Resource.Success(data = response as ResponsePrankRecordItem)
            }
            else -> {
                Resource.DataError(errorCode = response as Int)
            }
        }
    }

    override suspend fun requestItemVideo(filter: String): Resource<ResponsePrankRecordItem> {
        val itemVideoService = serviceGenerator.createService(PrankItemVideoService::class.java)
        return when (val response = processCall {
            itemVideoService.fetchItemVideo(filter, 0, 1000, "name") }) {
            is ResponsePrankRecordItem -> {
                Resource.Success(data = response as ResponsePrankRecordItem)
            }
            else -> {
                Resource.DataError(errorCode = response as Int)
            }
        }
    }





    private suspend fun <T> processCall(responseCall: suspend () -> Response<T>): Any {
        if (!networkConnectivity.isConnected()) return NO_INTERNET_CONNECTION

        return try {
            val response = responseCall.invoke()
            if (response.isSuccessful) {
                response.body() ?: FlushResult.UNKNOWN_ERROR
            } else {
                response.code()
            }
        } catch (e: IOException) {
            NETWORK_ERROR
        } catch (e: Exception) {
            FlushResult.UNKNOWN_ERROR
        }
    }

}
