package com.lucknow.waterbowl.data.models

import com.google.gson.annotations.SerializedName

data class GeocodeRequest(
    val addresses: List<String>
)

data class GeocodeResult(
    val address: String,
    val latitude: Double,
    val longitude: Double
)

data class LatLng(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)

data class OptimizeRouteRequest(
    val start: LatLng,
    val destinations: List<LatLng>
)

data class OptimizeRouteResponse(
    @SerializedName("optimized_order") val optimizedOrder: List<Int> = emptyList(),
    @SerializedName("optimized_destinations") val optimizedDestinations: List<LatLng> = emptyList(),
    @SerializedName("total_distance_km") val totalDistanceKm: Double = 0.0,
    @SerializedName("waypoints") val waypoints: List<RouteWaypoint> = emptyList()
)

data class RouteWaypoint(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null,
    val order: Int = 0
)
