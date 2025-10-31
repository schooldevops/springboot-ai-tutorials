package com.example.springai.model

/**
 * 공통으로 사용되는 데이터 클래스들
 */
data class ParseRequest(
    val question: String
)

data class CategoryItem(
    val name: String,
    val items: List<String>
)

