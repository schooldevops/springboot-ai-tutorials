package com.example.springai.service

import com.example.springai.util.ListOutputParser
import com.example.springai.util.MapOutputParser
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

/** ChatClient를 사용한 리스트/맵 파싱 서비스 ChatModel 대신 더 현대적인 ChatClient API를 사용 */
@Service
class ListMapClientService(private val chatClientBuilder: ChatClient.Builder) {
    private val chatClient = chatClientBuilder.build()

    /** 리스트 파싱 (기본 구분자: 줄바꿈) */
    fun parseList(question: String, separator: String = "\n"): List<String> {
        val parser = ListOutputParser(separator)
        val format = parser.format

        val systemPrompt =
                """
            다음 형식으로 응답해주세요:
            $format
        """.trimIndent()

        val response =
                chatClient.prompt().system(systemPrompt).user(question).call().content()
                        ?: throw IllegalStateException("응답 없음")

        return parser.parse(response)
    }

    /** CSV 형식 리스트 파싱 (쉼표 구분) */
    fun parseCsvList(question: String): List<String> {
        return parseList(question, ",")
    }

    /** 맵 파싱 (Key-Value 형식) */
    fun parseMap(question: String): Map<String, String> {
        val parser = MapOutputParser()
        val format = parser.format

        val systemPrompt =
                """
            다음 형식으로 응답해주세요:
            $format
        """.trimIndent()

        val response =
                chatClient.prompt().system(systemPrompt).user(question).call().content()
                        ?: throw IllegalStateException("응답 없음")

        return parser.parse(response)
    }

    /** 안전한 리스트 파싱 (에러 처리 포함) */
    fun safeParseList(question: String, separator: String = "\n"): Result<List<String>> {
        return try {
            val result = parseList(question, separator)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 안전한 맵 파싱 (에러 처리 포함) */
    fun safeParseMap(question: String): Result<Map<String, String>> {
        return try {
            val result = parseMap(question)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
