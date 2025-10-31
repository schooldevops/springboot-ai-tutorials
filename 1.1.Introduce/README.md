# 1.1: 과정 소개 및 로드맵

## 📖 학습 목표

이 강의를 마친 후 다음을 달성할 수 있습니다:
- **Spring AI의 역할과 목적**을 이해하고 설명할 수 있습니다
- **Kotlin과 Spring AI를 함께 사용했을 때의 이점**을 설명할 수 있습니다
- **Generative AI와 LLM의 기본 개념**을 이해합니다
- **전체 과정의 학습 목표**를 설정하고 로드맵을 파악할 수 있습니다
- Spring AI 생태계에서의 위치와 중요성을 이해합니다

---

## 🔑 핵심 키워드

이 장에서 다루는 핵심 키워드들:

1. **Generative AI** (생성형 AI)
2. **LLM** (Large Language Model, 대규모 언어 모델)
3. **Spring AI**
4. **Kotlin**
5. **Spring Boot**

---

## 1. Generative AI (생성형 AI)란?

### 1.1 정의 및 특징

**Generative AI**는 기존 데이터를 학습하여 새로운 콘텐츠를 생성하는 인공지능 기술입니다. 전통적인 AI가 분류나 예측에 집중했다면, 생성형 AI는 다음과 같은 작업을 수행합니다:

- **텍스트 생성**: 자연어로 된 답변, 문서, 코드 등
- **이미지 생성**: 사진, 일러스트, 디자인 등
- **음성 생성**: 텍스트를 음성으로 변환(TTS)
- **코드 생성**: 프로그래밍 코드 자동 생성

### 1.2 생성형 AI의 응용 분야

```
📝 텍스트 생성
   ├─ 챗봇 및 고객 지원
   ├─ 문서 자동 작성
   ├─ 코드 생성 및 리팩토링
   └─ 번역 및 요약

🎨 콘텐츠 생성
   ├─ 마케팅 카피 작성
   ├─ 소셜 미디어 게시물
   └─ 제품 설명 생성

🔍 정보 검색 및 분석
   ├─ 문서 요약
   ├─ 질의응답 시스템
   └─ 데이터 인사이트 추출
```

### 1.3 생성형 AI의 도전 과제

- **환각(Hallucination)**: 사실과 다른 정보를 생성할 수 있음
- **컨텍스트 한계**: 제한된 토큰 수 내에서만 작동
- **최신 정보 부족**: 학습 데이터 시점 이후의 정보를 모름
- **비용 및 성능**: 대규모 모델 실행에 많은 리소스 필요

> 💡 **팁**: 이러한 도전 과제를 해결하기 위해 RAG(Retrieval-Augmented Generation) 패턴이 등장했습니다. 이는 이후 강의에서 자세히 다룰 예정입니다.

---

## 2. LLM (Large Language Model)이란?

### 2.1 LLM의 기본 개념

**LLM (Large Language Model)**은 수십억 개의 매개변수를 가진 거대한 신경망 모델로, 방대한 텍스트 데이터를 학습하여 다음을 수행합니다:

- **언어 이해**: 자연어 질문의 의도를 파악
- **텍스트 생성**: 맥락에 맞는 응답 생성
- **작업 수행**: 번역, 요약, 코드 작성 등 다양한 작업

### 2.2 주요 LLM 모델

| 모델 | 개발사 | 특징 |
|------|--------|------|
| **GPT-4** | OpenAI | 높은 정확도, 멀티모달 지원 |
| **Claude** | Anthropic | 긴 컨텍스트 윈도우, 안전성 중시 |
| **Gemini** | Google | 다양한 크기의 모델 제공 |
| **Llama 2/3** | Meta | 오픈소스, 자체 호스팅 가능 |
| **Mistral** | Mistral AI | 효율적인 오픈소스 모델 |

### 2.3 LLM 작동 원리

```
1. 입력 (Input)
   └─ 사용자의 프롬프트(Prompt)
      예: "Spring Boot의 주요 특징을 설명해주세요"

2. 처리 (Processing)
   └─ LLM이 학습된 지식 기반에서 관련 정보 추출
      └─ 확률 기반으로 다음 단어 생성

3. 출력 (Output)
   └─ 생성된 텍스트 응답
      예: "Spring Boot는 자동 설정, 임베디드 서버..."
```

