package com.example.rag.controller

import com.example.rag.service.BasicRagService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rag")
class BasicRagController(private val ragService: BasicRagService) {

    data class QuestionRequest(val question: String)

    @PostMapping("/ask")
    fun ask(@RequestBody request: QuestionRequest): Map<String, String> {
        val answer = ragService.ask(request.question)
        return mapOf("question" to request.question, "answer" to answer)
    }

    @PostMapping("/ask-detailed")
    fun askDetailed(@RequestBody request: QuestionRequest): Map<String, Any> {
        return ragService.askWithMetadata(request.question)
    }

    @GetMapping("/info")
    fun getInfo(): Map<String, Any> {
        return mapOf(
                "name" to "Basic RAG Sample",
                "description" to "QuestionAnswerAdvisor를 사용한 기본 RAG",
                "features" to
                        listOf("SimpleVectorStore 사용", "QuestionAnswerAdvisor 통합", "샘플 문서 자동 로드")
        )
    }
}
