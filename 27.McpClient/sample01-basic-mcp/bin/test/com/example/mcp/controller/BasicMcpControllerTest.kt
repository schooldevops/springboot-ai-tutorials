package com.example.mcp.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BasicMcpControllerTest {

    @Autowired lateinit var controller: BasicMcpController

    @Test
    fun `should return MCP info`() {
        val info = controller.getInfo()
        assertThat(info["protocol"]).isEqualTo("MCP (Model Context Protocol)")
        assertThat(info["features"]).isNotNull()
    }

    @Test
    fun `should return status`() {
        val status = controller.getStatus()
        assertThat(status["status"]).isEqualTo("MCP Client Ready")
    }
}
