package com.example.springai.util

import com.fasterxml.jackson.databind.ObjectMapper

/**
 * BeanOutputParser를 직접 구현
 * Spring AI 1.0.0-M6에서는 BeanOutputParser가 기본 포함되지 않을 수 있으므로
 * Jackson을 사용하여 직접 구현
 */
class BeanOutputParser<T : Any>(
    private val clazz: Class<T>,
    private val objectMapper: ObjectMapper = com.fasterxml.jackson.databind.ObjectMapper()
        .findAndRegisterModules()
) {
    /**
     * LLM에 전달할 JSON 스키마 형식 문자열
     */
    val format: String
        get() {
            // 간단한 JSON 스키마 생성
            val schema = generateJsonSchema(clazz)
            return """
            {
              ${schema.lines().joinToString("\n  ") { "  $it" }}
            }
            """.trimIndent()
        }
    
    /**
     * JSON 문자열을 객체로 파싱
     */
    fun parse(jsonText: String): T {
        // JSON 정리
        val cleanedJson = JsonCleaner.cleanJsonText(jsonText)
        
        return try {
            @Suppress("UNCHECKED_CAST")
            objectMapper.readValue(cleanedJson, clazz) as T
        } catch (e: Exception) {
            throw IllegalArgumentException("JSON 파싱 실패: ${e.message}", e)
        }
    }
    
    /**
     * 간단한 JSON 스키마 생성 (기본 타입만 지원)
     */
    private fun generateJsonSchema(clazz: Class<T>): String {
        val fields = clazz.declaredFields
        val schemaFields = fields.mapNotNull { field ->
            val fieldName = field.name
            val fieldType = when {
                field.type == String::class.java -> "\"$fieldName\": \"string\""
                field.type == Int::class.java || field.type == Integer::class.java -> "\"$fieldName\": \"integer\""
                field.type == Long::class.java || field.type == java.lang.Long::class.java -> "\"$fieldName\": \"integer\""
                field.type == Double::class.java || field.type == java.lang.Double::class.java -> "\"$fieldName\": \"number\""
                field.type == Boolean::class.java || field.type == java.lang.Boolean::class.java -> "\"$fieldName\": \"boolean\""
                List::class.java.isAssignableFrom(field.type) -> "\"$fieldName\": \"array\""
                else -> null // 복잡한 타입은 생략
            }
            fieldType
        }
        return schemaFields.joinToString(",\n")
    }
}

