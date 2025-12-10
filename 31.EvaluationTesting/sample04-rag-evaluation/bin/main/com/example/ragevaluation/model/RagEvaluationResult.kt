package com.example.ragevaluation.model

import org.springframework.ai.evaluation.EvaluationResponse

/** RAG 평가 결과를 담는 데이터 클래스 */
data class RagEvaluationResult(
        val question: String,
        val answer: String,
        val retrievedDocuments: List<String>,
        val relevanceEvaluation: EvaluationResponse,
        val factCheckEvaluation: EvaluationResponse
)

/** 평가 품질 점수 */
data class QualityScore(
        val relevanceScore: Double,
        val factCheckScore: Double,
        val overallScore: Double
)
