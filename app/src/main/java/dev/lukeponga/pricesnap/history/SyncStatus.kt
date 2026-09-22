package dev.lukeponga.pricesnap.history

sealed class SyncStatus {
    object Idle : SyncStatus()
    object Syncing : SyncStatus()
    data class Synced(val lastSyncTime: Long, val itemCount: Int) : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}
