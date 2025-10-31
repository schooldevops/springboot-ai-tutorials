package com.example.springai.controller

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.web.bind.annotation.*
import com.example.springai.service.RoleService
import com.example.springai.model.MessageRequest

/**
 * RoleService를 사용하는 컨트롤러
 */
@RestController
@RequestMapping("/api/service")
class ServiceBasedController(
    private val chatModel: ChatModel,
    private val roleService: RoleService
) {
    
    /**
     * 서비스를 통한 역할 기반 채팅
     * POST http://localhost:8080/api/service/chat/{role}
     */
    @PostMapping("/chat/{role}")
    fun chat(
        @PathVariable role: String,
        @RequestBody request: MessageRequest
    ): String {
        val systemMessage = roleService.createSystemMessage(role)
        val prompt = Prompt(listOf(
            systemMessage,
            UserMessage(request.message)
        ))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
    
    /**
     * 커스텀 역할 채팅
     * POST http://localhost:8080/api/service/custom
     */
    @PostMapping("/custom")
    fun customRoleChat(@RequestBody request: CustomRoleRequest): String {
        val systemMessage = roleService.createCustomSystemMessage(
            role = request.role,
            instructions = request.instructions,
            principles = request.principles
        )
        val prompt = Prompt(listOf(
            systemMessage,
            UserMessage(request.message)
        ))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}

data class CustomRoleRequest(
    val role: String,
    val instructions: String,
    val principles: List<String>,
    val message: String
)

