package com.example.rag.service

import jakarta.annotation.PostConstruct
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor
import org.springframework.ai.document.Document
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.vectorstore.SimpleVectorStore
import org.springframework.stereotype.Service

/**
 * Sample 01: Basic RAG
 *
 * QuestionAnswerAdvisor를 사용한 기본 RAG 구현
 */
@Service
class BasicRagService(
        private val chatClientBuilder: ChatClient.Builder,
        private val embeddingModel: EmbeddingModel
) {
    private val vectorStore = SimpleVectorStore(embeddingModel)
    private lateinit var chatClient: ChatClient

    @PostConstruct
    fun init() {
        // 샘플 문서 로드
        loadSampleDocuments()

        // QuestionAnswerAdvisor로 ChatClient 구성
        chatClient = chatClientBuilder.defaultAdvisors(QuestionAnswerAdvisor(vectorStore)).build()
    }

    private fun loadSampleDocuments() {
        val documents =
                listOf(
                        Document(
                                "Spring AI는 AI 애플리케이션 개발을 위한 Spring 프레임워크입니다. " +
                                        "ChatClient, EmbeddingModel, VectorStore 등을 제공합니다.",
                                mapOf("source" to "spring-ai-intro", "category" to "framework")
                        ),
                        Document(
                                "RAG(Retrieval-Augmented Generation)는 외부 지식을 검색하여 " +
                                        "AI 응답의 정확성을 높이는 기술입니다. ETL Pipeline을 통해 문서를 처리합니다.",
                                mapOf("source" to "rag-intro", "category" to "concept")
                        ),
                        Document(
                                "Vector Store는 임베딩 벡터를 저장하고 유사도 검색을 수행합니다. " +
                                        "SimpleVectorStore, ChromaDB, Pinecone 등이 있습니다.",
                                mapOf("source" to "vector-store", "category" to "storage")
                        ),
                        Document(
                                "2024년 회사 매출은 100억원이며, 전년 대비 20% 증가했습니다. " +
                                        "주요 성장 동력은 AI 사업부문입니다.",
                                mapOf("source" to "company-report-2024", "category" to "finance")
                        )
                )

        vectorStore.add(documents)
    }

    fun ask(question: String): String {
        return chatClient.prompt().user(question).call().content()
    }

    fun askWithMetadata(question: String): Map<String, Any> {
        val response = chatClient.prompt().user(question).call().chatResponse()

        return mapOf("answer" to response.result.output.content, "metadata" to response.metadata)
    }
}