### 2.4 LLM API 접근 방식

LLM을 사용하는 주요 방법:

1. **클라우드 API**: OpenAI, Anthropic 등의 API 직접 호출
2. **로컬 실행**: Ollama, LM Studio 등으로 로컬에서 실행
3. **프레임워크 사용**: Spring AI 같은 프레임워크로 추상화

> 💡 **Spring AI의 장점**: 다양한 LLM 제공자(OpenAI, Anthropic, Ollama 등)를 통일된 인터페이스로 사용할 수 있습니다.

---

## 3. Spring AI란?

### 3.1 Spring AI의 정의

**Spring AI**는 Spring 생태계의 최신 프로젝트로, AI 애플리케이션 개발을 간소화하는 프레임워크입니다. 2024년에 정식 출시되었으며, 다음과 같은 목적을 가지고 있습니다:

- **표준화된 API**: 다양한 AI 제공자를 동일한 인터페이스로 사용
- **Spring 통합**: Spring Boot, Spring Cloud 등과 완벽 통합
- **개발자 경험**: 복잡한 AI 통합을 간단한 코드로 구현

### 3.2 Spring AI의 핵심 구성 요소

```
Spring AI 프레임워크
│
├─ 📡 ChatClient / ChatModel
│  └─ LLM과의 대화 인터페이스
│
├─ 🎯 PromptTemplate
│  └─ 동적 프롬프트 생성
│
├─ 📊 OutputParser
│  └─ 구조화된 응답 파싱
│
├─ 🔢 EmbeddingClient
│  └─ 텍스트를 벡터로 변환
│
├─ 💾 VectorStore
│  └─ 벡터 데이터 저장 및 검색
│
└─ 🔧 Function Calling
   └─ AI가 함수를 호출하도록 지원
```

### 3.3 Spring AI의 주요 기능

#### 3.3.1 통합 인터페이스
```kotlin
// 하나의 인터페이스로 다양한 LLM 사용
chatClient.call(prompt)  // OpenAI든 Ollama든 동일하게 사용
```

#### 3.3.2 자동 설정
```yaml
# application.yml만 설정하면 자동으로 Bean 생성
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
```

#### 3.3.3 추상화 레이어
- **ChatClient**: LLM과의 대화를 추상화
- **EmbeddingClient**: 임베딩 제공자를 추상화
- **VectorStore**: 벡터 데이터베이스를 추상화

### 3.4 Spring AI vs 직접 API 호출

| 특징 | Spring AI | 직접 API 호출 |
|------|-----------|--------------|
| **코드 복잡도** | 낮음 | 높음 |
| **LLM 교체** | 설정만 변경 | 코드 수정 필요 |
| **에러 처리** | 표준화됨 | 직접 구현 |
| **테스트** | Mock 가능 | 어려움 |
| **설정 관리** | Spring Boot 통합 | 별도 관리 |

---

## 4. Kotlin과 Spring AI를 함께 사용하는 이점

### 4.1 Kotlin의 특징

**Kotlin**은 JVM에서 실행되는 현대적인 프로그래밍 언어로, 다음과 같은 특징을 가지고 있습니다:

- **간결한 문법**: Java보다 코드가 30% 이상 짧음
- **널 안전성**: 컴파일 타임에 NullPointerException 방지
- **함수형 프로그래밍**: 고차 함수, 람다 표현식 지원
- **상호 운용성**: Java와 100% 호환
- **공식 지원**: Spring Framework에서 공식 지원 언어

### 4.2 Kotlin + Spring AI 시너지

#### 4.2.1 간결한 코드 작성

**Java 예시**:
```java
ChatResponse response = chatClient.call(
    new Prompt(
        new UserMessage("Spring AI에 대해 설명해주세요")
    )
);
String content = response.getResult().getOutput().getContent();
```

**Kotlin 예시**:
```kotlin
val response = chatClient.call(
    Prompt(UserMessage("Spring AI에 대해 설명해주세요"))
)
val content = response.result.output.content
```

