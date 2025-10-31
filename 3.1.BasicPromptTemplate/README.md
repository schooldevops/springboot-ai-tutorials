# 3.1: 기본 PromptTemplate 활용

## 📖 학습 목표

이 강의를 마친 후 다음을 달성할 수 있습니다:
- **PromptTemplate**의 개념과 필요성을 이해할 수 있습니다
- **동적인 값을 프롬프트에 주입**하는 방법을 학습할 수 있습니다
- **.create(variables)** 메서드로 변수를 바인딩하고 Prompt를 생성할 수 있습니다
- **여러 변수를 포함한 복잡한 프롬프트**를 작성할 수 있습니다
- **실제 사용 예제**를 통해 PromptTemplate을 활용할 수 있습니다

---

## 🔑 핵심 키워드

이 장에서 다루는 핵심 키워드들:

1. **PromptTemplate** - 동적 프롬프트를 생성하는 템플릿 클래스
2. **.create(variables)** - 변수를 바인딩하여 Prompt 객체를 직접 생성하는 메서드
3. **플레이스홀더** - {변수명} 형태로 템플릿에 표시되는 동적 값
4. **바인딩** - 변수 값을 템플릿에 연결하는 과정
5. **Map<String, Any>** - 변수 값을 전달하는 컬렉션 타입

---

## 1. PromptTemplate이란?

### 1.1 PromptTemplate의 필요성

#### 문제: 정적인 프롬프트의 한계

```kotlin
// ❌ 문제: 매번 다른 사용자 이름을 위한 프롬프트를 직접 만들어야 함
val message1 = "안녕하세요 홍길동님, 오늘 날씨가 좋네요!"
val message2 = "안녕하세요 김철수님, 오늘 날씨가 좋네요!"
val message3 = "안녕하세요 이영희님, 오늘 날씨가 좋네요!"

// 문제점:
// - 코드 중복
// - 유지보수 어려움
// - 실수 가능성 증가
```

#### 해결: PromptTemplate 사용

```kotlin
// ✅ 해결: 템플릿으로 재사용 가능한 프롬프트 생성
val template = PromptTemplate("안녕하세요 {name}님, 오늘 날씨가 좋네요!")

val prompt1 = template.create(mapOf("name" to "홍길동"))
val prompt2 = template.create(mapOf("name" to "김철수"))
val prompt3 = template.create(mapOf("name" to "이영희"))

// 장점:
// - 코드 재사용
// - 유지보수 용이
// - 일관된 프롬프트 구조
```

### 1.2 PromptTemplate의 정의

**PromptTemplate**은 동적인 값을 포함한 프롬프트 템플릿을 생성하고 관리하는 Spring AI의 핵심 클래스입니다.

**주요 특징:**
- **재사용 가능**: 하나의 템플릿으로 다양한 변수를 가진 프롬프트 생성
- **유지보수 용이**: 템플릿만 수정하면 모든 프롬프트에 반영
- **타입 안전**: Kotlin의 타입 시스템 활용 가능
- **유연성**: 다양한 방식으로 변수 주입 가능

### 1.3 PromptTemplate의 구조

```kotlin
class PromptTemplate(
    val text: String,           // 템플릿 문자열 (예: "안녕하세요 {name}님!")
    val options: Map<String, Any>? = null  // 기본 변수 값들 (선택적)
)
```

---

## 2. PromptTemplate 기본 사용법

### 2.1 단순한 템플릿 (변수 1개)

#### 단계별 예제

```kotlin
// 1. 템플릿 생성
val template = PromptTemplate("안녕하세요 {name}님!")

// 2. 변수 값 설정 및 Prompt 직접 생성
val variables = mapOf("name" to "홍길동")
val prompt = template.create(variables)

// 3. ChatModel 호출
val response = chatModel.call(prompt)
val text = response.results.firstOrNull()?.output?.text ?: "응답 없음"
```

