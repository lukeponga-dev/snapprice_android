package dev.lukeponga.pricesnap.model

import com.google.gson.annotations.SerializedName

data class ValuationRequest(
    @SerializedName("imageBase64") val imageBase64: String,
    @SerializedName("mimeType") val mimeType: String = "image/jpeg"
)

data class ValuationResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("status") val status: String?,
    @SerializedName("warnings") val warnings: List<String>?,
    @SerializedName("item") val item: ItemIdentity,
    @SerializedName("condition") val condition: ConditionResult,
    @SerializedName("valuation") val valuation: ValuationResult,
    @SerializedName("confidence") val confidence: ConfidenceResult,
    @SerializedName("comparables") val comparables: List<ComparableItem>,
    @SerializedName("generatedAt") val generatedAt: String
) {
    val isHeuristic: Boolean
        get() = status == "heuristic"

    val priceLabel: String
        get() = when {
            !hasUsablePrice -> "Unpriced"
            isHeuristic -> "AI estimate"
            else -> "Market estimate"
        }

    val hasUsablePrice: Boolean
        get() = ok && status in setOf("success", "heuristic") && valuation.currency == "NZD" &&
            listOf(valuation.estimatedValue, valuation.low, valuation.high)
                .all { it != null && it.isFinite() && it > 0 } &&
            valuation.low!! <= valuation.estimatedValue!! &&
            valuation.high!! >= valuation.estimatedValue!!

    val confidenceFraction: Float
        get() = if (hasUsablePrice && confidence.score.isFinite())
            confidence.score.coerceIn(0.0, 1.0).toFloat() else 0f
}

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
    @SerializedName("estimatedValue") val estimatedValue: Double?,
    @SerializedName("low") val low: Double?,
    @SerializedName("high") val high: Double?,
    @SerializedName("method") val method: String? = null
)

data class ConfidenceResult(
    @SerializedName("score") val score: Double,
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
    @SerializedName("code") val code: String? = null,
    @SerializedName("error") val error: String? = null
)
