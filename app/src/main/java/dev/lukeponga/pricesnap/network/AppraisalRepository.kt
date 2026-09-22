package dev.lukeponga.pricesnap.network

import dev.lukeponga.pricesnap.model.ImageRequest

class AppraisalRepository(
    private val apiService: PriceSnapApiService = NetworkClient.apiService
) {
    suspend fun ping() = apiService.ping()

    suspend fun analyzeImage(base64Image: String) =
        apiService.valuate(ImageRequest(imageBase64 = base64Image))
}
