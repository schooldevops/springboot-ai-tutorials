package com.example.springai.service

import com.example.springai.model.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.ai.chat.client.ChatClient

/** ParsingClientService 테스트 ChatClient를 사용한 구조화된 출력 파싱 서비스 테스트 */
@ExtendWith(MockitoExtension::class)
class ParsingClientServiceTest {

    @Mock private lateinit var chatClientBuilder: ChatClient.Builder

    @Mock private lateinit var chatClient: ChatClient

    @Mock private lateinit var chatClientRequestSpec: ChatClient.ChatClientRequestSpec

    @Mock private lateinit var chatClientCallResponseSpec: ChatClient.CallResponseSpec

    private lateinit var parsingClientService: ParsingClientService

    /** ChatClient 테스트 초기화 */
    private fun setupChatClient(expectedResponse: String) {
        whenever(chatClientBuilder.build()).thenReturn(chatClient)
        whenever(chatClient.prompt()).thenReturn(chatClientRequestSpec)
        whenever(chatClientRequestSpec.system(any<String>())).thenReturn(chatClientRequestSpec)
        whenever(chatClientRequestSpec.user(any<String>())).thenReturn(chatClientRequestSpec)
        whenever(chatClientRequestSpec.call()).thenReturn(chatClientCallResponseSpec)
        whenever(chatClientCallResponseSpec.content()).thenReturn(expectedResponse)

        parsingClientService = ParsingClientService(chatClientBuilder)
    }

    /** UserInfo 파싱 테스트 */
    @Test
    fun `parseResponse should parse UserInfo correctly`() {
        // Given
        val jsonResponse =
                """
            {
                "name": "홍길동",
                "age": 30,
                "email": "hong@example.com"
            }
        """.trimIndent()
        setupChatClient(jsonResponse)

        val systemMessage = "사용자 정보를 추출해주세요."
        val userMessage = "이름은 홍길동이고, 나이는 30세입니다. 이메일은 hong@example.com입니다."

        // When
        val result =
                parsingClientService.parseResponse(UserInfo::class.java, systemMessage, userMessage)

        // Then
        assertNotNull(result)
        assertEquals("홍길동", result.name)
        assertEquals(30, result.age)
        assertEquals("hong@example.com", result.email)
    }

    /** UserProfile 파싱 테스트 (선택적 필드 포함) */
    @Test
    fun `parseResponse should parse UserProfile with optional fields`() {
        // Given
        val jsonResponse =
                """
            {
                "name": "김철수",
                "age": 25,
                "email": "kim@example.com",
                "location": "서울",
                "bio": "개발자입니다"
            }
        """.trimIndent()
        setupChatClient(jsonResponse)

        // When
        val result =
                parsingClientService.parseResponse(
                        UserProfile::class.java,
                        "사용자 프로필을 추출해주세요.",
                        "김철수, 25세, 서울 거주, 개발자"
                )

        // Then
        assertNotNull(result)
        assertEquals("김철수", result.name)
        assertEquals(25, result.age)
        assertEquals("kim@example.com", result.email)
        assertEquals("서울", result.location)
        assertEquals("개발자입니다", result.bio)
    }

    /** Product 파싱 테스트 */
    @Test
    fun `parseResponse should parse Product correctly`() {
        // Given
        val jsonResponse =
                """
            {
                "name": "노트북",
                "price": 1500000.0,
                "category": "전자제품",
                "description": "고성능 노트북",
                "features": ["16GB RAM", "512GB SSD", "15.6인치"]
            }
        """.trimIndent()
        setupChatClient(jsonResponse)

        // When
        val result =
                parsingClientService.parseResponse(
                        Product::class.java,
                        "제품 정보를 추출해주세요.",
                        "노트북, 150만원, 전자제품"
                )

        // Then
        assertNotNull(result)
        assertEquals("노트북", result.name)
        assertEquals(1500000.0, result.price)
        assertEquals("전자제품", result.category)
        assertEquals("고성능 노트북", result.description)
        assertEquals(3, result.features.size)
    }

    /** Address 파싱 테스트 */
    @Test
    fun `parseResponse should parse Address correctly`() {
        // Given
        val jsonResponse =
                """
            {
                "street": "강남대로 123",
                "city": "서울",
                "zipCode": "06000",
                "country": "대한민국"
            }
        """.trimIndent()
        setupChatClient(jsonResponse)

        // When
        val result =
                parsingClientService.parseResponse(
                        Address::class.java,
                        "주소 정보를 추출해주세요.",
                        "서울시 강남대로 123, 우편번호 06000"
                )

        // Then
        assertNotNull(result)
        assertEquals("강남대로 123", result.street)
        assertEquals("서울", result.city)
        assertEquals("06000", result.zipCode)
        assertEquals("대한민국", result.country)
    }

