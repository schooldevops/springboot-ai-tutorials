# 역할 기반 메시지 샘플 프로젝트

이 프로젝트는 Spring AI에서 SystemMessage, UserMessage, AssistantMessage를 사용하여 역할 기반 대화를 구현하는 방법을 보여줍니다.

## 📁 프로젝트 구조

```
sample/
├── src/main/kotlin/com/example/springai/
│   ├── RoleBasedMessageApplication.kt       # 메인 애플리케이션
│   ├── controller/
│   │   ├── BasicMessageController.kt       # 기본 메시지 타입 사용
│   │   ├── RoleBasedController.kt          # 역할 기반 챗봇
│   │   ├── FewShotController.kt           # Few-shot prompting
│   │   ├── ConversationController.kt      # 대화 이력 관리
│   │   └── ServiceBasedController.kt      # 서비스 기반 사용
│   ├── service/
│   │   └── RoleService.kt                 # 역할 관리 서비스
│   └── model/
│       └── CommonModels.kt                 # 공통 데이터 클래스
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

#### 기본 메시지 타입 예제

```bash
# SystemMessage만 사용
curl http://localhost:8080/api/basic/system-only

# SystemMessage + UserMessage
curl -X POST http://localhost:8080/api/basic/system-user \
  -H "Content-Type: application/json" \
  -d '{"message": "안녕하세요!"}'

# 대화 이력 포함
curl -X POST http://localhost:8080/api/basic/conversation
```

#### 역할 기반 예제

```bash
# 역할 기반 채팅
curl -X POST http://localhost:8080/api/role/chat \
  -H "Content-Type: application/json" \
  -d '{
    "role": "teacher",
    "message": "수학을 어떻게 공부하면 좋을까요?"
  }'

# 동적 역할 설정
curl -X POST http://localhost:8080/api/role/dynamic \
  -H "Content-Type: application/json" \
  -d '{
    "role": "코딩 튜터",
    "instructions": "초보자에게 친절하게 설명해주세요.",
    "principles": ["간단하게", "예시 포함", "실습 추천"],
    "message": "의존성 주입이란 무엇인가요?"
  }'
```

#### Few-shot Prompting 예제

```bash
# 기본 Few-shot
curl -X POST http://localhost:8080/api/few-shot/basic \
  -H "Content-Type: application/json" \
  -d '{"question": "Kotlin에서 리스트를 어떻게 만드나요?"}'

# 형식 지정 Few-shot
curl -X POST http://localhost:8080/api/few-shot/format \
  -H "Content-Type: application/json" \
  -d '{"question": "의존성 주입이란 무엇인가요?"}'
```

#### 대화 이력 관리 예제

```bash
# 세션별 대화 계속하기
curl -X POST http://localhost:8080/api/conversation/session123 \
  -H "Content-Type: application/json" \
  -d '{"message": "안녕하세요"}'

# 대화 이력 조회
curl http://localhost:8080/api/conversation/session123

# 대화 이력 초기화
curl -X DELETE http://localhost:8080/api/conversation/session123

# 대화 이력 길이 제한
curl -X POST "http://localhost:8080/api/conversation/session123/limit?maxMessages=10"
```

#### 서비스 기반 예제

```bash
# 서비스를 통한 역할 기반 채팅
curl -X POST http://localhost:8080/api/service/chat/teacher \
  -H "Content-Type: application/json" \
  -d '{"message": "수학을 어떻게 공부하면 좋을까요?"}'

# 커스텀 역할
curl -X POST http://localhost:8080/api/service/custom \
  -H "Content-Type: application/json" \
  -d '{
    "role": "멘토",
    "instructions": "실무 경험을 바탕으로 조언해주세요.",
    "principles": ["현실적", "실용적", "구체적"],
    "message": "신입 개발자로서 무엇을 준비해야 할까요?"
  }'
```

## 📝 주요 예제 설명

### 1. BasicMessageController

**SystemMessage만 사용:**
- `/api/basic/system-only`: SystemMessage만으로 LLM 호출

**SystemMessage + UserMessage:**
- `/api/basic/system-user`: 역할 정의와 질문 조합

**대화 이력:**
- `/api/basic/conversation`: 이전 대화를 포함한 연속 대화

### 2. RoleBasedController

**역할 기반 채팅:**
- 지원 역할: teacher, doctor, chef, developer, translator
- 역할에 따라 다른 SystemMessage 적용

**동적 역할 설정:**
- 커스텀 역할, 지시사항, 원칙 설정

### 3. FewShotController

**Few-shot Prompting:**
- 예시를 제공하여 원하는 응답 형식 유도
- 기본 Few-shot과 형식 지정 Few-shot 지원

### 4. ConversationController

**대화 이력 관리:**
- 세션별 대화 이력 저장 및 관리
- 대화 이력 조회 및 초기화
- 토큰 관리 위한 이력 길이 제한

### 5. RoleService

자주 사용하는 역할의 SystemMessage를 재사용하기 위한 서비스:
- 선생님, 의사, 셰프, 개발자 역할
- 커스텀 역할 메시지 생성

## 💡 학습 포인트

이 샘플 프로젝트를 통해 학습할 수 있는 내용:

1. **메시지 타입 이해**
   - SystemMessage: 역할 정의
   - UserMessage: 사용자 입력
   - AssistantMessage: 대화 이력

2. **역할 기반 챗봇**
   - 역할에 따른 다른 응답
   - 동적 역할 설정

3. **Few-shot Prompting**
   - 예시를 통한 응답 형식 유도
   - 패턴 학습

4. **대화 이력 관리**
   - 세션 관리
   - 토큰 제한 관리

## 🔧 핵심 패턴

```kotlin
// 1. 메시지 구성
val messages = listOf(
    SystemMessage("당신은 친절한 어시스턴트입니다."),
    UserMessage("안녕하세요!")
)

// 2. Prompt 생성
val prompt = Prompt(messages)

// 3. ChatModel 호출
val response = chatModel.call(prompt)
val text = response.results.firstOrNull()?.output?.text ?: "응답 없음"
```

## 📚 역할 종류

지원되는 역할:
- `teacher`: 선생님
- `doctor`: 의사
- `chef`: 셰프
- `developer`: 개발자
- `translator`: 번역가
- 기타: 기본 어시스턴트

---

**다음 학습**: [3.3: 고급 PromptTemplate 활용](../../README.md#33-고급-prompttemplate-활용)

