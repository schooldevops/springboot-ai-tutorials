package com.example.springai.functions

/**
 * 시간 조회 함수 요청 데이터 클래스
 */
data class TimeRequest(
    /**
     * 시간대 (예: 'Asia/Seoul', 'America/New_York'). 선택사항입니다.
     */
    val timezone: String? = null
)

