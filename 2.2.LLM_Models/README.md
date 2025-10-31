# 2.2: 다양한 LLM 모델 연동하기

## 📖 학습 목표

이 강의를 마친 후 다음을 달성할 수 있습니다:
- **다양한 LLM 제공자**(OpenAI, Anthropic, Ollama)를 Spring AI로 연동할 수 있습니다
- **application.yml**을 통해 LLM 모델을 설정하고 교체할 수 있습니다
- **프로퍼티 설정**을 통해 모델 파라미터를 조정할 수 있습니다
- **여러 LLM을 동시에** 사용하고 선택적으로 활용할 수 있습니다
- **LLM 간 차이점**을 이해하고 상황에 맞게 선택할 수 있습니다

---

## 🔑 핵심 키워드

이 장에서 다루는 핵심 키워드들:

1. **application.yml** - Spring Boot 설정 파일
2. **프로퍼티 설정** - 모델 파라미터 구성
3. **ChatModel** - LLM 추상화 인터페이스
4. **@Primary** - 기본 Bean 지정
5. **Qualifier** - 특정 Bean 선택

---

## 1. Spring AI에서 지원하는 LLM 제공자

### 1.1 주요 LLM 제공자

Spring AI는 다양한 LLM 제공자를 지원합니다:

| 제공자 | 모델 예시 | 특징 | 비용 |
|--------|----------|------|------|
| **OpenAI** | GPT-4, GPT-3.5-turbo | 널리 사용됨, 높은 품질 | 유료 |
| **Anthropic** | Claude 3 (Sonnet, Opus, Haiku) | 긴 컨텍스트, 안전성 중시 | 유료 |
| **Ollama** | Llama 2, Mistral, CodeLlama | 로컬 실행, 무료 | 무료 |
| **Azure OpenAI** | GPT-4, GPT-3.5 | 엔터프라이즈 지원 | 유료 |
| **Vertex AI** | Gemini, PaLM | Google Cloud 통합 | 유료 |

### 1.2 각 제공자의 특징

#### OpenAI
- **장점**: 
  - 가장 널리 사용되는 모델
  - 높은 응답 품질
  - Vision 모델 지원 (GPT-4o)
  - 빠른 응답 속도
- **단점**: 
  - 유료 서비스
  - API 사용량 제한

#### Anthropic (Claude)
- **장점**:
  - 긴 컨텍스트 윈도우 (최대 200K 토큰)
  - 안전성 중심 설계
  - 높은 품질의 응답
- **단점**:
  - 유료 서비스
  - 모델 선택이 제한적

#### Ollama
- **장점**:
  - 완전 무료
  - 로컬 실행 (프라이버시 보호)
  - 인터넷 연결 불필요
  - 다양한 오픈소스 모델
- **단점**:
  - 로컬 리소스 필요
  - 응답 품질이 클라우드 모델보다 낮을 수 있음

---

## 2. OpenAI 연동하기

### 2.1 의존성 추가

`build.gradle.kts`에 의존성 추가:

```kotlin
dependencies {
    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter:1.0.0-M6")
}
```

### 2.2 application.yml 설정

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4          # 또는 gpt-3.5-turbo, gpt-4-turbo
          temperature: 0.7      # 0.0 ~ 2.0
          max-tokens: 1000
          top-p: 1.0
          frequency-penalty: 0.0
          presence-penalty: 0.0
