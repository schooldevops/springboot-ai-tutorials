package com.example.springai.functions

/**
 * 주문 상태 조회 함수 응답 데이터 클래스
 */
data class OrderStatusResponse(
    /**
     * 주문 ID
     */
    val orderId: String,
    
    /**
     * 주문 상태 (예: 주문완료, 배송중, 배송완료)
     */
    val status: String,
    
    /**
     * 주문 상품 목록
     */
    val items: List<String>,
    
    /**
     * 총 주문 금액
     */
    val totalAmount: Double,
    
    /**
     * 오류 메시지 (선택사항)
     */
    val error: String? = null
)

