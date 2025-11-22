package com.example.weatheralarm.controller

import com.example.weatheralarm.dto.ChatRequest
import com.example.weatheralarm.dto.ChatResponse
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.ollama.api.OllamaOptions
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/weather")
class WeatherController(
    private val chatModel: ChatModel
) {

    @PostMapping("/chat")
    fun chat(@RequestBody request: ChatRequest): ChatResponse {
        val options = OllamaOptions.builder()
            .withFunction("getWeather")
            .build()
        
        val prompt = Prompt(request.message, options)
        val response = chatModel.call(prompt)
        
        return ChatResponse(
            message = response.result.output.content,
            functionCalled = "getWeather"
        )
    }
    
    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "service" to "Weather Alarm API"
        )
    }
}
