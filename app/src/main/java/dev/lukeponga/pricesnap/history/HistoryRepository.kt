package dev.lukeponga.pricesnap.history

import com.google.firebase.firestore.FirebaseFirestore
import dev.lukeponga.pricesnap.model.ImageRequest
import dev.lukeponga.pricesnap.network.PriceSnapApiService
import kotlinx.coroutines.flow.Flow
import java.io.File

class HistoryRepository(
    private val historyDao: HistoryDao,
    private val apiService: PriceSnapApiService,
    private val firestore: FirebaseFirestore?
) {
    val allHistory: Flow<List<HistoryEntity>> = historyDao.getAllHistory()

    suspend fun appraiseImage(base64Image: String) = 
        apiService.analyzeItem(ImageRequest(base64Image)).body()?.appraisal

    suspend fun saveToHistory(entity: HistoryEntity) {
        historyDao.insertHistory(entity)
        saveToFirestore(entity)
    }

    private fun saveToFirestore(entity: HistoryEntity) {
        val firestoreInstance = firestore ?: return
        val scanData = hashMapOf(
            "id" to entity.id,
            "itemName" to entity.itemName,
            "price" to entity.price,
            "confidence" to entity.confidence,
            "category" to entity.category,
            "condition" to entity.condition,
            "timestamp" to entity.date
        )

        firestoreInstance.collection("scans")
            .document(entity.id)
            .set(scanData)
    }

    suspend fun deleteFromHistory(entity: HistoryEntity) {
        historyDao.deleteHistory(entity)
        deleteLocalImage(entity.imageUrl)
    }

    suspend fun clearAllHistory() {
        historyDao.getAllHistoryOnce().forEach { deleteLocalImage(it.imageUrl) }
        historyDao.clearHistory()
    }

    private fun deleteLocalImage(path: String) {
        if (path.isBlank()) return
        runCatching {
            val file = File(path)
            if (file.exists() && file.isFile) file.delete()
        }
    }
}
