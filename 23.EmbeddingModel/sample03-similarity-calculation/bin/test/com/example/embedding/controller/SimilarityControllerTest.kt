package com.example.embedding.controller

import com.example.embedding.util.SimilarityUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SimilarityControllerTest {

    @Autowired lateinit var embeddingModel: EmbeddingModel

    @Test
    fun `should calculate cosine similarity`() {
        // Given
        val text1 = "Spring AI is great"
        val text2 = "Spring AI is awesome"

        // When
        val embedding1 = embeddingModel.embed(text1)
        val embedding2 = embeddingModel.embed(text2)
        val similarity = SimilarityUtil.cosineSimilarity(embedding1, embedding2)

        // Then
        assertThat(similarity).isGreaterThan(0.8) // 매우 유사
    }

    @Test
    fun `should find similar texts have high similarity`() {
        // Given
        val similar1 = "Machine Learning"
        val similar2 = "Deep Learning"
        val different = "Cooking recipes"

        // When
        val emb1 = embeddingModel.embed(similar1)
        val emb2 = embeddingModel.embed(similar2)
        val emb3 = embeddingModel.embed(different)

        val similaritySimilar = SimilarityUtil.cosineSimilarity(emb1, emb2)
        val similarityDifferent = SimilarityUtil.cosineSimilarity(emb1, emb3)

        // Then
        assertThat(similaritySimilar).isGreaterThan(similarityDifferent)
    }
}
