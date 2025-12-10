package com.example.embedding.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/** TDD: Sample 01 - Basic Embedding 테스트 */
@SpringBootTest
class BasicEmbeddingControllerTest {

    @Autowired lateinit var embeddingModel: EmbeddingModel

    @Test
    fun `should embed single text`() {
        // When
        val embedding = embeddingModel.embed("Hello World")

        // Then
        assertThat(embedding).isNotNull()
        assertThat(embedding).isNotEmpty()
    }

    @Test
    fun `should have correct dimensions`() {
        // When
        val dimensions = embeddingModel.dimensions()

        // Then
        assertThat(dimensions).isGreaterThan(0)
        assertThat(dimensions).isEqualTo(1536) // text-embedding-3-small
    }

    @Test
    fun `should embed different texts differently`() {
        // When
        val embedding1 = embeddingModel.embed("Spring AI")
        val embedding2 = embeddingModel.embed("Quantum Physics")

        // Then
        assertThat(embedding1).isNotEqualTo(embedding2)
    }
}
