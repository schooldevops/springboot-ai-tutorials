package com.example.annotations.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class McpServerServiceTest {

    @Autowired lateinit var service: McpServerService

    @Test
    fun `should calculate addition`() {
        val result = service.calculate(10, 5, "add")
        assertThat(result).isEqualTo(15)
    }

    @Test
    fun `should get system info`() {
        val info = service.getSystemInfo()
        assertThat(info["version"]).isEqualTo("1.0.0")
    }

    @Test
    fun `should get prompt template`() {
        val template = service.getPromptTemplate("greeting")
        assertThat(template).contains("Hello")
    }
}
