package com.example.image.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.ai.image.ImageModel
import org.springframework.ai.image.ImagePrompt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OpenAIImageControllerTest {

    @Autowired lateinit var imageModel: ImageModel

    @Test
    fun `should generate image with OpenAI DALL-E`() {
        val prompt = ImagePrompt("A cute cat playing with a ball")
        val response = imageModel.call(prompt)

        assertThat(response).isNotNull()
        assertThat(response.result).isNotNull()
        assertThat(response.result.output.url).isNotEmpty()
    }
}
