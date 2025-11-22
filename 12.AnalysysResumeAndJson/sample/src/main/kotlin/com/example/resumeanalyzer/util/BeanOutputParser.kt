package com.example.resumeanalyzer.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.lang.reflect.ParameterizedType

/**
 * BeanOutputParser - LLM 응답을 Kotlin 객체로 변환
 */
class BeanOutputParser<T>(private val clazz: Class<T>) {
    
    private val objectMapper = ObjectMapper().apply {
        registerKotlinModule()
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
    }
    
    /**
     * JSON 스키마 형식 문자열 생성
     */
    val format: String
        get() = generateJsonSchema(clazz)
    
    /**
     * LLM 응답 텍스트를 객체로 파싱
     */
    fun parse(text: String): T {
        val cleanedJson = cleanJsonResponse(text)
        return try {
            objectMapper.readValue(cleanedJson, clazz)
        } catch (e: Exception) {
            throw IllegalArgumentException("JSON 파싱 실패: ${e.message}\n원본 텍스트: $cleanedJson", e)
        }
    }
    
    /**
     * JSON 응답에서 불필요한 마크다운 코드 블록 제거
     */
    private fun cleanJsonResponse(text: String): String {
        var cleaned = text.trim()
        
        // ```json ... ``` 형태 제거
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.removePrefix("```json").removeSuffix("```").trim()
        } 
        // ``` ... ``` 형태 제거
        else if (cleaned.startsWith("```")) {
            cleaned = cleaned.removePrefix("```").removeSuffix("```").trim()
        }
        
        // 첫 번째 { 부터 마지막 } 까지만 추출 (배열인 경우 [])
        val startIndex = cleaned.indexOfFirst { it == '{' || it == '[' }
        val endIndex = cleaned.indexOfLast { it == '}' || it == ']' }
        
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            cleaned = cleaned.substring(startIndex, endIndex + 1)
        }
        
        return cleaned
    }
    
    /**
     * 클래스 구조를 기반으로 간단한 JSON 스키마 생성
     */
    private fun generateJsonSchema(clazz: Class<*>): String {
        val fields = clazz.declaredFields
        val schema = StringBuilder()
        schema.append("{\n")
        
        fields.forEachIndexed { index, field ->
            val fieldName = field.name
            val fieldType = getFieldType(field.type, field.genericType)
            
            schema.append("  \"$fieldName\": $fieldType")
            if (index < fields.size - 1) {
                schema.append(",")
            }
            schema.append("\n")
        }
        
        schema.append("}")
        return schema.toString()
    }
    
    /**
     * 필드 타입을 JSON 스키마 형식으로 변환
     */
    private fun getFieldType(type: Class<*>, genericType: java.lang.reflect.Type): String {
        return when {
            type == String::class.java -> "\"string\""
            type == Int::class.java || type == Integer::class.java -> "number"
            type == Long::class.java -> "number"
            type == Double::class.java -> "number"
            type == Boolean::class.java -> "boolean"
            type == List::class.java || type == ArrayList::class.java -> {
                if (genericType is ParameterizedType) {
                    val elementType = genericType.actualTypeArguments[0] as? Class<*>
                    if (elementType != null && !elementType.isPrimitive && elementType != String::class.java) {
                        "[${generateJsonSchema(elementType)}]"
                    } else {
                        "[]"
                    }
                } else {
                    "[]"
                }
            }
            type.isArray -> "[]"
            else -> generateJsonSchema(type)
        }
    }
}
