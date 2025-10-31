package com.example.appinterface.Api

data class CatResponseDTO (
    val id: String?,
    val created_at: String?,
    val tags: List<String>?,
    val url: String?
)