package com.example.pranksound.data.remote.service

import com.example.pranksound.data.dto.response.ResponsePrankRecordFolder
import com.example.pranksound.data.dto.response.ResponseVideo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by TruyenDev
 */

interface PrankFolderVideoService {
    @GET("prankrecordvideocate/search")
    suspend fun fetchCategoryVideo(@Query("filter") filter: String, @Query("pageIndex") pageIndex: Int, @Query("pageSize") pageSize: Int): Response<ResponsePrankRecordFolder>
}
