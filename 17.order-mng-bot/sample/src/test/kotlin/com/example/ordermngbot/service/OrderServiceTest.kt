package com.example.ordermngbot.service

import com.example.ordermngbot.dto.Order
import com.example.ordermngbot.dto.OrderStatus
import com.example.ordermngbot.dto.Result
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrderServiceTest {
    
    private lateinit var orderService: OrderService
    
    @BeforeEach
    fun setup() {
        orderService = OrderService()
        // Add test data
        orderService.addOrder(Order("12345", "배송 중", "서울시 강남구", listOf("상품A", "상품B")))
        orderService.addOrder(Order("67890", "배송 준비 중", "부산시 해운대구", listOf("상품C")))
    }
    
    @Test
    fun `should get order status`() {
        // when
        val status = orderService.getOrderStatus("12345")
        
        // then
        assertEquals("12345", status.orderId)
        assertEquals("배송 중", status.status)
        assertEquals("서울시 강남구", status.deliveryAddress)
    }
    
    @Test
    fun `should throw exception for non-existent order`() {
        // when & then
        assertThrows<IllegalArgumentException> {
            orderService.getOrderStatus("99999")
        }
    }
    
    @Test
    fun `should change delivery address`() {
        // when
        val result = orderService.changeDeliveryAddress("12345", "제주시 제주대로")
        
        // then
        assertTrue(result.success)
        
        // verify address changed
        val status = orderService.getOrderStatus("12345")
        assertEquals("제주시 제주대로", status.deliveryAddress)
    }
    
    @Test
    fun `should cancel order`() {
        // when
        val result = orderService.cancelOrder("12345")
        
        // then
        assertTrue(result.success)
        
        // verify order is cancelled
        assertThrows<IllegalArgumentException> {
            orderService.getOrderStatus("12345")
        }
    }
    
    @Test
    fun `should not change address for cancelled order`() {
        // given
        orderService.cancelOrder("12345")
        
        // when & then
        assertThrows<IllegalArgumentException> {
            orderService.changeDeliveryAddress("12345", "새 주소")
        }
    }
}
