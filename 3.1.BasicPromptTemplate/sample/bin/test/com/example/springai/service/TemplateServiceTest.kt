package com.example.springai.service

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.model.Generation
import org.springframework.ai.chat.prompt.Prompt

/** TemplateService와 TemplateClientService의 테스트 ChatModel과 ChatClient 두 가지 접근 방식을 모두 테스트 */
@ExtendWith(MockitoExtension::class)
class TemplateServiceTest {

    @Mock private lateinit var chatModel: ChatModel

    @InjectMocks private lateinit var templateService: TemplateService

    // ChatClient 테스트를 위한 Mock
    @Mock private lateinit var chatClientBuilder: ChatClient.Builder

    @Mock private lateinit var chatClient: ChatClient

    @Mock private lateinit var chatClientRequestSpec: ChatClient.ChatClientRequestSpec

    @Mock private lateinit var chatClientCallResponseSpec: ChatClient.CallResponseSpec

    private lateinit var templateClientService: TemplateClientService

    /** ChatModel 기반 테스트 - 인사말 생성 */
    @Test
    fun `ChatModel - generateGreeting should return greeting message`() {
        // Given
        val name = "홍길동"
        val expectedResponse = "안녕하세요 홍길동님! 좋은 하루 되세요!"

        val mockGeneration = Generation(AssistantMessage(expectedResponse))
        val mockChatResponse = ChatResponse(listOf(mockGeneration))

        whenever(chatModel.call(any<Prompt>())).thenReturn(mockChatResponse)

        // When
        val result = templateService.generateGreeting(name)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse, result)
    }

    /** ChatModel 기반 테스트 - 질문 답변 (컨텍스트 없음) */
    @Test
    fun `ChatModel - answerQuestion without context should return answer`() {
        // Given
        val userName = "김철수"
        val question = "Spring AI가 무엇인가요?"
        val expectedResponse = "Spring AI는 Spring 프레임워크에서 AI 기능을 쉽게 통합할 수 있도록 도와주는 라이브러리입니다."

        val mockGeneration = Generation(AssistantMessage(expectedResponse))
        val mockChatResponse = ChatResponse(listOf(mockGeneration))

        whenever(chatModel.call(any<Prompt>())).thenReturn(mockChatResponse)

        // When
        val result = templateService.answerQuestion(userName, question)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse, result)
    }

    /** ChatModel 기반 테스트 - 질문 답변 (컨텍스트 포함) */
    @Test
    fun `ChatModel - answerQuestion with context should return contextual answer`() {
        // Given
        val userName = "이영희"
        val question = "어떻게 사용하나요?"
        val context = "Spring Boot 3.0 환경에서"
        val expectedResponse = "Spring Boot 3.0에서는 의존성을 추가하고 설정을 통해 사용할 수 있습니다."

        val mockGeneration = Generation(AssistantMessage(expectedResponse))
        val mockChatResponse = ChatResponse(listOf(mockGeneration))

        whenever(chatModel.call(any<Prompt>())).thenReturn(mockChatResponse)

        // When
        val result = templateService.answerQuestion(userName, question, context)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse, result)
    }

    /** ChatModel 기반 테스트 - 내용 요약 */
    @Test
    fun `ChatModel - summarize should return summary`() {
        // Given
        val content =
                """
            Spring AI는 인공지능 기능을 Spring 애플리케이션에 통합하기 위한 프레임워크입니다.
            다양한 AI 모델과의 통합을 지원하며, 프롬프트 템플릿, 벡터 저장소, 
            임베딩 등의 기능을 제공합니다.
        """.trimIndent()
        val expectedResponse =
                "Spring AI는 AI 기능을 Spring에 통합하는 프레임워크로, 다양한 AI 모델 지원과 프롬프트 템플릿 등의 기능을 제공합니다."

        val mockGeneration = Generation(AssistantMessage(expectedResponse))
        val mockChatResponse = ChatResponse(listOf(mockGeneration))

        whenever(chatModel.call(any<Prompt>())).thenReturn(mockChatResponse)

        // When
        val result = templateService.summarize(content)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse, result)
    }

    /** ChatModel 기반 테스트 - 프롬프트 생성 확인 */
    @Test
    fun `ChatModel - createGreetingPrompt should create valid prompt`() {
        // Given
        val name = "박민수"

        // When
        val prompt = templateService.createGreetingPrompt(name)

        // Then
        assertNotNull(prompt)
        assertNotNull(prompt.instructions)
    }

    /** ChatModel 기반 테스트 - 질문 프롬프트 생성 확인 */
    @Test
    fun `ChatModel - createQuestionPrompt should create valid prompt`() {
        // Given
        val userName = "최지원"
        val question = "테스트 질문입니다"
        val context = "테스트 컨텍스트"

        // When
        val prompt = templateService.createQuestionPrompt(userName, question, context)

        // Then
        assertNotNull(prompt)
        assertNotNull(prompt.instructions)
    }

    /** ChatModel 기반 테스트 - 요약 프롬프트 생성 확인 */
    @Test
    fun `ChatModel - createSummaryPrompt should create valid prompt`() {
        // Given
        val content = "요약할 내용입니다."

        // When
        val prompt = templateService.createSummaryPrompt(content)

        // Then
        assertNotNull(prompt)
        assertNotNull(prompt.instructions)
    }

    // ============================================
    // ChatClient 기반 테스트
    // ============================================

    /** ChatClient 테스트 초기화 */
    private fun setupChatClient() {
        whenever(chatClientBuilder.build()).thenReturn(chatClient)
        whenever(chatClient.prompt()).thenReturn(chatClientRequestSpec)
        whenever(
                        chatClientRequestSpec.user(
                                any<java.util.function.Consumer<ChatClient.PromptUserSpec>>()
                        )
                )
                .thenReturn(chatClientRequestSpec)
        whenever(chatClientRequestSpec.call()).thenReturn(chatClientCallResponseSpec)

        templateClientService = TemplateClientService(chatClientBuilder)
    }

    /** ChatClient 기반 테스트 - 인사말 생성 */
    @Test
    fun `ChatClient - generateGreeting should return greeting message`() {
        // Given
        setupChatClient()
        val name = "홍길동"
        val expectedResponse = "안녕하세요 홍길동님! 좋은 하루 되세요!"

        whenever(chatClientCallResponseSpec.content()).thenReturn(expectedResponse)

        // When
        val result = templateClientService.generateGreeting(name)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse, result)
    }

    /** ChatClient 기반 테스트 - 질문 답변 (컨텍스트 없음) */
    @Test
    fun `ChatClient - answerQuestion without context should return answer`() {
        // Given
        setupChatClient()
        val userName = "김철수"
        val question = "Spring AI가 무엇인가요?"
        val expectedResponse = "Spring AI는 Spring 프레임워크에서 AI 기능을 쉽게 통합할 수 있도록 도와주는 라이브러리입니다."

        whenever(chatClientCallResponseSpec.content()).thenReturn(expectedResponse)

        // When
        val result = templateClientService.answerQuestion(userName, question)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse, result)
    }

    /** ChatClient 기반 테스트 - 질문 답변 (컨텍스트 포함) */
    @Test
    fun `ChatClient - answerQuestion with context should return contextual answer`() {
        // Given
        setupChatClient()
        val userName = "이영희"
        val question = "어떻게 사용하나요?"
        val context = "Spring Boot 3.0 환경에서"
        val expectedResponse = "Spring Boot 3.0에서는 의존성을 추가하고 설정을 통해 사용할 수 있습니다."

        whenever(chatClientCallResponseSpec.content()).thenReturn(expectedResponse)

        // When
        val result = templateClientService.answerQuestion(userName, question, context)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse, result)
    }

    /** ChatClient 기반 테스트 - 내용 요약 */
    @Test
    fun `ChatClient - summarize should return summary`() {
        // Given
        setupChatClient()
        val content =
                """
            Spring AI는 인공지능 기능을 Spring 애플리케이션에 통합하기 위한 프레임워크입니다.
            다양한 AI 모델과의 통합을 지원하며, 프롬프트 템플릿, 벡터 저장소, 
            임베딩 등의 기능을 제공합니다.
        """.trimIndent()
        val expectedResponse =
                "Spring AI는 AI 기능을 Spring에 통합하는 프레임워크로, 다양한 AI 모델 지원과 프롬프트 템플릿 등의 기능을 제공합니다."

        whenever(chatClientCallResponseSpec.content()).thenReturn(expectedResponse)

        // When
        val result = templateClientService.summarize(content)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse, result)
    }

    /** ChatClient 기반 테스트 - 빈 응답 처리 */
    @Test
    fun `ChatClient - should handle null response gracefully`() {
        // Given
        setupChatClient()
        val name = "테스트"

        whenever(chatClientCallResponseSpec.content()).thenReturn(null)

        // When
        val result = templateClientService.generateGreeting(name)

        // Then
        assertEquals("응답 없음", result)
    }

    /** ChatModel 기반 테스트 - 빈 응답 처리 */
    @Test
    fun `ChatModel - should handle empty response gracefully`() {
        // Given
        val name = "테스트"
        val mockChatResponse = ChatResponse(emptyList())

        whenever(chatModel.call(any<Prompt>())).thenReturn(mockChatResponse)

        // When
        val result = templateService.generateGreeting(name)

        // Then
        assertEquals("응답 없음", result)
    }

    /** ChatClient 기반 테스트 - 특수 문자 처리 */
    @Test
    fun `ChatClient - should handle special characters in name`() {
        // Given
        setupChatClient()
        val name = "홍길동 (Hong Gil-Dong)"
        val expectedResponse = "안녕하세요 홍길동 (Hong Gil-Dong)님!"

        whenever(chatClientCallResponseSpec.content()).thenReturn(expectedResponse)

        // When
        val result = templateClientService.generateGreeting(name)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse, result)
    }

    /** ChatModel 기반 테스트 - 긴 컨텐츠 요약 */
    @Test
    fun `ChatModel - should summarize long content`() {
        // Given
        val longContent =
                """
            Spring Framework는 Java 플랫폼을 위한 오픈 소스 애플리케이션 프레임워크입니다.
            의존성 주입, 관점 지향 프로그래밍, 트랜잭션 관리 등의 기능을 제공합니다.
            Spring Boot는 Spring 기반 애플리케이션을 빠르게 개발할 수 있도록 도와줍니다.
            자동 설정과 스타터 의존성을 통해 개발자의 생산성을 높입니다.
            Spring AI는 이러한 Spring 생태계에 AI 기능을 통합합니다.
        """.trimIndent()
        val expectedResponse =
                "Spring은 Java 프레임워크로 DI, AOP 등을 제공하며, Spring Boot로 빠른 개발이 가능합니다. Spring AI는 AI 기능을 통합합니다."

        val mockGeneration = Generation(AssistantMessage(expectedResponse))
        val mockChatResponse = ChatResponse(listOf(mockGeneration))

        whenever(chatModel.call(any<Prompt>())).thenReturn(mockChatResponse)

        // When
        val result = templateService.summarize(longContent)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse, result)
    }
}
