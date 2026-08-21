package com.example.pranksound.data

import android.util.Log
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
import com.example.pranksound.data.local.LocalData
import com.example.pranksound.data.remote.RemoteData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


/**
 * Created by TruyenIT
 */

class DataRepository @Inject constructor(
    private val remoteRepository: RemoteData,
    private val localRepository: LocalData,
    private val ioDispatcher: CoroutineContext) : DataRepositorySource {


    override suspend fun requestRecipes(): Flow<Resource<Recipes>> {
        return flow {
            emit(remoteRepository.requestRecipes())
        }.flowOn(ioDispatcher)
    }

    override suspend fun doLogin(loginRequest: LoginRequest): Flow<Resource<LoginResponse>> {
        return flow {
            emit(localRepository.doLogin(loginRequest))
        }.flowOn(ioDispatcher)
    }

    override suspend fun addToFavourite(id: String): Flow<Resource<Boolean>> {
        return flow {
            localRepository.getCachedFavourites().let {
                it.data?.toMutableSet()?.let { set ->
                    val isAdded = set.add(id)
                    if (isAdded) {
                        emit(localRepository.cacheFavourites(set))
                    } else {
                        emit(Resource.Success(false))
                    }
                }
                it.errorCode?.let { errorCode ->
                    emit(Resource.DataError<Boolean>(errorCode))
                }
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun removeFromFavourite(id: String): Flow<Resource<Boolean>> {
        return flow {
            emit(localRepository.removeFromFavourites(id))
            emit(localRepository.removeFromFavourites(id))
        }.flowOn(ioDispatcher)
    }

    override suspend fun isFavourite(id: String): Flow<Resource<Boolean>> {
        return flow {
            emit(localRepository.isFavourite(id))
        }.flowOn(ioDispatcher)
    }

    override suspend fun requestFrames(): Flow<Resource<DataFrames>> {
        return flow {
            emit(remoteRepository.requestFrames())
        }.flowOn(ioDispatcher)
    }

    override suspend fun requestCategorySound(filter: String): Flow<Resource<ResponseCategorySound>> {
        return flow {
            emit(remoteRepository.requestSoundCategory(filter))
        }.flowOn(ioDispatcher)
    }

    override suspend fun requestVideo(filter: String): Flow<Resource<ResponseVideo>> {
        Log.d("MinhTN912 - LOGIC", "requestVideo: " + filter)
        return flow {
            emit(remoteRepository.requestVideo(filter))
        }.flowOn(ioDispatcher)
    }

//    override suspend fun requestCall(filter: String): Flow<Resource<ResponsePrankCall>> {
//        return flow {
//            emit(remoteRepository.requestCall(filter))
//        }.flowOn(ioDispatcher)
//    }

    override suspend fun getAllImage(): Flow<Resource<List<MyFolderImage>>> {
        return flow {
            emit(localRepository.getAllImage())
        }.flowOn(ioDispatcher)
    }

    override suspend fun getAllAudio(): Flow<Resource<List<MyFolderAudio>>> {
        return flow {
            emit(localRepository.getAllAudio())
        }.flowOn(ioDispatcher)
    }

    override suspend fun getAllVideo(): Flow<Resource<List<MyFolderVideo>>> {
        return flow {
            emit(localRepository.getAllVideo())
        }.flowOn(ioDispatcher)
    }

    override suspend fun getAllVideoFromFolder(path: String): Flow<Resource<List<MyVideo>>> {
        return flow {
            emit(localRepository.getAllVideoFromFolder(path))
        }.flowOn(ioDispatcher)
    }

    override suspend fun requestCategoryGif(filter: String): Flow<Resource<ResponsePrankRecordFolder>> {
        return flow {
            emit(remoteRepository.requestCategoryGif(filter))
        }.flowOn(ioDispatcher)
    }

    override suspend fun requestCategoryVideo(filter: String): Flow<Resource<ResponsePrankRecordFolder>> {
        return flow {
            emit(remoteRepository.requestCategoryVideo(filter))
        }.flowOn(ioDispatcher)
    }

    override suspend fun requestItemGif(filter: String): Flow<Resource<ResponsePrankRecordItem>> {
        return flow {
            emit(remoteRepository.requestItemGif(filter))
        }.flowOn(ioDispatcher)
    }

    override suspend fun requestItemVideo(filter: String): Flow<Resource<ResponsePrankRecordItem>> {
        return flow {
            emit(remoteRepository.requestItemVideo(filter))
        }.flowOn(ioDispatcher)
    }


    // MinhTNAPI
    override suspend fun getSoundFolders(): Resource<List<SoundFolder>> {
        return remoteRepository.getSoundFolders()
    }

    override suspend fun getSounds(): Flow<List<Sound>> {
        return remoteRepository.getSounds()

    }

    override suspend fun getPrankCall(): Flow<List<PrankCall>> {
        return remoteRepository.getPrankCalls()
    }

    override suspend fun insertFavoriteSound(sound: FavoriteSound) {
        return localRepository.insertFavoriteSound(sound)
    }

    override suspend fun deleteFavoriteSound(uniqueId: String) {
        return localRepository.deleteFavoriteSound(uniqueId)
    }

    override suspend fun getAllFavoriteSound(): Flow<List<FavoriteSound>> {
        return localRepository.getAllFavoriteSound()
    }

    override suspend fun updateSound(sound: Sound) {
        localRepository.update(sound)
    }

    override suspend fun insertCustomSound(customSound: CustomSound) {
        localRepository.insertCustomSound(customSound)
    }

    override suspend fun getAllCustomSound(): Flow<List<CustomSound>>{
        return localRepository.getAllCustomSound()
    }

    override fun getMaxId(): Int {
        return localRepository.getMaxId()
    }




}