> 💡 Kotlin은 세미콜론, 타입 선언 생략, 중괄호 축약 등으로 코드가 훨씬 간결합니다.

#### 4.2.2 Data Class 활용

```kotlin
// Kotlin Data Class
data class Resume(
    val name: String,
    val experience: Int,
    val skills: List<String>
)

// BeanOutputParser와 함께 사용
val parser = BeanOutputParser(Resume::class.java)
// 간단하게 구조화된 데이터 파싱
```

#### 4.2.3 Null 안전성

```kotlin
// Kotlin의 null 안전성으로 안전한 코드 작성
val content: String? = response.result?.output?.content

// Safe call operator로 NPE 방지
content?.let { 
    println("응답: $it") 
}
```

#### 4.2.4 확장 함수(Extension Functions)

```kotlin
// 유틸리티 함수를 확장 함수로 정의
fun ChatClient.simpleCall(message: String): String {
    return this.call(Prompt(UserMessage(message)))
        .result.output.content
}

// 사용
val response = chatClient.simpleCall("안녕하세요")
```

#### 4.2.5 코루틴 지원

```kotlin
// 비동기 처리
suspend fun asyncChatCall(message: String): String {
    return withContext(Dispatchers.IO) {
        chatClient.call(Prompt(UserMessage(message)))
            .result.output.content
    }
}
```

### 4.3 실제 사용 사례 비교

#### 챗봇 응답 처리 예시

**Java 스타일**:
```java
public String getChatResponse(String userMessage) {
    try {
        ChatResponse response = chatClient.call(
            new Prompt(new UserMessage(userMessage))
        );
        if (response != null && 
            response.getResult() != null &&
            response.getResult().getOutput() != null) {
            return response.getResult().getOutput().getContent();
        }
        return "응답을 생성할 수 없습니다.";
    } catch (Exception e) {
        return "오류가 발생했습니다: " + e.getMessage();
    }
}
```

**Kotlin 스타일**:
```kotlin
fun getChatResponse(userMessage: String): String {
    return runCatching {
        chatClient.call(Prompt(UserMessage(userMessage)))
            .result?.output?.content
            ?: "응답을 생성할 수 없습니다."
    }.getOrElse { "오류가 발생했습니다: ${it.message}" }
}
```

> 💡 Kotlin은 null 안전성, 확장 함수, runCatching 등을 활용해 더 안전하고 간결한 코드를 작성할 수 있습니다.

---

## 5. Spring Boot와의 통합

### 5.1 Spring Boot의 역할

**Spring Boot**는 Spring 애플리케이션을 빠르게 구축할 수 있게 해주는 프레임워크로:

- **자동 설정**: Spring AI도 자동으로 설정됨
- **의존성 관리**: starter를 통해 의존성 자동 관리
- **임베디드 서버**: Tomcat, Netty 등 내장 서버 제공
- **프로파일**: 개발/운영 환경 분리

### 5.2 Spring Boot + Spring AI 통합 흐름

```
1. 프로젝트 생성
   └─ start.spring.io에서 Spring AI 의존성 추가

2. 설정 파일 작성
   └─ application.yml에 API 키 등 설정

3. 자동 Bean 주입
   └─ Spring Boot가 ChatClient, EmbeddingClient 자동 생성

4. 서비스 개발
   └─ @Autowired로 Bean 주입 후 사용

5. REST API 구현
   └─ @RestController로 엔드포인트 제공
```

### 5.3 통합 예시

```kotlin
@SpringBootApplication
class SpringAiApplication {
    @Bean
    fun restTemplate() = RestTemplate()
}

@RestController
class ChatController(
    private val chatClient: ChatClient  // 자동 주입
) {
    @PostMapping("/chat")
    fun chat(@RequestBody request: ChatRequest): ChatResponse {
        val prompt = Prompt(UserMessage(request.message))
        return chatClient.call(prompt)
    }
}
```

---

## 6. 전체 과정 로드맵

### 6.1 과정 구조 개요

