package com.example.ragevaluation.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RagEvaluationServiceTest {

    @Autowired private lateinit var ragEvaluationService: RagEvaluationService

    @Test
    fun `should answer question using RAG`() {
        // Given
        val question = "Spring AI란 무엇인가요?"

        // When
        val answer = ragEvaluationService.askQuestion(question)

        // Then
        assertThat(answer).containsIgnoringCase("Spring AI")
    }

    @Test
    fun `should evaluate RAG response with both evaluators`() {
        // Given
        val question = "Vector Store의 역할은?"

        // When
        val result = ragEvaluationService.askAndEvaluate(question)

        // Then
        assertThat(result.answer).isNotBlank()
        assertThat(result.relevanceEvaluation).isNotNull
        assertThat(result.factCheckEvaluation).isNotNull
    }

    @Test
    fun `should pass relevance evaluation for good answer`() {
        // Given
        val question = "RAG란 무엇인가요?"

        // When
        val result = ragEvaluationService.askAndEvaluate(question)

        // Then
        assertThat(result.relevanceEvaluation.isPass).isTrue()
    }

    @Test
    fun `should get detailed evaluation report`() {
        // Given
        val question = "Spring Boot의 특징은?"

        // When
        val report = ragEvaluationService.getDetailedEvaluationReport(question)

        // Then
        assertThat(report)
                .containsKeys(
                        "question",
                        "answer",
                        "retrievedDocuments",
                        "relevanceScore",
                        "factCheckScore",
                        "overallQuality"
                )
    }

    @Test
    fun `should evaluate multiple questions`() {
        // Given
        val questions = listOf("Spring AI란?", "RAG의 구성요소는?", "Vector Store란?")

        // When
        val results = ragEvaluationService.evaluateMultipleQuestions(questions)

        // Then
        assertThat(results).hasSize(3)
        results.forEach { result ->
            assertThat(result.answer).isNotBlank()
            assertThat(result.relevanceEvaluation).isNotNull
        }
    }

    @Test
    fun `should calculate overall quality score`() {
        // Given
        val question = "Kotlin의 장점은?"

        // When
        val result = ragEvaluationService.askAndEvaluate(question)
        val qualityScore = ragEvaluationService.calculateQualityScore(result)

        // Then
        assertThat(qualityScore).isBetween(0.0, 1.0)
    }
}
