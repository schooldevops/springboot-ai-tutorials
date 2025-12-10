package com.example.springai.service

import com.example.springai.util.BeanOutputParser
import com.example.springai.util.JsonCleaner
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

/** ChatClient를 사용한 범용 파싱 서비스 ChatModel 대신 더 현대적인 ChatClient API를 사용 */
@Service
class ParsingClientService(private val chatClientBuilder: ChatClient.Builder) {
    private val chatClient = chatClientBuilder.build()

    /**
     * 제네릭을 사용한 범용 파싱 메서드 (ChatClient 사용)
     *
     * 사용 예: val userInfo: UserInfo = parsingClientService.parseResponse(
     * ```
     *     UserInfo::class.java,
     *     "사용자 정보를 추출해주세요.",
     *     "사용자 정보 텍스트"
     * ```
     * )
     */
    fun <T : Any> parseResponse(clazz: Class<T>, systemMessage: String, userMessage: String): T {
        val parser = BeanOutputParser(clazz)
        val format = parser.format

        val systemPrompt =
                """
            $systemMessage
            
            응답 형식:
            $format
            
            반드시 위 형식에 맞춰 JSON만 응답해주세요.
        """.trimIndent()

        val response =
                chatClient.prompt().system(systemPrompt).user(userMessage).call().content()
                        ?: throw IllegalStateException("응답 없음")

        // JSON 정리
        val cleanedJson = JsonCleaner.cleanJsonText(response)

        return parser.parse(cleanedJson)
    }

    /** 간단한 파싱 메서드 (시스템 메시지 기본값 사용) */
    fun <T : Any> parse(clazz: Class<T>, userMessage: String): T {
        return parseResponse(clazz, "다음 텍스트에서 정보를 추출해주세요.", userMessage)
    }
}
