package com.lucknow.waterbowl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucknow.waterbowl.data.api.ApiExceptionMapper
import com.lucknow.waterbowl.data.api.RetrofitClient
import com.lucknow.waterbowl.data.models.Distribution
import com.lucknow.waterbowl.data.models.Stats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val stats: Stats? = null,
    val recentDistributions: List<Distribution> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val api = RetrofitClient.apiService

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val stats = api.getStats()
                _uiState.value = _uiState.value.copy(
                    stats = stats,
                    recentDistributions = stats.recentDistributions,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = ApiExceptionMapper.userMessage(e)
                )
            }
        }
    }
}
