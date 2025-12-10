package com.example.mcp.controller

import org.springframework.web.bind.annotation.*

/**
 * Sample 01: Basic MCP Client
 *
 * MCP (Model Context Protocol) 기본 사용법
 * - MCP Client 설정
 * - Transport 방식 (STDIO, HTTP, SSE)
 */
@RestController
@RequestMapping("/api/mcp")
class BasicMcpController {

    @GetMapping("/info")
    fun getInfo(): Map<String, Any> {
        return mapOf(
                "protocol" to "MCP (Model Context Protocol)",
                "description" to "Standardized protocol for AI model communication",
                "features" to
                        listOf(
                                "Resources - External data sources",
                                "Tools - Function calling",
                                "Prompts - Template management"
                        ),
                "transports" to
                        listOf(
                                "STDIO - Process communication",
                                "HTTP - Remote servers",
                                "SSE - Server-Sent Events"
                        )
        )
    }

    @GetMapping("/status")
    fun getStatus(): Map<String, String> {
        return mapOf(
                "status" to "MCP Client Ready",
                "message" to "Configure MCP servers in application.yml"
        )
    }
}
