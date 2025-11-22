package com.example.springai.functions

/**
 * 시간 조회 함수 응답 데이터 클래스
 */
data class TimeResponse(
    /**
     * 현재 시간 (형식: yyyy-MM-dd HH:mm:ss)
     */
    val time: String,
    
    /**
     * 사용된 시간대
     */
    val timezone: String
)

