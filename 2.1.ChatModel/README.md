# 2.1: ChatModel의 이해와 활용

## 📖 학습 목표

이 강의를 마친 후 다음을 달성할 수 있습니다:
- **ChatModel 인터페이스**의 역할과 구조를 이해할 수 있습니다
- **간단한 프롬프트**를 생성하고 ChatModel에 전송할 수 있습니다
- **ChatResponse**의 구조를 이해하고 응답에서 텍스트를 추출할 수 있습니다
- **Generation 객체**를 이해하고 활용할 수 있습니다
- Kotlin의 null 안전성을 활용하여 안전하게 응답을 처리할 수 있습니다

---

## 🔑 핵심 키워드

이 장에서 다루는 핵심 키워드들:

1. **ChatModel** - Spring AI의 핵심 인터페이스
2. **.call()** - 프롬프트를 LLM에 전송하는 메서드
3. **Prompt** - LLM에 전달할 프롬프트 객체
4. **ChatResponse** - LLM으로부터 받은 응답 객체
5. **Generation** - 응답의 생성 결과를 담는 객체

---

## 1. ChatModel이란?

### 1.1 ChatModel의 정의

**ChatModel**은 Spring AI에서 LLM(Large Language Model)과 상호작용하기 위한 핵심 인터페이스입니다.

**주요 특징:**
- **추상화 계층**: 다양한 LLM 제공자(OpenAI, Anthropic, Ollama 등)를 통일된 인터페이스로 사용
- **의존성 주입**: Spring의 의존성 주입을 통해 자동으로 Bean 생성 및 주입
- **간단한 API**: 복잡한 HTTP 요청/응답 처리를 Spring AI가 대신 처리

### 1.2 ChatModel의 위치

```
Spring AI 아키텍처
│
├─ ChatModel (인터페이스)
│  │
│  ├─ OpenAIChatModel (구현체)
│  ├─ AnthropicChatModel (구현체)
│  ├─ OllamaChatModel (구현체)
│  └─ 기타 LLM 제공자 구현체들
│
└─ 자동 설정 (Auto-Configuration)
   └─ application.yml 설정만으로 Bean 자동 생성
```

### 1.3 ChatModel 인터페이스 구조

```kotlin
interface ChatModel {
    fun call(prompt: Prompt): ChatResponse
    fun call(prompt: Prompt, options: ChatOptions): ChatResponse
    fun stream(prompt: Prompt): Flux<ChatResponse>
}
```

**주요 메서드:**
- `call(prompt: Prompt)`: 동기적으로 응답을 받음
- `call(prompt: Prompt, options: ChatOptions)`: 옵션과 함께 호출
- `stream(prompt: Prompt)`: 스트리밍 방식으로 응답 받음 (반응형 프로그래밍)

---

## 2. ChatModel 사용하기

### 2.1 기본 사용법

#### 2.1.1 의존성 주입

ChatModel은 Spring Boot의 자동 설정을 통해 자동으로 Bean으로 생성됩니다:

```kotlin
@RestController
class ChatController(
    private val chatModel: ChatModel  // 자동 주입
) {
    // ...
}
```

#### 2.1.2 간단한 프롬프트 전송

```kotlin
fun simpleChat(): String {
    // 1. UserMessage 생성
    val userMessage = UserMessage("안녕하세요!")
    
    // 2. Prompt 생성
    val prompt = Prompt(userMessage)
    
    // 3. ChatModel 호출
    val response = chatModel.call(prompt)
    
    // 4. 응답 추출
    return response.results.firstOrNull()?.output?.text ?: "응답 없음"
}
```

### 2.2 단계별 설명

#### 단계 1: UserMessage 생성

```kotlin
val userMessage = UserMessage("안녕하세요!")
```

- `UserMessage`: 사용자가 입력한 메시지를 나타내는 객체
- 생성자에 문자열을 전달하여 메시지 생성

#### 단계 2: Prompt 생성