> 💡 **중요**: Spring AI 1.0.0-M6에서 `PromptTemplate.create()`는 `Prompt` 객체를 직접 반환합니다. 별도로 `UserMessage`나 `Prompt` 생성자가 필요 없습니다.

#### 전체 코드 예제

```kotlin
@RestController
class SimpleTemplateController(
    private val chatModel: ChatModel
) {
    @GetMapping("/greet/{name}")
    fun greet(@PathVariable name: String): String {
        // 템플릿 생성
        val template = PromptTemplate("안녕하세요 {name}님! 간단히 자기소개 해주세요.")
        
        // 변수 바인딩하여 Prompt 직접 생성
        val prompt = template.create(mapOf("name" to name))
        
        // ChatModel 호출
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}
```

### 2.2 여러 변수를 포함한 템플릿

```kotlin
val template = PromptTemplate(
    """
    안녕하세요 {name}님!
    
    당신의 역할: {role}
    현재 상황: {situation}
    
    위 정보를 바탕으로 도와주세요.
    """.trimIndent()
)

val variables = mapOf(
    "name" to "홍길동",
    "role" to "소프트웨어 개발자",
    "situation" to "코드 리뷰를 받고 싶습니다"
)

val prompt = template.create(variables)
```

### 2.3 실제 사용 예제

```kotlin
@RestController
class TemplateController(
    private val chatModel: ChatModel
) {
    @PostMapping("/personalized-chat")
    fun personalizedChat(@RequestBody request: ChatRequest): String {
        val template = PromptTemplate(
            """
            당신은 {userName}님의 개인 어시스턴트입니다.
            
            사용자 정보:
            - 이름: {userName}
            - 직업: {job}
            - 관심사: {interest}
            
            질문: {question}
            
            위 정보를 고려하여 친절하게 답변해주세요.
            """.trimIndent()
        )
        
        val variables = mapOf(
            "userName" to request.userName,
            "job" to request.job,
            "interest" to request.interest,
            "question" to request.question
        )
        
        val prompt = template.create(variables)
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}

data class ChatRequest(
    val userName: String,
    val job: String,
    val interest: String,
    val question: String
)
```

---

## 3. PromptTemplate 메서드 상세

### 3.1 .create() 메서드

**기본 사용법:**

```kotlin
val template = PromptTemplate("안녕하세요 {name}님!")
val prompt = template.create(mapOf("name" to "홍길동"))  // Prompt 직접 반환
```

> 💡 **중요**: Spring AI 1.0.0-M6에서 `PromptTemplate.create()`는 `Prompt` 객체를 직접 반환합니다. 별도로 `UserMessage`나 `Prompt` 생성자가 필요 없습니다.

**여러 변수 사용:**

```kotlin
val template = PromptTemplate("안녕하세요 {firstName} {lastName}님!")
val prompt = template.create(mapOf(
    "firstName" to "홍",
    "lastName" to "길동"
))
```

### 3.2 변수 추가 방법

#### 방법 1: Map으로 한 번에 전달 (권장)

```kotlin
val template = PromptTemplate("안녕하세요 {name}님! 오늘 날씨는 {weather}입니다.")
val prompt = template.create(mapOf(
    "name" to "홍길동",
    "weather" to "맑음"
))
// prompt는 이미 Prompt 객체입니다
```

#### 방법 2: Kotlin의 to 사용

```kotlin
val variables = mapOf(
    "name" to "홍길동",
    "age" to "30",
    "city" to "서울"
)
val prompt = template.create(variables)
```

#### 방법 3: 빌더 패턴 스타일 (가능한 경우)

```kotlin
// 직접 지원은 안 되지만, 유틸리티 함수로 구현 가능
fun buildVariables(vararg pairs: Pair<String, Any>): Map<String, Any> {
    return pairs.toMap()
}

val prompt = template.create(buildVariables(
    "name" to "홍길동",
    "age" to 30
))
```

### 3.3 템플릿 문법

#### 기본 플레이스홀더

```kotlin
// 단순 변수
"안녕하세요 {name}님!"

// 여러 변수
"{greeting} {name}님, {message}"
```

