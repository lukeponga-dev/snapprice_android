package dev.lukeponga.pricesnap.model

import com.google.gson.annotations.SerializedName

data class ImageRequest(
    @SerializedName("image") val image: String
)

data class PingResponse(
    @SerializedName("status") val status: String,
    @SerializedName("service") val service: String,
    @SerializedName("timestamp") val timestamp: Long
)

data class AppraisalResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("appraisal") val appraisal: AppraisalData?
)

data class AppraisalData(
    @SerializedName("item_category") val itemCategory: String?,
    @SerializedName("item_name") val itemName: String?,
    @SerializedName("brand") val brand: String?,
    @SerializedName("condition_score") val conditionScore: Int?,
    @SerializedName("defects") val defects: List<String>?,
    @SerializedName("resale_price_nz") val resalePriceNz: Int?,
    @SerializedName("confidence") val confidence: Double?,
    @SerializedName("product") val product: ProductDetails?,
    @SerializedName("market") val market: MarketData?
)

data class ProductDetails(
    @SerializedName("name") val name: String?,
    @SerializedName("brand") val brand: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("condition_score") val conditionScore: Int?,
    @SerializedName("condition_grade") val conditionGrade: String?,
    @SerializedName("defects") val defects: List<String>?,
    @SerializedName("resale_price_nz") val resalePriceNz: Int?,
    @SerializedName("confidence") val confidence: Double?,
    @SerializedName("confidence_color") val confidenceColor: String?,
    @SerializedName("summary") val summary: String?
)

data class MarketData(
    @SerializedName("trademe") val trademe: MarketplaceStats?,
    @SerializedName("facebook") val facebook: MarketplaceStats?,
    @SerializedName("ebay") val ebay: MarketplaceStats?,
    @SerializedName("trend") val trend: String?,
    @SerializedName("recommended_price") val recommendedPrice: Int?,
    @SerializedName("best_platform") val bestPlatform: String?
)

data class MarketplaceStats(
    @SerializedName("low") val low: Int?,
    @SerializedName("median") val median: Int?,
    @SerializedName("high") val high: Int?,
    @SerializedName("sample_listings") val sampleListings: List<String>?
)
