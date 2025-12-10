package com.example.annotations.controller

import com.example.annotations.service.McpServerService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/mcp")
class ServerAnnotationsController(private val mcpService: McpServerService) {

    @PostMapping("/tool/calculate")
    fun calculate(@RequestBody request: Map<String, Any>): Map<String, Int> {
        val a = (request["a"] as Number).toInt()
        val b = (request["b"] as Number).toInt()
        val operation = request["operation"] as String
        return mapOf("result" to mcpService.calculate(a, b, operation))
    }

    @GetMapping("/resource/system-info")
    fun getSystemInfo(): Map<String, Any> {
        return mcpService.getSystemInfo()
    }

    @GetMapping("/prompt/{name}")
    fun getPrompt(@PathVariable name: String): Map<String, String> {
        return mapOf("template" to mcpService.getPromptTemplate(name))
    }

    @GetMapping("/info")
    fun getInfo(): Map<String, Any> {
        return mapOf(
                "annotations" to
                        mapOf(
                                "@McpTool" to "Executable functions (calculate, convertCase)",
                                "@McpResource" to "Data sources (systemInfo, userProfile)",
                                "@McpPrompt" to "Templates (greeting, farewell)"
                        )
        )
    }
}
