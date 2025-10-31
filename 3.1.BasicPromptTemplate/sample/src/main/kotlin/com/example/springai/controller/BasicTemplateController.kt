package com.example.springai.controller

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.web.bind.annotation.*

/**
 * PromptTemplate의 기본 사용법을 보여주는 컨트롤러
 */
@RestController
@RequestMapping("/api/basic")
class BasicTemplateController(
    private val chatModel: ChatModel
) {
    
    /**
     * 단순한 변수 1개 사용 예제
     * GET http://localhost:8080/api/basic/greet/홍길동
     */
    @GetMapping("/greet/{name}")
    fun greet(@PathVariable name: String): String {
        // 1. 템플릿 생성
        val template = PromptTemplate("안녕하세요 {name}님! 간단히 자기소개 해주세요.")
        
        // 2. 변수 바인딩하여 Prompt 직접 생성
        val prompt = template.create(mapOf("name" to name))
        
        // 4. ChatModel 호출
        val response = chatModel.call(prompt)
        
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
    
    /**
     * 여러 변수 사용 예제
     * POST http://localhost:8080/api/basic/personalized
     * Body: {"name": "홍길동", "job": "개발자", "interest": "AI"}
     */
    @PostMapping("/personalized")
    fun personalizedChat(@RequestBody request: PersonalizedRequest): String {
        val template = PromptTemplate(
            """
            당신은 {name}님의 개인 어시스턴트입니다.
            
            사용자 정보:
            - 이름: {name}
            - 직업: {job}
            - 관심사: {interest}
            
            위 정보를 바탕으로 친절하게 인사해주세요.
            """.trimIndent()
        )
        
        val prompt = template.create(mapOf(
            "name" to request.name,
            "job" to request.job,
            "interest" to request.interest
        ))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
    
    /**
     * 동적 질문 처리 예제
     * POST http://localhost:8080/api/basic/question
     * Body: {"userName": "홍길동", "question": "Spring AI에 대해 설명해주세요"}
     */
    @PostMapping("/question")
    fun answerQuestion(@RequestBody request: QuestionRequest): String {
        val template = PromptTemplate(
            """
            {userName}님이 질문하셨습니다:
            
            질문: {question}
            
            친절하고 정확하게 답변해주세요.
            """.trimIndent()
        )
        
        val prompt = template.create(mapOf(
            "userName" to request.userName,
            "question" to request.question
        ))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}

data class PersonalizedRequest(
    val name: String,
    val job: String,
    val interest: String
)

data class QuestionRequest(
    val userName: String,
    val question: String
)

