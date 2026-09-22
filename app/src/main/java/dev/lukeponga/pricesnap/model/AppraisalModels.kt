package dev.lukeponga.pricesnap.model

import com.google.gson.annotations.SerializedName

data class ImageRequest(
    @SerializedName("image") val imageBase64: String
)

data class AppraisalResponse(
    @SerializedName("item") val item: ItemIdentity,
    @SerializedName("condition") val condition: ConditionResult,
    @SerializedName("valuation") val valuation: ValuationResult,
    @SerializedName("market") val market: MarketEvidence,
    @SerializedName("confidence") val confidence: Int,
    @SerializedName("metadata") val metadata: Metadata
)

data class ItemIdentity(
    @SerializedName("name") val name: String,
    @SerializedName("brand") val brand: String?,
    @SerializedName("category") val category: String?
)

data class ConditionResult(
    @SerializedName("score") val score: Int,
    @SerializedName("grade") val grade: String,
    @SerializedName("defects") val defects: List<String>
)

data class ValuationResult(
    @SerializedName("resalePrice") val resalePrice: Double,
    @SerializedName("currency") val currency: String = "NZD"
)

data class MarketEvidence(
    @SerializedName("trademe") val trademe: MarketStats?,
    @SerializedName("facebook") val facebook: MarketStats?,
    @SerializedName("ebay") val ebay: MarketStats?,
    @SerializedName("recommendedPrice") val recommendedPrice: Double?,
    @SerializedName("bestPlatform") val bestPlatform: String?
)

data class MarketStats(
    @SerializedName("low") val low: Double?,
    @SerializedName("median") val median: Double?,
    @SerializedName("high") val high: Double?
)

data class Metadata(
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("version") val version: String
)
