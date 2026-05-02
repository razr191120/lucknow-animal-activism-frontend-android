package com.lucknow.waterbowl.data.models

import com.google.gson.annotations.SerializedName

data class Drive(
    val id: Int? = null,
    val name: String,
    val description: String? = null,
    val addresses: List<String> = emptyList(),
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("distribution_count") val distributionCount: Int = 0
)

data class CreateDriveRequest(
    val name: String,
    val description: String? = null,
    val addresses: List<String> = emptyList()
)
