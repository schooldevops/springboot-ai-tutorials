package com.example.ordermngbot.config

import com.example.ordermngbot.dto.AddressChangeRequest
import com.example.ordermngbot.dto.OrderRequest
import com.example.ordermngbot.dto.OrderStatus
import com.example.ordermngbot.dto.Result
import com.example.ordermngbot.service.OrderService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Description
import java.util.function.Function

@Configuration
class FunctionConfig(
    private val orderService: OrderService
) {
    
    @Bean
    @Description("Get the current status of an order by order ID. Returns order status, delivery address, and current state.")
    fun getOrderStatus(): Function<OrderRequest, OrderStatus> {
        return Function { request ->
            orderService.getOrderStatus(request.orderId)
        }
    }
    
    @Bean
    @Description("Change the delivery address for an existing order. Requires order ID and new delivery address.")
    fun changeDeliveryAddress(): Function<AddressChangeRequest, Result> {
        return Function { request ->
            orderService.changeDeliveryAddress(request.orderId, request.newAddress)
        }
    }
    
    @Bean
    @Description("Cancel an order by order ID. This will remove the order from the system.")
    fun cancelOrder(): Function<OrderRequest, Result> {
        return Function { request ->
            orderService.cancelOrder(request.orderId)
        }
    }
}
