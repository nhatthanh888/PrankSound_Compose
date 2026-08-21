package com.example.pranksound.data.remote.service

import com.example.pranksound.data.dto.response.PrankCallResponse
import com.example.pranksound.data.dto.response.ResponseCategorySound
import com.example.pranksound.data.dto.response.ResponsePrankCall
import com.example.pranksound.data.dto.response.ResponseSound
import com.example.pranksound.data.dto.response.ResponseVideo
import com.example.pranksound.data.dto.response.SoundResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by TruyenDev
 */

interface CallContactService {
    @GET("prankcall.json")
    suspend fun getPrankCall(): Response<PrankCallResponse>
}
