package com.example.foodapp.network

import com.example.foodapp.model.SearchRequest

fun buildSearchString(request: SearchRequest): String {
    return "SEARCH:${request.category},${request.minStars},${request.maxStars},${request.price},${request.maxDistance},${request.latitude},${request.longitude}"
}