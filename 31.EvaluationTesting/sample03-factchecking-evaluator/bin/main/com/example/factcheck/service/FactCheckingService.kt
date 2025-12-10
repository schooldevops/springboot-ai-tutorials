package com.example.factcheck.service

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.evaluation.EvaluationRequest
import org.springframework.ai.evaluation.EvaluationResponse
import org.springframework.ai.evaluation.FactCheckingEvaluator
import org.springframework.stereotype.Service

/**
 * FactChecking Service
 *
 * FactCheckingEvaluator를 사용하여 AI 응답의 사실성을 검증하고 환각을 탐지하는 서비스입니다.
 *
 * 주요 기능:
 * 1. 주장(claim)과 문서(document) 간 사실 검증
 * 2. AI 환각(hallucination) 탐지
 * 3. 다중 주장 검증
 * 4. 상세한 사실 확인 결과 제공
 */
@Service
class FactCheckingService(private val chatModel: ChatModel) {

    private val chatClientBuilder: ChatClient.Builder = ChatClient.builder(chatModel)

    /**
     * 주장이 문서에 의해 뒷받침되는지 확인합니다.
     *
     * @param document 참조 문서 (사실의 근거)
     * @param claim 검증할 주장
     * @return EvaluationResponse 사실 확인 결과
     */
    fun checkFact(document: String, claim: String): EvaluationResponse {
        // FactCheckingEvaluator 생성
        val evaluator = FactCheckingEvaluator(chatClientBuilder)

        // EvaluationRequest 생성
        // userText에 document를, responseContent에 claim을 전달
        val request =
                EvaluationRequest(
                        document, // 참조 문서
                        emptyList(), // dataList는 사용하지 않음
                        claim // 검증할 주장
                )

        return evaluator.evaluate(request)
    }

    /**
     * AI 응답에서 환각(hallucination)을 탐지합니다.
     *
     * @param sourceDocument 원본 문서
     * @param aiResponse AI가 생성한 응답
     * @return HallucinationResult 환각 탐지 결과
     */
    fun detectHallucination(sourceDocument: String, aiResponse: String): HallucinationResult {
        val evaluationResult = checkFact(sourceDocument, aiResponse)

        // isPass가 false이면 환각으로 판단
        return HallucinationResult(
                isHallucination = !evaluationResult.isPass,
                sourceDocument = sourceDocument,
                aiResponse = aiResponse
        )
    }

    /**
     * 여러 주장을 한 번에 검증합니다.
     *
     * @param document 참조 문서
     * @param claims 검증할 주장 리스트
     * @return List<Boolean> 각 주장의 사실 여부
     */
    fun checkMultipleClaims(document: String, claims: List<String>): List<Boolean> {
        return claims.map { claim -> checkFact(document, claim).isPass }
    }

    /**
     * 상세한 사실 확인 결과를 반환합니다.
     *
     * @param document 참조 문서
     * @param claim 검증할 주장
     * @return Map 사실 확인 결과 상세 정보
     */
    fun getDetailedFactCheck(document: String, claim: String): Map<String, Any> {
        val evaluationResult = checkFact(document, claim)

        return mapOf(
                "document" to document,
                "claim" to claim,
                "isSupported" to evaluationResult.isPass,
                "timestamp" to System.currentTimeMillis()
        )
    }

    /**
     * 여러 문서를 기반으로 주장을 검증합니다.
     *
     * @param documents 참조 문서 리스트
     * @param claim 검증할 주장
     * @return Boolean 하나라도 뒷받침하면 true
     */
    fun checkFactAgainstMultipleDocuments(documents: List<String>, claim: String): Boolean {
        // 여러 문서 중 하나라도 주장을 뒷받침하면 true
        return documents.any { document -> checkFact(document, claim).isPass }
    }
}

/** 환각 탐지 결과를 담는 데이터 클래스 */
data class HallucinationResult(
        val isHallucination: Boolean,
        val sourceDocument: String,
        val aiResponse: String
)
