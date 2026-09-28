package dev.lukeponga.pricesnap.model

import com.google.gson.annotations.SerializedName

data class ImageRequest(@SerializedName("image") val image: String)

/** Canonical response from pricesnapai /api/analyze. Prices are absent when evidence is insufficient. */
data class AppraisalResponse(
    val ok: Boolean,
    val status: String,
    val product: ItemIdentity,
    val valuation: ValuationResult,
    val confidence: ConfidenceResult,
    val evidence: EvidenceResult,
    val market: MarketResult,
    val warnings: List<String>?
) {
    val isPriced: Boolean get() = ok && status == "success" && (valuation.estimatedValue ?: 0.0) > 0.0
}

data class ItemIdentity(
    val name: String,
    val brand: String?,
    val category: String?,
    val summary: String,
    val condition: ConditionResult
)

data class ConditionResult(val score: Int, val grade: String, val defects: List<String>)

data class ValuationResult(
    val estimatedValue: Double?,
    val lowEstimate: Double?,
    val highEstimate: Double?,
    val currency: String
)

data class ConfidenceResult(val score: Double, val percentage: Int, val level: String)

data class EvidenceResult(val sources: List<ComparableItem>)

data class ComparableItem(val title: String, val priceNZD: Double, val platform: String, val url: String)

data class MarketResult(@SerializedName("best_platform") val bestPlatform: String?)
