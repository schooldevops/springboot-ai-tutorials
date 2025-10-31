package com.example.springai.util

/**
 * ListOutputParser 구현
 * LLM 응답을 리스트로 파싱하는 파서
 */
class ListOutputParser(
    val separator: String = "\n"
) {
    /**
     * LLM에 전달할 리스트 형식 설명
     */
    val format: String
        get() = """
            각 항목을 $separator 구분자로 구분하여 나열해주세요.
            각 항목은 한 줄에 하나씩 작성해주세요.
            """.trimIndent()
    
    /**
     * 텍스트를 리스트로 파싱
     */
    fun parse(text: String): List<String> {
        return text
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .flatMap { line ->
                // 구분자가 줄바꿈이 아닌 경우 (예: 쉼표)
                if (separator != "\n") {
                    line.split(separator)
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                } else {
                    listOf(line)
                }
            }
            .distinct()
    }
}

