package com.example.springai.service

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.ai.chat.client.ChatClient

/** RoleClientService 테스트 ChatClient를 사용한 역할 기반 메시지 서비스 테스트 */
@ExtendWith(MockitoExtension::class)
class RoleClientServiceTest {

    @Mock private lateinit var chatClientBuilder: ChatClient.Builder

    @Mock private lateinit var chatClient: ChatClient

    @Mock private lateinit var chatClientRequestSpec: ChatClient.ChatClientRequestSpec

    @Mock private lateinit var chatClientCallResponseSpec: ChatClient.CallResponseSpec

    private lateinit var roleClientService: RoleClientService

    /** 최소한의 서비스 초기화 (SystemMessage 생성만 필요한 경우) */
    private fun setupService() {
        whenever(chatClientBuilder.build()).thenReturn(chatClient)
        roleClientService = RoleClientService(chatClientBuilder)
    }

    /** ChatClient 테스트 초기화 (chat 메서드 호출이 필요한 경우) */
    private fun setupChatClient(expectedResponse: String = "") {
        whenever(chatClientBuilder.build()).thenReturn(chatClient)
        whenever(chatClient.prompt()).thenReturn(chatClientRequestSpec)
        whenever(chatClientRequestSpec.system(any<String>())).thenReturn(chatClientRequestSpec)
        whenever(chatClientRequestSpec.user(any<String>())).thenReturn(chatClientRequestSpec)
        whenever(chatClientRequestSpec.call()).thenReturn(chatClientCallResponseSpec)
        whenever(chatClientCallResponseSpec.content()).thenReturn(expectedResponse)

        // Initialize service with mocked builder
        roleClientService = RoleClientService(chatClientBuilder)
    }

    /** Teacher 역할 SystemMessage 생성 테스트 */
    @Test
    fun `createSystemMessage should create teacher role message`() {
        // Given
        setupService()

        // When
        val systemMessage = roleClientService.createSystemMessage("teacher")

        // Then
        assertNotNull(systemMessage)
        assert(systemMessage.text.contains("선생님"))
        assert(systemMessage.text.contains("쉽게 설명"))
    }

    /** Doctor 역할 SystemMessage 생성 테스트 */
    @Test
    fun `createSystemMessage should create doctor role message`() {
        // Given
        setupService()

        // When
        val systemMessage = roleClientService.createSystemMessage("doctor")

        // Then
        assertNotNull(systemMessage)
        assert(systemMessage.text.contains("의사"))
        assert(systemMessage.text.contains("의학적"))
    }

    /** Chef 역할 SystemMessage 생성 테스트 */
    @Test
    fun `createSystemMessage should create chef role message`() {
        // Given
        setupService()

        // When
        val systemMessage = roleClientService.createSystemMessage("chef")

        // Then
        assertNotNull(systemMessage)
        assert(systemMessage.text.contains("셰프"))
        assert(systemMessage.text.contains("요리"))
    }

    /** Developer 역할 SystemMessage 생성 테스트 */
    @Test
    fun `createSystemMessage should create developer role message`() {
        // Given
        setupService()

        // When
        val systemMessage = roleClientService.createSystemMessage("developer")

        // Then
        assertNotNull(systemMessage)
        assert(systemMessage.text.contains("개발자"))
        assert(systemMessage.text.contains("코드"))
    }

    /** 기본 역할 SystemMessage 생성 테스트 */
    @Test
    fun `createSystemMessage should create default role message for unknown role`() {
        // Given
        setupService()

        // When
        val systemMessage = roleClientService.createSystemMessage("unknown")

        // Then
        assertNotNull(systemMessage)
        assertEquals("당신은 도움이 되는 어시스턴트입니다.", systemMessage.text)
    }

    /** 커스텀 SystemMessage 생성 테스트 */
    @Test
    fun `createCustomSystemMessage should create custom role message`() {
        // Given
        setupService()
        val role = "시인"
        val instructions = "아름다운 시를 작성해주세요."
        val principles = listOf("운율을 살리기", "감성적으로 표현하기")

        // When
        val systemMessage =
                roleClientService.createCustomSystemMessage(role, instructions, principles)

        // Then
        assertNotNull(systemMessage)
        assert(systemMessage.text.contains(role))
        assert(systemMessage.text.contains(instructions))
        assert(systemMessage.text.contains("운율을 살리기"))
        assert(systemMessage.text.contains("감성적으로 표현하기"))
    }

