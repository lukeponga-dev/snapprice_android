package dev.lukeponga.pricesnap.network

import com.google.gson.Gson
import dev.lukeponga.pricesnap.model.ApiError
import dev.lukeponga.pricesnap.model.ValuationRequest
import dev.lukeponga.pricesnap.model.ValuationResponse
import kotlinx.coroutines.CancellationException
import retrofit2.Response

class AppraisalRepository(
    private val apiService: PriceSnapApiService = NetworkClient.apiService
) {
    /** Configuration readiness only; a configured key does not prove Gemini quota. */
    suspend fun checkConnection(): Result<Boolean> = try {
        val response = apiService.connection()
        val body = response.body()
        if (response.isSuccessful && body?.ok == true &&
            body.engine?.hasApiKey == true && body.engine.status == "configured") {
            Result.success(true)
        } else {
            Result.failure(BackendException("The valuation engine is not configured. Please try again later."))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(BackendException("Unable to connect to PriceSnap. Check your connection.", e))
    }

    suspend fun analyzeImage(base64Image: String): Result<ValuationResponse> = try {
        val mime = if (base64Image.startsWith("data:"))
            base64Image.substringAfter("data:").substringBefore(";base64,") else "image/jpeg"
        val raw = if (base64Image.startsWith("data:")) base64Image.substringAfter(",") else base64Image
        if (mime !in setOf("image/jpeg", "image/png", "image/webp")) {
            throw BackendException("Please choose a JPEG, PNG or WebP image.")
        }
        if (raw.length > 4_000_000) throw BackendException("That photo is too large. Please choose a smaller image.")
        val response = apiService.valuate(ValuationRequest(raw, mime))
        if (response.isSuccessful) {
            val body = response.body() ?: throw BackendException("PriceSnap returned an empty response.")
            if (!body.ok || body.status !in setOf("success", "insufficient_evidence")) {
                throw BackendException("PriceSnap returned an unexpected result. Please try again.")
            }
            Result.success(body)
        } else {
            Result.failure(BackendException(errorMessage(response)))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: java.net.SocketTimeoutException) {
        Result.failure(BackendException("The valuation timed out. Please try again.", e))
    } catch (e: java.io.IOException) {
        Result.failure(BackendException("Unable to connect to PriceSnap. Check your connection.", e))
    } catch (e: Exception) {
        Result.failure(if (e is BackendException) e else BackendException("Unable to read the valuation result. Please try again.", e))
    }

    private fun errorMessage(response: Response<*>): String {
        val code = runCatching {
            Gson().fromJson(response.errorBody()?.string(), ApiError::class.java)?.code
        }.getOrNull()
        return when (code) {
            "INVALID_REQUEST", "INVALID_IMAGE", "INVALID_MIME_TYPE" -> "We couldn't read that image. Please choose another photo."
            "IMAGE_TOO_LARGE" -> "That photo is too large. Please choose a smaller image."
            "IDENTIFICATION_UNCERTAIN" -> "We couldn't identify the item. Try a clearer photo."
            "PROVIDER_RATE_LIMIT" -> "PriceSnap is busy. Please try again shortly."
            "SERVICE_NOT_CONFIGURED" -> "The valuation engine is not configured. Please try again later."
            "ANALYSIS_TIMEOUT" -> "The valuation timed out. Please try again."
            else -> "The valuation couldn't be completed (HTTP ${response.code()}). Please try again."
        }
    }
}

class BackendException(message: String, cause: Throwable? = null) : Exception(message, cause)
