package com.example.embedding.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OpenAIEmbeddingControllerTest {

    @Autowired lateinit var embeddingModel: EmbeddingModel

    @Test
    fun `should embed with OpenAI`() {
        val embedding = embeddingModel.embed("OpenAI embedding test")
        assertThat(embedding).isNotNull()
        assertThat(embedding.size).isEqualTo(1536)
    }

    @Test
    fun `should batch embed with OpenAI`() {
        val texts = listOf("Text 1", "Text 2", "Text 3")
        val embeddings = embeddingModel.embed(texts)
        assertThat(embeddings).hasSize(3)
        embeddings.forEach { assertThat(it.size).isEqualTo(1536) }
    }
}
