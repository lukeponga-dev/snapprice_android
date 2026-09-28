package dev.lukeponga.pricesnap.network

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

    @GET("api/connection")
    suspend fun connection(): Response<ConnectionResponse>
}

data class PingResponse(val ok: Boolean)

data class ConnectionResponse(
    val ok: Boolean,
    val engine: EngineStatus?
)
data class EngineStatus(
    val status: String?,
    val hasApiKey: Boolean,
    val model: String?,
    val engineVersion: String?,
    val providerChecked: Boolean,
    val geminiLatencyMs: Long?
)