이 과정은 **기초 이론(1~10장)**과 **실전 프로젝트(11~20장)**로 구성됩니다:

```
📚 기초 이론 (1~10장)
├─ 1장: Spring AI와 Kotlin 소개
├─ 2장: LLM과 대화하기 (ChatClient)
├─ 3장: 효과적인 프롬프트 엔지니어링
├─ 4장: LLM 응답 구조화 (OutputParser)
├─ 5장: 임베딩과 시맨틱 검색
├─ 6장: 벡터 저장소 (VectorStore)
├─ 7장: RAG - 기본
├─ 8장: RAG - 심화 (데이터 처리)
├─ 9장: Function Calling
└─ 10장: 멀티모달

🚀 실전 프로젝트 (11~20장)
├─ 11장: 간단한 Q&A 챗봇 API
├─ 12장: 이력서 분석 및 JSON 추출기
├─ 13장: 시맨틱 문서 검색 API
├─ 14장: 사내 위키 기반 RAG 챗봇 (기초)
├─ 15장: RAG 챗봇 고도화
├─ 16장: AI 기반 스마트 날씨 알리미
├─ 17장: AI 에이전트: 주문 관리 봇
├─ 18장: 상품 이미지 태그 생성기
├─ 19장: 대화형 챗봇 (채팅 기록 관리)
└─ 20장: 풀스택 챗봇 (종합 프로젝트)
```

### 6.2 학습 순서 및 의존성

```
기초 이론
│
├─ [필수] 1장: 소개 및 환경 구축
│  └─ 모든 장의 기초
│
├─ [필수] 2장: ChatClient
│  └─ 3, 4, 7, 9, 10장의 기초
│
├─ [권장] 3장: PromptTemplate
│  └─ 7, 8장에서 활용
│
├─ [권장] 4장: OutputParser
│  └─ 12, 18장에서 활용
│
├─ [필수] 5장: EmbeddingClient
│  └─ 6, 7, 8, 13, 14, 15장의 기초
│
├─ [필수] 6장: VectorStore
│  └─ 7, 8, 13, 14, 15장의 기초
│
└─ [심화] 7~10장: 고급 기능
   └─ 실전 프로젝트에서 활용
```

### 6.3 각 장별 학습 목표 요약

#### 기초 이론 섹션
- **1장**: 전체 과정 이해 및 환경 설정
- **2장**: LLM과의 기본 통신
- **3장**: 효과적인 프롬프트 작성
- **4장**: 구조화된 데이터 추출
- **5장**: 텍스트 벡터화 및 유사도 계산
- **6장**: 벡터 데이터 저장 및 검색
- **7장**: RAG 패턴 이해 및 기본 구현
- **8장**: 문서 처리 파이프라인 구축
- **9장**: AI 함수 호출 기능
- **10장**: 이미지 등 멀티모달 처리

#### 실전 프로젝트 섹션
- **11~15장**: 단계별로 복잡도가 증가하는 RAG 챗봇 구축
- **16~17장**: Function Calling 활용 프로젝트
- **18장**: 멀티모달 기능 활용
- **19장**: 대화형 기능 추가
- **20장**: 전체 기술 스택 통합 프로젝트

---

## 7. 학습 목표 설정

### 7.1 과정 종료 시 달성 목표

이 과정을 완료하면 다음을 수행할 수 있어야 합니다:

✅ **기술적 역량**
- [ ] Spring AI의 주요 컴포넌트를 이해하고 활용
- [ ] 다양한 LLM 모델을 Spring AI로 연동
- [ ] RAG 패턴을 구현하여 LLM의 한계 극복
- [ ] Function Calling을 활용한 AI 에이전트 구현
- [ ] 멀티모달 기능을 활용한 이미지 분석 시스템 구축

✅ **실전 프로젝트 역량**
- [ ] 독립적으로 AI 기반 REST API 개발
- [ ] 문서 기반 질의응답 시스템 구축
- [ ] 외부 API와 연동하는 AI 애플리케이션 개발
- [ ] 풀스택 AI 챗봇 서비스 구현

