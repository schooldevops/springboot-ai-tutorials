package com.example.embedding.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BatchEmbeddingControllerTest {

    @Autowired lateinit var embeddingModel: EmbeddingModel

    @Test
    fun `should embed batch of texts`() {
        // Given
        val texts = listOf("Spring AI", "Kotlin", "OpenAI")

        // When
        val embeddings = embeddingModel.embed(texts)

        // Then
        assertThat(embeddings).hasSize(3)
        embeddings.forEach { embedding -> assertThat(embedding).isNotEmpty() }
    }

    @Test
    fun `should return EmbeddingResponse`() {
        // Given
        val texts = listOf("Test 1", "Test 2")

        // When
        val response = embeddingModel.embedForResponse(texts)

        // Then
        assertThat(response.results).hasSize(2)
        assertThat(response.metadata).isNotNull()
    }
}
