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

    private fun getRawBase64AndMime(base64Image: String): Pair<String, String> {
        if (base64Image.startsWith("data:")) {
            val commaIndex = base64Image.indexOf(",")
            if (commaIndex != -1) {
                val prefix = base64Image.substring(0, commaIndex)
                val rawBase64 = base64Image.substring(commaIndex + 1)
                val mimeType = try {
                    prefix.substringAfter("data:").substringBefore(";base64")
                } catch (e: Exception) {
                    "image/jpeg"
                }
                return Pair(rawBase64, mimeType)
            }
        }
        return Pair(base64Image, "image/jpeg")
    }

    suspend fun analyzeImage(base64Image: String): Result<ValuationResponse> {
        val (rawBase64, mime) = getRawBase64AndMime(base64Image)
        return try {
            val response = apiService.valuate(ValuationRequest(imageBase64 = rawBase64, mimeType = mime))
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
        val (rawBase64, mime) = getRawBase64AndMime(base64Image)
        return try {
            val response = apiService.analyze(ValuationRequest(imageBase64 = rawBase64, mimeType = mime))
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
