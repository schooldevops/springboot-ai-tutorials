package com.example.productimagetag.dto

data class ProductTags(
    val colors: List<String> = emptyList(),
    val style: String = "",
    val features: List<String> = emptyList(),
    val category: String = "",
    val tags: List<String> = emptyList(),
    val description: String = ""
)

data class AnalysisResponse(
    val productTags: ProductTags,
    val processingTime: Long = 0
)