```kotlin
val prompt = Prompt(userMessage)
```

- `Prompt`: LLM에 전송할 프롬프트를 담는 컨테이너
- 하나 이상의 메시지를 포함할 수 있음

#### 단계 3: ChatModel 호출

```kotlin
val response = chatModel.call(prompt)
```

- `call()`: 동기적으로 LLM에 요청을 전송하고 응답을 대기
- 반환 타입: `ChatResponse`

#### 단계 4: 응답 추출

```kotlin
val text = response.results.firstOrNull()?.output?.text ?: "응답 없음"
```

- `response.results`: `List<Generation>` 객체 (여러 결과를 포함할 수 있음)
- `response.results.firstOrNull()`: 첫 번째 `Generation` 객체 (null일 수 있음)
- `response.results.firstOrNull()?.output`: `Message` 객체 (null일 수 있음)
- `response.results.firstOrNull()?.output?.text`: 실제 텍스트 응답 (String)

> 💡 **참고**: Spring AI 1.0.0-M6에서는 `results`가 List 형태입니다. 일반적으로 첫 번째 결과(`firstOrNull()`)를 사용합니다.

---

## 3. ChatResponse 구조 이해하기

### 3.1 ChatResponse 계층 구조

```
ChatResponse
│
├─ results: List<Generation> (여러 결과를 포함)
│  │
│  └─ [0] (첫 번째 Generation)
│     │
│     └─ output: Message
│        │
│        └─ text: String (실제 응답 텍스트)
│
└─ metadata: ChatResponseMetadata
```

> 💡 **중요**: Spring AI 1.0.0-M6에서는 `result` (단수)가 아닌 `results` (복수, List)를 사용합니다. 첫 번째 결과를 가져오려면 `results.firstOrNull()`을 사용하세요.

### 3.2 Generation 객체

**Generation**은 LLM이 생성한 하나의 응답 결과를 나타냅니다:

```kotlin
data class Generation(
    val output: Message,        // 생성된 메시지
    val metadata: Map<String, Any>? = null  // 메타데이터
)
```

**주요 속성:**
- `output`: 생성된 메시지 객체 (`AssistantMessage`)
- `metadata`: 토큰 사용량, 모델 정보 등 추가 정보

### 3.3 Message 타입

Spring AI는 다양한 메시지 타입을 지원합니다:

```kotlin
// 사용자 메시지
val userMessage = UserMessage("질문 내용")

// AI 어시스턴트 메시지 (응답)
val assistantMessage = AssistantMessage("응답 내용")

// 시스템 메시지 (역할 설정)
val systemMessage = SystemMessage("당신은 친절한 어시스턴트입니다.")
```

### 3.4 안전한 응답 처리

Kotlin의 null 안전성을 활용하여 안전하게 응답을 처리합니다:

```kotlin
// ❌ 안전하지 않은 방법
val text = response.results[0].output.text  // IndexOutOfBoundsException 위험!

// ✅ 안전한 방법 1: firstOrNull과 Safe call operator 사용
val text = response.results.firstOrNull()?.output?.text ?: "응답 없음"

// ✅ 안전한 방법 2: let 사용
val text = response.results.firstOrNull()?.output?.text?.let {
    "응답: $it"
} ?: "응답을 생성할 수 없습니다."

// ✅ 안전한 방법 3: runCatching 사용
val text = runCatching {
    response.results.firstOrNull()?.output?.text 
        ?: throw IllegalArgumentException("응답 없음")
}.getOrElse { "오류 발생: ${it.message}" }
```

---

## 4. 실제 사용 예제

### 4.1 기본 질의응답

```kotlin
@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatModel: ChatModel
) {
    @PostMapping
    fun chat(@RequestBody request: ChatRequest): ChatResponse {
        // 1. 사용자 메시지를 UserMessage로 변환
        val userMessage = UserMessage(request.message)
        
        // 2. Prompt 생성
        val prompt = Prompt(userMessage)
        
        // 3. ChatModel 호출
        val response = chatModel.call(prompt)
        
        // 4. 응답 추출 및 반환
        return ChatResponse(
            message = response.results.firstOrNull()?.output?.text 
                ?: "응답을 생성할 수 없습니다."
        )
    }
}
```

