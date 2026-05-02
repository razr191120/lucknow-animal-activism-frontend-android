package com.lucknow.waterbowl.data.models

import com.google.gson.annotations.SerializedName

data class Distribution(
    val id: Int? = null,
    val name: String,
    val contact: String? = null,
    val description: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    @SerializedName("drive_id") val driveId: Int? = null,
    @SerializedName("water_bowl_photo_url") val waterBowlPhotoUrl: String? = null,
    @SerializedName("owner_photo_url") val ownerPhotoUrl: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class Stats(
    @SerializedName("total_distributions") val totalDistributions: Int = 0,
    @SerializedName("total_drives") val totalDrives: Int = 0,
    @SerializedName("total_bowls_placed") val totalBowlsPlaced: Int = 0,
    @SerializedName("recent_distributions") val recentDistributions: List<Distribution> = emptyList()
)
