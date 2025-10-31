package com.example.springai.service

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.*
import org.springframework.stereotype.Service
import com.example.springai.util.BeanOutputParser
import com.example.springai.util.JsonCleaner

/**
 * 범용 파싱 서비스
 */
@Service
class ParsingService(
    private val chatModel: ChatModel
) {
    /**
     * 제네릭을 사용한 범용 파싱 메서드
     * 
     * 사용 예:
     * val userInfo: UserInfo = parsingService.parseResponse(
     *     UserInfo::class.java,
     *     "사용자 정보를 추출해주세요.",
     *     "사용자 정보 텍스트"
     * )
     */
    fun <T : Any> parseResponse(
        clazz: Class<T>,
        systemMessage: String,
        userMessage: String
    ): T {
        val parser = BeanOutputParser(clazz)
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    $systemMessage
                    
                    응답 형식:
                    $format
                    
                    반드시 위 형식에 맞춰 JSON만 응답해주세요.
                    """.trimIndent()
                ),
                UserMessage(userMessage)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.results.firstOrNull()?.output?.text 
            ?: throw IllegalStateException("응답 없음")
        
        // JSON 정리
        val cleanedJson = com.example.springai.util.JsonCleaner.cleanJsonText(text)
        
        return parser.parse(cleanedJson)
    }
}