### 4.2 여러 메시지 전송 (대화 컨텍스트)

```kotlin
fun multiMessageChat(): String {
    // 대화 이력 생성
    val messages = listOf(
        SystemMessage("당신은 친절한 어시스턴트입니다."),
        UserMessage("안녕하세요!"),
        AssistantMessage("안녕하세요! 무엇을 도와드릴까요?"),
        UserMessage("Spring AI에 대해 설명해주세요")
    )
    
    // Prompt에 여러 메시지 포함
    val prompt = Prompt(messages)
    
    // ChatModel 호출
    val response = chatModel.call(prompt)
    
    return response.results.firstOrNull()?.output?.text ?: "응답 없음"
}
```

### 4.3 에러 처리

```kotlin
fun safeChat(message: String): String {
    return try {
        val prompt = Prompt(UserMessage(message))
        val response = chatModel.call(prompt)
        
        response.results.firstOrNull()?.output?.text 
            ?: "응답을 생성할 수 없습니다."
    } catch (e: Exception) {
        "오류 발생: ${e.message}"
    }
}
```

---

## 5. Prompt 객체 상세

### 5.1 Prompt 생성 방법

#### 방법 1: 단일 메시지

```kotlin
val prompt = Prompt(UserMessage("안녕하세요"))
```

#### 방법 2: 여러 메시지 리스트

```kotlin
val messages = listOf(
    UserMessage("첫 번째 질문"),
    UserMessage("두 번째 질문")
)
val prompt = Prompt(messages)
```

#### 방법 3: PromptTemplate 사용 (다음 장에서 학습)

```kotlin
val template = PromptTemplate("안녕하세요 {name}님!")
val prompt = template.create(mapOf("name" to "홍길동"))
```

### 5.2 Prompt의 구조

```kotlin
class Prompt(
    val instructions: List<Message>,  // 메시지 리스트
    val options: ChatOptions? = null   // 옵션 (선택적)
)
```

---

## 6. ChatModel의 다양한 호출 방법

### 6.1 기본 호출 (동기)

```kotlin
val response = chatModel.call(prompt)
```

- **동기적**: 응답이 올 때까지 대기
- **블로킹**: 스레드를 블로킹함
- **용도**: 일반적인 REST API 응답

### 6.2 옵션과 함께 호출

```kotlin
val options = ChatOptions.builder()
    .withTemperature(0.7)
    .withMaxTokens(1000)
    .build()

val prompt = Prompt(userMessage, options)
val response = chatModel.call(prompt)
```

### 6.3 스트리밍 호출 (비동기)

```kotlin
import reactor.core.publisher.Flux

fun streamChat(message: String): Flux<String> {
    val prompt = Prompt(UserMessage(message))
    return chatModel.stream(prompt)
        .map { it.results.firstOrNull()?.output?.text ?: "" }
        .filter { it.isNotEmpty() }
}
```

- **비동기**: 응답이 도착하는 대로 처리
- **스트리밍**: 토큰 단위로 점진적으로 응답 수신
- **용도**: 실시간 채팅, 긴 응답 처리

---

## 7. 실전 예제

### 7.1 간단한 Q&A 챗봇

```kotlin
@RestController
@RequestMapping("/api/chat")
class SimpleChatBot(
    private val chatModel: ChatModel
) {
    @PostMapping("/ask")
    fun ask(@RequestBody request: QuestionRequest): AnswerResponse {
        val prompt = Prompt(UserMessage(request.question))
        val response = chatModel.call(prompt)
        
        return AnswerResponse(
            question = request.question,
            answer = response.results.firstOrNull()?.output?.text ?: "응답을 생성할 수 없습니다.",
            timestamp = Instant.now()
        )
    }
}

data class QuestionRequest(
    val question: String
)

data class AnswerResponse(
    val question: String,
    val answer: String,
    val timestamp: Instant
)
```

