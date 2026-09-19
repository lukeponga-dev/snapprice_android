package dev.lukeponga.pricesnap.history

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import dev.lukeponga.pricesnap.auth.FirebaseAuthenticationManager
import dev.lukeponga.pricesnap.model.ImageRequest
import dev.lukeponga.pricesnap.network.PriceSnapApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

sealed class SyncStatus {
    object Idle : SyncStatus()
    object Syncing : SyncStatus()
    data class Synced(val lastSyncTime: Long, val itemCount: Int) : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}

class HistoryRepository(
    private val historyDao: HistoryDao,
    private val apiService: PriceSnapApiService,
    private val firestore: FirebaseFirestore?,
    private val authManager: FirebaseAuthenticationManager? = null
) {
    val allHistory: Flow<List<HistoryEntity>> = historyDao.getAllHistory()

    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private var realtimeListener: ListenerRegistration? = null

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    init {
        // Automatically start real-time sync when user auth changes
        repositoryScope.launch {
            authManager?.currentUser?.collect { user ->
                if (user != null && !user.isAnonymous && user.uid.isNotBlank()) {
                    startRealtimeSync(user.uid)
                    syncFromFirestore(user.uid)
                } else {
                    stopRealtimeSync()
                }
            }
        }
    }

    suspend fun appraiseImage(base64Image: String) = 
        apiService.analyzeItem(ImageRequest(base64Image)).body()?.appraisal

    suspend fun saveToHistory(entity: HistoryEntity) {
        historyDao.insertHistory(entity)
        saveToFirestore(entity)
    }

    private fun saveToFirestore(entity: HistoryEntity) {
        val firestoreInstance = firestore ?: return
        val currentUid = authManager?.getCurrentUid()
        val currentUser = authManager?.getCurrentUser()

        val scanData = hashMapOf(
            "id" to entity.id,
            "itemName" to entity.itemName,
            "price" to entity.price,
            "confidence" to entity.confidence,
            "category" to entity.category,
            "condition" to entity.condition,
            "timestamp" to entity.date,
            "imageUrl" to entity.imageUrl,
            "userId" to (currentUid ?: "guest"),
            "userEmail" to (currentUser?.email ?: "guest")
        )

        try {
            // 1. Save to global scans collection
            firestoreInstance.collection("scans")
                .document(entity.id)
                .set(scanData, SetOptions.merge())

            // 2. If user is authenticated, save under user-specific subcollection
            if (!currentUid.isNullOrBlank()) {
                firestoreInstance.collection("users")
                    .document(currentUid)
                    .collection("scans")
                    .document(entity.id)
                    .set(scanData, SetOptions.merge())

                // Update user metadata in Firestore
                val userMetadata = hashMapOf(
                    "lastActive" to System.currentTimeMillis(),
                    "email" to (currentUser?.email ?: "")
                )
                firestoreInstance.collection("users")
                    .document(currentUid)
                    .set(userMetadata, SetOptions.merge())
            }
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Failed to save to Firestore", e)
        }
    }

    suspend fun syncFromFirestore(userId: String? = authManager?.getCurrentUid()) {
        val uid = userId ?: return
        val firestoreInstance = firestore ?: return
        _syncStatus.value = SyncStatus.Syncing

        try {
            val snapshot = firestoreInstance.collection("users")
                .document(uid)
                .collection("scans")
                .get()
                .await()

            val remoteEntities = snapshot.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: doc.id
                val itemName = doc.getString("itemName") ?: return@mapNotNull null
                val price = doc.getDouble("price") ?: 0.0
                val confidence = (doc.getDouble("confidence") ?: 0.0).toFloat()
                val category = doc.getString("category") ?: "General"
                val condition = doc.getString("condition") ?: "Good"
                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                val imageUrl = doc.getString("imageUrl") ?: ""

                HistoryEntity(
                    id = id,
                    itemName = itemName,
                    price = price,
                    imageUrl = imageUrl,
                    date = timestamp,
                    confidence = confidence,
                    category = category,
                    condition = condition
                )
            }

            if (remoteEntities.isNotEmpty()) {
                historyDao.insertAll(remoteEntities)
            }
            _syncStatus.value = SyncStatus.Synced(System.currentTimeMillis(), remoteEntities.size)
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Sync failed", e)
            _syncStatus.value = SyncStatus.Error(e.localizedMessage ?: "Sync failed")
        }
    }

    suspend fun syncLocalScansToFirestore(userId: String? = authManager?.getCurrentUid()) {
        val uid = userId ?: return
        val firestoreInstance = firestore ?: return
        _syncStatus.value = SyncStatus.Syncing

        try {
            val localItems = historyDao.getAllHistoryOnce()
            for (entity in localItems) {
                saveToFirestore(entity)
            }
            _syncStatus.value = SyncStatus.Synced(System.currentTimeMillis(), localItems.size)
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Sync to Firestore failed", e)
            _syncStatus.value = SyncStatus.Error(e.localizedMessage ?: "Sync to cloud failed")
        }
    }

    fun startRealtimeSync(userId: String) {
        val firestoreInstance = firestore ?: return
        stopRealtimeSync()

        try {
            realtimeListener = firestoreInstance.collection("users")
                .document(userId)
                .collection("scans")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("HistoryRepository", "Firestore listener error", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        repositoryScope.launch {
                            val remoteEntities = snapshot.documents.mapNotNull { doc ->
                                val id = doc.getString("id") ?: doc.id
                                val itemName = doc.getString("itemName") ?: return@mapNotNull null
                                val price = doc.getDouble("price") ?: 0.0
                                val confidence = (doc.getDouble("confidence") ?: 0.0).toFloat()
                                val category = doc.getString("category") ?: "General"
                                val condition = doc.getString("condition") ?: "Good"
                                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                                val imageUrl = doc.getString("imageUrl") ?: ""

                                HistoryEntity(
                                    id = id,
                                    itemName = itemName,
                                    price = price,
                                    imageUrl = imageUrl,
                                    date = timestamp,
                                    confidence = confidence,
                                    category = category,
                                    condition = condition
                                )
                            }
                            historyDao.insertAll(remoteEntities)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Failed to start realtime sync", e)
        }
    }

    fun stopRealtimeSync() {
        realtimeListener?.remove()
        realtimeListener = null
    }

    suspend fun deleteFromHistory(entity: HistoryEntity) {
        historyDao.deleteHistory(entity)
        deleteLocalImage(entity.imageUrl)

        // Delete from Firestore
        val firestoreInstance = firestore ?: return
        val currentUid = authManager?.getCurrentUid()

        try {
            firestoreInstance.collection("scans").document(entity.id).delete()
            if (!currentUid.isNullOrBlank()) {
                firestoreInstance.collection("users")
                    .document(currentUid)
                    .collection("scans")
                    .document(entity.id)
                    .delete()
            }
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Failed to delete from Firestore", e)
        }
    }

    suspend fun clearAllHistory() {
        val items = historyDao.getAllHistoryOnce()
        items.forEach { deleteLocalImage(it.imageUrl) }
        historyDao.clearHistory()

        // Delete all from Firestore for this user
        val firestoreInstance = firestore ?: return
        val currentUid = authManager?.getCurrentUid()

        try {
            for (item in items) {
                firestoreInstance.collection("scans").document(item.id).delete()
                if (!currentUid.isNullOrBlank()) {
                    firestoreInstance.collection("users")
                        .document(currentUid)
                        .collection("scans")
                        .document(item.id)
                        .delete()
                }
            }
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Failed to clear Firestore history", e)
        }
    }

    private fun deleteLocalImage(path: String) {
        if (path.isBlank()) return
        runCatching {
            val file = File(path)
            if (file.exists() && file.isFile) file.delete()
        }
    }
}

