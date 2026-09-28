package dev.lukeponga.pricesnap.ui

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.lukeponga.pricesnap.data.ScanPreferenceManager
import dev.lukeponga.pricesnap.history.HistoryEntity
import dev.lukeponga.pricesnap.history.HistoryRepository
import dev.lukeponga.pricesnap.model.ValuationResponse
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
        val result = appraisalRepository.checkConnection()
        _backendStatus.value = result.fold(
            onSuccess = { BackendStatus.Connected("PriceSnap", System.currentTimeMillis()) },
            onFailure = { BackendStatus.Error(it.message ?: "Unknown error") }
        )
    }

    fun analyzeCapturedImage(base64Image: String, imageFile: File? = null, isGuest: Boolean = false) {
        viewModelScope.launch {
            if (isGuest && dailyScanCount.value >= 10) {
                _uiState.value = AppraisalUiState.Error("Daily scan limit reached for guests (10/day). Please sign in for unlimited scans!")
                return@launch
            }

            _uiState.value = AppraisalUiState.Loading
            // Check internal engine configuration before uploading the image.
            val healthCheck = appraisalRepository.checkConnection()
            if (healthCheck.isFailure) {
                _backendStatus.value = BackendStatus.Error(healthCheck.exceptionOrNull()?.message ?: "Connection failed")
                _uiState.value = AppraisalUiState.Error(healthCheck.exceptionOrNull()?.message ?: "Unable to connect to PriceSnap.")
                return@launch
            }

            _uiState.value = AppraisalUiState.Loading
            val result = appraisalRepository.analyzeImage(base64Image)
            
            result.onSuccess { appraisal: ValuationResponse ->
                if (isGuest) {
                    scanPreferenceManager.incrementScanCount()
                }
                _backendStatus.value = BackendStatus.Connected("PriceSnap", System.currentTimeMillis())
                _uiState.value = AppraisalUiState.Success(appraisal)
                // Unpriced scans are shown, but never saved as zero-dollar valuations.
                if (appraisal.hasUsablePrice) imageFile?.let { file ->
                    repository.saveToHistory(
                        HistoryEntity(
                            id = UUID.randomUUID().toString(),
                            itemName = appraisal.item.name,
                            price = appraisal.valuation.estimatedValue!!,
                            imageUrl = file.absolutePath,
                            date = System.currentTimeMillis(),
                            confidence = appraisal.confidenceFraction,
                            category = appraisal.item.category,
                            condition = appraisal.condition.grade
                        )
                    )
                }
            }.onFailure { e ->
                android.util.Log.e("AppraisalViewModel", "Appraisal failed", e)
                _uiState.value = AppraisalUiState.Error(
                    e.message ?: "Appraisal failed. Please try again."
                )
            }
        }
    }

    fun appraiseImage(imageFile: File, isGuest: Boolean = false) {
        viewModelScope.launch {
            try {
                val base64Image = withContext(Dispatchers.IO) {
                    val bitmap = android.graphics.BitmapFactory.decodeFile(imageFile.absolutePath)
                        ?: error("Unable to decode image")
                    val scaled = bitmap.scaleForUpload()
                    val output = java.io.ByteArrayOutputStream()
                    scaled.compress(android.graphics.Bitmap.CompressFormat.JPEG, 82, output)
                    if (scaled !== bitmap) scaled.recycle()
                    bitmap.recycle()
                    "data:image/jpeg;base64," + Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
                }
                analyzeCapturedImage(base64Image, imageFile, isGuest)
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                android.util.Log.e("AppraisalViewModel", "Failed to prepare image for appraisal", e)
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

private fun android.graphics.Bitmap.scaleForUpload(): android.graphics.Bitmap {
    val maxDimension = 1600
    val largest = maxOf(width, height)
    if (largest <= maxDimension) return this
    val ratio = maxDimension.toFloat() / largest
    return android.graphics.Bitmap.createScaledBitmap(
        this, (width * ratio).toInt(), (height * ratio).toInt(), true
    )
}
