package com.example.springai

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.*
import org.springframework.web.bind.annotation.*
import java.time.Instant

/**
 * ChatModel의 고급 사용법을 보여주는 컨트롤러
 */
@RestController
@RequestMapping("/api/advanced")
class AdvancedChatController(
    private val chatModel: ChatModel
) {
    
    /**
     * 여러 메시지를 포함한 대화 예제
     * GET http://localhost:8080/api/advanced/conversation
     */
    @GetMapping("/conversation")
    fun conversation(): String {
        // 대화 이력 생성
        val messages = listOf(
            SystemMessage("당신은 친절한 어시스턴트입니다."),
            UserMessage("안녕하세요!"),
            AssistantMessage("안녕하세요! 무엇을 도와드릴까요?"),
            UserMessage("Spring AI에 대해 설명해주세요")
        )
        
        val prompt = Prompt(messages)
        val response = chatModel.call(prompt)
        
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
    
    /**
     * 역할 기반 챗봇 예제
     * POST http://localhost:8080/api/advanced/role
     * Body: {"role": "teacher", "message": "수학을 어떻게 공부하면 좋을까요?"}
     */
    @PostMapping("/role")
    fun roleBasedChat(@RequestBody request: RoleChatRequest): String {
        // 역할에 따라 SystemMessage 설정
        val systemMessage = when (request.role.lowercase()) {
            "teacher" -> SystemMessage("당신은 친절한 선생님입니다. 교육적이고 이해하기 쉽게 설명해주세요.")
            "doctor" -> SystemMessage("당신은 전문 의사입니다. 의학적 지식을 바탕으로 정확하게 답변해주세요.")
            "chef" -> SystemMessage("당신은 유명한 셰프입니다. 요리에 대한 열정과 전문성을 보여주세요.")
            else -> SystemMessage("당신은 도움이 되는 어시스턴트입니다.")
        }
        
        val prompt = Prompt(listOf(
            systemMessage,
            UserMessage(request.message)
        ))
        
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
    
    /**
     * 대화 이력 관리 예제
     * POST http://localhost:8080/api/advanced/history
     * Body: {"sessionId": "user123", "message": "안녕하세요"}
     */
    @PostMapping("/history")
    fun chatWithHistory(@RequestBody request: HistoryChatRequest): HistoryChatResponse {
        // 실제로는 서비스나 세션 스토리지를 사용해야 함
        // 여기서는 간단한 예제로 보여줌
        
        val messages = mutableListOf<Message>()
        
        // SystemMessage 추가 (선택적)
        messages.add(SystemMessage("당신은 친절한 어시스턴트입니다."))
        
        // 사용자 메시지 추가
        messages.add(UserMessage(request.message))
        
        val prompt = Prompt(messages)
        val response = chatModel.call(prompt)
        
        val assistantText = response.results.firstOrNull()?.output?.text ?: "응답 없음"
        
        // 대화 이력에 AI 응답 추가 (실제 구현에서는 저장소에 저장)
        messages.add(AssistantMessage(assistantText))
        
        return HistoryChatResponse(
            sessionId = request.sessionId,
            message = assistantText,
            messageCount = messages.size,
            timestamp = Instant.now()
        )
    }
    
    /**
     * 응답 메타데이터 확인 예제
     * GET http://localhost:8080/api/advanced/metadata
     */
    @GetMapping("/metadata")
    fun metadataExample(): Map<String, Any> {
        val prompt = Prompt(UserMessage("간단히 인사해주세요"))
        val response = chatModel.call(prompt)
        
        val metadataMap = if (response.metadata != null) {
            // ChatResponseMetadata를 Map으로 변환
            mapOf(
                "usage" to (response.metadata.usage?.let {
                    mapOf(
                        "promptTokens" to it.promptTokens,
                        "generationTokens" to it.generationTokens,
                        "totalTokens" to it.totalTokens
                    )
                } ?: emptyMap<String, Any>()),
                "model" to (response.metadata.model ?: "unknown")
            )
        } else {
            emptyMap<String, Any>()
        }
        
        return mapOf(
            "result" to (response.results.firstOrNull() != null),
            "output" to (response.results.firstOrNull()?.output != null),
            "text" to (response.results.firstOrNull()?.output?.text ?: "없음"),
            "metadata" to metadataMap
        )
    }
}

data class RoleChatRequest(
    val role: String,
    val message: String
)

data class HistoryChatRequest(
    val sessionId: String,
    val message: String
)

data class HistoryChatResponse(
    val sessionId: String,
    val message: String,
    val messageCount: Int,
    val timestamp: Instant
)
