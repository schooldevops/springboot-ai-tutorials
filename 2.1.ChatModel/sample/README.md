# ChatModel 샘플 프로젝트

이 프로젝트는 Spring AI의 ChatModel 인터페이스를 학습하기 위한 실전 예제를 제공합니다.

## 📁 프로젝트 구조

```
sample/
├── src/main/kotlin/com/example/springai/
│   ├── ChatModelDemoApplication.kt          # 메인 애플리케이션
│   ├── SimpleChatController.kt              # 기본 ChatModel 사용 예제
│   ├── AdvancedChatController.kt            # 고급 ChatModel 사용 예제
│   ├── ChatModelExtension.kt                # 확장 함수 예제
│   └── ExtensionUsageController.kt          # 확장 함수 사용 예제
└── src/main/resources/
    └── application.yml                      # 설정 파일
```

## 🚀 빠른 시작

### 1. 환경 변수 설정

```bash
export OPENAI_API_KEY="sk-your-api-key-here"
```

### 2. 프로젝트 실행

```bash
./gradlew bootRun
```

### 3. API 테스트

#### 기본 사용 예제

```bash
# 기본 ChatModel 사용
curl http://localhost:8080/api/simple/basic

# POST 요청
curl -X POST http://localhost:8080/api/simple/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Spring AI에 대해 설명해주세요"}'
```

#### 고급 사용 예제

```bash
# 대화 이력 예제
curl http://localhost:8080/api/advanced/conversation

# 역할 기반 챗봇
curl -X POST http://localhost:8080/api/advanced/role \
  -H "Content-Type: application/json" \
  -d '{"role": "teacher", "message": "수학을 어떻게 공부하면 좋을까요?"}'

# 대화 이력 관리
curl -X POST http://localhost:8080/api/advanced/history \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "user123", "message": "안녕하세요"}'
```

#### 확장 함수 사용 예제

```bash
# 간단한 확장 함수
curl "http://localhost:8080/api/extension/simple?message=안녕하세요"

# 안전한 호출
curl "http://localhost:8080/api/extension/safe?message=안녕하세요"

# 여러 메시지 전송
curl http://localhost:8080/api/extension/multi
```

## 📝 주요 예제 설명

### 1. SimpleChatController

기본적인 ChatModel 사용법을 보여줍니다:

- `UserMessage` 생성
- `Prompt` 생성
- `ChatModel.call()` 호출
- 응답 추출

### 2. AdvancedChatController

고급 ChatModel 사용법을 보여줍니다:

- 여러 메시지를 포함한 대화
- 역할 기반 챗봇 (SystemMessage 활용)
- 대화 이력 관리
- 메타데이터 확인

### 3. ChatModelExtension

Kotlin의 확장 함수를 활용한 유틸리티:

- `simpleCall()`: 간단한 문자열 메시지 전송
- `safeCall()`: 예외 처리가 포함된 안전한 호출
- `multiCall()`: 여러 메시지를 한 번에 전송

## 🔧 설정

### application.yml

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4
          temperature: 0.7
          max-tokens: 1000
```

## 📚 학습 포인트

이 샘플 프로젝트를 통해 학습할 수 있는 내용:

1. **ChatModel의 기본 사용법**
   - 의존성 주입
   - 기본 호출 패턴
   - 응답 처리

2. **Prompt 객체**
   - 단일 메시지
   - 여러 메시지
   - SystemMessage 활용

3. **안전한 응답 처리**
   - Null 안전성
   - 예외 처리
   - 기본값 설정

4. **Kotlin 확장 함수**
   - 코드 재사용성
   - 간편한 API 설계
   - 유틸리티 함수 작성

## 🧪 테스트 예시

### Postman Collection

다음 엔드포인트들을 테스트할 수 있습니다:

1. `GET /api/simple/basic` - 기본 사용
2. `POST /api/simple/chat` - POST 요청
3. `GET /api/advanced/conversation` - 대화 예제
4. `POST /api/advanced/role` - 역할 기반
5. `GET /api/extension/simple` - 확장 함수

### cURL 예시

```bash
# 간단한 질문
curl -X POST http://localhost:8080/api/simple/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Kotlin의 주요 특징 3가지를 설명해주세요"}'

# 역할 기반 질문
curl -X POST http://localhost:8080/api/advanced/role \
  -H "Content-Type: application/json" \
  -d '{"role": "teacher", "message": "프로그래밍을 처음 시작하는 학생에게 조언해주세요"}'
```

## 💡 팁

- **Null 안전성**: 항상 `?.` 연산자를 사용하여 안전하게 접근하세요
- **예외 처리**: try-catch를 사용하여 예상치 못한 오류를 처리하세요
- **로깅**: DEBUG 레벨을 활성화하여 내부 동작을 확인하세요
- **확장 함수**: 자주 사용하는 패턴을 확장 함수로 만들어 재사용하세요

## 🐛 문제 해결

### API Key 오류

```
Error: API key not found
```

환경 변수가 설정되었는지 확인:
```bash
echo $OPENAI_API_KEY
```

### 응답이 null인 경우

항상 null-safe 접근을 사용:
```kotlin
val text = response.result?.output?.text ?: "응답 없음"
```

---

**다음 학습**: [2.2: 다양한 LLM 모델 연동하기](../../README.md#22-다양한-llm-모델-연동하기)

