package com.example.springai.config

import com.example.springai.functions.OrderStatusRequest
import com.example.springai.functions.OrderStatusResponse
import com.example.springai.functions.WeatherRequest
import com.example.springai.functions.WeatherResponse
import com.example.springai.service.MockOrderService
import com.example.springai.service.MockWeatherService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Description
import java.util.function.Function

/**
 * 외부 API 연동을 위한 Function Calling Configuration
 * 
 * Spring AI 1.0.0-M6에서는 함수를 java.util.function.Function으로 정의하고
 * @Bean과 @Description 어노테이션을 사용하여 등록합니다.
 */
@Configuration
class FunctionConfiguration(
    private val weatherService: MockWeatherService,
    private val orderService: MockOrderService
) {
    
    /**
     * 날씨 조회 함수
     * 특정 도시의 현재 날씨 정보를 조회합니다.
     * 
     * Bean 이름이 함수 이름으로 사용됩니다 (getWeatherFunction).
     */
    @Bean
    @Description("특정 도시의 현재 날씨 정보를 조회합니다. location은 도시 이름입니다 (예: 서울, 부산, 제주).")
    fun getWeatherFunction(): Function<WeatherRequest, WeatherResponse> {
        return Function { request ->
            weatherService.getWeather(request.location)
        }
    }
    
    /**
     * 주문 상태 조회 함수
     * 주문 ID로 주문 상태를 조회합니다.
     * 
     * Bean 이름이 함수 이름으로 사용됩니다 (getOrderStatusFunction).
     */
    @Bean
    @Description("주문 ID로 주문 상태를 조회합니다. orderId는 주문 번호입니다 (예: ORD-001, ORD-002).")
    fun getOrderStatusFunction(): Function<OrderStatusRequest, OrderStatusResponse> {
        return Function { request ->
            val order = orderService.getOrderById(request.orderId)
            
            if (order == null) {
                OrderStatusResponse(
                    orderId = request.orderId,
                    status = "NOT_FOUND",
                    items = emptyList(),
                    totalAmount = 0.0,
                    error = "주문을 찾을 수 없습니다"
                )
            } else {
                OrderStatusResponse(
                    orderId = order.orderId,
                    status = order.status,
                    items = order.items,
                    totalAmount = order.totalAmount
                )
            }
        }
    }
}

