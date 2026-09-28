package dev.lukeponga.pricesnap.model

import com.google.gson.annotations.SerializedName

data class ValuationRequest(
    @SerializedName("imageBase64") val imageBase64: String,
    @SerializedName("mimeType") val mimeType: String = "image/jpeg"
)

data class ValuationResponse(
    @SerializedName("item") val item: ItemIdentity,
    @SerializedName("condition") val condition: ConditionResult,
    @SerializedName("valuation") val valuation: ValuationResult,
    @SerializedName("confidence") val confidence: ConfidenceResult,
    @SerializedName("comparables") val comparables: List<ComparableItem>,
    @SerializedName("generatedAt") val generatedAt: String
)

data class AnalysisResponse(
    @SerializedName("item") val item: ItemIdentity,
    @SerializedName("confidence") val confidence: ConfidenceResult
)

data class ItemIdentity(
    @SerializedName("name") val name: String,
    @SerializedName("brand") val brand: String?,
    @SerializedName("model") val model: String?,
    @SerializedName("category") val category: String,
    @SerializedName("attributes") val attributes: Map<String, String> = emptyMap()
)

data class ConditionResult(
    @SerializedName("grade") val grade: String,
    @SerializedName("score") val score: Int,
    @SerializedName("notes") val notes: List<String>
)

data class ValuationResult(
    @SerializedName("currency") val currency: String = "NZD",
    @SerializedName("estimatedValue") val estimatedValue: Double,
    @SerializedName("low") val low: Double,
    @SerializedName("high") val high: Double,
    @SerializedName("method") val method: String
)

data class ConfidenceResult(
    @SerializedName("score") val score: Int,
    @SerializedName("level") val level: String
)

data class ComparableItem(
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("source") val source: String,
    @SerializedName("url") val url: String?
)

data class ApiError(
    @SerializedName("ok") val ok: Boolean? = false,
    @SerializedName("error") val code: String? = null,
    @SerializedName("message") val message: String? = null
)
