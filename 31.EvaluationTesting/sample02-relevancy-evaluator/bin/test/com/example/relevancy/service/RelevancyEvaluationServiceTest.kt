package com.example.relevancy.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RelevancyEvaluationServiceTest {

    @Autowired private lateinit var relevancyService: RelevancyEvaluationService

    @Test
    fun `should evaluate RAG response as relevant`() {
        // Given
        val question = "Spring AI란 무엇인가요?"
        val response = "Spring AI는 AI 애플리케이션 개발을 위한 Spring 프레임워크입니다."

        // When
        val result = relevancyService.evaluateRagResponse(question, response)

        // Then
        assertThat(result.isPass).isTrue()
    }

    @Test
    fun `should evaluate irrelevant RAG response as not relevant`() {
        // Given
        val question = "Spring AI란 무엇인가요?"
        val response = "오늘 날씨가 좋습니다."

        // When
        val result = relevancyService.evaluateRagResponse(question, response)

        // Then
        assertThat(result.isPass).isFalse()
    }

    @Test
    fun `should evaluate with custom prompt template`() {
        // Given
        val question = "Kotlin의 장점은?"
        val response = "Kotlin은 간결하고 안전한 프로그래밍 언어입니다."
        val context = listOf("Kotlin은 JetBrains가 개발한 현대적인 프로그래밍 언어입니다.")

        // When
        val result = relevancyService.evaluateWithCustomTemplate(question, response, context)

        // Then
        assertThat(result.isPass).isTrue()
    }

    @Test
    fun `should evaluate technical question with context`() {
        // Given
        val question = "RAG의 주요 구성요소는?"
        val response = "RAG는 Retrieval, Augmentation, Generation으로 구성됩니다."
        val context =
                listOf(
                        "RAG는 검색 증강 생성(Retrieval-Augmented Generation)의 약자입니다.",
                        "RAG는 외부 지식을 검색하여 AI 응답의 정확성을 높입니다."
                )

        // When
        val result = relevancyService.evaluateWithContext(question, response, context)

        // Then
        assertThat(result.isPass).isTrue()
    }

    @Test
    fun `should get detailed evaluation result`() {
        // Given
        val question = "Vector Store란?"
        val response = "Vector Store는 임베딩 벡터를 저장하고 검색하는 데이터베이스입니다."

        // When
        val details = relevancyService.getDetailedEvaluation(question, response)

        // Then
        assertThat(details).containsKeys("question", "response", "isRelevant", "timestamp")
        assertThat(details["isRelevant"]).isEqualTo(true)
    }

    @Test
    fun `should evaluate multiple responses`() {
        // Given
        val question = "Spring Boot의 특징은?"
        val responses =
                listOf(
                        "Spring Boot는 자동 설정을 제공합니다.",
                        "오늘은 월요일입니다.",
                        "Spring Boot는 독립 실행 가능한 애플리케이션을 만듭니다."
                )

        // When
        val results = relevancyService.evaluateMultipleResponses(question, responses)

        // Then
        assertThat(results).hasSize(3)
        assertThat(results[0]).isTrue() // 관련성 있음
        assertThat(results[1]).isFalse() // 관련성 없음
        assertThat(results[2]).isTrue() // 관련성 있음
    }
}