#### 중첩된 변수 (일반적으로 지원 안 됨)

```kotlin
// ❌ 일반적으로 지원 안 됨
// "{user.{field}}" 형태는 사용 불가

// ✅ 해결 방법: 미리 변수 조합
val fullName = "${firstName} ${lastName}"
val prompt = template.create(mapOf("name" to fullName))
```

---

## 4. 실전 활용 예제

### 4.1 개인화된 이메일 작성기

```kotlin
@RestController
class EmailController(
    private val chatModel: ChatModel
) {
    @PostMapping("/email/generate")
    fun generateEmail(@RequestBody request: EmailRequest): String {
        val template = PromptTemplate(
            """
            다음 정보를 바탕으로 전문적인 이메일을 작성해주세요:
            
            수신자: {recipient}
            제목: {subject}
            목적: {purpose}
            추가 요구사항: {requirements}
            
            이메일은 정중하고 명확하게 작성해주세요.
            """.trimIndent()
        )
        
        val prompt = template.create(mapOf(
            "recipient" to request.recipient,
            "subject" to request.subject,
            "purpose" to request.purpose,
            "requirements" to request.requirements ?: "없음"
        ))
        
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "이메일 생성 실패"
    }
}

data class EmailRequest(
    val recipient: String,
    val subject: String,
    val purpose: String,
    val requirements: String? = null
)
```

### 4.2 코딩 질문 도우미

```kotlin
@RestController
class CodeHelperController(
    private val chatModel: ChatModel
) {
    @PostMapping("/code/help")
    fun helpWithCode(@RequestBody request: CodeHelpRequest): String {
        val template = PromptTemplate(
            """
            다음 정보를 바탕으로 코딩 질문에 답변해주세요:
            
            프로그래밍 언어: {language}
            프레임워크: {framework}
            질문 내용: {question}
            현재 코드 컨텍스트: {context}
            
            명확하고 실행 가능한 예제 코드를 포함하여 답변해주세요.
            """.trimIndent()
        )
        
        val prompt = template.create(mapOf(
            "language" to request.language,
            "framework" to request.framework ?: "없음",
            "question" to request.question,
            "context" to request.context ?: "없음"
        ))
        
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "답변 생성 실패"
    }
}

data class CodeHelpRequest(
    val language: String,
    val framework: String?,
    val question: String,
    val context: String? = null
)
```

### 4.3 번역 서비스

```kotlin
@RestController
class TranslationController(
    private val chatModel: ChatModel
) {
    @PostMapping("/translate")
    fun translate(@RequestBody request: TranslationRequest): String {
        val template = PromptTemplate(
            """
            다음 텍스트를 {targetLanguage}로 번역해주세요:
            
            원본 언어: {sourceLanguage}
            번역할 텍스트: {text}
            
            자연스럽고 정확하게 번역해주세요.
            """.trimIndent()
        )
        
        val prompt = template.create(mapOf(
            "sourceLanguage" to request.sourceLanguage,
            "targetLanguage" to request.targetLanguage,
            "text" to request.text
        ))
        
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "번역 실패"
    }
}

data class TranslationRequest(
    val sourceLanguage: String,
    val targetLanguage: String,
    val text: String
)
```

---

## 5. 고급 활용 기법

### 5.1 템플릿 재사용 (Service 레이어 분리)

