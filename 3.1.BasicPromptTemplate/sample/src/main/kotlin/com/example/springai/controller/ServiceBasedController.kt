package com.example.springai.controller

import org.springframework.web.bind.annotation.*
import com.example.springai.service.TemplateService

/**
 * TemplateService를 사용하는 컨트롤러
 * 서비스 레이어 분리를 통한 재사용성 향상 예제
 */
@RestController
@RequestMapping("/api/service")
class ServiceBasedController(
    private val templateService: TemplateService
) {
    
    /**
     * 인사말 생성
     * GET http://localhost:8080/api/service/greet/홍길동
     */
    @GetMapping("/greet/{name}")
    fun greet(@PathVariable name: String): String {
        return templateService.generateGreeting(name)
    }
    
    /**
     * 질문 답변
     * POST http://localhost:8080/api/service/ask
     */
    @PostMapping("/ask")
    fun ask(@RequestBody request: AskRequest): String {
        return templateService.answerQuestion(
            userName = request.userName,
            question = request.question,
            context = request.context ?: ""
        )
    }
    
    /**
     * 내용 요약
     * POST http://localhost:8080/api/service/summarize
     */
    @PostMapping("/summarize")
    fun summarize(@RequestBody request: SummarizeRequest): String {
        return templateService.summarize(request.content)
    }
}

data class AskRequest(
    val userName: String,
    val question: String,
    val context: String? = null
)

data class SummarizeRequest(
    val content: String
)

