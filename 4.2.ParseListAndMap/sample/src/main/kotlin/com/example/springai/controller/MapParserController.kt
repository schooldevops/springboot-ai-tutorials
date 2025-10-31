package com.example.springai.controller

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.*
import org.springframework.web.bind.annotation.*
import com.example.springai.model.ParseRequest
import com.example.springai.util.MapOutputParser

/**
 * MapOutputParser의 기본 사용법을 보여주는 컨트롤러
 */
@RestController
@RequestMapping("/api/map-parser")
class MapParserController(
    private val chatModel: ChatModel
) {
    
    /**
     * 기본 맵 파싱 예제
     * POST http://localhost:8080/api/map-parser/parse
     * Body: {"question": "프로그래밍 언어별 특징을 Key-Value 형식으로 제공해주세요"}
     */
    @PostMapping("/parse")
    fun parseMap(@RequestBody request: ParseRequest): Map<String, Any> {
        val parser = MapOutputParser()
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    다음 형식으로 응답해주세요:
                    $format
                    """.trimIndent()
                ),
                UserMessage(request.question)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.results.firstOrNull()?.output?.text 
            ?: throw IllegalStateException("응답 없음")
        
        val resultMap = parser.parse(text)
        
        return mapOf(
            "data" to resultMap,
            "count" to resultMap.size
        )
    }
    
    /**
     * 안전한 맵 파싱 (에러 처리 포함)
     * POST http://localhost:8080/api/map-parser/safe
     */
    @PostMapping("/safe")
    fun safeParseMap(@RequestBody request: ParseRequest): Map<String, Any> {
        val parser = MapOutputParser()
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage("다음 형식으로 응답해주세요:\n$format"),
                UserMessage(request.question)
            )
        )
        
        return try {
            val response = chatModel.call(prompt)
            val text = response.results.firstOrNull()?.output?.text 
                ?: throw IllegalStateException("응답 없음")
            
            val resultMap = parser.parse(text)
            
            mapOf(
                "success" to true,
                "data" to resultMap,
                "count" to resultMap.size
            )
        } catch (e: Exception) {
            mapOf(
                "success" to false,
                "error" to (e.message ?: "알 수 없는 오류")
            )
        }
    }
}

