package com.example.wikichatbot.controller

import com.example.wikichatbot.dto.ChatRequest
import com.example.wikichatbot.dto.ChatResponse
import com.example.wikichatbot.dto.IngestRequest
import com.example.wikichatbot.service.DocumentService
import com.example.wikichatbot.service.RAGService
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.web.bind.annotation.*
import java.io.File

@RestController
@RequestMapping("/api/wiki")
class WikiChatController(
    private val documentService: DocumentService,
    private val ragService: RAGService,
    private val vectorStore: VectorStore
) {

    /**
     * 문서 인제스트 (디렉토리)
     * POST /api/wiki/ingest
     */
    @PostMapping("/ingest")
    fun ingestDocuments(@RequestBody request: IngestRequest): Map<String, Any> {
        val directory = request.directory ?: "wiki-documents"
        
        return try {
            val documents = documentService.loadMarkdownDirectory(directory)
            vectorStore.add(documents)
            
            // 파일 목록 추출
            val files = File(directory).listFiles { file ->
                file.extension.lowercase() == "md"
            }?.map { it.name } ?: emptyList()
            
            mapOf(
                "status" to "success",
                "message" to "문서 인제스트 완료",
                "directory" to directory,
                "filesProcessed" to files.size,
                "files" to files,
                "totalChunks" to documents.size
            )
        } catch (e: Exception) {
            mapOf(
                "status" to "error",
                "message" to "문서 인제스트 실패: ${e.message}",
                "directory" to directory
            )
        }
    }

    /**
     * 단일 파일 인제스트
     * POST /api/wiki/ingest/file
     */
    @PostMapping("/ingest/file")
    fun ingestFile(@RequestBody request: IngestRequest): Map<String, Any> {
        val filePath = request.filePath
            ?: return mapOf(
                "status" to "error",
                "message" to "filePath가 필요합니다"
            )
        
        return try {
            val documents = documentService.loadAndSplitMarkdownFile(filePath)
            vectorStore.add(documents)
            
            mapOf(
                "status" to "success",
                "message" to "파일 인제스트 완료",
                "filePath" to filePath,
                "chunks" to documents.size
            )
        } catch (e: Exception) {
            mapOf(
                "status" to "error",
                "message" to "파일 인제스트 실패: ${e.message}",
                "filePath" to filePath
            )
        }
    }

    /**
     * RAG 기반 질문 답변 (POST)
     * POST /api/wiki/ask
     */
    @PostMapping("/ask")
    fun ask(@RequestBody request: ChatRequest): ChatResponse {
        return ragService.askQuestion(request.question, request.topK ?: 3)
    }

    /**
     * RAG 기반 질문 답변 (GET)
     * GET /api/wiki/ask?question=질문&topK=3
     */
    @GetMapping("/ask")
    fun askGet(
        @RequestParam question: String,
        @RequestParam(defaultValue = "3") topK: Int
    ): ChatResponse {
        return ragService.askQuestion(question, topK)
    }

    /**
     * 헬스 체크
     */
    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "service" to "Wiki Chatbot API",
            "timestamp" to System.currentTimeMillis().toString()
        )
    }
}
