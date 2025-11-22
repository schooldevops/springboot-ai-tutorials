package com.example.ordermngbot.service

import com.example.ordermngbot.dto.Order
import com.example.ordermngbot.dto.OrderStatus
import com.example.ordermngbot.dto.Result
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class OrderService {
    
    private val orders = ConcurrentHashMap<String, Order>()
    
    fun addOrder(order: Order) {
        orders[order.orderId] = order
    }
    
    fun getOrderStatus(orderId: String): OrderStatus {
        val order = orders[orderId] 
            ?: throw IllegalArgumentException("주문을 찾을 수 없습니다: $orderId")
        
        return OrderStatus(
            orderId = order.orderId,
            status = order.status,
            deliveryAddress = order.deliveryAddress
        )
    }
    
    fun changeDeliveryAddress(orderId: String, newAddress: String): Result {
        val order = orders[orderId]
            ?: throw IllegalArgumentException("주문을 찾을 수 없습니다: $orderId")
        
        order.deliveryAddress = newAddress
        
        return Result(
            success = true,
            message = "배송지가 변경되었습니다: $newAddress"
        )
    }
    
    fun cancelOrder(orderId: String): Result {
        val order = orders.remove(orderId)
            ?: throw IllegalArgumentException("주문을 찾을 수 없습니다: $orderId")
        
        return Result(
            success = true,
            message = "주문이 취소되었습니다: $orderId"
        )
    }
    
    fun getAllOrders(): List<Order> {
        return orders.values.toList()
    }
}
