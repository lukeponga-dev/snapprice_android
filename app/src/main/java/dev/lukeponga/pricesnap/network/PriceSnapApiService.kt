package dev.lukeponga.pricesnap.network

import dev.lukeponga.pricesnap.model.AppraisalResponse
import dev.lukeponga.pricesnap.model.ImageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PriceSnapApiService {

    @GET("api/ping")
    suspend fun ping(): Response<PingResponse>

    @POST("api/valuate")
    suspend fun valuate(@Body request: ImageRequest): Response<AppraisalResponse>
}

data class PingResponse(val ok: Boolean)
