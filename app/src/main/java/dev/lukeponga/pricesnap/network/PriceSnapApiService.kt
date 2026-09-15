package dev.lukeponga.pricesnap.network

import dev.lukeponga.pricesnap.model.AppraisalResponse
import dev.lukeponga.pricesnap.model.ImageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PriceSnapApiService {
    @POST("api/analyze")
    suspend fun analyzeItem(@Body request: ImageRequest): Response<AppraisalResponse>
}