```kotlin
@Service
class PromptTemplateService {
    
    // 자주 사용하는 템플릿들을 미리 정의
    private val greetingTemplate = PromptTemplate(
        "안녕하세요 {name}님! 오늘도 좋은 하루 되세요."
    )
    
    private val questionTemplate = PromptTemplate(
        """
        {userName}님이 질문하셨습니다:
        
        질문: {question}
        
        {additionalContext}
        
        친절하고 정확하게 답변해주세요.
        """.trimIndent()
    )
    
    fun createGreetingPrompt(name: String): Prompt {
        return greetingTemplate.create(mapOf("name" to name))
    }
    
    fun createQuestionPrompt(
        userName: String,
        question: String,
        additionalContext: String = ""
    ): Prompt {
        return questionTemplate.create(mapOf(
            "userName" to userName,
            "question" to question,
            "additionalContext" to if (additionalContext.isNotEmpty()) {
                "추가 컨텍스트: $additionalContext"
            } else {
                ""
            }
        ))
    }
}

> 💡 **참고**: `template.create()`는 이미 `Prompt` 객체를 반환하므로 추가 변환이 필요 없습니다.

@RestController
class AdvancedTemplateController(
    private val chatModel: ChatModel,
    private val templateService: PromptTemplateService
) {
    @GetMapping("/greet/{name}")
    fun greet(@PathVariable name: String): String {
        val prompt = templateService.createGreetingPrompt(name)
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
    
    @PostMapping("/ask")
    fun ask(@RequestBody request: QuestionRequest): String {
        val prompt = templateService.createQuestionPrompt(
            userName = request.userName,
            question = request.question,
            additionalContext = request.context ?: ""
        )
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}
```

### 5.2 동적 템플릿 선택

```kotlin
@Service
class DynamicTemplateService {
    
    fun selectTemplate(type: String): PromptTemplate {
        return when (type) {
            "greeting" -> PromptTemplate("안녕하세요 {name}님!")
            "question" -> PromptTemplate("{name}님의 질문: {question}")
            "summary" -> PromptTemplate("다음 내용을 요약해주세요: {content}")
            else -> PromptTemplate("{message}")
        }
    }
    
    fun createPrompt(type: String, variables: Map<String, Any>): Prompt {
        val template = selectTemplate(type)
        return template.create(variables)  // Prompt 직접 반환
    }
}

@RestController
class DynamicTemplateController(
    private val chatModel: ChatModel,
    private val templateService: DynamicTemplateService
) {
    @PostMapping("/dynamic/{type}")
    fun useDynamicTemplate(
        @PathVariable type: String,
        @RequestBody variables: Map<String, Any>
    ): String {
        val prompt = templateService.createPrompt(type, variables)
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}
```

---

## 6. 베스트 프랙티스

### 6.1 템플릿 문자열 관리

#### ✅ 좋은 예: 명확한 구조

```kotlin
val template = PromptTemplate(
    """
    당신은 {role} 역할을 맡고 있습니다.
    
    사용자 정보:
    - 이름: {userName}
    - 레벨: {level}
    
    요청 사항: {request}
    
    위 정보를 바탕으로 답변해주세요.
    """.trimIndent()
)
```

#### ❌ 나쁜 예: 가독성 저하

```kotlin
val template = PromptTemplate("안녕하세요 {name}님. 당신의 역할은 {role}입니다. 요청사항은 {request}입니다.")
```

### 6.2 변수 이름 규칙

#### ✅ 좋은 예: 명확하고 의미 있는 이름

```kotlin
mapOf(
    "userName" to "홍길동",
    "userRole" to "개발자",
    "requestContent" to "코드 리뷰"
)
```

#### ❌ 나쁜 예: 모호한 이름

```kotlin
mapOf(
    "n" to "홍길동",
    "r" to "개발자",
    "x" to "코드 리뷰"
)
```

### 6.3 Null 안전성

```kotlin
@RestController
class SafeTemplateController(
    private val chatModel: ChatModel
) {
    @PostMapping("/safe-chat")
    fun safeChat(@RequestBody request: ChatRequest): String {
        // Null-safe 변수 처리
        val variables = buildMap {
            put("name", request.name ?: "사용자")
            put("question", request.question)
            request.context?.let { put("context", it) }
        }
        
        val template = PromptTemplate(
            "안녕하세요 {name}님! 질문: {question}" +
                if (variables.containsKey("context")) "\n컨텍스트: {context}" else ""
        )
        
        val prompt = template.create(variables)
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}
```

### 6.4 템플릿 캐싱

