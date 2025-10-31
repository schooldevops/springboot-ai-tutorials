# Spring AI 샘플 프로젝트

이 디렉토리는 Spring AI 개발 환경 구축을 위한 완전한 샘플 프로젝트입니다.

## 📁 프로젝트 구조

```
sample/
├── build.gradle.kts          # Gradle 빌드 파일
├── src/
│   ├── main/
│   │   ├── kotlin/com/example/springai/
│   │   │   ├── SpringAiAppApplication.kt    # 메인 애플리케이션
│   │   │   └── controller/
│   │   │       └── ChatController.kt       # 챗봇 컨트롤러
│   │   └── resources/
│   │       └── application.yml              # 설정 파일
│   └── test/
│       └── kotlin/com/example/springai/
│           └── SpringAiAppApplicationTests.kt
└── README.md                  # 이 파일
```

## 🚀 빠른 시작

### 1. 환경 변수 설정

터미널에서 API Key를 환경 변수로 설정:

**macOS / Linux:**
```bash
export OPENAI_API_KEY="sk-your-api-key-here"
```

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="sk-your-api-key-here"
```

### 2. 프로젝트 실행

**Gradle 사용:**
```bash
./gradlew bootRun
```

**IntelliJ IDEA:**
1. 프로젝트 열기
2. `SpringAiAppApplication.kt` 실행

### 3. 테스트

애플리케이션이 실행되면 다음 URL로 테스트:

**GET 요청 (간단한 테스트):**
```bash
curl http://localhost:8080/api/chat/test
```

**POST 요청 (메시지 전송):**
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Spring AI에 대해 간단히 설명해주세요"}'
```

또는 브라우저에서:
- http://localhost:8080/api/chat/test

## 🔧 설정 변경

### OpenAI 대신 Anthropic (Claude) 사용

1. `build.gradle.kts`에서 의존성 변경:
```kotlin
// OpenAI 주석 처리
// implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter:1.0.0")

// Anthropic 활성화
implementation("org.springframework.ai:spring-ai-anthropic-spring-boot-starter:1.0.0")
```

2. `application.yml`에서 설정 변경:
```yaml
spring:
  ai:
    anthropic:
      api-key: ${ANTHROPIC_API_KEY}
      chat:
        options:
          model: claude-3-sonnet-20240229
```

3. 환경 변수 설정:
```bash
export ANTHROPIC_API_KEY="sk-ant-your-key"
```

### Ollama (로컬 LLM) 사용

1. Ollama 설치:
```bash
# macOS
brew install ollama

# 또는 공식 사이트에서 다운로드
# https://ollama.ai/
```

2. Ollama 실행 및 모델 다운로드:
```bash
ollama serve
ollama pull llama2
```

3. `build.gradle.kts`에서 의존성 변경:
```kotlin
implementation("org.springframework.ai:spring-ai-ollama-spring-boot-starter:1.0.0")
```

4. `application.yml`에서 설정:
```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama2
```

## 📝 주요 파일 설명

### ChatController.kt

간단한 REST API 엔드포인트를 제공합니다:

- `GET /api/chat/test`: 간단한 테스트 메시지
- `POST /api/chat`: 사용자 메시지를 받아서 AI 응답 반환

### application.yml

Spring AI 설정을 관리합니다:

- API Key 설정 (환경 변수 사용)
- 모델 선택 (gpt-4, gpt-3.5-turbo 등)
- 모델 파라미터 (temperature, max-tokens 등)

## 🧪 테스트 예시

### 1. 간단한 인사말 테스트
```bash
curl http://localhost:8080/api/chat/test
```

예상 응답:
```
안녕하세요! 저는 AI 어시스턴트입니다...
```

### 2. 질문하기
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Kotlin의 주요 특징 3가지를 설명해주세요"}'
```

### 3. Spring AI에 대해 물어보기
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Spring AI 프레임워크는 무엇인가요?"}'
```

## 🔍 문제 해결

### API Key 오류
```
Error: API key not found
```

**해결책:**
- 환경 변수가 설정되었는지 확인: `echo $OPENAI_API_KEY`
- 애플리케이션 재시작

### 의존성 오류
```
Could not resolve: spring-ai-openai-spring-boot-starter
```

**해결책:**
```bash
./gradlew clean build --refresh-dependencies
```

### 포트 충돌
```
Port 8080 is already in use
```

**해결책:**
`application.yml`에서 포트 변경:
```yaml
server:
  port: 8081
```

## 📚 다음 단계

이 샘플 프로젝트를 성공적으로 실행했다면:

1. ✅ 환경이 올바르게 설정되었습니다
2. ✅ Spring AI가 정상 작동합니다
3. ✅ 이제 다음 장으로 진행할 수 있습니다!

**다음 학습**: [2장: LLM과 대화하기 (ChatClient)](../../README.md#2장-llm과-대화하기-chatclient)

---

## 💡 추가 학습 자료

- [Spring AI 공식 문서](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API 문서](https://platform.openai.com/docs)
- [Anthropic API 문서](https://docs.anthropic.com/)
- [Ollama 공식 사이트](https://ollama.ai/)

