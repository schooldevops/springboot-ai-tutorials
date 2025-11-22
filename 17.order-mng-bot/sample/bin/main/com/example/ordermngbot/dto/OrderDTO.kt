package com.example.ordermngbot.dto

data class OrderRequest(
    val orderId: String
)

data class AddressChangeRequest(
    val orderId: String,
    val newAddress: String
)

data class OrderStatus(
    val orderId: String,
    val status: String,
    val deliveryAddress: String = ""
)

data class Result(
    val success: Boolean,
    val message: String = ""
)

data class ChatRequest(
    val message: String
)

data class ChatResponse(
    val message: String,
    val functionCalled: String? = null
)

// Internal Order model
data class Order(
    val orderId: String,
    var status: String,
    var deliveryAddress: String,
    val items: List<String> = emptyList()
)
