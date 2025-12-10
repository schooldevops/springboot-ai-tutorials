package com.example.evaluation.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.evaluation.EvaluationRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BasicEvaluationServiceTest {

    @Autowired private lateinit var evaluationService: BasicEvaluationService

    @Autowired private lateinit var chatModel: ChatModel

    @Test
    fun `should evaluate simple response as relevant`() {
        // Given
        val userQuestion = "What is the capital of France?"
        val aiResponse = "The capital of France is Paris."
        val context = listOf("France is a country in Europe. Its capital city is Paris.")

        // When
        val result = evaluationService.evaluateRelevance(userQuestion, aiResponse, context)

        // Then
        assertThat(result.isPass).isTrue()
    }

    @Test
    fun `should evaluate irrelevant response as not relevant`() {
        // Given
        val userQuestion = "What is the capital of France?"
        val aiResponse = "The weather is nice today."
        val context = listOf("France is a country in Europe. Its capital city is Paris.")

        // When
        val result = evaluationService.evaluateRelevance(userQuestion, aiResponse, context)

        // Then
        assertThat(result.isPass).isFalse()
    }

    @Test
    fun `should create custom evaluator and evaluate`() {
        // Given
        val chatClient = ChatClient.builder(chatModel).build()
        val evaluator = evaluationService.createCustomEvaluator(chatClient)

        val request =
                EvaluationRequest(
                        "What is Spring AI?",
                        emptyList(),
                        "Spring AI is a framework for building AI applications with Spring Boot."
                )

        // When
        val result = evaluator.evaluate(request)

        // Then
        assertThat(result).isNotNull
        assertThat(result.isPass).isTrue()
    }

    @Test
    fun `should evaluate with empty context`() {
        // Given
        val userQuestion = "Tell me about AI"
        val aiResponse = "AI stands for Artificial Intelligence."
        val context = emptyList<String>()

        // When
        val result = evaluationService.evaluateRelevance(userQuestion, aiResponse, context)

        // Then
        assertThat(result).isNotNull
        // With empty context, evaluation should still work
        assertThat(result.isPass).isTrue()
    }

    @Test
    fun `should get evaluation details`() {
        // Given
        val userQuestion = "What is Kotlin?"
        val aiResponse = "Kotlin is a modern programming language."
        val context = listOf("Kotlin is a statically typed programming language for JVM.")

        // When
        val result = evaluationService.evaluateRelevance(userQuestion, aiResponse, context)

        // Then
        assertThat(result).isNotNull
        assertThat(result.isPass).isNotNull
    }
}
