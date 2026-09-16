package dev.lukeponga.pricesnap.ui

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.lukeponga.pricesnap.history.HistoryEntity
import dev.lukeponga.pricesnap.history.HistoryRepository
import dev.lukeponga.pricesnap.model.AppraisalData
import dev.lukeponga.pricesnap.model.ImageRequest
import dev.lukeponga.pricesnap.network.NetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
    data class Connected(
        val service: String,
        val timestamp: Long
    ) : BackendStatus()
    object Offline : BackendStatus()
    data class Error(val msg: String) : BackendStatus()
}

class AppraisalViewModel(private val repository: HistoryRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AppraisalUiState>(AppraisalUiState.Idle)
    val uiState: StateFlow<AppraisalUiState> = _uiState.asStateFlow()

    // Backend connection status state
    private val _backendStatus = MutableStateFlow<BackendStatus>(BackendStatus.Checking)
    val backendStatus: StateFlow<BackendStatus> = _backendStatus.asStateFlow()

    init {
        checkBackendHealth()
    }

    fun checkBackendHealth() {
        viewModelScope.launch {
            refreshBackendHealth()
        }
    }

    private suspend fun refreshBackendHealth() {
        _backendStatus.value = BackendStatus.Checking
        try {
            val response = NetworkClient.apiService.ping()
            val ping = response.body()
            if (response.isSuccessful && ping?.status == "ok") {
                _backendStatus.value = BackendStatus.Connected(
                    service = ping.service,
                    timestamp = ping.timestamp
                )
            } else {
                _backendStatus.value = BackendStatus.Error("Degraded (${response.code()})")
            }
        } catch (e: Exception) {
            _backendStatus.value = BackendStatus.Offline
        }
    }

    val history: StateFlow<List<HistoryEntity>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Submits the Base64 image payload to the Vercel Edge backend and updates the UI state.
     */
    fun analyzeCapturedImage(base64Image: String, imageFile: File? = null) {
        viewModelScope.launch {
            _uiState.value = AppraisalUiState.Loading
            refreshBackendHealth()
            try {
                val request = ImageRequest(image = base64Image)
                val response = NetworkClient.apiService.analyzeItem(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.ok == true && body.appraisal != null) {
                        val appraisal = body.appraisal
                        _uiState.value = AppraisalUiState.Success(appraisal)
                        
                        // Save to history if we have a file reference
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
                        _uiState.value = AppraisalUiState.Error("Invalid appraisal response structure.")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        400 -> "Missing image payload (400 NO_IMAGE)"
                        413 -> "Image exceeds 15MB limit (413 IMAGE_TOO_LARGE)"
                        502 -> "AI Studio gateway unreachable (502 AI_STUDIO_UNREACHABLE)"
                        else -> "Server error: ${response.code()}"
                    }
                    _uiState.value = AppraisalUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = AppraisalUiState.Error(e.localizedMessage ?: "Network connection failed.")
            }
        }
    }

    /**
     * Helper for gallery uploads.
     */
    fun appraiseImage(imageFile: File) {
        val bytes = imageFile.readBytes()
        val base64Image = "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
        analyzeCapturedImage(base64Image, imageFile)
    }

    fun resetState() {
        _uiState.value = AppraisalUiState.Idle
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }

    class Factory(private val repository: HistoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppraisalViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AppraisalViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
