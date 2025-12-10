package com.example.chatclient.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.client.ChatClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/** TDD: 테스트 먼저 작성 BasicChatClientController의 기능을 테스트 */
@SpringBootTest
class BasicChatClientControllerTest {

    @Autowired lateinit var chatClient: ChatClient

    @Test
    fun `should create ChatClient bean`() {
        // Then
        assertThat(chatClient).isNotNull
    }

    @Test
    fun `should return response from simple string prompt`() {
        // Given
        val question = "What is 2+2? Answer with just the number."

        // When
        val response = chatClient.prompt(question).call().content()

        // Then
        assertThat(response).isNotNull()
        assertThat(response).isNotEmpty()
        assertThat(response).contains("4")
    }

    @Test
    fun `should return response using fluent API`() {
        // Given
        val question = "Tell me a very short joke in one sentence."

        // When
        val response = chatClient.prompt().user(question).call().content()

        // Then
        assertThat(response).isNotNull()
        assertThat(response).isNotEmpty()
    }

    @Test
    fun `should return ChatResponse with metadata`() {
        // Given
        val question = "Say 'Hello'"

        // When
        val chatResponse = chatClient.prompt(question).call().chatResponse()

        // Then
        assertThat(chatResponse).isNotNull()
        assertThat(chatResponse.results).isNotEmpty()

        val result = chatResponse.results.first()
        assertThat(result.output.content).isNotEmpty()

        // Metadata should exist
        assertThat(chatResponse.metadata).isNotNull()
    }

    @Test
    fun `should use system message with user message`() {
        // Given
        val systemMessage = "You are a helpful math tutor. Answer concisely."
        val userMessage = "What is 5+3?"

        // When
        val response = chatClient.prompt().system(systemMessage).user(userMessage).call().content()

        // Then
        assertThat(response).isNotNull()
        assertThat(response).contains("8")
    }
}
