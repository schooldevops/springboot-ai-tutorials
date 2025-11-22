package com.example.weatheralarm.service

import com.example.weatheralarm.dto.WeatherApiResponse
import com.example.weatheralarm.dto.WeatherData
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class WeatherService(
    private val restTemplate: RestTemplate,
    
    @Value("\${weather.api.key}")
    private val apiKey: String,
    
    @Value("\${weather.api.base-url}")
    private val baseUrl: String
) {
    
    private val logger = LoggerFactory.getLogger(javaClass)
    
    fun getWeather(city: String): WeatherData {
        logger.info("Fetching weather for city: $city")
        
        val url = "$baseUrl/weather?q=$city&appid=$apiKey&units=metric"
        
        try {
            val response = restTemplate.getForObject(url, WeatherApiResponse::class.java)
                ?: throw RuntimeException("Failed to fetch weather data")
            
            return WeatherData(
                city = city,
                temperature = response.main.temp,
                description = response.weather.firstOrNull()?.description ?: "알 수 없음",
                humidity = response.main.humidity,
                windSpeed = response.wind.speed
            )
        } catch (e: Exception) {
            logger.error("Error fetching weather for $city", e)
            throw RuntimeException("Failed to fetch weather: ${e.message}", e)
        }
    }
}
