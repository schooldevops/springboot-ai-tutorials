package com.example.factcheck.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FactCheckingServiceTest {

    @Autowired private lateinit var factCheckingService: FactCheckingService

    @Test
    fun `should verify correct fact as supported`() {
        // Given
        val document =
                "The Earth is the third planet from the Sun and the only astronomical object known to harbor life."
        val claim = "The Earth is the third planet from the Sun."

        // When
        val result = factCheckingService.checkFact(document, claim)

        // Then
        assertThat(result.isPass).isTrue()
    }

    @Test
    fun `should detect incorrect fact as not supported`() {
        // Given
        val document =
                "The Earth is the third planet from the Sun and the only astronomical object known to harbor life."
        val claim = "The Earth is the fourth planet from the Sun."

        // When
        val result = factCheckingService.checkFact(document, claim)

        // Then
        assertThat(result.isPass).isFalse()
    }

    @Test
    fun `should detect hallucination in AI response`() {
        // Given
        val sourceDocument = "Spring AI는 2023년에 출시된 프레임워크입니다."
        val aiResponse = "Spring AI는 2020년에 출시되었습니다."

        // When
        val result = factCheckingService.detectHallucination(sourceDocument, aiResponse)

        // Then
        assertThat(result.isHallucination).isTrue()
    }

    @Test
    fun `should verify factual AI response as not hallucination`() {
        // Given
        val sourceDocument = "Kotlin은 JetBrains가 개발한 프로그래밍 언어입니다."
        val aiResponse = "Kotlin은 JetBrains에서 만든 언어입니다."

        // When
        val result = factCheckingService.detectHallucination(sourceDocument, aiResponse)

        // Then
        assertThat(result.isHallucination).isFalse()
    }

    @Test
    fun `should check multiple claims against document`() {
        // Given
        val document =
                """
            Spring Boot는 2014년에 출시되었습니다.
            Spring Boot는 자동 설정 기능을 제공합니다.
            Spring Boot는 독립 실행 가능한 애플리케이션을 만듭니다.
        """.trimIndent()

        val claims =
                listOf(
                        "Spring Boot는 2014년에 출시되었습니다.", // 사실
                        "Spring Boot는 2010년에 출시되었습니다.", // 거짓
                        "Spring Boot는 자동 설정을 제공합니다." // 사실
                )

        // When
        val results = factCheckingService.checkMultipleClaims(document, claims)

        // Then
        assertThat(results).hasSize(3)
        assertThat(results[0]).isTrue() // 사실
        assertThat(results[1]).isFalse() // 거짓
        assertThat(results[2]).isTrue() // 사실
    }

    @Test
    fun `should get detailed fact check result`() {
        // Given
        val document = "Vector Store는 임베딩 벡터를 저장하고 검색합니다."
        val claim = "Vector Store는 벡터를 저장합니다."

        // When
        val details = factCheckingService.getDetailedFactCheck(document, claim)

        // Then
        assertThat(details).containsKeys("document", "claim", "isSupported", "timestamp")
        assertThat(details["isSupported"]).isEqualTo(true)
    }

    @Test
    fun `should handle partially correct claim`() {
        // Given
        val document = "RAG는 Retrieval, Augmentation, Generation으로 구성됩니다."
        val claim = "RAG는 Retrieval과 Generation으로 구성됩니다."

        // When
        val result = factCheckingService.checkFact(document, claim)

        // Then
        // 부분적으로 맞지만 완전하지 않은 경우, 평가 모델에 따라 결과가 다를 수 있음
        assertThat(result).isNotNull
    }
}
