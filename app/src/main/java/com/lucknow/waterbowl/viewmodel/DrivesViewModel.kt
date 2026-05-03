package com.lucknow.waterbowl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucknow.waterbowl.data.api.ApiExceptionMapper
import com.lucknow.waterbowl.data.api.RetrofitClient
import com.lucknow.waterbowl.data.models.CreateDriveRequest
import com.lucknow.waterbowl.data.models.Drive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DrivesUiState(
    val drives: List<Drive> = emptyList(),
    val selectedDrive: Drive? = null,
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val error: String? = null,
    val createSuccess: Boolean = false
)

class DrivesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DrivesUiState())
    val uiState: StateFlow<DrivesUiState> = _uiState.asStateFlow()

    private val api = RetrofitClient.apiService

    init {
        loadDrives()
    }

    fun loadDrives() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val drives = api.getDrives()
                _uiState.value = _uiState.value.copy(
                    drives = drives,
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

    fun loadDriveDetail(driveId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val drive = api.getDriveDetail(driveId)
                _uiState.value = _uiState.value.copy(
                    selectedDrive = drive,
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

    fun createDrive(name: String, description: String, addresses: List<String>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true, error = null, createSuccess = false)
            try {
                api.createDrive(
                    CreateDriveRequest(
                        name = name,
                        description = description,
                        addresses = addresses
                    )
                )
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    createSuccess = true
                )
                loadDrives()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    error = ApiExceptionMapper.userMessage(e)
                )
            }
        }
    }

    fun clearCreateSuccess() {
        _uiState.value = _uiState.value.copy(createSuccess = false)
    }
}
