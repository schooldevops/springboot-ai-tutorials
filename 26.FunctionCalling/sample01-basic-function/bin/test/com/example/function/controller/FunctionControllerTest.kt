package com.example.function.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.client.ChatClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FunctionControllerTest {

    @Autowired lateinit var chatClient: ChatClient

    @Test
    fun `should call calculator function for addition`() {
        val response = chatClient.prompt().user("What is 15 plus 27?").call().content()

        assertThat(response).containsIgnoringCase("42")
    }

    @Test
    fun `should call calculator function for multiplication`() {
        val response = chatClient.prompt().user("Calculate 8 times 7").call().content()

        assertThat(response).containsIgnoringCase("56")
    }
}
