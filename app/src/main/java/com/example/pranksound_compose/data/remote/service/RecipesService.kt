package com.example.pranksound.data.remote.service

import com.example.pranksound.data.dto.recipes.RecipesItem
import retrofit2.Response
import retrofit2.http.GET

/**
 * Created by TruyenIT
 */

interface RecipesService {
    @GET("recipes.json")
    suspend fun fetchRecipes(): Response<List<RecipesItem>>
}
