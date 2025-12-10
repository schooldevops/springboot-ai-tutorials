package com.example.springai.service

import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

/** ChatClient를 사용한 PromptTemplate 서비스 ChatModel 대신 더 현대적인 ChatClient API를 사용 */
@Service
class TemplateClientService(private val chatClientBuilder: ChatClient.Builder) {

    private val chatClient = chatClientBuilder.build()

    // 자주 사용하는 템플릿들을 미리 정의
    private val greetingTemplate = "안녕하세요 {name}님! 오늘도 좋은 하루 되세요."

    private val questionTemplate =
            """
        {userName}님이 질문하셨습니다:
        
        질문: {question}
        
        {additionalContext}
        
        친절하고 정확하게 답변해주세요.
    """.trimIndent()

    private val summaryTemplate =
            """
        다음 내용을 요약해주세요:
        
        {content}
        
        핵심 내용을 3-5문장으로 간결하게 요약해주세요.
    """.trimIndent()

    /** 인사말 생성 (ChatClient 사용) */
    fun generateGreeting(name: String): String {
        return chatClient
                .prompt()
                .user { u -> u.text(greetingTemplate).param("name", name) }
                .call()
                .content()
                ?: "응답 없음"
    }

    /** 질문 답변 생성 (ChatClient 사용) */
    fun answerQuestion(userName: String, question: String, context: String = ""): String {
        val additionalContext =
                if (context.isNotEmpty()) {
                    "추가 컨텍스트: $context"
                } else {
                    ""
                }

        return chatClient
                .prompt()
                .user { u ->
                    u.text(questionTemplate)
                            .param("userName", userName)
                            .param("question", question)
                            .param("additionalContext", additionalContext)
                }
                .call()
                .content()
                ?: "응답 없음"
    }

    /** 내용 요약 생성 (ChatClient 사용) */
    fun summarize(content: String): String {
        return chatClient
                .prompt()
                .user { u -> u.text(summaryTemplate).param("content", content) }
                .call()
                .content()
                ?: "응답 없음"
    }
}
