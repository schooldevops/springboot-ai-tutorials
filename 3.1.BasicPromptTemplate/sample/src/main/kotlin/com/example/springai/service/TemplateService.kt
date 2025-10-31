package com.example.springai.service

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.stereotype.Service

/**
 * PromptTemplate을 재사용하기 위한 서비스
 * 자주 사용하는 템플릿들을 관리
 */
@Service
class TemplateService(
    private val chatModel: ChatModel
) {
    
    // 자주 사용하는 템플릿들을 미리 정의
    private val greetingTemplate = PromptTemplate(
        "안녕하세요 {name}님! 오늘도 좋은 하루 되세요."
    )
    
    private val questionTemplate = PromptTemplate(
        """
        {userName}님이 질문하셨습니다:
        
        질문: {question}
        
        {additionalContext}
        
        친절하고 정확하게 답변해주세요.
        """.trimIndent()
    )
    
    private val summaryTemplate = PromptTemplate(
        """
        다음 내용을 요약해주세요:
        
        {content}
        
        핵심 내용을 3-5문장으로 간결하게 요약해주세요.
        """.trimIndent()
    )
    
    /**
     * 인사 프롬프트 생성
     */
    fun createGreetingPrompt(name: String): Prompt {
        return greetingTemplate.create(mapOf("name" to name))
    }
    
    /**
     * 질문 프롬프트 생성
     */
    fun createQuestionPrompt(
        userName: String,
        question: String,
        additionalContext: String = ""
    ): Prompt {
        return questionTemplate.create(mapOf(
            "userName" to userName,
            "question" to question,
            "additionalContext" to if (additionalContext.isNotEmpty()) {
                "추가 컨텍스트: $additionalContext"
            } else {
                ""
            }
        ))
    }
    
    /**
     * 요약 프롬프트 생성
     */
    fun createSummaryPrompt(content: String): Prompt {
        return summaryTemplate.create(mapOf("content" to content))
    }
    
    /**
     * 인사말 생성
     */
    fun generateGreeting(name: String): String {
        val prompt = createGreetingPrompt(name)
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
    
    /**
     * 질문 답변 생성
     */
    fun answerQuestion(
        userName: String,
        question: String,
        context: String = ""
    ): String {
        val prompt = createQuestionPrompt(userName, question, context)
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
    
    /**
     * 내용 요약 생성
     */
    fun summarize(content: String): String {
        val prompt = createSummaryPrompt(content)
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}

