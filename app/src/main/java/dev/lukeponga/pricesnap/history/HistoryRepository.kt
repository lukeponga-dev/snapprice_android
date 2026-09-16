package dev.lukeponga.pricesnap.history

import dev.lukeponga.pricesnap.model.ImageRequest
import dev.lukeponga.pricesnap.network.PriceSnapApiService
import kotlinx.coroutines.flow.Flow

class HistoryRepository(
    private val historyDao: HistoryDao,
    private val apiService: PriceSnapApiService
) {
    val allHistory: Flow<List<HistoryEntity>> = historyDao.getAllHistory()

    suspend fun appraiseImage(base64Image: String) = 
        apiService.analyzeItem(ImageRequest(base64Image)).body()?.appraisal

    suspend fun saveToHistory(entity: HistoryEntity) {
        historyDao.insertHistory(entity)
    }

    suspend fun deleteFromHistory(entity: HistoryEntity) {
        historyDao.deleteHistory(entity)
    }

    suspend fun clearAllHistory() {
        historyDao.clearHistory()
    }
}
