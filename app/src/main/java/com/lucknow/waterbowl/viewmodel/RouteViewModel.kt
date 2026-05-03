package com.lucknow.waterbowl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucknow.waterbowl.data.api.ApiExceptionMapper
import com.lucknow.waterbowl.data.api.RetrofitClient
import com.lucknow.waterbowl.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RouteUiState(
    val addresses: List<String> = listOf(""),
    val geocodedResults: List<GeocodeResult> = emptyList(),
    val optimizedRoute: OptimizeRouteResponse? = null,
    val startLatitude: Double = 26.8467,
    val startLongitude: Double = 80.9462,
    val isGeocoding: Boolean = false,
    val isOptimizing: Boolean = false,
    val error: String? = null,
    val showMap: Boolean = false
)

class RouteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RouteUiState())
    val uiState: StateFlow<RouteUiState> = _uiState.asStateFlow()

    private val api = RetrofitClient.apiService

    fun updateAddress(index: Int, value: String) {
        val newAddresses = _uiState.value.addresses.toMutableList()
        if (index < newAddresses.size) {
            newAddresses[index] = value
            _uiState.value = _uiState.value.copy(addresses = newAddresses)
        }
    }

    fun addAddress() {
        val newAddresses = _uiState.value.addresses.toMutableList()
        newAddresses.add("")
        _uiState.value = _uiState.value.copy(addresses = newAddresses)
    }

    fun removeAddress(index: Int) {
        val newAddresses = _uiState.value.addresses.toMutableList()
        if (newAddresses.size > 1 && index < newAddresses.size) {
            newAddresses.removeAt(index)
            _uiState.value = _uiState.value.copy(addresses = newAddresses)
        }
    }

    fun updateStartLocation(lat: Double, lng: Double) {
        _uiState.value = _uiState.value.copy(startLatitude = lat, startLongitude = lng)
    }

    fun geocodeAddresses() {
        val validAddresses = _uiState.value.addresses.filter { it.isNotBlank() }
        if (validAddresses.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Please enter at least one address")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeocoding = true, error = null)
            try {
                val results = api.geocodeAddresses(GeocodeRequest(validAddresses))
                _uiState.value = _uiState.value.copy(
                    geocodedResults = results,
                    isGeocoding = false,
                    showMap = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGeocoding = false,
                    error = ApiExceptionMapper.userMessage(e)
                )
            }
        }
    }

    fun optimizeRoute() {
        val results = _uiState.value.geocodedResults
        if (results.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Geocode addresses first")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOptimizing = true, error = null)
            try {
                val start = LatLng(
                    lat = _uiState.value.startLatitude,
                    lng = _uiState.value.startLongitude
                )
                val destinations = results.map { LatLng(lat = it.latitude, lng = it.longitude) }

                val optimized = api.optimizeRoute(
                    OptimizeRouteRequest(start = start, destinations = destinations)
                )
                _uiState.value = _uiState.value.copy(
                    optimizedRoute = optimized,
                    isOptimizing = false,
                    showMap = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isOptimizing = false,
                    error = ApiExceptionMapper.userMessage(e)
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
