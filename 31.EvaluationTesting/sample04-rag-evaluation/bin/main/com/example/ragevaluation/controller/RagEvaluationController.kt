package com.example.ragevaluation.controller

import com.example.ragevaluation.service.RagEvaluationService
import org.springframework.web.bind.annotation.*

/**
 * RAG Evaluation Controller
 *
 * RAG 평가 기능을 제공하는 REST API 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/rag-evaluation")
class RagEvaluationController(private val ragEvaluationService: RagEvaluationService) {

    /** RAG를 사용하여 질문에 답변합니다. */
    @PostMapping("/ask")
    fun ask(@RequestBody request: QuestionRequest): Map<String, String> {
        val answer = ragEvaluationService.askQuestion(request.question)
        return mapOf("answer" to answer)
    }

    /** 질문에 답변하고 평가 결과를 함께 반환합니다. */
    @PostMapping("/ask-and-evaluate")
    fun askAndEvaluate(@RequestBody request: QuestionRequest): Map<String, Any> {
        val result = ragEvaluationService.askAndEvaluate(request.question)

        return mapOf(
                "question" to result.question,
                "answer" to result.answer,
                "retrievedDocuments" to result.retrievedDocuments,
                "relevancePass" to result.relevanceEvaluation.isPass,
                "factCheckPass" to result.factCheckEvaluation.isPass,
                "qualityScore" to ragEvaluationService.calculateQualityScore(result)
        )
    }

    /** 상세한 평가 리포트를 반환합니다. */
    @PostMapping("/detailed-report")
    fun getDetailedReport(@RequestBody request: QuestionRequest): Map<String, Any> {
        return ragEvaluationService.getDetailedEvaluationReport(request.question)
    }

    /** 여러 질문을 한 번에 평가합니다. */
    @PostMapping("/evaluate-batch")
    fun evaluateBatch(@RequestBody request: BatchQuestionRequest): List<Map<String, Any>> {
        val results = ragEvaluationService.evaluateMultipleQuestions(request.questions)

        return results.map { result ->
            mapOf(
                    "question" to result.question,
                    "answer" to result.answer,
                    "relevancePass" to result.relevanceEvaluation.isPass,
                    "factCheckPass" to result.factCheckEvaluation.isPass,
                    "qualityScore" to ragEvaluationService.calculateQualityScore(result)
            )
        }
    }

    /** 시스템 정보를 반환합니다. */
    @GetMapping("/info")
    fun getInfo(): Map<String, String> {
        return mapOf(
                "service" to "RAG Evaluation Service",
                "version" to "1.0.0",
                "description" to
                        "Complete RAG system with integrated evaluation using RelevancyEvaluator and FactCheckingEvaluator"
        )
    }
}

data class QuestionRequest(val question: String)

data class BatchQuestionRequest(val questions: List<String>)