✅ **문제 해결 역량**
- [ ] LLM의 환각 문제를 RAG로 해결
- [ ] 프롬프트 엔지니어링을 통한 응답 품질 향상
- [ ] 벡터 검색 최적화
- [ ] 프로덕션 환경에서의 AI 애플리케이션 운영

### 7.2 학습 방법 제안

#### 단계별 학습 가이드

1. **이론 학습 (1~10장)**
   ```
   각 장을 읽고 → 핵심 개념 정리 → 예제 코드 실행 → 직접 수정해보기
   ```

2. **실전 프로젝트 (11~20장)**
   ```
   요구사항 이해 → 설계 → 구현 → 테스트 → 리팩토링
   ```

3. **복습 및 심화**
   ```
   프로젝트 개선 → 추가 기능 구현 → 다른 데이터셋 적용
   ```

### 7.3 사전 지식 요구사항

이 과정을 시작하기 전에 다음 지식이 있으면 도움이 됩니다:

**필수 지식**
- ✅ Java 또는 Kotlin 기초 문법
- ✅ Spring Boot 기본 사용법
- ✅ REST API 개념
- ✅ Maven 또는 Gradle 기본 이해

**권장 지식**
- ⭐ Kotlin 중급 문법 (data class, 확장 함수 등)
- ⭐ Spring의 의존성 주입(DI) 개념
- ⭐ 데이터베이스 기본 개념
- ⭐ Docker 기본 사용법 (6장에서 필요)

---

## 8. 학습 환경 준비 체크리스트

강의를 시작하기 전에 다음을 준비하세요:

### 8.1 개발 환경
- [ ] JDK 17 이상 설치
- [ ] IntelliJ IDEA 또는 VS Code 설치
- [ ] Git 설치 및 GitHub 계정
- [ ] Docker Desktop 설치 (6장 이후)

### 8.2 AI 서비스 계정
- [ ] OpenAI API 키 발급 (선택)
- [ ] Anthropic API 키 발급 (선택)
- [ ] 또는 Ollama 로컬 설치 (무료)

### 8.3 학습 자료
- [ ] Spring AI 공식 문서: https://docs.spring.io/spring-ai/reference/
- [ ] Kotlin 공식 문서: https://kotlinlang.org/docs/home.html

---

## 9. 요약

### 9.1 핵심 내용 정리

1. **Generative AI**는 새로운 콘텐츠를 생성하는 AI 기술로, 텍스트, 이미지, 코드 등을 생성할 수 있습니다.

2. **LLM**은 대규모 언어 모델로, 자연어 이해 및 생성이 가능하며 GPT-4, Claude 등이 대표적입니다.

3. **Spring AI**는 Spring 생태계의 AI 통합 프레임워크로, 다양한 AI 제공자를 통일된 인터페이스로 사용할 수 있습니다.

4. **Kotlin**은 간결한 문법, null 안전성, 함수형 프로그래밍 지원으로 Spring AI와 함께 사용할 때 우수한 개발자 경험을 제공합니다.

5. **Spring Boot**와의 통합으로 자동 설정, 의존성 관리, REST API 구축이 매우 간편해집니다.

### 9.2 다음 단계

이제 **1.2: Spring AI 개발 환경 구축**으로 넘어가서 실제 프로젝트를 생성하고 실행해봅시다!

---

## 📚 참고 자료

- [Spring AI 공식 레퍼런스](https://docs.spring.io/spring-ai/reference/)
- [Spring Initializr](https://start.spring.io/)
- [Kotlin 공식 문서](https://kotlinlang.org/docs/home.html)
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)

---

## ❓ 학습 확인 문제

다음 질문에 답할 수 있는지 확인해보세요:

1. Generative AI와 전통적인 AI의 차이점은 무엇인가요?
2. Spring AI를 사용하는 주요 이점은 무엇인가요?
3. Kotlin과 Java를 비교했을 때 Kotlin의 주요 장점은 무엇인가요?
4. RAG 패턴이 필요한 이유는 무엇인가요?
5. 이 과정에서 배울 주요 기술 스택은 무엇인가요?

---

**다음 장**: [1.2: Spring AI 개발 환경 구축](../README.md#12-spring-ai-개발-환경-구축)

