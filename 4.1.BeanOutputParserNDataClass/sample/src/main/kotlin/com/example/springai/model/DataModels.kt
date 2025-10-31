package com.example.springai.model

/**
 * BeanOutputParser와 함께 사용할 Data Class들
 */

data class UserInfo(
    val name: String,
    val age: Int,
    val email: String
)

data class UserProfile(
    val name: String,
    val age: Int?,
    val email: String?,
    val location: String?,
    val bio: String?
)

data class Product(
    val name: String,
    val price: Double,
    val category: String,
    val description: String,
    val features: List<String>
)

data class Address(
    val street: String,
    val city: String,
    val zipCode: String,
    val country: String
)

data class CompanyInfo(
    val name: String,
    val address: Address,
    val employees: Int,
    val departments: List<String>
)

data class Recipe(
    val name: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val cookingTime: Int?
)

