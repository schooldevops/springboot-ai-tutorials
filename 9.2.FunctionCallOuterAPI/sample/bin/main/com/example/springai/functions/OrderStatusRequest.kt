package com.example.springai.functions

/**
 * 주문 상태 조회 함수 요청 데이터 클래스
 */
data class OrderStatusRequest(
    /**
     * 주문 ID (예: ORD-001)
     */
    val orderId: String
)

