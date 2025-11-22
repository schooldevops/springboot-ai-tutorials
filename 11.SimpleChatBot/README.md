# 11장: [실전] 간단한 Q&A 챗봇 API 구현

## 📚 학습 목표

1~3장에서 배운 **ChatModel**과 **PromptTemplate**을 활용하여 간단한 질의응답이 가능한 Spring Boot REST API를 구현합니다.

## 🔑 핵심 키워드

- `@RestController`
- `@PostMapping`
- `ChatModel`
- `PromptTemplate`
- API 엔드포인트
- REST API

## 📖 개요

이 장에서는 Spring AI의 핵심 기능인 ChatModel과 PromptTemplate을 사용하여 실제 동작하는 Q&A 챗봇 REST API를 구축합니다. 사용자가 HTTP POST 요청으로 질문을 보내면, AI가 답변을 생성하여 반환하는 간단하지만 실용적인 애플리케이션을 만들어봅니다.

## 🎯 구현할 기능

### 1. 간단한 채팅 API
- 사용자의 질문을 받아 ChatModel로 전달
- AI의 응답을 JSON 형태로 반환

### 2. 템플릿 기반 채팅 API
- PromptTemplate을 사용하여 동적으로 프롬프트 생성
- 변수를 주입하여 맞춤형 응답 생성

### 3. 역할 기반 채팅 API
- SystemMessage로 AI의 역할 정의
- UserMessage로 사용자 질문 전달
- 더 정교한 컨텍스트 제공

## 🏗️ 프로젝트 구조

```
11.SimpleChatBot/
├── README.md                          # 이 문서
└── sample/                            # 샘플 프로젝트
    ├── build.gradle.kts               # Gradle 빌드 설정
    ├── settings.gradle.kts            # Gradle 설정
    ├── gradlew                        # Gradle Wrapper (Unix)
    ├── gradlew.bat                    # Gradle Wrapper (Windows)
    ├── gradle/                        # Gradle Wrapper 파일
    ├── test-requests.http             # HTTP 테스트 요청
    └── src/
        └── main/
            ├── kotlin/com/example/simplechatbot/
            │   ├── SimpleChatBotApplication.kt    # 메인 애플리케이션
            │   ├── controller/
            │   │   └── ChatBotController.kt       # REST 컨트롤러
            │   └── dto/
            │       ├── ChatRequest.kt             # 요청 DTO
            │       └── ChatResponse.kt            # 응답 DTO
            └── resources/
                └── application.yml                # 설정 파일
```

## 💻 구현 상세

### 1. 의존성 설정 (build.gradle.kts)

```kotlin
plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    // Spring AI
    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter")
    
    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}
```

### 2. 애플리케이션 설정 (application.yml)

```yaml
spring:
  application:
    name: simple-chatbot
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}  # 환경 변수로 설정
      chat:
        options:
          model: gpt-4o-mini
          temperature: 0.7

server:
  port: 8080
```

> [!IMPORTANT]
> OpenAI API 키는 환경 변수 `OPENAI_API_KEY`로 설정하거나, `application.yml`에 직접 입력할 수 있습니다.
> 보안을 위해 환경 변수 사용을 권장합니다.

### 3. DTO 클래스

#### ChatRequest.kt
```kotlin
data class ChatRequest(
    val message: String,
    val topic: String? = null,
    val role: String? = null
)
```

#### ChatResponse.kt
```kotlin
data class ChatResponse(
    val response: String,
    val timestamp: Long = System.currentTimeMillis()
)
```

### 4. ChatBot Controller

```kotlin
@RestController
@RequestMapping("/api/chat")
class ChatBotController(
    private val chatModel: ChatModel
) {
    // 1. 간단한 채팅
    @PostMapping("/simple")
    fun simpleChat(@RequestBody request: ChatRequest): ChatResponse {
        val response = chatModel.call(request.message)
        return ChatResponse(response)
    }
    
    // 2. 템플릿 기반 채팅
    @PostMapping("/template")
    fun templateChat(@RequestBody request: ChatRequest): ChatResponse {
        val template = PromptTemplate(
            "당신은 {topic}에 대한 전문가입니다. 다음 질문에 답변해주세요: {question}"
        )
        
        val prompt = template.create(mapOf(
            "topic" to (request.topic ?: "일반"),
            "question" to request.message
        ))
        
        val response = chatModel.call(prompt).result.output.content
        return ChatResponse(response)
    }
    
    // 3. 역할 기반 채팅
    @PostMapping("/role")
    fun roleBasedChat(@RequestBody request: ChatRequest): ChatResponse {
        val systemMessage = SystemMessage(
            request.role ?: "당신은 친절하고 도움이 되는 AI 어시스턴트입니다."
        )
        val userMessage = UserMessage(request.message)
        
        val prompt = Prompt(listOf(systemMessage, userMessage))
        val response = chatModel.call(prompt).result.output.content
        
        return ChatResponse(response)
    }
}
```

