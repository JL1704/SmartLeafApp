package com.deltasquad.smartleafapp.data.api

import com.deltasquad.smartleafapp.data.model.FlowerResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FlowerApiService {

    // 🌸 Endpoint principal del servidor FastAPI
    @Multipart
    @POST("predict")
    suspend fun classifyFlower(
        @Part image: MultipartBody.Part
    ): Response<FlowerResponse>
}
