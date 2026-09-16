package dev.lukeponga.pricesnap.network

import dev.lukeponga.pricesnap.model.AppraisalResponse
import dev.lukeponga.pricesnap.model.ImageRequest
import dev.lukeponga.pricesnap.model.PingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PriceSnapApiService {
    @GET("api/ping")
    suspend fun ping(): Response<PingResponse>

    @POST("api/analyze")
    suspend fun analyzeItem(@Body request: ImageRequest): Response<AppraisalResponse>
}
