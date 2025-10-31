package com.example.springai.controller

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.*
import org.springframework.web.bind.annotation.*

/**
 * Few-shot prompting 예제를 보여주는 컨트롤러
 */
@RestController
@RequestMapping("/api/few-shot")
class FewShotController(
    private val chatModel: ChatModel
) {
    
    /**
     * 기본 Few-shot 예제
     * POST http://localhost:8080/api/few-shot/basic
     * Body: {"question": "Kotlin에서 리스트를 어떻게 만드나요?"}
     */
    @PostMapping("/basic")
    fun basicFewShot(@RequestBody request: FewShotRequest): String {
        val messages = listOf(
            SystemMessage(
                """
                당신은 코딩 도우미입니다.
                사용자의 질문에 대해 간단하고 명확하게 답변해주세요.
                """.trimIndent()
            ),
            // 예시 1
            UserMessage("Python에서 문자열을 어떻게 합치나요?"),
            AssistantMessage("문자열은 + 연산자나 .join() 메서드를 사용하여 합칠 수 있습니다. 예: 'Hello' + 'World'"),
            
            // 예시 2
            UserMessage("리스트를 어떻게 정렬하나요?"),
            AssistantMessage("리스트는 .sort() 메서드나 sorted() 함수를 사용하여 정렬할 수 있습니다. 예: [3, 1, 2].sort()"),
            
            // 실제 질문
            UserMessage(request.question)
        )
        
        val prompt = Prompt(messages)
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
    
    /**
     * 형식 지정 Few-shot 예제
     * POST http://localhost:8080/api/few-shot/format
     */
    @PostMapping("/format")
    fun formatFewShot(@RequestBody request: FormatRequest): String {
        val messages = listOf(
            SystemMessage(
                """
                당신은 정보를 구조화하여 제공하는 어시스턴트입니다.
                항상 다음 형식으로 답변해주세요:
                - 요약 (1문장)
                - 주요 포인트 (3-5개)
                - 예시 (1개)
                """.trimIndent()
            ),
            // 예시
            UserMessage("의존성 주입이란 무엇인가요?"),
            AssistantMessage(
                """
                요약: 의존성 주입은 객체가 필요한 의존성을 외부에서 주입받는 디자인 패턴입니다.
                
                주요 포인트:
                1. 객체 간 결합도를 낮춤
                2. 테스트 용이성 향상
                3. 코드 재사용성 증대
                4. 유연한 구조 제공
                
                예시: Spring Framework의 @Autowired 어노테이션
                """.trimIndent()
            ),
            
            // 실제 질문
            UserMessage(request.question)
        )
        
        val prompt = Prompt(messages)
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}

data class FewShotRequest(
    val question: String
)

data class FormatRequest(
    val question: String
)

