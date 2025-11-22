package com.example.springai.service

import com.example.springai.functions.OrderStatusResponse
import org.springframework.stereotype.Service

/**
 * Mock 주문 서비스
 * 실제 외부 API 대신 사용하는 테스트용 서비스
 */
@Service
class MockOrderService {
    
    private val orders = mapOf(
        "ORD-001" to OrderData("ORD-001", "배송중", listOf("노트북", "마우스"), 1500000.0),
        "ORD-002" to OrderData("ORD-002", "주문완료", listOf("키보드"), 150000.0),
        "ORD-003" to OrderData("ORD-003", "배송완료", listOf("모니터"), 300000.0),
        "ORD-004" to OrderData("ORD-004", "배송중", listOf("스피커", "헤드셋"), 250000.0),
        "ORD-005" to OrderData("ORD-005", "주문완료", listOf("웹캠"), 80000.0)
    )
    
    /**
     * 주문 ID로 주문 정보 조회
     */
    fun getOrderById(orderId: String): OrderData? {
        return orders[orderId]
    }
    
    /**
     * 모든 주문 목록 조회
     */
    fun getAllOrders(): List<OrderData> {
        return orders.values.toList()
    }
}

/**
 * 주문 데이터 클래스
 */
data class OrderData(
    val orderId: String,
    val status: String,
    val items: List<String>,
    val totalAmount: Double
)

