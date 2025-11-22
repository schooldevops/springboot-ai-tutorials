package com.example.semanticsearch.config

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.vectorstore.SimpleVectorStore
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * VectorStore 설정
 * SimpleVectorStore를 사용한 인메모리 벡터 저장소
 */
@Configuration
class VectorStoreConfig {
    
    @Bean
    fun vectorStore(embeddingModel: EmbeddingModel): VectorStore {
        // SimpleVectorStore: 개발/테스트용 인메모리 벡터 저장소
        return SimpleVectorStore(embeddingModel)
    }
}
