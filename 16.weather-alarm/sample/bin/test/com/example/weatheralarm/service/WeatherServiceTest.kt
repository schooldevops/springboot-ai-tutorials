package com.example.weatheralarm.service

import com.example.weatheralarm.dto.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class WeatherServiceTest {
    
    private val restTemplate = mockk<RestTemplate>()
    private val weatherService = WeatherService(
        restTemplate = restTemplate,
        apiKey = "test-api-key",
        baseUrl = "https://api.test.com"
    )
    
    @Test
    fun `should fetch weather data for given city`() {
        // given
        val city = "서울"
        val mockResponse = WeatherApiResponse(
            main = Main(temp = 15.0, humidity = 60),  // Already in Celsius
            weather = listOf(Weather(description = "맑음")),
            wind = Wind(speed = 3.5)
        )
        
        every { 
            restTemplate.getForObject(any<String>(), WeatherApiResponse::class.java) 
        } returns mockResponse
        
        // when
        val result = weatherService.getWeather(city)
        
        // then
        assertEquals("서울", result.city)
        assertEquals(15.0, result.temperature, 0.1)
        assertEquals("맑음", result.description)
        assertEquals(60, result.humidity)
        assertEquals(3.5, result.windSpeed)
        
        verify(exactly = 1) { 
            restTemplate.getForObject(any<String>(), WeatherApiResponse::class.java) 
        }
    }
    
    @Test
    fun `should handle API errors gracefully`() {
        // given
        val city = "InvalidCity"
        
        every { 
            restTemplate.getForObject(any<String>(), WeatherApiResponse::class.java) 
        } throws RuntimeException("API Error")
        
        // when & then
        assertThrows<RuntimeException> {
            weatherService.getWeather(city)
        }
    }
    
    @Test
    fun `should convert Kelvin to Celsius correctly`() {
        // given
        val city = "부산"
        val mockResponse = WeatherApiResponse(
            main = Main(temp = 27.0, humidity = 70),  // Already in Celsius
            weather = listOf(Weather(description = "흐림")),
            wind = Wind(speed = 5.0)
        )
        
        every { 
            restTemplate.getForObject(any<String>(), WeatherApiResponse::class.java) 
        } returns mockResponse
        
        // when
        val result = weatherService.getWeather(city)
        
        // then
        assertEquals(27.0, result.temperature, 0.1)
    }
}
