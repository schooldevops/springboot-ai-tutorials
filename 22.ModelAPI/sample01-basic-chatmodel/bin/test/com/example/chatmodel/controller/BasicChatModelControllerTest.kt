package com.example.chatmodel.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/** TDD: Sample 01 - Basic ChatModel 테스트 */
@SpringBootTest
class BasicChatModelControllerTest {

    @Autowired lateinit var chatModel: ChatModel

    @Test
    fun `should call with simple string`() {
        // When
        val response = chatModel.call("What is 2+2? Answer with just the number.")

        // Then
        assertThat(response).isNotNull()
        assertThat(response).contains("4")
    }

    @Test
    fun `should call with Prompt`() {
        // Given
        val prompt = Prompt("Say 'Hello'")

        // When
        val chatResponse = chatModel.call(prompt)

        // Then
        assertThat(chatResponse).isNotNull()
        assertThat(chatResponse.result).isNotNull()
        assertThat(chatResponse.result.output.content).isNotEmpty()
    }

    @Test
    fun `should access ChatResponse metadata`() {
        // Given
        val prompt = Prompt("Say 'Test'")

        // When
        val chatResponse = chatModel.call(prompt)

        // Then
        assertThat(chatResponse.metadata).isNotNull()
        assertThat(chatResponse.results).isNotEmpty()
    }

    @Test
    fun `should get result from ChatResponse`() {
        // Given
        val prompt = Prompt(UserMessage("What is AI?"))

        // When
        val chatResponse = chatModel.call(prompt)
        val content = chatResponse.result.output.content

        // Then
        assertThat(content).isNotNull()
        assertThat(content).isNotEmpty()
    }
}
