package dev.lukeponga.pricesnap.ui

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.lukeponga.pricesnap.data.ScanPreferenceManager
import dev.lukeponga.pricesnap.history.HistoryEntity
import dev.lukeponga.pricesnap.history.HistoryRepository
import dev.lukeponga.pricesnap.model.AppraisalData
import dev.lukeponga.pricesnap.network.AppraisalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

sealed class AppraisalUiState {
    object Idle : AppraisalUiState()
    object Loading : AppraisalUiState()
    data class Success(val appraisal: AppraisalData) : AppraisalUiState()
    data class Error(val message: String) : AppraisalUiState()
}

sealed class BackendStatus {
    object Checking : BackendStatus()
    data class Connected(val service: String, val timestamp: Long) : BackendStatus()
    object Offline : BackendStatus()
    data class Error(val msg: String) : BackendStatus()
}

class AppraisalViewModel(
    private val repository: HistoryRepository,
    private val scanPreferenceManager: ScanPreferenceManager,
    private val appraisalRepository: AppraisalRepository = AppraisalRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow<AppraisalUiState>(AppraisalUiState.Idle)
    val uiState: StateFlow<AppraisalUiState> = _uiState.asStateFlow()

    private val _backendStatus = MutableStateFlow<BackendStatus>(BackendStatus.Checking)
    val backendStatus: StateFlow<BackendStatus> = _backendStatus.asStateFlow()

    val history: StateFlow<List<HistoryEntity>> = repository.allHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val syncStatus: StateFlow<dev.lukeponga.pricesnap.history.SyncStatus> = repository.syncStatus

    val dailyScanCount: StateFlow<Int> = scanPreferenceManager.dailyScanCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    init { checkBackendHealth() }

    fun syncCloudScans() {
        viewModelScope.launch {
            repository.syncFromFirestore()
            repository.syncLocalScansToFirestore()
        }
    }

    fun deleteHistoryItem(entity: HistoryEntity) {
        viewModelScope.launch {
            repository.deleteFromHistory(entity)
        }
    }

    fun checkBackendHealth() {
        viewModelScope.launch { refreshBackendHealth() }
    }

    private suspend fun refreshBackendHealth() {
        _backendStatus.value = BackendStatus.Checking
        try {
            val response = appraisalRepository.ping()
            val ping = response.body()
            _backendStatus.value = if (response.isSuccessful && ping?.status == "ok") {
                BackendStatus.Connected(ping.service, ping.timestamp)
            } else {
                BackendStatus.Error("Backend returned HTTP ${response.code()}")
            }
        } catch (_: Exception) {
            _backendStatus.value = BackendStatus.Offline
        }
    }

    fun analyzeCapturedImage(base64Image: String, imageFile: File? = null, isGuest: Boolean = false) {
        viewModelScope.launch {
            if (isGuest && dailyScanCount.value >= 10) {
                _uiState.value = AppraisalUiState.Error("Daily scan limit reached for guests (10/day). Please sign in for unlimited scans!")
                return@launch
            }

            _uiState.value = AppraisalUiState.Loading
            try {
                // Do not block appraisal on a separate health-check request. A successful
                // appraisal itself proves connectivity and avoids an unnecessary round trip.
                val response = appraisalRepository.analyzeImage(base64Image)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.ok == true && body.appraisal != null) {
                        if (isGuest) {
                            scanPreferenceManager.incrementScanCount()
                        }
                        val appraisal = body.appraisal
                        _backendStatus.value = BackendStatus.Connected("PriceSnap", System.currentTimeMillis())
                        _uiState.value = AppraisalUiState.Success(appraisal)
                        imageFile?.let { file ->
                            repository.saveToHistory(
                                HistoryEntity(
                                    id = UUID.randomUUID().toString(),
                                    itemName = appraisal.itemName ?: "Unknown Item",
                                    price = appraisal.resalePriceNz?.toDouble() ?: 0.0,
                                    imageUrl = file.absolutePath,
                                    date = System.currentTimeMillis(),
                                    confidence = appraisal.confidence?.toFloat() ?: 0f,
                                    category = appraisal.itemCategory ?: "Unknown",
                                    condition = appraisal.product?.conditionGrade ?: "N/A"
                                )
                            )
                        }
                    } else {
                        _uiState.value = AppraisalUiState.Error("We couldn't read the appraisal result. Please try again.")
                    }
                } else {
                    _uiState.value = AppraisalUiState.Error(userMessageFor(response.code()))
                }
            } catch (_: Exception) {
                _backendStatus.value = BackendStatus.Offline
                _uiState.value = AppraisalUiState.Error("We couldn't connect to PriceSnap. Check your connection and try again.")
            }
        }
    }

    private fun userMessageFor(code: Int): String = when (code) {
        400 -> "We couldn't read that image. Please choose another photo and try again."
        413 -> "That photo is too large. Please choose a smaller image and try again."
        502, 503, 504 -> "The appraisal service is temporarily unavailable. Please try again shortly."
        else -> "The appraisal couldn't be completed (HTTP $code). Please try again."
    }

    fun appraiseImage(imageFile: File, isGuest: Boolean = false) {
        viewModelScope.launch {
            try {
                val base64Image = withContext(Dispatchers.IO) {
                    val bytes = imageFile.readBytes()
                    "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
                }
                analyzeCapturedImage(base64Image, imageFile, isGuest)
            } catch (_: Exception) {
                _uiState.value = AppraisalUiState.Error("We couldn't open that photo. Please choose another image.")
            }
        }
    }

    fun resetState() { _uiState.value = AppraisalUiState.Idle }

    fun clearHistory() {
        viewModelScope.launch { repository.clearAllHistory() }
    }

    fun clearLocalCacheOnLogout() {
        viewModelScope.launch { repository.clearLocalCacheOnLogout() }
    }

    class Factory(
        private val repository: HistoryRepository,
        private val scanPreferenceManager: ScanPreferenceManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppraisalViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AppraisalViewModel(repository, scanPreferenceManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