### 7.2 역할 기반 챗봇

```kotlin
@RestController
class RoleBasedChatBot(
    private val chatModel: ChatModel
) {
    @PostMapping("/chat/{role}")
    fun chat(
        @PathVariable role: String,
        @RequestBody request: ChatRequest
    ): String {
        // 역할에 따라 SystemMessage 설정
        val systemMessage = when (role) {
            "teacher" -> SystemMessage("당신은 친절한 선생님입니다.")
            "doctor" -> SystemMessage("당신은 전문 의사입니다.")
            "chef" -> SystemMessage("당신은 유명한 셰프입니다.")
            else -> SystemMessage("당신은 도움이 되는 어시스턴트입니다.")
        }
        
        val prompt = Prompt(listOf(
            systemMessage,
            UserMessage(request.message)
        ))
        
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}
```

### 7.3 대화 이력 관리

```kotlin
@Service
class ConversationService(
    private val chatModel: ChatModel
) {
    private val conversations = mutableMapOf<String, MutableList<Message>>()
    
    fun chat(sessionId: String, userMessage: String): String {
        // 기존 대화 이력 가져오기
        val history = conversations.getOrPut(sessionId) { mutableListOf() }
        
        // 사용자 메시지 추가
        history.add(UserMessage(userMessage))
        
        // Prompt 생성 (전체 대화 이력 포함)
        val prompt = Prompt(history)
        
        // ChatModel 호출
        val response = chatModel.call(prompt)
        
        // AI 응답 추가
        val assistantMessage = response.results.firstOrNull()?.output
        if (assistantMessage != null) {
            history.add(assistantMessage)
        }
        
        return assistantMessage?.text ?: "응답 없음"
    }
    
    fun clearHistory(sessionId: String) {
        conversations.remove(sessionId)
    }
}
```

---

## 8. 주의사항 및 베스트 프랙티스

### 8.1 주의사항

#### ⚠️ Null 안전성

```kotlin
// ❌ 위험한 코드
val text = response.results[0].output.text  // IndexOutOfBoundsException 위험!

// ✅ 안전한 코드
val text = response.results.firstOrNull()?.output?.text ?: "기본값"
```

#### ⚠️ 예외 처리

```kotlin
// ✅ 항상 예외 처리
try {
    val response = chatModel.call(prompt)
    // 처리
} catch (e: Exception) {
    // 에러 처리
}
```

#### ⚠️ 비용 관리

- LLM 호출은 비용이 발생할 수 있음
- 불필요한 호출을 피하고, 캐싱을 고려하세요
- 토큰 수 제한을 적절히 설정하세요

### 8.2 베스트 프랙티스

#### ✅ 의존성 주입 사용

```kotlin
// ✅ 좋은 예: 생성자 주입
class ChatController(
    private val chatModel: ChatModel
)

// ❌ 나쁜 예: 직접 생성
val chatModel = OpenAiChatModel(...)
```

#### ✅ 명확한 변수명 사용

```kotlin
// ✅ 좋은 예
val userPrompt = Prompt(UserMessage(userInput))
val aiResponse = chatModel.call(userPrompt)
val responseText = aiResponse.results.firstOrNull()?.output?.text

// ❌ 나쁜 예
val p = Prompt(UserMessage(u))
val r = chatModel.call(p)
val t = r.results.firstOrNull()?.output?.text
```

#### ✅ 확장 함수 활용

```kotlin
// 유틸리티 함수를 확장 함수로 정의
fun ChatModel.simpleCall(message: String): String {
    val prompt = Prompt(UserMessage(message))
    return this.call(prompt)
        .results.firstOrNull()?.output?.text ?: "응답 없음"
}

// 사용
val response = chatModel.simpleCall("안녕하세요!")
```

---

## 9. 트러블슈팅

