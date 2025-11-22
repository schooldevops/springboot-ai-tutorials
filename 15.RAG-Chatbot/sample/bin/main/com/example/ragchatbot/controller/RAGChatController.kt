package com.example.ragchatbot.controller

import com.example.ragchatbot.dto.ChatRequest
import com.example.ragchatbot.dto.ChatResponse
import com.example.ragchatbot.service.DocumentTracker
import com.example.ragchatbot.service.ETLPipelineService
import com.example.ragchatbot.service.RAGService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rag")
class RAGChatController(
    private val ragService: RAGService,
    private val etlPipelineService: ETLPipelineService,
    private val documentTracker: DocumentTracker
) {

    /**
     * RAG 기반 질문 답변 (POST)
     */
    @PostMapping("/ask")
    fun ask(@RequestBody request: ChatRequest): ChatResponse {
        return ragService.askQuestion(request.question, request.topK ?: 3)
    }

    /**
     * RAG 기반 질문 답변 (GET)
     */
    @GetMapping("/ask")
    fun askGet(
        @RequestParam question: String,
        @RequestParam(defaultValue = "3") topK: Int
    ): ChatResponse {
        return ragService.askQuestion(question, topK)
    }

    /**
     * 문서 상태 조회
     */
    @GetMapping("/documents/status")
    fun getDocumentStatus(): Map<String, Any> {
        return mapOf(
            "totalDocuments" to documentTracker.getTrackedDocumentCount(),
            "documents" to documentTracker.getTrackedDocuments().sorted()
        )
    }

    /**
     * 수동 문서 리프레시
     */
    @PostMapping("/refresh")
    fun refreshDocuments(): Map<String, Any> {
        val result = etlPipelineService.loadAndProcessDocuments()
        
        return mapOf(
            "status" to "success",
            "message" to "문서 리프레시 완료",
            "new" to (result["new"] as Any),
            "updated" to (result["updated"] as Any),
            "skipped" to (result["skipped"] as Any),
            "total" to (result["total"] as Any)
        )
    }

    /**
     * 헬스 체크
     */
    @GetMapping("/health")
    fun health(): Map<String, Any> {
        return mapOf(
            "status" to "UP",
            "service" to "RAG Chatbot API",
            "documentsLoaded" to documentTracker.getTrackedDocumentCount(),
            "timestamp" to System.currentTimeMillis().toString()
        )
    }
}
