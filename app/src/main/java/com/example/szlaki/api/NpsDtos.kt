package com.example.szlaki.api

import com.google.gson.annotations.SerializedName

data class NpsResponse<T>(
    val total: String,
    val limit: String,
    val start: String,
    val data: List<T>
)

// Nowy DTO dla endpointu /parks
data class ParkData(
    val id: String,
    val parkCode: String,
    val fullName: String,
    val description: String,
    val designation: String,
    val states: String,
    val images: List<ImageItem>,
    val activities: List<ParkActivity>
)

data class ParkActivity(
    val id: String,
    val name: String
)

data class ImageItem(
    val url: String,
    val altText: String,
    val title: String
)

// DTO dla "Things to do"
data class ThingToDoItem(
    val id: String,
    val title: String,
    val longDescription: String,
    val shortDescription: String
)

data class ActivityItem(
    val id: String,
    val name: String,
    val parks: List<ParkItem>
)

data class ParkItem(
    val parkCode: String,
    val fullName: String,
    val states: String,
    val designation: String
)

data class GalleryItem(
    val id: String,
    val title: String,
    val images: List<ImageItem>
)
