package dev.lukeponga.pricesnap.history

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY date DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(entity: HistoryEntity)

    @Delete
    suspend fun deleteHistory(entity: HistoryEntity)

    @Query("DELETE FROM history")
    suspend fun clearHistory()
}