## 🧪 테스트 방법

### 1. 애플리케이션 실행

```bash
cd sample

# OpenAI API 키 설정
export OPENAI_API_KEY=your-api-key-here

# 애플리케이션 실행
./gradlew bootRun
```

### 2. API 테스트

#### 간단한 채팅 테스트
```bash
curl -X POST http://localhost:8080/api/chat/simple \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Spring AI가 무엇인가요?"
  }'
```

**예상 응답:**
```json
{
  "response": "Spring AI는 Spring Framework 생태계에서 AI 기능을 쉽게 통합할 수 있도록 도와주는 프레임워크입니다...",
  "timestamp": 1700000000000
}
```

#### 템플릿 기반 채팅 테스트
```bash
curl -X POST http://localhost:8080/api/chat/template \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Kotlin의 주요 특징은 무엇인가요?",
    "topic": "Kotlin 프로그래밍"
  }'
```

**예상 응답:**
```json
{
  "response": "Kotlin 프로그래밍 전문가로서 말씀드리자면, Kotlin의 주요 특징은 다음과 같습니다:\n1. Null 안전성...",
  "timestamp": 1700000000000
}
```

#### 역할 기반 채팅 테스트
```bash
curl -X POST http://localhost:8080/api/chat/role \
  -H "Content-Type: application/json" \
  -d '{
    "message": "REST API 설계 시 주의할 점은?",
    "role": "당신은 20년 경력의 백엔드 아키텍트입니다. 실무 경험을 바탕으로 구체적이고 실용적인 조언을 제공합니다."
  }'
```

**예상 응답:**
```json
{
  "response": "20년간의 백엔드 아키텍처 경험을 바탕으로 말씀드리면, REST API 설계 시 다음 사항들을 주의해야 합니다...",
  "timestamp": 1700000000000
}
```

## 📝 주요 개념 설명

### ChatModel
Spring AI의 핵심 인터페이스로, 다양한 LLM(Large Language Model)과 통신하는 표준화된 방법을 제공합니다.

```kotlin
// 간단한 사용법
val response = chatModel.call("질문 내용")

// Prompt 객체 사용
val prompt = Prompt("질문 내용")
val chatResponse = chatModel.call(prompt)
val answer = chatResponse.result.output.content
```

### PromptTemplate
동적으로 프롬프트를 생성할 수 있게 해주는 템플릿 엔진입니다.

```kotlin
val template = PromptTemplate("안녕하세요, {name}님!")
val prompt = template.create(mapOf("name" to "홍길동"))
```

### Message Types
- **SystemMessage**: AI의 역할과 행동 방식을 정의
- **UserMessage**: 사용자의 실제 질문이나 요청
- **AssistantMessage**: AI의 이전 응답 (대화 기록 관리 시 사용)

## 🎓 학습 포인트

1. **REST API 설계**: Spring Boot의 `@RestController`와 `@PostMapping`을 사용한 API 엔드포인트 구현
2. **ChatModel 활용**: Spring AI의 ChatModel을 통한 LLM 호출
3. **PromptTemplate**: 동적 프롬프트 생성으로 유연한 AI 응답 구현
4. **역할 기반 메시지**: SystemMessage를 통한 AI 페르소나 설정
5. **DTO 패턴**: 요청/응답 데이터 구조화

## 🚀 다음 단계

- **12장**: BeanOutputParser를 활용한 구조화된 데이터 추출
- **13장**: VectorStore를 활용한 시맨틱 검색 API
- **14장**: RAG 패턴을 적용한 사내 위키 챗봇

## 📚 참고 자료

- [Spring AI 공식 문서](https://docs.spring.io/spring-ai/reference/)
- [Chat API 레퍼런스](https://docs.spring.io/spring-ai/reference/api/chat.html)
- [Prompts 레퍼런스](https://docs.spring.io/spring-ai/reference/api/prompts.html)

## 💡 팁

> [!TIP]
> **API 키 보안**: 프로덕션 환경에서는 절대 API 키를 코드에 하드코딩하지 마세요. 환경 변수나 AWS Secrets Manager, HashiCorp Vault 같은 비밀 관리 도구를 사용하세요.

> [!TIP]
> **Temperature 설정**: `temperature` 값을 조정하여 AI 응답의 창의성을 제어할 수 있습니다.
> - 0.0 ~ 0.3: 일관되고 정확한 답변 (FAQ, 문서 요약)
> - 0.4 ~ 0.7: 균형잡힌 답변 (일반 대화)
> - 0.8 ~ 1.0: 창의적인 답변 (브레인스토밍, 스토리텔링)

> [!TIP]
> **에러 처리**: 실제 프로덕션 환경에서는 `@ControllerAdvice`를 사용하여 전역 예외 처리를 구현하세요.
