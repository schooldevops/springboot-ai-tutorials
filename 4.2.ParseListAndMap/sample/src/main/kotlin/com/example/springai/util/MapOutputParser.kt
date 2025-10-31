package com.example.springai.util

/**
 * MapOutputParser 구현
 * LLM 응답을 Key-Value 맵으로 파싱하는 파서
 */
class MapOutputParser(
    val separator: String = ":"
) {
    /**
     * LLM에 전달할 맵 형식 설명
     */
    val format: String
        get() = """
            Key$separator Value 형식으로 응답해주세요.
            각 Key-Value 쌍은 줄바꿈으로 구분해주세요.
            예시:
            Key1$separator Value1
            Key2$separator Value2
            """.trimIndent()
    
    /**
     * 텍스트를 Map으로 파싱
     */
    fun parse(text: String): Map<String, String> {
        return text
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() && it.contains(separator) }
            .associate { line ->
                val parts = line.split(separator, limit = 2)
                val key = parts[0].trim()
                val value = if (parts.size > 1) parts[1].trim() else ""
                key to value
            }
    }
}