```

### 2.3 환경 변수 설정

```bash
export OPENAI_API_KEY="sk-your-api-key-here"
```

### 2.4 사용 예제

```kotlin
@RestController
class OpenAIController(
    private val chatModel: ChatModel  // 자동으로 OpenAI ChatModel 주입
) {
    @GetMapping("/openai")
    fun chat(): String {
        val prompt = Prompt(UserMessage("안녕하세요!"))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}
```

---

## 3. Anthropic (Claude) 연동하기

### 3.1 의존성 추가

```kotlin
dependencies {
    implementation("org.springframework.ai:spring-ai-anthropic-spring-boot-starter:1.0.0-M6")
}
```

### 3.2 application.yml 설정

```yaml
spring:
  ai:
    anthropic:
      api-key: ${ANTHROPIC_API_KEY}
      chat:
        options:
          model: claude-3-sonnet-20240229  # 또는 claude-3-opus, claude-3-haiku
          temperature: 0.7
          max-tokens: 1000
```

### 3.3 환경 변수 설정

```bash
export ANTHROPIC_API_KEY="sk-ant-your-api-key-here"
```

### 3.4 사용 예제

```kotlin
@RestController
class AnthropicController(
    private val chatModel: ChatModel  // Anthropic ChatModel 주입
) {
    @GetMapping("/anthropic")
    fun chat(): String {
        val prompt = Prompt(UserMessage("안녕하세요!"))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}
```

---

## 4. Ollama (로컬 LLM) 연동하기

### 4.1 Ollama 설치

**macOS:**
```bash
brew install ollama
```

**Linux:**
```bash
curl -fsSL https://ollama.ai/install.sh | sh
```

**Windows:**
[Ollama 공식 사이트](https://ollama.ai/)에서 다운로드

### 4.2 Ollama 실행 및 모델 다운로드

```bash
# Ollama 서버 시작
ollama serve

# 모델 다운로드 (별도 터미널)
ollama pull llama2
ollama pull mistral
ollama pull codellama
```

### 4.3 의존성 추가

```kotlin
dependencies {
    implementation("org.springframework.ai:spring-ai-ollama-spring-boot-starter:1.0.0-M6")
}
```

### 4.4 application.yml 설정

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434  # Ollama 기본 URL
      chat:
        options:
          model: llama2        # 다운로드한 모델 이름
          temperature: 0.7
```

### 4.5 사용 예제

```kotlin
@RestController
class OllamaController(
    private val chatModel: ChatModel  // Ollama ChatModel 주입
) {
    @GetMapping("/ollama")
    fun chat(): String {
        val prompt = Prompt(UserMessage("안녕하세요!"))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}
```

---

## 5. 여러 LLM 동시 사용하기

### 5.1 왜 여러 LLM을 사용하는가?

- **비용 최적화**: 간단한 질문은 무료 Ollama, 복잡한 질문은 유료 모델
- **성능 비교**: 여러 모델의 응답을 비교
- **장애 복구**: 한 모델이 실패하면 다른 모델로 대체
- **특화된 용도**: 용도별로 다른 모델 사용

### 5.2 여러 의존성 추가

```kotlin
dependencies {
    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter:1.0.0-M6")
    implementation("org.springframework.ai:spring-ai-ollama-spring-boot-starter:1.0.0-M6")
}
```

### 5.3 application.yml 설정

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama2
```

### 5.4 기본 모델 자동 설정

Spring Boot 자동 설정이 각 Starter에 따라 ChatModel Bean을 자동 생성합니다:

- **OpenAI Starter**: `ChatModel` Bean이 자동 생성 (일반적으로 `@Primary`)
- **Ollama Starter**: `ollamaChatModel` Bean이 자동 생성 (Bean 이름: "ollamaChatModel")
- **Anthropic Starter**: `ChatModel` Bean이 자동 생성

> 💡 **참고**: 실제로는 별도의 Configuration이 필요 없습니다. Spring Boot 자동 설정이 모든 것을 처리합니다. 커스텀 설정이 필요한 경우에만 Configuration 클래스를 작성하세요.

### 5.5 Qualifier로 특정 모델 선택

Spring AI 1.0.0-M6에서는 Ollama ChatModel이 자동으로 `ollamaChatModel`이라는 이름의 Bean으로 등록됩니다:

```kotlin
@RestController
class MultiModelController(
    private val chatModel: ChatModel,  // 기본 모델 (OpenAI, @Primary)
    @Qualifier("ollamaChatModel") 
    private val ollamaChatModel: ChatModel? = null  // Ollama 모델 (선택적)
) {
    @GetMapping("/chat/openai")
    fun chatWithOpenAI(): String {
        val prompt = Prompt(UserMessage("안녕하세요!"))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
    
    @GetMapping("/chat/ollama")
    fun chatWithOllama(): String {
        if (ollamaChatModel == null) {
            return "Ollama is not configured"
        }
        val prompt = Prompt(UserMessage("안녕하세요!"))
        val response = ollamaChatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}
```

> 💡 **주의**: Ollama Starter가 없으면 `ollamaChatModel` Bean이 생성되지 않으므로 nullable로 선언하거나 `@Autowired(required = false)`를 사용하세요.

### 5.6 모델 선택 로직 구현

실제 구현 예제 (sample 코드 참고):

```kotlin
@Service
class ModelSelectorService(
    private val primaryChatModel: ChatModel,  // 기본 모델 (OpenAI)
    @Qualifier("ollamaChatModel")
    private val ollamaChatModel: ChatModel? = null  // Ollama (선택적)
) {
    fun selectModel(question: String): ChatModel {
        return when {
            // 간단한 질문은 Ollama (무료) - 사용 가능한 경우
            question.length < 50 && ollamaChatModel != null -> ollamaChatModel
            // 복잡한 질문은 기본 모델 (OpenAI)
            else -> primaryChatModel
        }
    }
    
    fun smartChat(message: String): Map<String, Any> {
        val model = selectModel(message)
        val modelName = if (model == ollamaChatModel) "Ollama" else "Primary (OpenAI)"
        
        val prompt = Prompt(UserMessage(message))
        val response = model.call(prompt)
        
        return mapOf(
            "selectedModel" to modelName,
            "message" to (response.results.firstOrNull()?.output?.text ?: "응답 없음"),
            "questionLength" to message.length
        )
    }
}
```

---

## 6. 모델 파라미터 설정

### 6.1 주요 파라미터 설명

#### Temperature (온도)
- **범위**: 0.0 ~ 2.0
- **의미**: 응답의 창의성/랜덤성 조절
- **낮은 값 (0.0-0.3)**: 일관적이고 예측 가능한 응답
- **중간 값 (0.7-1.0)**: 균형잡힌 응답
- **높은 값 (1.5-2.0)**: 창의적이고 다양한 응답

```yaml
temperature: 0.7  # 권장값
```

#### Max Tokens
- **의미**: 최대 생성할 토큰 수
- **영향**: 응답의 길이 제한
- **주의**: 너무 작으면 응답이 잘릴 수 있음

```yaml
max-tokens: 1000  # 일반적인 설정
```

#### Top-P (Nucleus Sampling)
- **범위**: 0.0 ~ 1.0
- **의미**: 확률 분포의 상위 P%만 고려
- **효과**: 응답 다양성 조절

```yaml
top-p: 1.0  # 모든 토큰 고려
```

### 6.2 용도별 권장 설정

#### 정확한 정보 요청
```yaml
temperature: 0.0
max-tokens: 500
top-p: 0.9
```

#### 창의적 글쓰기
```yaml
temperature: 1.2
max-tokens: 2000
top-p: 1.0
```

#### 코딩 질문
```yaml
temperature: 0.2
max-tokens: 1500
top-p: 0.95
```

---

## 7. 환경별 설정 관리

### 7.1 프로파일 사용

#### application-dev.yml (개발 환경)
```yaml
spring:
  ai:
    ollama:  # 무료 모델 사용
      base-url: http://localhost:11434
      chat:
        options:
          model: llama2
```

#### application-prod.yml (운영 환경)
```yaml
spring:
  ai:
    openai:  # 유료 모델 사용
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4
```

### 7.2 프로파일 활성화

```bash
# 개발 환경
java -jar app.jar --spring.profiles.active=dev

# 운영 환경
java -jar app.jar --spring.profiles.active=prod
```

---

## 8. 모델 비교 및 선택 가이드

### 8.1 상황별 모델 선택

| 상황 | 추천 모델 | 이유 |
|------|----------|------|
| **개발/테스트** | Ollama | 무료, 빠른 반복 테스트 |
| **프로덕션** | OpenAI GPT-4 | 높은 품질, 안정성 |
| **긴 문서 분석** | Anthropic Claude | 긴 컨텍스트 윈도우 |
| **비용 최적화** | Ollama + OpenAI 조합 | 용도별 선택 |
| **프라이버시 중요** | Ollama | 로컬 실행 |

### 8.2 모델 비교 테스트

실제 구현 예제 (sample의 MultiModelController 참고):

```kotlin
@RestController
class MultiModelController(
    private val primaryChatModel: ChatModel,
    @Qualifier("ollamaChatModel")
    private val ollamaChatModel: ChatModel? = null
) {
    @GetMapping("/compare")
    fun compareModels(@RequestParam message: String): Map<String, Any> {
        val prompt = Prompt(UserMessage(message))
        
        val defaultResponse = primaryChatModel.call(prompt)
        val defaultText = defaultResponse.results.firstOrNull()?.output?.text ?: "응답 없음"
        
        val result = mutableMapOf<String, Any>(
            "default" to mapOf(
                "model" to "Primary",
                "message" to defaultText
            )
        )
        
        if (ollamaChatModel != null) {
            val ollamaResponse = ollamaChatModel.call(prompt)
            val ollamaText = ollamaResponse.results.firstOrNull()?.output?.text ?: "응답 없음"
            
            result["ollama"] = mapOf(
                "model" to "Ollama",
                "message" to ollamaText
            )
        }
        
        return result
    }
}
```

---

## 9. 실전 예제

### 9.1 LLM 선택 서비스

실제 구현은 sample의 `ModelSelectorService`를 참고하세요. 환경별 선택 예제:

```kotlin
@Service
class LLMSelectionService(
    private val primaryChatModel: ChatModel,
    @Qualifier("ollamaChatModel")
    private val ollamaChatModel: ChatModel?,
    private val environment: Environment
) {
    fun getBestModel(): ChatModel {
        // 환경에 따라 모델 선택
        val activeProfile = environment.activeProfiles.firstOrNull()
        
        return when (activeProfile) {
            "dev", "ollama" -> ollamaChatModel ?: primaryChatModel
            "prod" -> primaryChatModel
            else -> primaryChatModel
        }
    }
}
```

> 💡 **참고**: 실제로는 타입 체크보다는 Bean 이름(@Qualifier)을 사용하는 것이 더 안전합니다.

### 9.2 모델 상태 체크

```kotlin
@Component
class ModelHealthChecker(
    @Qualifier("ollamaChatModel")
    private val ollamaChatModel: ChatModel? = null
) {
    @Scheduled(fixedRate = 60000)  // 1분마다 체크
    fun checkOllamaHealth() {
        if (ollamaChatModel == null) {
            logger.warn("Ollama is not configured")
            return
        }
        
        try {
            val prompt = Prompt(UserMessage("test"))
            ollamaChatModel.call(prompt)
            logger.info("Ollama is healthy")
        } catch (e: Exception) {
            logger.error("Ollama is not available: ${e.message}")
        }
    }
}
```

> 💡 **참고**: `@Scheduled`를 사용하려면 `@EnableScheduling`을 메인 클래스에 추가해야 합니다.

---

## 10. 트러블슈팅

### 10.1 일반적인 문제들

#### 문제 1: 여러 ChatModel Bean 충돌

```
NoUniqueBeanDefinitionException: No qualifying bean of type 'ChatModel' available
```

**원인**: 여러 LLM Starter를 추가했지만 기본 Bean이 지정되지 않음

**해결책**:
- OpenAI Starter는 자동으로 `@Primary` ChatModel Bean을 생성합니다
- Ollama는 자동으로 `ollamaChatModel` Bean을 생성합니다
- 필요시 명시적으로 `@Primary` 지정:
```kotlin
@Configuration
class ChatModelConfig {
    @Bean
    @Primary
    fun primaryChatModel(properties: OpenAiChatProperties): ChatModel {
        // 기본 모델 지정 (일반적으로 자동 설정으로 충분)
        return OpenAiChatModel(properties)
    }
}
```

#### 문제 2: Ollama 연결 실패

```
Connection refused: localhost:11434
```

**원인**: Ollama 서버가 실행되지 않음

**해결책**:
```bash
# Ollama 서버 시작
ollama serve
```

#### 문제 3: API Key 오류

```
Invalid API Key
```

**원인**: 잘못된 API Key 또는 환경 변수 미설정

**해결책**:
- 환경 변수 확인: `echo $OPENAI_API_KEY`
- application.yml에서 직접 설정 (개발 환경만)

---

## 11. 요약

### 11.1 핵심 내용 정리

1. **OpenAI**: 가장 널리 사용, 높은 품질, 유료
2. **Anthropic**: 긴 컨텍스트, 안전성, 유료
3. **Ollama**: 로컬 실행, 무료, 프라이버시 보호
4. **여러 모델 동시 사용**: `@Primary`, `@Qualifier` 활용
5. **프로파일 활용**: 환경별 다른 모델 사용

### 11.2 설정 패턴

```yaml
# 단일 모델 사용
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4
          temperature: 0.7
```

### 11.3 다음 학습 내용

이제 다양한 LLM을 연동하는 방법을 배웠으니, 다음 장에서는:
- **PromptTemplate**: 동적 프롬프트 생성
- **프롬프트 엔지니어링**: 더 나은 응답을 위한 기법

---

## 📚 참고 자료

- [Spring AI 공식 레퍼런스](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API 문서](https://platform.openai.com/docs)
- [Anthropic API 문서](https://docs.anthropic.com/)
- [Ollama 공식 사이트](https://ollama.ai/)

---

## ❓ 학습 확인 문제

다음 질문에 답할 수 있는지 확인해보세요:

1. OpenAI와 Ollama의 주요 차이점은 무엇인가요?
2. 여러 LLM을 동시에 사용할 때 `@Primary`와 `@Qualifier`의 역할은?
3. Temperature 파라미터가 응답에 어떤 영향을 미치나요?
4. 개발 환경과 운영 환경에서 다른 모델을 사용하려면 어떻게 해야 하나요?
5. Ollama를 사용하기 위한 사전 준비사항은 무엇인가요?

---

**다음 장**: [3장: 효과적인 프롬프트 엔지니어링](../README.md#3장-효과적인-프롬프트-엔지니어링-prompttemplate)

