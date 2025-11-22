package com.example.springai.service

import com.example.springai.functions.WeatherResponse
import org.springframework.stereotype.Service

/**
 * Mock 날씨 서비스
 * 실제 외부 API 대신 사용하는 테스트용 서비스
 */
@Service
class MockWeatherService {
    
    private val weatherData = mapOf(
        "서울" to WeatherResponse("서울", 15.0, "맑음", 60),
        "부산" to WeatherResponse("부산", 18.0, "흐림", 70),
        "제주" to WeatherResponse("제주", 20.0, "맑음", 65),
        "대구" to WeatherResponse("대구", 16.0, "맑음", 55),
        "인천" to WeatherResponse("인천", 14.0, "흐림", 75),
        "광주" to WeatherResponse("광주", 17.0, "맑음", 58),
        "대전" to WeatherResponse("대전", 15.5, "맑음", 62),
        "수원" to WeatherResponse("수원", 14.5, "흐림", 68)
    )
    
    /**
     * 도시 이름으로 날씨 정보 조회
     */
    fun getWeather(location: String): WeatherResponse {
        return weatherData[location] 
            ?: WeatherResponse(
                location = location,
                temperature = 0.0,
                description = "정보 없음",
                humidity = 0
            )
    }
    
    /**
     * 지원하는 도시 목록 조회
     */
    fun getSupportedLocations(): List<String> {
        return weatherData.keys.toList()
    }
}

