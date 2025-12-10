package com.example.evaluation.service

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.document.Document
import org.springframework.ai.evaluation.EvaluationRequest
import org.springframework.ai.evaluation.EvaluationResponse
import org.springframework.ai.evaluation.Evaluator
import org.springframework.ai.evaluation.RelevancyEvaluator
import org.springframework.stereotype.Service

/**
 * Basic Evaluation Service
 *
 * Spring AI의 Evaluator 인터페이스를 사용하여 AI 응답을 평가하는 기본 서비스입니다.
 *
 * 주요 기능:
 * 1. AI 응답의 관련성 평가
 * 2. 커스텀 평가자 생성
 * 3. EvaluationRequest/Response 패턴 활용
 */
@Service
class BasicEvaluationService(private val chatModel: ChatModel) {

    /**
     * AI 응답의 관련성을 평가합니다.
     *
     * @param userQuestion 사용자 질문
     * @param aiResponse AI 응답
     * @param context 컨텍스트 정보 (RAG에서 검색된 문서 등)
     * @return EvaluationResponse 평가 결과
     */
    fun evaluateRelevance(
            userQuestion: String,
            aiResponse: String,
            context: List<String>
    ): EvaluationResponse {
        // ChatClient 빌더 생성
        val chatClientBuilder = ChatClient.builder(chatModel)

        // RelevancyEvaluator 생성
        val evaluator = RelevancyEvaluator(chatClientBuilder)

        // 컨텍스트를 Document 리스트로 변환
        val dataList = context.map { Document(it) }

        // EvaluationRequest 생성
        val request = EvaluationRequest(userQuestion, dataList, aiResponse)

        // 평가 수행
        return evaluator.evaluate(request)
    }

    /**
     * 커스텀 평가자를 생성합니다.
     *
     * 이 예제에서는 간단한 관련성 평가자를 반환하지만, 실제로는 더 복잡한 평가 로직을 구현할 수 있습니다.
     *
     * @param chatClient ChatClient 인스턴스
     * @return Evaluator 커스텀 평가자
     */
    fun createCustomEvaluator(chatClient: ChatClient): Evaluator {
        return object : Evaluator {
            override fun evaluate(request: EvaluationRequest): EvaluationResponse {
                // 간단한 평가 로직: 응답이 비어있지 않으면 통과
                val isPass = request.responseContent.isNotBlank()

                return EvaluationResponse(isPass)
            }
        }
    }

    /**
     * 상세한 평가 정보를 포함한 결과를 반환합니다.
     *
     * @param userQuestion 사용자 질문
     * @param aiResponse AI 응답
     * @param context 컨텍스트 정보
     * @return Map 평가 결과 상세 정보
     */
    fun evaluateWithDetails(
            userQuestion: String,
            aiResponse: String,
            context: List<String>
    ): Map<String, Any> {
        val evaluationResult = evaluateRelevance(userQuestion, aiResponse, context)

        return mapOf(
                "userQuestion" to userQuestion,
                "aiResponse" to aiResponse,
                "contextSize" to context.size,
                "isPass" to evaluationResult.isPass,
                "evaluationTimestamp" to System.currentTimeMillis()
        )
    }
}
