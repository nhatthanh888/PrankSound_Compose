package com.example.pranksound.data.remote.service

import com.example.pranksound.data.Resource
import com.example.pranksound.data.dto.prank.SoundFolder
import com.example.pranksound.data.dto.response.SoundFolderResponse
import retrofit2.Response
import retrofit2.http.GET

interface SoundFolderService {
    @GET("pranksound-type.json")
    suspend fun getSoundFolder(): Response<SoundFolderResponse>
}