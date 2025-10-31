package com.example.springai.controller

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.*
import org.springframework.web.bind.annotation.*
import com.example.springai.model.MessageRequest
import java.util.concurrent.ConcurrentHashMap

/**
 * 대화 이력을 관리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/conversation")
class ConversationController(
    private val chatModel: ChatModel
) {
    // 세션별 대화 이력 저장 (실제 프로덕션에서는 DB 사용 권장)
    private val conversations = ConcurrentHashMap<String, MutableList<Message>>()
    
    /**
     * 대화 계속하기
     * POST http://localhost:8080/api/conversation/{sessionId}
     */
    @PostMapping("/{sessionId}")
    fun continueConversation(
        @PathVariable sessionId: String,
        @RequestBody request: MessageRequest
    ): ConversationResponse {
        // 세션별 대화 이력 가져오기 또는 생성
        val messages = conversations.getOrPut(sessionId) {
            mutableListOf(
                SystemMessage("당신은 친절한 어시스턴트입니다.")
            )
        }
        
        // 사용자 메시지 추가
        messages.add(UserMessage(request.message))
        
        // LLM 호출
        val prompt = Prompt(messages)
        val response = chatModel.call(prompt)
        val assistantText = response.results.firstOrNull()?.output?.text ?: "응답 없음"
        
        // 어시스턴트 응답을 대화 이력에 추가
        messages.add(AssistantMessage(assistantText))
        
        return ConversationResponse(
            message = assistantText,
            sessionId = sessionId
        )
    }
    
    /**
     * 대화 이력 조회
     * GET http://localhost:8080/api/conversation/{sessionId}
     */
    @GetMapping("/{sessionId}")
    fun getConversationHistory(@PathVariable sessionId: String): ConversationHistoryResponse {
        val messages = conversations[sessionId] ?: emptyList()
        return ConversationHistoryResponse(
            sessionId = sessionId,
            messageCount = messages.size,
            messages = messages.map { msg ->
                // Message의 내용을 가져오는 방법 (toString() 사용)
                val content = msg.toString().let { str ->
                    // Message 타입에 따라 내용 추출
                    when {
                        str.contains("content=") -> {
                            str.substringAfter("content=")
                                .substringBefore(",")
                                .trim('"', ' ', ')', '}')
                        }
                        else -> str
                    }
                }
                mapOf(
                    "type" to msg.javaClass.simpleName,
                    "content" to content
                )
            }
        )
    }
    
    /**
     * 대화 이력 초기화
     * DELETE http://localhost:8080/api/conversation/{sessionId}
     */
    @DeleteMapping("/{sessionId}")
    fun clearConversation(@PathVariable sessionId: String): Map<String, String> {
        conversations.remove(sessionId)
        return mapOf(
            "status" to "cleared",
            "sessionId" to sessionId
        )
    }
    
    /**
     * 대화 이력 길이 제한 (토큰 관리)
     * POST http://localhost:8080/api/conversation/{sessionId}/limit
     */
    @PostMapping("/{sessionId}/limit")
    fun limitConversationHistory(
        @PathVariable sessionId: String,
        @RequestParam(defaultValue = "10") maxMessages: Int
    ): Map<String, Any> {
        val messages = conversations[sessionId] ?: return mapOf("error" to "Session not found")
        
        // SystemMessage는 항상 유지
        val systemMessages = messages.filterIsInstance<SystemMessage>()
        val otherMessages = messages.filter { it !is SystemMessage }
        
        // 최근 메시지만 유지
        val maxOtherMessages = maxOf(1, maxMessages - systemMessages.size)
        val recentMessages = otherMessages.takeLast(maxOtherMessages)
        
        conversations[sessionId] = (systemMessages + recentMessages).toMutableList()
        
        return mapOf(
            "sessionId" to sessionId,
            "originalCount" to messages.size,
            "newCount" to (systemMessages.size + recentMessages.size)
        )
    }
}

data class ConversationResponse(
    val message: String,
    val sessionId: String
)

data class ConversationHistoryResponse(
    val sessionId: String,
    val messageCount: Int,
    val messages: List<Map<String, Any>>
)

