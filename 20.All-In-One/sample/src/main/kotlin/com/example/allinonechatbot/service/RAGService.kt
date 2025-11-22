package com.example.allinonechatbot.service

import jakarta.annotation.PostConstruct
import org.springframework.ai.document.Document
import org.springframework.ai.reader.TextReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Service

@Service
class RAGService(
    private val vectorStore: VectorStore
) {
    
    @PostConstruct
    fun loadDocuments() {
        val resolver = PathMatchingResourcePatternResolver()
        val resources = try {
            resolver.getResources("classpath:documents/*.txt")
        } catch (e: Exception) {
            emptyArray<Resource>()
        }
        
        if (resources.isEmpty()) {
            // Create sample documents
            val sampleDocs = listOf(
                Document("회사 정책: 재택근무는 주 2회 가능합니다."),
                Document("휴가 규정: 연차는 입사 1년 후부터 15일 제공됩니다."),
                Document("복지 제도: 중식 지원, 건강검진, 경조사 지원이 있습니다.")
            )
            vectorStore.add(sampleDocs)
        } else {
            resources.forEach { resource ->
                val textReader = TextReader(resource)
                val documents = textReader.get()
                val splitter = TokenTextSplitter()
                val chunks = splitter.apply(documents)
                vectorStore.add(chunks)
            }
        }
    }
    
    fun searchSimilarDocuments(query: String, topK: Int = 3): List<Document> {
        return vectorStore.similaritySearch(
            SearchRequest.query(query).withTopK(topK)
        )
    }
}