    /** Teacher 역할 채팅 테스트 */
    @Test
    fun `chat should return response for teacher role`() {
        // Given
        val expectedResponse = "수학은 논리적 사고를 기르는 데 중요합니다. 매일 꾸준히 연습하는 것이 좋습니다."
        setupChatClient(expectedResponse)

        val role = "teacher"
        val message = "수학을 어떻게 공부하면 좋을까요?"

        // When
        val result = roleClientService.chat(role, message)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse, result)
    }

    /** Doctor 역할 채팅 테스트 */
    @Test
    fun `chat should return response for doctor role`() {
        // Given
        val expectedResponse = "두통은 다양한 원인이 있을 수 있습니다. 충분한 수면과 수분 섭취가 중요하며, 증상이 지속되면 의료진과 상담하세요."
        setupChatClient(expectedResponse)

        val role = "doctor"
        val message = "두통이 있을 때 어떻게 해야 하나요?"

        // When
        val result = roleClientService.chat(role, message)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse, result)
    }

    /** Chef 역할 채팅 테스트 */
    @Test
    fun `chat should return response for chef role`() {
        // Given
        val expectedResponse = "파스타는 간단하면서도 맛있는 요리입니다. 신선한 재료와 적절한 소스가 중요합니다."
        setupChatClient(expectedResponse)

        val role = "chef"
        val message = "파스타를 맛있게 만드는 방법을 알려주세요."

        // When
        val result = roleClientService.chat(role, message)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse, result)
    }

    /** Developer 역할 채팅 테스트 */
    @Test
    fun `chat should return response for developer role`() {
        // Given
        val expectedResponse =
                """
            Spring Boot에서 REST API를 만들려면 @RestController를 사용합니다.
            
            예제:
            @RestController
            class MyController {
                @GetMapping("/api/hello")
                fun hello() = "Hello World"
            }
        """.trimIndent()
        setupChatClient(expectedResponse)

        val role = "developer"
        val message = "Spring Boot에서 REST API를 어떻게 만드나요?"

        // When
        val result = roleClientService.chat(role, message)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse, result)
    }

    /** 커스텀 역할 채팅 테스트 */
    @Test
    fun `customChat should return response for custom role`() {
        // Given
        val expectedResponse =
                """
            봄의 향기
            
            따스한 햇살 아래
            꽃잎이 춤을 추고
            새들의 노래가
            마음을 적시네
        """.trimIndent()
        setupChatClient(expectedResponse)

        val role = "시인"
        val instructions = "아름다운 시를 작성해주세요."
        val principles = listOf("운율을 살리기", "감성적으로 표현하기")
        val message = "봄에 대한 시를 써주세요"

        // When
        val result = roleClientService.customChat(role, instructions, principles, message)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse, result)
    }

    /** null 응답 처리 테스트 */
    @Test
    fun `chat should handle null response gracefully`() {
        // Given
        setupChatClient("")
        whenever(chatClientCallResponseSpec.content()).thenReturn(null)

        val role = "teacher"
        val message = "테스트 메시지"

        // When
        val result = roleClientService.chat(role, message)

        // Then
        assertEquals("응답 없음", result)
    }

    /** 대소문자 구분 없는 역할 처리 테스트 */
    @Test
    fun `createSystemMessage should be case insensitive`() {
        // Given
        setupService()

        // When
        val upperCase = roleClientService.createSystemMessage("TEACHER")
        val lowerCase = roleClientService.createSystemMessage("teacher")
        val mixedCase = roleClientService.createSystemMessage("TeAcHeR")

        // Then
        assertEquals(upperCase.text, lowerCase.text)
        assertEquals(lowerCase.text, mixedCase.text)
    }

    /** 빈 principles 리스트 처리 테스트 */
    @Test
    fun `createCustomSystemMessage should handle empty principles list`() {
        // Given
        setupService()
        val role = "코치"
        val instructions = "동기부여를 해주세요."
        val principles = emptyList<String>()

        // When
        val systemMessage =
                roleClientService.createCustomSystemMessage(role, instructions, principles)

        // Then
        assertNotNull(systemMessage)
        assert(systemMessage.text.contains(role))
        assert(systemMessage.text.contains(instructions))
    }

    /** 여러 principles 처리 테스트 */
    @Test
    fun `createCustomSystemMessage should handle multiple principles`() {
        // Given
        setupService()
        val role = "멘토"
        val instructions = "경력 개발에 대해 조언해주세요."
        val principles = listOf("실용적인 조언 제공", "개인의 상황 고려", "구체적인 예시 사용", "긍정적인 태도 유지")

        // When
        val systemMessage =
                roleClientService.createCustomSystemMessage(role, instructions, principles)

        // Then
        assertNotNull(systemMessage)
        principles.forEach { principle -> assert(systemMessage.text.contains(principle)) }
    }
}
