package com.example.springai.controller

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.web.bind.annotation.*

/**
 * PromptTemplate의 고급 활용 예제
 */
@RestController
@RequestMapping("/api/advanced")
class AdvancedTemplateController(
    private val chatModel: ChatModel
) {
    
    /**
     * 이메일 생성 예제
     * POST http://localhost:8080/api/advanced/email
     */
    @PostMapping("/email")
    fun generateEmail(@RequestBody request: EmailRequest): String {
        val template = PromptTemplate(
            """
            다음 정보를 바탕으로 전문적인 이메일을 작성해주세요:
            
            수신자: {recipient}
            제목: {subject}
            목적: {purpose}
            추가 요구사항: {requirements}
            
            이메일은 정중하고 명확하게 작성해주세요.
            """.trimIndent()
        )
        
        val prompt = template.create(mapOf(
            "recipient" to request.recipient,
            "subject" to request.subject,
            "purpose" to request.purpose,
            "requirements" to (request.requirements ?: "없음")
        ))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "이메일 생성 실패"
    }
    
    /**
     * 코딩 도우미 예제
     * POST http://localhost:8080/api/advanced/code-help
     */
    @PostMapping("/code-help")
    fun helpWithCode(@RequestBody request: CodeHelpRequest): String {
        val template = PromptTemplate(
            """
            다음 정보를 바탕으로 코딩 질문에 답변해주세요:
            
            프로그래밍 언어: {language}
            프레임워크: {framework}
            질문 내용: {question}
            현재 코드 컨텍스트: {context}
            
            명확하고 실행 가능한 예제 코드를 포함하여 답변해주세요.
            """.trimIndent()
        )
        
        val prompt = template.create(mapOf(
            "language" to request.language,
            "framework" to (request.framework ?: "없음"),
            "question" to request.question,
            "context" to (request.context ?: "없음")
        ))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "답변 생성 실패"
    }
    
    /**
     * 번역 서비스 예제
     * POST http://localhost:8080/api/advanced/translate
     */
    @PostMapping("/translate")
    fun translate(@RequestBody request: TranslationRequest): String {
        val template = PromptTemplate(
            """
            다음 텍스트를 {targetLanguage}로 번역해주세요:
            
            원본 언어: {sourceLanguage}
            번역할 텍스트: {text}
            
            자연스럽고 정확하게 번역해주세요.
            """.trimIndent()
        )
        
        val prompt = template.create(mapOf(
            "sourceLanguage" to request.sourceLanguage,
            "targetLanguage" to request.targetLanguage,
            "text" to request.text
        ))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "번역 실패"
    }
}

data class EmailRequest(
    val recipient: String,
    val subject: String,
    val purpose: String,
    val requirements: String? = null
)

data class CodeHelpRequest(
    val language: String,
    val framework: String?,
    val question: String,
    val context: String? = null
)

data class TranslationRequest(
    val sourceLanguage: String,
    val targetLanguage: String,
    val text: String
)

