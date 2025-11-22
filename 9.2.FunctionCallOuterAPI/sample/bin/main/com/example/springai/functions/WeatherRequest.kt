package com.example.springai.functions

/**
 * 날씨 조회 함수 요청 데이터 클래스
 */
data class WeatherRequest(
    /**
     * 도시 이름 (예: 서울, 부산, 제주)
     */
    val location: String
)