    /** Recipe 파싱 테스트 */
    @Test
    fun `parseResponse should parse Recipe correctly`() {
        // Given
        val jsonResponse =
                """
            {
                "name": "김치찌개",
                "ingredients": ["김치", "돼지고기", "두부", "파"],
                "steps": ["재료 준비", "볶기", "끓이기"],
                "cookingTime": 30
            }
        """.trimIndent()
        setupChatClient(jsonResponse)

        // When
        val result =
                parsingClientService.parseResponse(
                        Recipe::class.java,
                        "레시피 정보를 추출해주세요.",
                        "김치찌개 만들기"
                )

        // Then
        assertNotNull(result)
        assertEquals("김치찌개", result.name)
        assertEquals(4, result.ingredients.size)
        assertEquals(3, result.steps.size)
        assertEquals(30, result.cookingTime)
    }

    /** parse 메서드 테스트 (기본 시스템 메시지 사용) */
    @Test
    fun `parse should use default system message`() {
        // Given
        val jsonResponse =
                """
            {
                "name": "이영희",
                "age": 28,
                "email": "lee@example.com"
            }
        """.trimIndent()
        setupChatClient(jsonResponse)

        // When
        val result = parsingClientService.parse(UserInfo::class.java, "이영희, 28세, lee@example.com")

        // Then
        assertNotNull(result)
        assertEquals("이영희", result.name)
        assertEquals(28, result.age)
        assertEquals("lee@example.com", result.email)
    }

    /** JSON 정리 기능 테스트 (마크다운 코드 블록 포함) */
    @Test
    fun `parseResponse should handle JSON with markdown code blocks`() {
        // Given
        val jsonResponse =
                """
            ```json
            {
                "name": "박민수",
                "age": 35,
                "email": "park@example.com"
            }
            ```
        """.trimIndent()
        setupChatClient(jsonResponse)

        // When
        val result =
                parsingClientService.parseResponse(
                        UserInfo::class.java,
                        "사용자 정보를 추출해주세요.",
                        "박민수, 35세"
                )

        // Then
        assertNotNull(result)
        assertEquals("박민수", result.name)
        assertEquals(35, result.age)
        assertEquals("park@example.com", result.email)
    }

    /** 빈 리스트 처리 테스트 */
    @Test
    fun `parseResponse should handle empty lists`() {
        // Given
        val jsonResponse =
                """
            {
                "name": "간단한 요리",
                "ingredients": [],
                "steps": [],
                "cookingTime": null
            }
        """.trimIndent()
        setupChatClient(jsonResponse)

        // When
        val result =
                parsingClientService.parseResponse(Recipe::class.java, "레시피 정보를 추출해주세요.", "간단한 요리")

        // Then
        assertNotNull(result)
        assertEquals("간단한 요리", result.name)
        assertEquals(0, result.ingredients.size)
        assertEquals(0, result.steps.size)
        assertEquals(null, result.cookingTime)
    }

    /** null 필드 처리 테스트 */
    @Test
    fun `parseResponse should handle null optional fields`() {
        // Given
        val jsonResponse =
                """
            {
                "name": "최지원",
                "age": null,
                "email": null,
                "location": null,
                "bio": null
            }
        """.trimIndent()
        setupChatClient(jsonResponse)

        // When
        val result =
                parsingClientService.parseResponse(
                        UserProfile::class.java,
                        "사용자 프로필을 추출해주세요.",
                        "최지원"
                )

        // Then
        assertNotNull(result)
        assertEquals("최지원", result.name)
        assertEquals(null, result.age)
        assertEquals(null, result.email)
        assertEquals(null, result.location)
        assertEquals(null, result.bio)
    }

    /** 복잡한 중첩 객체 파싱 테스트 */
    @Test
    fun `parseResponse should parse nested objects`() {
        // Given
        val jsonResponse =
                """
            {
                "name": "테크 컴퍼니",
                "address": {
                    "street": "테헤란로 123",
                    "city": "서울",
                    "zipCode": "06000",
                    "country": "대한민국"
                },
                "employees": 100,
                "departments": ["개발", "디자인", "마케팅"]
            }
        """.trimIndent()
        setupChatClient(jsonResponse)

        // When
        val result =
                parsingClientService.parseResponse(
                        CompanyInfo::class.java,
                        "회사 정보를 추출해주세요.",
                        "테크 컴퍼니, 서울 테헤란로, 직원 100명"
                )

        // Then
        assertNotNull(result)
        assertEquals("테크 컴퍼니", result.name)
        assertEquals("테헤란로 123", result.address.street)
        assertEquals("서울", result.address.city)
        assertEquals(100, result.employees)
        assertEquals(3, result.departments.size)
    }
}
