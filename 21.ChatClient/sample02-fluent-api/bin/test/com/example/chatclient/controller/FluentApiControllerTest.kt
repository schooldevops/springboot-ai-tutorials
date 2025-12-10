package com.example.chatclient.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/** TDD: Sample 02 - Fluent API 테스트 */
@SpringBootTest
class FluentApiControllerTest {

    @Autowired lateinit var chatClient: ChatClient

    @Test
    fun `should use prompt with no arguments`() {
        // When
        val response =
                chatClient
                        .prompt()
                        .user("What is 3+3? Answer with just the number.")
                        .call()
                        .content()

        // Then
        assertThat(response).isNotNull()
        assertThat(response).contains("6")
    }

    @Test
    fun `should use prompt with String argument`() {
        // Given
        val question = "Say 'Hello World'"

        // When
        val response = chatClient.prompt(question).call().content()

        // Then
        assertThat(response).isNotNull()
        assertThat(response).containsIgnoringCase("hello")
    }

    @Test
    fun `should use prompt with Prompt object`() {
        // Given
        val prompt =
                Prompt(listOf(SystemMessage("You are a math tutor"), UserMessage("What is 10-3?")))

        // When
        val response = chatClient.prompt(prompt).call().content()

        // Then
        assertThat(response).isNotNull()
        assertThat(response).contains("7")
    }

    @Test
    fun `should chain user and system methods`() {
        // When
        val response =
                chatClient
                        .prompt()
                        .system("You are a helpful assistant. Be concise.")
                        .user("Explain AI in one sentence")
                        .call()
                        .content()

        // Then
        assertThat(response).isNotNull()
        assertThat(response).isNotEmpty()
    }

    @Test
    fun `should get content from call`() {
        // When
        val content = chatClient.prompt().user("Say 'Test'").call().content()

        // Then
        assertThat(content).isNotNull()
        assertThat(content).isNotEmpty()
    }
}
