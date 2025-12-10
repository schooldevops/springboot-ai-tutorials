package com.example.springai.controller

import com.example.springai.model.MessageRequest
import com.example.springai.service.RoleClientService
import org.springframework.web.bind.annotation.*

/** ChatClient를 사용하는 역할 기반 컨트롤러 */
@RestController
@RequestMapping("/api/client")
class RoleClientController(private val roleClientService: RoleClientService) {

    /**
     * ChatClient를 사용한 역할 기반 채팅 POST http://localhost:9000/api/client/chat/{role} Body: {"message":
     * "수학을 어떻게 공부하면 좋을까요?"}
     *
     * 지원되는 역할: teacher, doctor, chef, developer
     */
    @PostMapping("/chat/{role}")
    fun chat(@PathVariable role: String, @RequestBody request: MessageRequest): String {
        return roleClientService.chat(role, request.message)
    }

    /**
     * 커스텀 역할 채팅 POST http://localhost:9000/api/client/custom Body: { "role": "시인", "instructions":
     * "아름다운 시를 작성해주세요.", "principles": ["운율을 살리기", "감성적으로 표현하기"], "message": "봄에 대한 시를 써주세요" }
     */
    @PostMapping("/custom")
    fun customRoleChat(@RequestBody request: CustomRoleClientRequest): String {
        return roleClientService.customChat(
                role = request.role,
                instructions = request.instructions,
                principles = request.principles,
                message = request.message
        )
    }
}

data class CustomRoleClientRequest(
        val role: String,
        val instructions: String,
        val principles: List<String>,
        val message: String
)
