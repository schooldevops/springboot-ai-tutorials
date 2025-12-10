package com.example.chatclient.config

import org.springframework.ai.chat.client.ChatClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/** ChatClient 설정 ChatClient.Builder를 사용하여 ChatClient 빈 생성 */
@Configuration
class ChatClientConfig {

    /** ChatClient 빈 생성 Spring AI가 자동으로 ChatClient.Builder를 제공 */
    @Bean
    fun chatClient(builder: ChatClient.Builder): ChatClient {
        return builder.build()
    }
}
