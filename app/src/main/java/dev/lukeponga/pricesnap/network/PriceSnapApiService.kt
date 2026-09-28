package dev.lukeponga.pricesnap.network

import dev.lukeponga.pricesnap.model.AnalysisResponse
import dev.lukeponga.pricesnap.model.ValuationRequest
import dev.lukeponga.pricesnap.model.ValuationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PriceSnapApiService {

    @GET("api/ping")
    suspend fun ping(): Response<PingResponse>

    @POST("api/valuate")
    suspend fun valuate(@Body request: ValuationRequest): Response<ValuationResponse>

    @POST("api/analyze")
    suspend fun analyze(@Body request: ValuationRequest): Response<AnalysisResponse>
}

data class PingResponse(val ok: Boolean)
