package com.example.mcpserver.controller

import org.springframework.web.bind.annotation.*

/**
 * Sample 01: Basic MCP Server
 *
 * MCP Server 기본 구성
 * - Resources, Tools, Prompts 제공
 * - STDIO/WebMVC Transport
 */
@RestController
@RequestMapping("/api/server")
class BasicMcpServerController {

    @GetMapping("/info")
    fun getInfo(): Map<String, Any> {
        return mapOf(
                "name" to "Basic MCP Server",
                "version" to "1.0.0",
                "capabilities" to
                        listOf(
                                "Resources - Data sources",
                                "Tools - Functions",
                                "Prompts - Templates"
                        ),
                "transports" to
                        listOf(
                                "STDIO - Process communication",
                                "WebMVC - HTTP",
                                "WebMVC Reactive - Async HTTP"
                        )
        )
    }

    @GetMapping("/status")
    fun getStatus(): Map<String, String> {
        return mapOf("status" to "Running", "message" to "MCP Server is ready to serve")
    }
}
