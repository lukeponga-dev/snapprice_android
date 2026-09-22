package dev.lukeponga.pricesnap.model

import com.google.gson.annotations.SerializedName

data class ImageRequest(
    @SerializedName("imageBase64") val imageBase64: String
)

data class AppraisalResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("item") val item: ItemIdentity,
    @SerializedName("condition") val condition: ConditionResult,
    @SerializedName("valuation") val valuation: ValuationResult,
    @SerializedName("confidence") val confidence: ConfidenceResult,
    @SerializedName("comparables") val comparables: List<ComparableItem>,
    @SerializedName("marketplaceRecommendation") val marketplaceRecommendation: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("metadata") val metadata: Metadata?
)

data class ItemIdentity(
    @SerializedName("name") val name: String,
    @SerializedName("brand") val brand: String?,
    @SerializedName("model") val model: String?,
    @SerializedName("variant") val variant: String?,
    @SerializedName("category") val category: String?
)

data class ConditionResult(
    @SerializedName("score") val score: Int,
    @SerializedName("grade") val grade: String,
    @SerializedName("defects") val defects: List<String>
)

data class ValuationResult(
    @SerializedName("low") val low: Double,
    @SerializedName("expected") val expected: Double,
    @SerializedName("high") val high: Double,
    @SerializedName("currency") val currency: String = "NZD"
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
    @SerializedName("url") val url: String?,
    @SerializedName("condition") val condition: String?,
    @SerializedName("relevance") val relevance: Double,
    @SerializedName("verified") val verified: Boolean
)

data class Metadata(
    @SerializedName("model") val model: String?,
    @SerializedName("generated_at") val generatedAt: String?
)

data class ApiError(
    @SerializedName("ok") val ok: Boolean? = false,
    @SerializedName("error") val code: String? = null,
    @SerializedName("message") val message: String? = null
)
