package com.example.springai.util

/**
 * LLM 응답에서 JSON을 추출하고 정리하는 유틸리티
 */
object JsonCleaner {
    /**
     * 텍스트에서 JSON만 추출하고 정리
     */
    fun cleanJsonText(text: String): String {
        return text
            .replace("```json", "")
            .replace("```", "")
            .trim()
            .lines()
            .filter { line ->
                // JSON 주석 제거 (// 로 시작하는 줄)
                !line.trim().startsWith("//") &&
                // 빈 줄 유지
                line.isNotBlank() || line.isEmpty()
            }
            .joinToString("\n")
            .trim()
    }
    
    /**
     * JSON 객체만 추출 (중괄호로 시작하고 끝나는 부분)
     */
    fun extractJsonObject(text: String): String {
        val startIndex = text.indexOf('{')
        val endIndex = text.lastIndexOf('}')
        
        return if (startIndex >= 0 && endIndex > startIndex) {
            text.substring(startIndex, endIndex + 1)
        } else {
            text
        }
    }
}

