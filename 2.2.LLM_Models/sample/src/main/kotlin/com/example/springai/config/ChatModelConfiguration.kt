package com.example.springai.config

import org.springframework.ai.chat.model.ChatModel
import org.springframework.context.annotation.Configuration

/**
 * 여러 LLM 모델을 동시에 사용하기 위한 설정 설명
 * 
 * 참고사항:
 * - OpenAI ChatModel은 Spring Boot 자동 설정에 의해 @Primary로 자동 등록됩니다
 * - Ollama를 추가로 사용하려면:
 *   1. build.gradle.kts에 spring-ai-ollama-starter 의존성 추가
 *   2. application.yml에 ollama 설정 추가
 *   3. 자동 설정이 OllamaChatModel Bean을 생성합니다
 * 
 * 여러 모델을 사용할 때:
 * - 기본 모델: @Primary가 지정된 ChatModel (일반적으로 OpenAI)
 * - 특정 모델: @Qualifier("ollamaChatModel")로 선택
 * 
 * 실제 Configuration이 필요한 경우 (예: 커스텀 설정):
 * 
 * @Configuration
 * class ChatModelConfiguration {
 *     
 *     // Ollama 의존성이 있을 때만 활성화
 *     @Bean("ollamaChatModel")
 *     @ConditionalOnProperty(prefix = "spring.ai.ollama", name = ["base-url"])
 *     @ConditionalOnClass(OllamaChatModel::class)
 *     fun ollamaChatModel(ollamaChatProperties: OllamaChatProperties): ChatModel {
 *         // Spring Boot 자동 설정을 사용하므로 
 *         // 별도 Configuration이 필요 없을 수 있음
 *         // 필요시 커스텀 로직 추가
 *     }
 * }
 */
@Configuration
class ChatModelConfiguration {
    // 실제로는 Spring Boot 자동 설정이 모든 ChatModel Bean을 생성합니다
    // 여러 모델을 사용하려면:
    // 1. build.gradle.kts에 여러 Starter 추가
    // 2. application.yml에서 모두 설정
    // 3. @Qualifier로 특정 모델 선택
}

