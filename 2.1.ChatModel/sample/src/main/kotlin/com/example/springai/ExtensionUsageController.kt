package com.example.springai

import org.springframework.ai.chat.model.ChatModel
import org.springframework.web.bind.annotation.*

/**
 * 확장 함수 사용 예제를 보여주는 컨트롤러
 */
@RestController
@RequestMapping("/api/extension")
class ExtensionUsageController(
    private val chatModel: ChatModel
) {
    
    /**
     * 확장 함수 사용 예제
     * GET http://localhost:8080/api/extension/simple?message=안녕하세요
     */
    @GetMapping("/simple")
    fun useSimpleExtension(@RequestParam message: String): String {
        // 확장 함수 사용
        return chatModel.simpleCall(message)
    }
    
    /**
     * 안전한 호출 확장 함수 사용 예제
     * GET http://localhost:8080/api/extension/safe?message=안녕하세요
     */
    @GetMapping("/safe")
    fun useSafeExtension(@RequestParam message: String): Map<String, Any?> {
        val result = chatModel.safeCall(message)
        
        return result.fold(
            onSuccess = { text ->
                mapOf("success" to true, "message" to text)
            },
            onFailure = { error ->
                mapOf("success" to false, "error" to (error.message ?: "알 수 없는 오류"))
            }
        )
    }
    
    /**
     * 여러 메시지를 한 번에 전송하는 확장 함수 사용
     * GET http://localhost:8080/api/extension/multi
     */
    @GetMapping("/multi")
    fun useMultiExtension(): String {
        return chatModel.multiCall(
            "첫 번째 질문입니다.",
            "두 번째 질문입니다.",
            "세 번째 질문입니다."
        )
    }
}

