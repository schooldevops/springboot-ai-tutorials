package com.example.springai.functions

/**
 * 날씨 조회 함수 응답 데이터 클래스
 */
data class WeatherResponse(
    /**
     * 도시 이름
     */
    val location: String,
    
    /**
     * 온도 (섭씨)
     */
    val temperature: Double,
    
    /**
     * 날씨 설명
     */
    val description: String,
    
    /**
     * 습도 (%)
     */
    val humidity: Int
)

