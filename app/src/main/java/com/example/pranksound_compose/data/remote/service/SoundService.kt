package com.example.pranksound.data.remote.service

import com.example.pranksound.data.dto.response.SoundResponse
import retrofit2.Response
import retrofit2.http.GET

/**
 * Created by TruyenDev
 */

interface SoundService {
    @GET("pranksound.json")
    suspend fun getSound(): Response<SoundResponse>
}
