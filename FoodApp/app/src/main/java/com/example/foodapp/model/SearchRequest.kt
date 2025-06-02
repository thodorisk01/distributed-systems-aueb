package com.example.foodapp.model

data class SearchRequest(
    val category: String,
    val minStars: Int,
    val maxStars: Int,
    val price: String,
    val maxDistance: Double,
    val latitude: Double,
    val longitude: Double
)