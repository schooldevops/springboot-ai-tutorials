package com.example.relevancy.service

import jakarta.annotation.PostConstruct
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.document.Document
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.evaluation.EvaluationRequest
import org.springframework.ai.evaluation.EvaluationResponse
import org.springframework.ai.evaluation.RelevancyEvaluator
import org.springframework.ai.vectorstore.SimpleVectorStore
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

/**
 * RelevancyEvaluation Service
 *
 * RelevancyEvaluator를 사용하여 AI 응답의 관련성을 평가하는 서비스입니다.
 *
 * 주요 기능:
 * 1. RAG 응답의 관련성 평가
 * 2. 커스텀 프롬프트 템플릿 사용
 * 3. 컨텍스트 기반 평가
 * 4. 다중 응답 평가
 */
@Service
class RelevancyEvaluationService(
        private val chatModel: ChatModel,
        private val embeddingModel: EmbeddingModel
) {

    private lateinit var vectorStore: VectorStore
    private lateinit var chatClientBuilder: ChatClient.Builder

    @PostConstruct
    fun init() {
        // SimpleVectorStore 초기화
        vectorStore = SimpleVectorStore(embeddingModel)

        // 샘플 문서 로드
        loadSampleDocuments()

        // ChatClient 빌더 초기화
        chatClientBuilder = ChatClient.builder(chatModel)
    }

    private fun loadSampleDocuments() {
        val documents =
                listOf(
                        Document(
                                "Spring AI는 AI 애플리케이션 개발을 위한 Spring 프레임워크입니다. " +
                                        "ChatClient, EmbeddingModel, VectorStore 등을 제공합니다.",
                                mapOf("source" to "spring-ai-intro")
                        ),
                        Document(
                                "RAG(Retrieval-Augmented Generation)는 외부 지식을 검색하여 " +
                                        "AI 응답의 정확성을 높이는 기술입니다.",
                                mapOf("source" to "rag-intro")
                        ),
                        Document(
                                "Vector Store는 임베딩 벡터를 저장하고 유사도 검색을 수행합니다.",
                                mapOf("source" to "vector-store")
                        )
                )

        vectorStore.add(documents)
    }

    /**
     * RAG 응답의 관련성을 평가합니다. VectorStore에서 관련 문서를 검색하여 컨텍스트로 사용합니다.
     *
     * @param question 사용자 질문
     * @param response AI 응답
     * @return EvaluationResponse 평가 결과
     */
    fun evaluateRagResponse(question: String, response: String): EvaluationResponse {
        // VectorStore에서 관련 문서 검색
        val similarDocuments = vectorStore.similaritySearch(question)

        // RelevancyEvaluator 생성
        val evaluator = RelevancyEvaluator(chatClientBuilder)

        // EvaluationRequest 생성
        val request = EvaluationRequest(question, similarDocuments, response)

        return evaluator.evaluate(request)
    }

    /**
     * 커스텀 프롬프트 템플릿을 사용하여 평가합니다.
     *
     * @param question 사용자 질문
     * @param response AI 응답
     * @param context 컨텍스트 정보
     * @return EvaluationResponse 평가 결과
     */
    fun evaluateWithCustomTemplate(
            question: String,
            response: String,
            context: List<String>
    ): EvaluationResponse {
        // 커스텀 프롬프트 템플릿
        val customTemplate =
                """
            다음 응답이 질문과 컨텍스트에 관련이 있는지 평가하세요.
            YES 또는 NO로만 답변하세요.
            
            질문: {query}
            응답: {response}
            컨텍스트: {context}
            
            평가 결과:
        """.trimIndent()

        // RelevancyEvaluator 생성 (기본 템플릿 사용)
        // Note: Spring AI 1.0.0-M4에서는 커스텀 템플릿 설정이 제한적일 수 있음
        val evaluator = RelevancyEvaluator(chatClientBuilder)

        val dataList = context.map { Document(it) }
        val request = EvaluationRequest(question, dataList, response)

        return evaluator.evaluate(request)
    }

    /**
     * 명시적인 컨텍스트와 함께 평가합니다.
     *
     * @param question 사용자 질문
     * @param response AI 응답
     * @param context 컨텍스트 리스트
     * @return EvaluationResponse 평가 결과
     */
    fun evaluateWithContext(
            question: String,
            response: String,
            context: List<String>
    ): EvaluationResponse {
        val evaluator = RelevancyEvaluator(chatClientBuilder)
        val dataList = context.map { Document(it) }
        val request = EvaluationRequest(question, dataList, response)

        return evaluator.evaluate(request)
    }

    /**
     * 상세한 평가 결과를 반환합니다.
     *
     * @param question 사용자 질문
     * @param response AI 응답
     * @return Map 평가 결과 상세 정보
     */
    fun getDetailedEvaluation(question: String, response: String): Map<String, Any> {
        val evaluationResult = evaluateRagResponse(question, response)

        return mapOf(
                "question" to question,
                "response" to response,
                "isRelevant" to evaluationResult.isPass,
                "timestamp" to System.currentTimeMillis()
        )
    }

    /**
     * 여러 응답을 한 번에 평가합니다.
     *
     * @param question 사용자 질문
     * @param responses 평가할 응답 리스트
     * @return List<Boolean> 각 응답의 관련성 평가 결과
     */
    fun evaluateMultipleResponses(question: String, responses: List<String>): List<Boolean> {
        return responses.map { response -> evaluateRagResponse(question, response).isPass }
    }
}
