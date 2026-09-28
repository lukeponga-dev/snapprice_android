package dev.lukeponga.pricesnap.network

import dev.lukeponga.pricesnap.model.ValuationRequest
import dev.lukeponga.pricesnap.model.ValuationResponse
import dev.lukeponga.pricesnap.model.AnalysisResponse

class AppraisalRepository(
    private val apiService: PriceSnapApiService = NetworkClient.apiService
) {
    suspend fun ping(): Result<Boolean> {
        return try {
            val response = apiService.ping()
            if (response.isSuccessful && response.body()?.ok == true) {
                Result.success(true)
            } else {
                Result.failure(Exception("Backend returned HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun analyzeImage(base64Image: String): Result<ValuationResponse> {
        return try {
            val response = apiService.valuate(ValuationRequest(imageBase64 = base64Image))
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Backend returned an empty response"))
            } else {
                Result.failure(Exception("Valuation failed: HTTP ${response.code()}"))
            }
        } catch (e: java.io.IOException) {
            Result.failure(Exception("Unable to connect to PriceSnap", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun analyzeOnly(base64Image: String): Result<AnalysisResponse> {
        return try {
            val response = apiService.analyze(ValuationRequest(imageBase64 = base64Image))
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Backend returned an empty response"))
            } else {
                Result.failure(Exception("Analysis failed: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
