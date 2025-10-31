# 다양한 LLM 모델 연동 샘플 프로젝트

이 프로젝트는 Spring AI에서 다양한 LLM 모델(OpenAI, Anthropic, Ollama)을 연동하는 방법을 보여줍니다.

## 📁 프로젝트 구조

```
sample/
├── src/main/kotlin/com/example/springai/
│   ├── LLMModelsApplication.kt           # 메인 애플리케이션
│   ├── controller/
│   │   ├── OpenAIController.kt           # OpenAI 사용 예제
│   │   ├── MultiModelController.kt       # 여러 모델 동시 사용
│   │   └── SmartChatController.kt        # 스마트 모델 선택
│   ├── service/
│   │   ├── ModelSelectorService.kt        # 모델 선택 로직
│   │   └── SmartChatController.kt         # 서비스 사용 컨트롤러
│   └── config/
│       └── ChatModelConfiguration.kt      # 여러 모델 설정
└── src/main/resources/
    ├── application.yml                    # 기본 설정
    └── application-ollama.yml             # Ollama 전용 설정
```

## 🚀 빠른 시작

### 1. OpenAI만 사용 (기본)

#### 환경 변수 설정
```bash
export OPENAI_API_KEY="sk-your-api-key-here"
```

#### 실행
```bash
./gradlew bootRun
```

#### 테스트
```bash
# 기본 OpenAI 테스트
curl http://localhost:8080/api/openai/test

# OpenAI 채팅
curl -X POST http://localhost:8080/api/openai/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Spring AI에 대해 설명해주세요"}'
```

### 2. Ollama 추가 사용 (여러 모델)

#### Ollama 설치 및 실행
```bash
# Ollama 설치 (macOS)
brew install ollama

# Ollama 서버 시작
ollama serve

# 모델 다운로드 (별도 터미널)
ollama pull llama2
```

#### build.gradle.kts 수정
Ollama 의존성 주석 해제:
```kotlin
implementation("org.springframework.ai:spring-ai-ollama-spring-boot-starter:1.0.0-M6")
```

#### application.yml 수정
Ollama 설정 주석 해제:
```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama2
```

#### 테스트
```bash
# 기본 모델 사용
curl "http://localhost:8080/api/multi/default?message=안녕하세요"

# Ollama 모델 사용
curl "http://localhost:8080/api/multi/ollama?message=안녕하세요"

# 두 모델 비교
curl "http://localhost:8080/api/multi/compare?message=Spring AI에 대해 설명해주세요"

# 스마트 모델 선택
curl -X POST http://localhost:8080/api/smart/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "안녕하세요"}'

# 비용 최적화 모델 선택
curl -X POST http://localhost:8080/api/smart/cost-optimized \
  -H "Content-Type: application/json" \
  -d '{"message": "감사합니다"}'
```

## 📝 주요 예제 설명

### 1. OpenAIController

기본 OpenAI 모델 사용:

- `/api/openai/test`: 간단한 테스트
- `/api/openai/chat`: POST 요청으로 채팅

### 2. MultiModelController

여러 모델을 동시에 사용:

- `/api/multi/default`: 기본 모델 (OpenAI)
- `/api/multi/ollama`: Ollama 모델
- `/api/multi/compare`: 두 모델 비교

### 3. ModelSelectorService

상황에 따라 적절한 모델 선택:

- 질문 길이에 따라 모델 선택
- 비용 최적화를 위한 모델 선택

## 🔧 설정 방법

### OpenAI 설정

`application.yml`:
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4
          temperature: 0.7
```

### Ollama 설정

`application.yml`:
```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama2
```

### 여러 모델 동시 사용

1. `build.gradle.kts`에 여러 Starter 추가
2. `application.yml`에서 모두 설정
3. `@Primary`로 기본 모델 지정
4. `@Qualifier`로 특정 모델 선택

## 🎯 사용 시나리오

### 시나리오 1: 개발 환경에서 Ollama만 사용

```bash
# application-ollama.yml 활성화
java -jar app.jar --spring.profiles.active=ollama
```

### 시나리오 2: 프로덕션에서 OpenAI만 사용

기본 설정으로 실행 (Ollama 의존성 제거)

### 시나리오 3: 개발 환경에서 여러 모델 테스트

모든 의존성 추가하고 `@Qualifier`로 선택

## 📚 학습 포인트

이 샘플 프로젝트를 통해 학습할 수 있는 내용:

1. **단일 LLM 모델 사용**
   - OpenAI 설정 및 사용
   - 기본 ChatModel 주입

2. **여러 LLM 모델 동시 사용**
   - @Primary와 @Qualifier
   - 여러 Bean 등록

3. **스마트 모델 선택**
   - 상황에 따른 모델 선택 로직
   - 비용 최적화 전략

4. **환경별 설정**
   - 프로파일 활용
   - 개발/운영 환경 분리

## 💡 팁

- **비용 절감**: 간단한 질문은 Ollama, 복잡한 질문은 OpenAI
- **개발 환경**: Ollama로 빠른 테스트
- **프로덕션**: OpenAI로 높은 품질 보장
- **모델 비교**: `/api/multi/compare`로 여러 모델 응답 비교

## 🐛 문제 해결

### Ollama 연결 오류

```
Connection refused: localhost:11434
```

**해결책:**
```bash
ollama serve
```

### 여러 ChatModel Bean 충돌

```
NoUniqueBeanDefinitionException
```

**해결책:**
`ChatModelConfiguration.kt`에서 `@Primary` 사용 확인

---

**다음 학습**: [3장: 효과적인 프롬프트 엔지니어링](../../README.md#3장-효과적인-프롬프트-엔지니어링-prompttemplate)

