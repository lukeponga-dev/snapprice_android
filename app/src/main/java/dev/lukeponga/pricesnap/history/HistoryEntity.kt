package dev.lukeponga.pricesnap.history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey val id: String,
    val itemName: String,
    val price: Double,
    val imageUrl: String,
    val date: Long,
    val confidence: Float,
    val category: String,
    val condition: String
)
