package com.example.springai.service

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.ai.chat.client.ChatClient

/** ListMapClientService 테스트 ChatClient를 사용한 리스트/맵 파싱 서비스 테스트 */
@ExtendWith(MockitoExtension::class)
class ListMapClientServiceTest {

    @Mock private lateinit var chatClientBuilder: ChatClient.Builder

    @Mock private lateinit var chatClient: ChatClient

    @Mock private lateinit var chatClientRequestSpec: ChatClient.ChatClientRequestSpec

    @Mock private lateinit var chatClientCallResponseSpec: ChatClient.CallResponseSpec

    private lateinit var listMapClientService: ListMapClientService

    /** ChatClient 테스트 초기화 */
    private fun setupChatClient(expectedResponse: String) {
        whenever(chatClientBuilder.build()).thenReturn(chatClient)
        whenever(chatClient.prompt()).thenReturn(chatClientRequestSpec)
        whenever(chatClientRequestSpec.system(any<String>())).thenReturn(chatClientRequestSpec)
        whenever(chatClientRequestSpec.user(any<String>())).thenReturn(chatClientRequestSpec)
        whenever(chatClientRequestSpec.call()).thenReturn(chatClientCallResponseSpec)
        whenever(chatClientCallResponseSpec.content()).thenReturn(expectedResponse)

        listMapClientService = ListMapClientService(chatClientBuilder)
    }

    /** 기본 리스트 파싱 테스트 */
    @Test
    fun `parseList should parse newline-separated list correctly`() {
        // Given
        val response =
                """
            Python
            Java
            JavaScript
            Kotlin
            Go
        """.trimIndent()
        setupChatClient(response)

        val question = "5가지 프로그래밍 언어를 나열해주세요"

        // When
        val result = listMapClientService.parseList(question)

        // Then
        assertNotNull(result)
        assertEquals(5, result.size)
        assertTrue(result.contains("Python"))
        assertTrue(result.contains("Java"))
        assertTrue(result.contains("JavaScript"))
        assertTrue(result.contains("Kotlin"))
        assertTrue(result.contains("Go"))
    }

    /** CSV 형식 리스트 파싱 테스트 */
    @Test
    fun `parseCsvList should parse comma-separated list correctly`() {
        // Given
        val response = "Python, Java, JavaScript, Kotlin, Go"
        setupChatClient(response)

        val question = "프로그래밍 언어를 쉼표로 구분해서 알려주세요"

        // When
        val result = listMapClientService.parseCsvList(question)

        // Then
        assertNotNull(result)
        assertEquals(5, result.size)
        assertTrue(result.contains("Python"))
        assertTrue(result.contains("Java"))
    }

    /** 맵 파싱 테스트 */
    @Test
    fun `parseMap should parse key-value pairs correctly`() {
        // Given
        val response =
                """
            Python: 간결하고 읽기 쉬운 문법
            Java: 플랫폼 독립적
            JavaScript: 웹 개발의 필수
        """.trimIndent()
        setupChatClient(response)

        val question = "프로그래밍 언어별 특징을 알려주세요"

        // When
        val result = listMapClientService.parseMap(question)

        // Then
        assertNotNull(result)
        assertEquals(3, result.size)
        assertTrue(result.containsKey("Python"))
        assertTrue(result.containsKey("Java"))
        assertTrue(result.containsKey("JavaScript"))
    }

    /** 안전한 리스트 파싱 성공 테스트 */
    @Test
    fun `safeParseList should return success result on valid response`() {
        // Given
        val response =
                """
            Item 1
            Item 2
            Item 3
        """.trimIndent()
        setupChatClient(response)

        val question = "3가지 항목을 나열해주세요"

        // When
        val result = listMapClientService.safeParseList(question)

        // Then
        assertTrue(result.isSuccess)
        val items = result.getOrNull()
        assertNotNull(items)
        assertEquals(3, items.size)
    }

    /** 안전한 맵 파싱 성공 테스트 */
    @Test
    fun `safeParseMap should return success result on valid response`() {
        // Given
        val response =
                """
            key1: value1
            key2: value2
        """.trimIndent()
        setupChatClient(response)

        val question = "키-값 쌍을 제공해주세요"

        // When
        val result = listMapClientService.safeParseMap(question)

        // Then
        assertTrue(result.isSuccess)
        val map = result.getOrNull()
        assertNotNull(map)
        assertEquals(2, map.size)
    }

    /** 빈 응답 처리 테스트 */
    @Test
    fun `parseList should handle empty response`() {
        // Given
        val response = ""
        setupChatClient(response)

        val question = "빈 리스트를 반환해주세요"

        // When
        val result = listMapClientService.parseList(question)

        // Then
        assertNotNull(result)
        assertEquals(0, result.size)
    }

    /** 중복 제거 테스트 */
    @Test
    fun `parseList should remove duplicates`() {
        // Given
        val response =
                """
            Python
            Java
            Python
            JavaScript
            Java
        """.trimIndent()
        setupChatClient(response)

        val question = "프로그래밍 언어를 나열해주세요"

        // When
        val result = listMapClientService.parseList(question)

        // Then
        assertNotNull(result)
        assertEquals(3, result.size) // 중복 제거됨
        assertTrue(result.contains("Python"))
        assertTrue(result.contains("Java"))
        assertTrue(result.contains("JavaScript"))
    }

    /** 공백 처리 테스트 */
    @Test
    fun `parseList should trim whitespace`() {
        // Given
        val response =
                """
            
            Python   
              Java  
            JavaScript
            
        """.trimIndent()
        setupChatClient(response)

        val question = "프로그래밍 언어를 나열해주세요"

        // When
        val result = listMapClientService.parseList(question)

        // Then
        assertNotNull(result)
        assertEquals(3, result.size)
        assertTrue(result.all { it.trim() == it }) // 모든 항목이 trim되어 있음
    }

    /** 커스텀 구분자 테스트 */
    @Test
    fun `parseList should support custom separator`() {
        // Given
        val response = "Python|Java|JavaScript|Kotlin"
        setupChatClient(response)

        val question = "프로그래밍 언어를 파이프로 구분해서 알려주세요"

        // When
        val result = listMapClientService.parseList(question, "|")

        // Then
        assertNotNull(result)
        assertEquals(4, result.size)
        assertTrue(result.contains("Python"))
        assertTrue(result.contains("Kotlin"))
    }

    /** 복잡한 맵 파싱 테스트 */
    @Test
    fun `parseMap should handle complex values`() {
        // Given
        val response =
                """
            Python: 간결하고 읽기 쉬운 문법, 다양한 라이브러리
            Java: 플랫폼 독립적, 엔터프라이즈 애플리케이션에 적합
            JavaScript: 웹 개발의 필수, 프론트엔드와 백엔드 모두 사용 가능
        """.trimIndent()
        setupChatClient(response)

        val question = "프로그래밍 언어별 상세 특징을 알려주세요"

        // When
        val result = listMapClientService.parseMap(question)

        // Then
        assertNotNull(result)
        assertEquals(3, result.size)
        assertTrue(result["Python"]?.contains("간결") == true)
        assertTrue(result["Java"]?.contains("플랫폼") == true)
        assertTrue(result["JavaScript"]?.contains("웹") == true)
    }
}
