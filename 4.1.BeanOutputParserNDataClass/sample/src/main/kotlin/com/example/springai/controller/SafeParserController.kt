package com.example.springai.controller

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.*
import org.springframework.web.bind.annotation.*
import com.example.springai.model.UserInfo
import com.example.springai.model.ParseRequest
import com.example.springai.util.BeanOutputParser
import com.example.springai.util.JsonCleaner

/**
 * 안전한 파싱을 위한 컨트롤러 (에러 처리 포함)
 */
@RestController
@RequestMapping("/api/safe-parser")
class SafeParserController(
    private val chatModel: ChatModel
) {
    
    /**
     * 안전한 파싱 예제 (에러 처리 포함)
     * POST http://localhost:8080/api/safe-parser/parse
     */
    @PostMapping("/parse")
    fun safeParse(@RequestBody request: ParseRequest): Map<String, Any> {
        val parser = BeanOutputParser(UserInfo::class.java)
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    다음 JSON 형식으로 응답해주세요:
                    $format
                    
                    응답은 JSON만 포함하고 다른 텍스트는 포함하지 마세요.
                    """.trimIndent()
                ),
                UserMessage(request.question)
            )
        )
        
        return try {
            val response = chatModel.call(prompt)
            val text = response.results.firstOrNull()?.output?.text 
                ?: throw IllegalStateException("응답 없음")
            
            // JSON 정리 (마크다운 코드 블록 제거 등)
            val cleanedJson = JsonCleaner.cleanJsonText(text)
            
            // 파싱 시도
            val userInfo = parser.parse(cleanedJson)
            
            mapOf(
                "success" to true,
                "data" to userInfo
            )
        } catch (e: Exception) {
            mapOf(
                "success" to false,
                "error" to (e.message ?: "알 수 없는 오류"),
                "errorType" to e.javaClass.simpleName
            )
        }
    }
}

