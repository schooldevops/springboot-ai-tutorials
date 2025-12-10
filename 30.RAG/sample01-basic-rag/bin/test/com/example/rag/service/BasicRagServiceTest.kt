package com.example.rag.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BasicRagServiceTest {

    @Autowired lateinit var ragService: BasicRagService

    @Test
    fun `should answer question using RAG`() {
        val answer = ragService.ask("Spring AI란 무엇인가요?")
        assertThat(answer).containsIgnoringCase("Spring")
    }

    @Test
    fun `should answer company revenue question`() {
        val answer = ragService.ask("2024년 매출은?")
        assertThat(answer).containsAnyOf("100억", "100억원")
    }
}