```kotlin
@Service
class CachedTemplateService {
    
    // 자주 사용하는 템플릿을 캐시
    private val templateCache = mutableMapOf<String, PromptTemplate>()
    
    fun getTemplate(key: String, templateString: String): PromptTemplate {
        return templateCache.getOrPut(key) {
            PromptTemplate(templateString)
        }
    }
}
```

---

## 7. 주의사항 및 트러블슈팅

### 7.1 일반적인 문제들

#### 문제 1: 변수가 치환되지 않음

**증상:**
```
템플릿에 {name}이 그대로 출력됨
```

**원인**: 변수 이름이 일치하지 않거나 변수가 제공되지 않음

**해결책:**
```kotlin
// ✅ 올바른 사용
val template = PromptTemplate("안녕하세요 {name}님!")
val prompt = template.create(mapOf("name" to "홍길동"))  // Prompt 직접 반환

// ❌ 잘못된 사용
val prompt = template.create(mapOf("userName" to "홍길동"))  // 변수명 불일치
```

#### 문제 2: 변수 누락

**증상:**
```
IllegalStateException: Variable 'name' not found
```

**해결책:**
```kotlin
// Null-safe 변수 제공
val variables = mapOf(
    "name" to (request.name ?: ""),
    "question" to request.question
)
```

#### 문제 3: 특수 문자 처리

```kotlin
// 중괄호를 텍스트로 사용하려면 이스케이프 필요 없음 (일반적으로)
// 하지만 변수명과 충돌하는 경우 주의

// ✅ 일반적인 경우 문제 없음
val template = PromptTemplate("집합 {1, 2, 3}에 대해 설명해주세요")
```

---

## 8. 요약

### 8.1 핵심 내용 정리

1. **PromptTemplate**: 동적 프롬프트를 생성하는 템플릿 클래스
2. **.create()**: 변수를 바인딩하여 Prompt 객체 생성
3. **변수 바인딩**: Map을 사용하여 변수 값 제공
4. **재사용성**: 하나의 템플릿으로 다양한 프롬프트 생성

### 8.2 기본 패턴

```kotlin
// 1. 템플릿 생성
val template = PromptTemplate("안녕하세요 {name}님!")

// 2. 변수 바인딩하여 Prompt 직접 생성
val prompt = template.create(mapOf("name" to "홍길동"))

// 3. ChatModel 호출
val response = chatModel.call(prompt)
val result = response.results.firstOrNull()?.output?.text ?: "응답 없음"
```

> 💡 **중요**: Spring AI 1.0.0-M6에서 `PromptTemplate.create()`는 `Prompt` 객체를 직접 반환합니다. 별도로 `UserMessage`나 `Prompt` 생성자가 필요 없습니다.

### 8.3 다음 학습 내용

이제 기본 PromptTemplate 사용법을 배웠으니, 다음 장에서는:
- **고급 PromptTemplate 기능**: 복잡한 템플릿 구조
- **템플릿 파일 관리**: 외부 파일에서 템플릿 로드
- **프롬프트 엔지니어링 기법**: 더 나은 응답을 위한 프롬프트 작성

---

## 📚 참고 자료

- [Spring AI PromptTemplate 공식 문서](https://docs.spring.io/spring-ai/reference/api/prompt.html)
- [프롬프트 엔지니어링 가이드](https://platform.openai.com/docs/guides/prompt-engineering)

---

## ❓ 학습 확인 문제

다음 질문에 답할 수 있는지 확인해보세요:

1. PromptTemplate을 사용하는 이유는 무엇인가요?
2. .create() 메서드는 어떤 역할을 하나요?
3. 여러 변수를 포함한 템플릿을 만드는 방법은?
4. 템플릿에 변수가 제공되지 않으면 어떻게 되나요?
5. 실제 프로젝트에서 PromptTemplate을 어떻게 활용할 수 있나요?

---

**다음 장**: [3.2: 고급 PromptTemplate 활용](../README.md#32-고급-prompttemplate-활용)

