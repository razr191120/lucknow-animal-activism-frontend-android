package com.lucknow.waterbowl.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucknow.waterbowl.data.api.ApiExceptionMapper
import com.lucknow.waterbowl.data.api.RetrofitClient
import com.lucknow.waterbowl.data.models.Distribution
import com.lucknow.waterbowl.data.models.Drive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

data class DistributionUiState(
    val distributions: List<Distribution> = emptyList(),
    val drives: List<Drive> = emptyList(),
    val name: String = "",
    val contact: String = "",
    val description: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val selectedDriveId: Int? = null,
    val waterBowlPhotoUri: Uri? = null,
    val ownerPhotoUri: Uri? = null,
    val waterBowlPhotoFile: File? = null,
    val ownerPhotoFile: File? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val submitSuccess: Boolean = false
)

class DistributionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DistributionUiState())
    val uiState: StateFlow<DistributionUiState> = _uiState.asStateFlow()

    private val api = RetrofitClient.apiService

    init {
        loadDrives()
    }

    fun loadDistributions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val distributions = api.getDistributions()
                _uiState.value = _uiState.value.copy(
                    distributions = distributions,
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

    private fun loadDrives() {
        viewModelScope.launch {
            try {
                val drives = api.getDrives()
                _uiState.value = _uiState.value.copy(drives = drives)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = ApiExceptionMapper.userMessage(e)
                )
            }
        }
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateContact(contact: String) {
        _uiState.value = _uiState.value.copy(contact = contact)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updateAddress(address: String) {
        _uiState.value = _uiState.value.copy(address = address)
    }

    fun updateLocation(lat: Double, lng: Double) {
        _uiState.value = _uiState.value.copy(latitude = lat, longitude = lng)
    }

    fun updateSelectedDrive(driveId: Int?) {
        _uiState.value = _uiState.value.copy(selectedDriveId = driveId)
    }

    fun updateWaterBowlPhoto(uri: Uri?, file: File?) {
        _uiState.value = _uiState.value.copy(waterBowlPhotoUri = uri, waterBowlPhotoFile = file)
    }

    fun updateOwnerPhoto(uri: Uri?, file: File?) {
        _uiState.value = _uiState.value.copy(ownerPhotoUri = uri, ownerPhotoFile = file)
    }

    fun submitDistribution() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.value = state.copy(error = "Name is required")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, error = null, submitSuccess = false)
            try {
                val textType = "text/plain".toMediaTypeOrNull()
                val namePart = state.name.toRequestBody(textType)
                val contactPart = state.contact.toRequestBody(textType)
                val descPart = state.description.toRequestBody(textType)
                val latPart = state.latitude.toString().toRequestBody(textType)
                val lngPart = state.longitude.toString().toRequestBody(textType)
                val addressPart = state.address.toRequestBody(textType)
                val driveIdPart = (state.selectedDriveId?.toString() ?: "").toRequestBody(textType)

                val waterBowlPhotoPart = state.waterBowlPhotoFile?.let { file ->
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("water_bowl_photo", file.name, requestFile)
                }

                val ownerPhotoPart = state.ownerPhotoFile?.let { file ->
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("owner_photo", file.name, requestFile)
                }

                api.recordDistribution(
                    name = namePart,
                    contact = contactPart,
                    description = descPart,
                    latitude = latPart,
                    longitude = lngPart,
                    address = addressPart,
                    driveId = driveIdPart,
                    waterBowlPhoto = waterBowlPhotoPart,
                    ownerPhoto = ownerPhotoPart
                )

                _uiState.value = DistributionUiState(
                    drives = state.drives,
                    submitSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    error = ApiExceptionMapper.userMessage(e)
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSubmitSuccess() {
        _uiState.value = _uiState.value.copy(submitSuccess = false)
    }
}
