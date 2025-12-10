package com.example.annotations.service

import org.springframework.stereotype.Service

/**
 * Sample 01: Server Annotations
 *
 * Demonstrates @McpTool, @McpResource, @McpPrompt
 */
@Service
class McpServerService {

    // @McpTool - Executable functions for AI
    fun calculate(a: Int, b: Int, operation: String): Int {
        return when (operation.lowercase()) {
            "add" -> a + b
            "subtract" -> a - b
            "multiply" -> a * b
            "divide" -> if (b != 0) a / b else 0
            else -> throw IllegalArgumentException("Unknown operation: $operation")
        }
    }

    fun convertCase(text: String, toCase: String): String {
        return when (toCase.lowercase()) {
            "upper" -> text.uppercase()
            "lower" -> text.lowercase()
            "title" -> text.split(" ").joinToString(" ") { it.capitalize() }
            else -> text
        }
    }

    // @McpResource - Data sources for AI
    fun getSystemInfo(): Map<String, Any> {
        return mapOf(
                "version" to "1.0.0",
                "environment" to "production",
                "maxConnections" to 100,
                "timeout" to 30
        )
    }

    fun getUserProfile(userId: String): Map<String, Any> {
        return mapOf("id" to userId, "name" to "User $userId", "role" to "admin", "active" to true)
    }

    // @McpPrompt - Templates for AI
    fun getPromptTemplate(templateName: String): String {
        return when (templateName.lowercase()) {
            "greeting" -> "Hello {name}, welcome to our service!"
            "farewell" -> "Goodbye {name}, see you soon!"
            "notification" -> "Hi {name}, you have {count} new messages."
            else -> "Template not found"
        }
    }

    fun fillTemplate(template: String, params: Map<String, String>): String {
        var result = template
        params.forEach { (key, value) -> result = result.replace("{$key}", value) }
        return result
    }
}
