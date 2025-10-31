package com.example.springai.controller

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.*
import org.springframework.web.bind.annotation.*
import com.example.springai.model.ParseRequest
import com.example.springai.model.CategoryItem
import com.example.springai.util.ListOutputParser
import com.example.springai.util.MapOutputParser

/**
 * 복합 형식 파싱 예제 (리스트와 맵 조합)
 */
@RestController
@RequestMapping("/api/complex-parser")
class ComplexParserController(
    private val chatModel: ChatModel
) {
    
    /**
     * 리스트와 맵을 함께 파싱
     * POST http://localhost:8080/api/complex-parser/combined
     */
    @PostMapping("/combined")
    fun parseCombined(@RequestBody request: ParseRequest): Map<String, Any> {
        val listParser = ListOutputParser()
        val listFormat = listParser.format
        
        val mapParser = MapOutputParser()
        val mapFormat = mapParser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    다음 형식으로 응답해주세요:
                    
                    1. 카테고리 목록:
                    $listFormat
                    
                    2. 카테고리별 세부사항:
                    $mapFormat
                    
                    위 두 부분을 구분하여 작성해주세요.
                    """.trimIndent()
                ),
                UserMessage(request.question)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.results.firstOrNull()?.output?.text 
            ?: throw IllegalStateException("응답 없음")
        
        // 텍스트를 두 부분으로 분리하여 파싱
        val parts = text.split("\n\n").filter { it.trim().isNotEmpty() }
        
        val categories = if (parts.isNotEmpty()) {
            listParser.parse(parts[0])
        } else {
            emptyList()
        }
        
        val details = if (parts.size > 1) {
            mapParser.parse(parts[1])
        } else {
            emptyMap()
        }
        
        return mapOf(
            "categories" to categories,
            "details" to details,
            "categoryCount" to categories.size,
            "detailCount" to details.size
        )
    }
    
    /**
     * 구조화된 카테고리별 데이터 파싱
     * POST http://localhost:8080/api/complex-parser/structured
     */
    @PostMapping("/structured")
    fun parseStructured(@RequestBody request: ParseRequest): Map<String, Any> {
        val parser = ListOutputParser()
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    카테고리별로 항목을 나열해주세요.
                    각 카테고리는 다음과 같은 형식으로:
                    
                    카테고리명:
                    $format
                    """.trimIndent()
                ),
                UserMessage(request.question)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.results.firstOrNull()?.output?.text 
            ?: throw IllegalStateException("응답 없음")
        
        // 카테고리별로 분리하여 파싱
        val categories = mutableListOf<CategoryItem>()
        val lines = text.lines()
        var currentCategory = ""
        var currentItems = mutableListOf<String>()
        
        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.endsWith(":")) {
                if (currentCategory.isNotEmpty()) {
                    categories.add(CategoryItem(currentCategory, currentItems))
                }
                currentCategory = trimmedLine.removeSuffix(":").trim()
                currentItems = mutableListOf()
            } else if (trimmedLine.isNotEmpty()) {
                currentItems.add(trimmedLine)
            }
        }
        
        if (currentCategory.isNotEmpty()) {
            categories.add(CategoryItem(currentCategory, currentItems))
        }
        
        return mapOf(
            "categories" to categories.map { mapOf(
                "name" to it.name,
                "items" to it.items,
                "itemCount" to it.items.size
            )},
            "totalCategories" to categories.size
        )
    }
}

