package com.example.springai.controller

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.*
import org.springframework.web.bind.annotation.*

/**
 * 역할 기반 메시지를 사용하는 컨트롤러
 */
@RestController
@RequestMapping("/api/role")
class RoleBasedController(
    private val chatModel: ChatModel
) {
    
    /**
     * 역할 기반 챗봇 예제
     * POST http://localhost:8080/api/role/chat
     * Body: {"role": "teacher", "message": "수학을 어떻게 공부하면 좋을까요?"}
     */
    @PostMapping("/chat")
    fun roleBasedChat(@RequestBody request: RoleChatRequest): String {
        // 역할에 따라 SystemMessage 설정
        val systemMessage = when (request.role.lowercase()) {
            "teacher" -> SystemMessage(
                "당신은 친절한 선생님입니다. 교육적이고 이해하기 쉽게 설명해주세요."
            )
            "doctor" -> SystemMessage(
                "당신은 전문 의사입니다. 의학적 지식을 바탕으로 정확하게 답변해주세요."
            )
            "chef" -> SystemMessage(
                "당신은 유명한 셰프입니다. 요리에 대한 열정과 전문성을 보여주세요."
            )
            "developer" -> SystemMessage(
                "당신은 숙련된 소프트웨어 개발자입니다. 명확하고 실행 가능한 코드 예제를 제공해주세요."
            )
            "translator" -> SystemMessage(
                "당신은 전문 번역가입니다. 자연스럽고 정확한 번역을 제공해주세요."
            )
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
     * 동적 역할 설정 예제
     * POST http://localhost:8080/api/role/dynamic
     */
    @PostMapping("/dynamic")
    fun dynamicRoleChat(@RequestBody request: DynamicRoleRequest): String {
        val systemMessage = SystemMessage(
            """
            당신은 ${request.role} 역할을 맡고 있습니다.
            ${request.instructions}
            
            다음 원칙을 따라주세요:
            ${request.principles.joinToString("\n") { "- $it" }}
            """.trimIndent()
        )
        
        val prompt = Prompt(listOf(
            systemMessage,
            UserMessage(request.message)
        ))
        
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}

data class RoleChatRequest(
    val role: String,
    val message: String
)

data class DynamicRoleRequest(
    val role: String,
    val instructions: String,
    val principles: List<String>,
    val message: String
)

