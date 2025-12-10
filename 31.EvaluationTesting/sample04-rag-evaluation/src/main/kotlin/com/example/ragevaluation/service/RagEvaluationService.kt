package com.example.ragevaluation.service

import com.example.ragevaluation.model.RagEvaluationResult
import jakarta.annotation.PostConstruct
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.document.Document
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.evaluation.EvaluationRequest
import org.springframework.ai.evaluation.FactCheckingEvaluator
import org.springframework.ai.evaluation.RelevancyEvaluator
import org.springframework.ai.vectorstore.SimpleVectorStore
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

/**
 * RAG Evaluation Service
 *
 * 완전한 RAG 시스템과 통합 평가 기능을 제공하는 서비스입니다.
 *
 * 주요 기능:
 * 1. RAG 기반 질의응답
 * 2. RelevancyEvaluator를 통한 관련성 평가
 * 3. FactCheckingEvaluator를 통한 사실성 검증
 * 4. 통합 평가 리포트 생성
 * 5. 품질 점수 계산
 */
@Service
class RagEvaluationService(
        private val chatModel: ChatModel,
        private val embeddingModel: EmbeddingModel,
        private val chatClientBuilder: ChatClient.Builder
) {

    private lateinit var vectorStore: VectorStore
    private lateinit var chatClient: ChatClient
    private lateinit var relevancyEvaluator: RelevancyEvaluator
    private lateinit var factCheckingEvaluator: FactCheckingEvaluator

    @PostConstruct
    fun init() {
        // VectorStore 초기화
        vectorStore = SimpleVectorStore(embeddingModel)

        // 샘플 문서 로드
        loadSampleDocuments()

        // ChatClient 구성 (QuestionAnswerAdvisor 사용)
        chatClient = chatClientBuilder.defaultAdvisors(QuestionAnswerAdvisor(vectorStore)).build()

        // Evaluators 초기화
        relevancyEvaluator = RelevancyEvaluator(chatClientBuilder)
        factCheckingEvaluator = FactCheckingEvaluator(chatClientBuilder)
    }

    private fun loadSampleDocuments() {
        val documents =
                listOf(
                        Document(
                                "Spring AI는 AI 애플리케이션 개발을 위한 Spring 프레임워크입니다. " +
                                        "ChatClient, EmbeddingModel, VectorStore 등을 제공하여 " +
                                        "개발자가 쉽게 AI 기능을 통합할 수 있습니다.",
                                mapOf("source" to "spring-ai-intro", "category" to "framework")
                        ),
                        Document(
                                "RAG(Retrieval-Augmented Generation)는 검색, 증강, 생성의 3단계로 구성됩니다. " +
                                        "외부 지식을 검색하여 AI 응답의 정확성을 높이는 기술입니다. " +
                                        "ETL Pipeline을 통해 문서를 처리하고 Vector Store에 저장합니다.",
                                mapOf("source" to "rag-concept", "category" to "concept")
                        ),
                        Document(
                                "Vector Store는 임베딩 벡터를 저장하고 유사도 검색을 수행합니다. " +
                                        "SimpleVectorStore, ChromaDB, Pinecone 등 다양한 구현체가 있습니다. " +
                                        "문서의 의미적 유사성을 기반으로 관련 정보를 검색합니다.",
                                mapOf("source" to "vector-store", "category" to "storage")
                        ),
                        Document(
                                "Spring Boot는 2014년에 출시된 프레임워크입니다. " +
                                        "자동 설정, 독립 실행, 내장 서버 등의 특징을 가지고 있습니다. " +
                                        "마이크로서비스 개발에 널리 사용됩니다.",
                                mapOf("source" to "spring-boot", "category" to "framework")
                        ),
                        Document(
                                "Kotlin은 JetBrains가 개발한 현대적인 프로그래밍 언어입니다. " +
                                        "간결성, 안전성, 상호운용성이 주요 장점입니다. " +
                                        "Android 공식 언어이며 서버 개발에도 널리 사용됩니다.",
                                mapOf("source" to "kotlin", "category" to "language")
                        )
                )

        vectorStore.add(documents)
    }

    /**
     * RAG를 사용하여 질문에 답변합니다.
     *
     * @param question 사용자 질문
     * @return String AI 응답
     */
    fun askQuestion(question: String): String {
        return chatClient.prompt().user(question).call().content()
    }

    /**
     * 질문에 답변하고 평가를 수행합니다.
     *
     * @param question 사용자 질문
     * @return RagEvaluationResult 답변 및 평가 결과
     */
    fun askAndEvaluate(question: String): RagEvaluationResult {
        // RAG 응답 생성
        val chatResponse: ChatResponse = chatClient.prompt().user(question).call().chatResponse()

        val answer = chatResponse.result.output.content

        // 검색된 문서 추출
        val retrievedDocuments = vectorStore.similaritySearch(question)
        val documentContents = retrievedDocuments.map { it.content }

        // 관련성 평가
        val relevanceRequest = EvaluationRequest(question, retrievedDocuments, answer)
        val relevanceEvaluation = relevancyEvaluator.evaluate(relevanceRequest)

        // 사실성 검증
        val combinedDocument = documentContents.joinToString("\n")
        val factCheckRequest = EvaluationRequest(combinedDocument, emptyList(), answer)
        val factCheckEvaluation = factCheckingEvaluator.evaluate(factCheckRequest)

        return RagEvaluationResult(
                question = question,
                answer = answer,
                retrievedDocuments = documentContents,
                relevanceEvaluation = relevanceEvaluation,
                factCheckEvaluation = factCheckEvaluation
        )
    }

    /**
     * 상세한 평가 리포트를 생성합니다.
     *
     * @param question 사용자 질문
     * @return Map 평가 리포트
     */
    fun getDetailedEvaluationReport(question: String): Map<String, Any> {
        val result = askAndEvaluate(question)
        val qualityScore = calculateQualityScore(result)

        return mapOf(
                "question" to result.question,
                "answer" to result.answer,
                "retrievedDocuments" to result.retrievedDocuments,
                "relevanceScore" to if (result.relevanceEvaluation.isPass) 1.0 else 0.0,
                "factCheckScore" to if (result.factCheckEvaluation.isPass) 1.0 else 0.0,
                "overallQuality" to qualityScore,
                "timestamp" to System.currentTimeMillis()
        )
    }

    /**
     * 여러 질문을 평가합니다.
     *
     * @param questions 질문 리스트
     * @return List<RagEvaluationResult> 평가 결과 리스트
     */
    fun evaluateMultipleQuestions(questions: List<String>): List<RagEvaluationResult> {
        return questions.map { question -> askAndEvaluate(question) }
    }

    /**
     * 평가 결과를 기반으로 품질 점수를 계산합니다.
     *
     * @param result RAG 평가 결과
     * @return Double 품질 점수 (0.0 ~ 1.0)
     */
    fun calculateQualityScore(result: RagEvaluationResult): Double {
        val relevanceScore = if (result.relevanceEvaluation.isPass) 1.0 else 0.0
        val factCheckScore = if (result.factCheckEvaluation.isPass) 1.0 else 0.0

        // 가중 평균 (관련성 60%, 사실성 40%)
        return (relevanceScore * 0.6) + (factCheckScore * 0.4)
    }
}
