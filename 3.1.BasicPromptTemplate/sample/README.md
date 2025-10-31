# PromptTemplate 기본 활용 샘플 프로젝트

이 프로젝트는 Spring AI에서 PromptTemplate을 사용하여 동적인 프롬프트를 생성하는 방법을 보여줍니다.

## 📁 프로젝트 구조

```
sample/
├── src/main/kotlin/com/example/springai/
│   ├── PromptTemplateApplication.kt        # 메인 애플리케이션
│   ├── controller/
│   │   ├── BasicTemplateController.kt      # 기본 PromptTemplate 사용
│   │   ├── AdvancedTemplateController.kt   # 고급 활용 예제
│   │   └── ServiceBasedController.kt      # 서비스 레이어 분리
│   └── service/
│       └── TemplateService.kt             # 템플릿 재사용 서비스
└── src/main/resources/
    └── application.yml                     # 설정 파일
```

## 🚀 빠른 시작

### 1. 환경 변수 설정

```bash
export OPENAI_API_KEY="sk-your-api-key-here"
```

### 2. 실행

```bash
./gradlew bootRun
```

### 3. 테스트

#### 기본 예제

```bash
# 인사말 생성 (변수 1개)
curl http://localhost:8080/api/basic/greet/홍길동

# 개인화된 채팅 (여러 변수)
curl -X POST http://localhost:8080/api/basic/personalized \
  -H "Content-Type: application/json" \
  -d '{"name": "홍길동", "job": "개발자", "interest": "AI"}'

# 질문 답변
curl -X POST http://localhost:8080/api/basic/question \
  -H "Content-Type: application/json" \
  -d '{"userName": "홍길동", "question": "Spring AI에 대해 설명해주세요"}'
```

#### 고급 예제

```bash
# 이메일 생성
curl -X POST http://localhost:8080/api/advanced/email \
  -H "Content-Type: application/json" \
  -d '{
    "recipient": "김철수",
    "subject": "회의 일정 안내",
    "purpose": "다음 주 회의 일정을 안내합니다",
    "requirements": "간결하고 정중하게"
  }'

# 코딩 도우미
curl -X POST http://localhost:8080/api/advanced/code-help \
  -H "Content-Type: application/json" \
  -d '{
    "language": "Kotlin",
    "framework": "Spring Boot",
    "question": "의존성 주입이란 무엇인가요?",
    "context": "Spring Boot 프로젝트에서 사용 중"
  }'

# 번역 서비스
curl -X POST http://localhost:8080/api/advanced/translate \
  -H "Content-Type: application/json" \
  -d '{
    "sourceLanguage": "한국어",
    "targetLanguage": "영어",
    "text": "안녕하세요, 반갑습니다!"
  }'
```

#### 서비스 기반 예제

```bash
# 인사말 (서비스 사용)
curl http://localhost:8080/api/service/greet/홍길동

# 질문 답변 (서비스 사용)
curl -X POST http://localhost:8080/api/service/ask \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "홍길동",
    "question": "Kotlin의 주요 특징은?",
    "context": "Spring Boot 프로젝트"
  }'

# 내용 요약
curl -X POST http://localhost:8080/api/service/summarize \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Spring AI는 Spring 생태계에서 AI 기능을 통합하는 프레임워크입니다..."
  }'
```

## 📝 주요 예제 설명

### 1. BasicTemplateController

**단순한 변수 1개:**
- `/api/basic/greet/{name}`: 이름을 받아 인사말 생성

**여러 변수:**
- `/api/basic/personalized`: 이름, 직업, 관심사를 받아 개인화된 채팅

**질문 처리:**
- `/api/basic/question`: 사용자 이름과 질문을 받아 답변 생성

### 2. AdvancedTemplateController

**이메일 생성:**
- 수신자, 제목, 목적을 바탕으로 전문적인 이메일 작성

**코딩 도우미:**
- 프로그래밍 언어, 프레임워크, 질문, 컨텍스트를 받아 코딩 질문에 답변

**번역 서비스:**
- 원본 언어, 대상 언어, 텍스트를 받아 번역

### 3. TemplateService

자주 사용하는 템플릿을 재사용하기 위한 서비스:
- 인사말 템플릿
- 질문 템플릿
- 요약 템플릿

## 💡 학습 포인트

이 샘플 프로젝트를 통해 학습할 수 있는 내용:

1. **PromptTemplate 기본 사용**
   - 템플릿 생성
   - 변수 바인딩
   - Prompt 생성

2. **여러 변수 활용**
   - Map을 사용한 변수 전달
   - 복잡한 템플릿 구조

3. **템플릿 재사용**
   - 서비스 레이어에서 템플릿 관리
   - 코드 중복 제거

4. **실전 활용 예제**
   - 이메일 생성
   - 코딩 도우미
   - 번역 서비스

## 🔧 핵심 패턴

```kotlin
// 1. 템플릿 생성
val template = PromptTemplate("안녕하세요 {name}님!")

// 2. 변수 바인딩하여 Prompt 직접 생성
val prompt = template.create(mapOf("name" to name))

// 3. ChatModel 호출
val response = chatModel.call(prompt)
val result = response.results.firstOrNull()?.output?.text ?: "응답 없음"
```

> 💡 **중요**: `PromptTemplate.create()`는 `Prompt` 객체를 직접 반환합니다. 추가 변환이 필요 없습니다.

## ❌ 문제 해결

### 문제: PromptTemplate.create() 반환 타입

**해결책:**
`PromptTemplate.create()`는 `Prompt` 객체를 직접 반환합니다:

```kotlin
val prompt = template.create(mapOf("name" to name))
// 이미 Prompt 객체이므로 바로 사용 가능
```

---

**다음 학습**: [3.2: 고급 PromptTemplate 활용](../../README.md#32-고급-prompttemplate-활용)

