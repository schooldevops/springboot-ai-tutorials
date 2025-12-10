package com.example.mcpserver.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BasicMcpServerControllerTest {

    @Autowired lateinit var controller: BasicMcpServerController

    @Test
    fun `should return server info`() {
        val info = controller.getInfo()
        assertThat(info["name"]).isEqualTo("Basic MCP Server")
        assertThat(info["capabilities"]).isNotNull()
    }

    @Test
    fun `should return running status`() {
        val status = controller.getStatus()
        assertThat(status["status"]).isEqualTo("Running")
    }
}