### 9.1 일반적인 문제들

#### 문제 1: NullPointerException

```
Exception: java.lang.NullPointerException
```

**원인**: `response.results`가 비어있거나 null일 수 있음

**해결책**:
```kotlin
val text = response.results.firstOrNull()?.output?.text ?: "응답 없음"
```

#### 문제 2: ChatModel Bean을 찾을 수 없음

```
No qualifying bean of type 'ChatModel' available
```

**원인**: 
- Spring AI 의존성이 누락됨
- application.yml 설정이 잘못됨
- API Key가 설정되지 않음

**해결책**:
1. `build.gradle.kts`에 의존성 추가 확인
2. `application.yml`에 API Key 설정 확인
3. 애플리케이션 재시작

#### 문제 3: 응답이 너무 느림

**원인**: LLM API 호출은 네트워크 지연이 있을 수 있음

**해결책**:
- 타임아웃 설정
- 비동기 처리 고려
- 캐싱 적용

### 9.2 디버깅 팁

#### 로깅 활성화

```yaml
# application.yml
logging:
  level:
    org.springframework.ai: DEBUG
```

#### 응답 전체 확인

```kotlin
fun debugChat(message: String) {
    val prompt = Prompt(UserMessage(message))
    val response = chatModel.call(prompt)
    
    println("Response: $response")
    println("Results: ${response.results}")
    println("First Result: ${response.results.firstOrNull()}")
    println("Output: ${response.results.firstOrNull()?.output}")
    println("Text: ${response.results.firstOrNull()?.output?.text}")
}
```

---

## 10. 요약

### 10.1 핵심 내용 정리

1. **ChatModel**: Spring AI의 핵심 인터페이스로 LLM과 통신
2. **.call()**: 동기적으로 프롬프트를 전송하고 응답을 받는 메서드
3. **Prompt**: LLM에 전달할 메시지를 담는 컨테이너
4. **ChatResponse**: LLM의 응답을 담는 객체 (results: List<Generation>)
5. **안전한 접근**: `response.results.firstOrNull()?.output?.text`로 null-safe하게 접근

### 10.2 코드 패턴

```kotlin
// 기본 패턴
val userMessage = UserMessage("질문")
val prompt = Prompt(userMessage)
val response = chatModel.call(prompt)
val text = response.results.firstOrNull()?.output?.text ?: "응답 없음"
```

> 💡 **중요**: Spring AI 1.0.0-M6에서는 `results` (List)를 사용합니다. 첫 번째 결과를 가져오려면 `firstOrNull()`을 사용하세요.

### 10.3 다음 학습 내용

이제 ChatModel의 기본 사용법을 배웠으니, 다음 장에서는:
- **PromptTemplate**: 동적인 값을 프롬프트에 주입하는 방법
- **메시지 타입**: SystemMessage, UserMessage, AssistantMessage의 활용
- **프롬프트 엔지니어링**: 더 나은 응답을 위한 프롬프트 작성 기법

---

## 📚 참고 자료

- [Spring AI Chat API 공식 문서](https://docs.spring.io/spring-ai/reference/api/chat.html)
- [Spring AI 공식 레퍼런스](https://docs.spring.io/spring-ai/reference/)
- [Kotlin Null 안전성 가이드](https://kotlinlang.org/docs/null-safety.html)

---

## ❓ 학습 확인 문제

다음 질문에 답할 수 있는지 확인해보세요:

1. ChatModel이란 무엇이며, 왜 필요한가요?
2. ChatModel의 `call()` 메서드는 어떤 역할을 하나요?
3. ChatResponse에서 실제 응답 텍스트를 어떻게 안전하게 추출하나요?
4. Prompt 객체는 어떤 역할을 하나요?
5. 여러 메시지를 포함한 대화를 ChatModel로 전송하는 방법은?

---

**다음 장**: [2.2: 다양한 LLM 모델 연동하기](../README.md#22-다양한-llm-모델-연동하기)

