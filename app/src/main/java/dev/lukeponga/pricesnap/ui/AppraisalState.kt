package dev.lukeponga.pricesnap.ui

import dev.lukeponga.pricesnap.model.AppraisalResponse

sealed class AppraisalUiState {
    object Idle : AppraisalUiState()
    object Loading : AppraisalUiState()
    data class Success(val appraisal: AppraisalResponse) : AppraisalUiState()
    data class Error(val message: String) : AppraisalUiState()
}

sealed class BackendStatus {
    object Checking : BackendStatus()
    data class Connected(val service: String, val timestamp: Long) : BackendStatus()
    object Offline : BackendStatus()
    data class Error(val msg: String) : BackendStatus()
}
