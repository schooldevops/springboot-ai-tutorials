package com.example.weatheralarm.config

import com.example.weatheralarm.dto.WeatherRequest
import com.example.weatheralarm.service.WeatherService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Description
import java.util.function.Function

@Configuration
class FunctionConfig(
    private val weatherService: WeatherService
) {
    
    @Bean
    @Description("Get current weather information for a specific city. Returns temperature in Celsius, weather description, humidity percentage, and wind speed.")
    fun getWeather(): Function<WeatherRequest, String> {
        return Function { request ->
            val weather = weatherService.getWeather(request.city)
            
            """
            도시: ${weather.city}
            온도: ${weather.temperature}°C
            날씨: ${weather.description}
            습도: ${weather.humidity}%
            풍속: ${weather.windSpeed}m/s
            """.trimIndent()
        }
    }
}
