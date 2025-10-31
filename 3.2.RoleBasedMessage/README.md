# 3.2: 역할 기반 메시지 (Message Types)

## 📖 학습 목표

이 강의를 마친 후 다음을 달성할 수 있습니다:
- **SystemMessage, UserMessage, AssistantMessage**의 차이와 역할을 이해할 수 있습니다
- **역할 기반 메시지**를 사용하여 LLM의 동작을 정교하게 제어할 수 있습니다
- **Few-shot prompting** 기법을 활용하여 예시를 제공하고 원하는 응답 형식을 유도할 수 있습니다
- **대화 이력 관리**를 통해 연속적인 대화를 구현할 수 있습니다
- **역할별 맞춤형 챗봇**을 구현할 수 있습니다

---

## 🔑 핵심 키워드

이 장에서 다루는 핵심 키워드들:

1. **SystemMessage** - LLM의 역할과 행동 방식을 정의하는 시스템 메시지
2. **UserMessage** - 사용자가 입력한 질문이나 요청을 나타내는 사용자 메시지
3. **AssistantMessage** - AI 어시스턴트의 응답을 나타내는 어시스턴트 메시지
4. **Few-shot prompting** - 예시를 제공하여 원하는 응답 형식을 유도하는 기법
5. **역할 기반 대화** - 특정 역할(선생님, 의사, 요리사 등)로 LLM을 설정하는 기법

---

## 1. 메시지 타입이란?

### 1.1 메시지 타입의 필요성

#### 문제: 단순 텍스트의 한계

```kotlin
// ❌ 문제: LLM에게 역할이나 컨텍스트를 명확히 전달할 수 없음
val prompt = Prompt("안녕하세요")
val response = chatModel.call(prompt)

// 문제점:
// - LLM의 역할이 불명확
// - 대화 컨텍스트를 제공할 수 없음
// - 원하는 응답 형식을 지정하기 어려움
```

#### 해결: 역할 기반 메시지 사용

```kotlin
// ✅ 해결: 역할을 명확히 정의하여 더 정교한 대화 가능
val messages = listOf(
    SystemMessage("당신은 친절한 선생님입니다. 교육적이고 이해하기 쉽게 설명해주세요."),
    UserMessage("수학을 어떻게 공부하면 좋을까요?")
)
val prompt = Prompt(messages)
val response = chatModel.call(prompt)

// 장점:
// - 명확한 역할 정의
// - 컨텍스트 제공 가능
// - 원하는 응답 형식 유도
```

### 1.2 메시지 타입의 종류

Spring AI는 세 가지 주요 메시지 타입을 제공합니다:

1. **SystemMessage**: LLM의 역할과 행동 방식을 정의
2. **UserMessage**: 사용자의 입력
3. **AssistantMessage**: AI 어시스턴트의 응답

---

## 2. 메시지 타입별 상세 설명

### 2.1 SystemMessage

**SystemMessage**는 LLM에게 역할, 성격, 응답 방식을 지시하는 메시지입니다.

#### 기본 사용법

```kotlin
import org.springframework.ai.chat.messages.SystemMessage

val systemMessage = SystemMessage("당신은 친절한 어시스턴트입니다.")
val prompt = Prompt(systemMessage, UserMessage("안녕하세요"))
val response = chatModel.call(prompt)
```

#### 다양한 SystemMessage 예제

**역할 정의:**
```kotlin
// 선생님 역할
SystemMessage("당신은 친절한 선생님입니다. 교육적이고 이해하기 쉽게 설명해주세요.")

// 의사 역할
SystemMessage("당신은 전문 의사입니다. 의학적 지식을 바탕으로 정확하게 답변해주세요.")

// 요리사 역할
SystemMessage("당신은 유명한 셰프입니다. 요리에 대한 열정과 전문성을 보여주세요.")

// 코딩 도우미
SystemMessage("당신은 숙련된 소프트웨어 개발자입니다. 명확하고 실행 가능한 코드 예제를 제공해주세요.")
```

**응답 형식 지정:**
```kotlin
SystemMessage(
    """
    당신은 친절한 어시스턴트입니다.
    
    응답 시 다음 형식을 따라주세요:
    1. 간단한 인사
    2. 핵심 내용 설명
    3. 요약
    """
)
```

**제약 조건 명시:**
```kotlin
SystemMessage(
    """
    당신은 도움이 되는 어시스턴트입니다.
    
    다음 규칙을 지켜주세요:
    - 모르는 것은 솔직하게 말하기
    - 전문 용어는 쉽게 설명하기
    - 긍정적인 톤 유지
    """
)
```

### 2.2 UserMessage

**UserMessage**는 사용자가 입력한 질문이나 요청을 나타냅니다.

#### 기본 사용법

```kotlin
import org.springframework.ai.chat.messages.UserMessage

val userMessage = UserMessage("Spring AI에 대해 설명해주세요")
val prompt = Prompt(userMessage)
val response = chatModel.call(prompt)
```

#### UserMessage 활용 예제

**단순 질문:**
```kotlin
UserMessage("안녕하세요!")
```

**복잡한 요청:**
```kotlin
UserMessage(
    """
    다음 요구사항을 바탕으로 코드를 작성해주세요:
    1. Kotlin 언어 사용
    2. Spring Boot 프레임워크
    3. REST API 엔드포인트
    """
)
```

**맥락이 있는 질문:**
```kotlin
UserMessage(
    """
    제 프로젝트는:
    - 언어: Kotlin
    - 프레임워크: Spring Boot
    - 목적: REST API 개발
    
    의존성 주입에 대해 설명해주세요.
    """
)
```

### 2.3 AssistantMessage

**AssistantMessage**는 AI 어시스턴트의 이전 응답을 나타냅니다. 주로 대화 이력을 유지하거나 Few-shot prompting에 사용됩니다.

#### 기본 사용법

```kotlin
import org.springframework.ai.chat.messages.AssistantMessage

val assistantMessage = AssistantMessage("안녕하세요! 무엇을 도와드릴까요?")
```

#### AssistantMessage 활용 예제

**대화 이력 유지:**
```kotlin
val messages = listOf(
    SystemMessage("당신은 친절한 어시스턴트입니다."),
    UserMessage("안녕하세요!"),
    AssistantMessage("안녕하세요! 무엇을 도와드릴까요?"),
    UserMessage("Spring AI에 대해 설명해주세요")
)
val prompt = Prompt(messages)
```

**Few-shot prompting:**
```kotlin
val messages = listOf(
    SystemMessage("당신은 코딩 도우미입니다."),
    UserMessage("Python에서 리스트를 어떻게 만드나요?"),
    AssistantMessage("리스트는 대괄호 []를 사용하여 만들 수 있습니다. 예: my_list = [1, 2, 3]"),
    UserMessage("Kotlin에서는 어떻게 하나요?")
)
val prompt = Prompt(messages)
```

---

## 3. 실제 활용 예제

### 3.1 역할 기반 챗봇

```kotlin
@RestController
class RoleBasedController(
    private val chatModel: ChatModel
) {
    @PostMapping("/role-based")
    fun roleBasedChat(@RequestBody request: RoleChatRequest): String {
        // 역할에 따라 SystemMessage 설정
        val systemMessage = when (request.role.lowercase()) {
            "teacher" -> SystemMessage(
                "당신은 친절한 선생님입니다. 교육적이고 이해하기 쉽게 설명해주세요."
            )
            "doctor" -> SystemMessage(
                "당신은 전문 의사입니다. 의학적 지식을 바탕으로 정확하게 답변해주세요."
            )
            "chef" -> SystemMessage(
                "당신은 유명한 셰프입니다. 요리에 대한 열정과 전문성을 보여주세요."
            )
            "developer" -> SystemMessage(
                "당신은 숙련된 소프트웨어 개발자입니다. 명확하고 실행 가능한 코드 예제를 제공해주세요."
            )
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

data class RoleChatRequest(
    val role: String,
    val message: String
)
```

### 3.2 Few-shot Prompting

```kotlin
@RestController
class FewShotController(
    private val chatModel: ChatModel
) {
    @PostMapping("/few-shot")
    fun fewShotExample(@RequestBody request: FewShotRequest): String {
        val messages = listOf(
            SystemMessage(
                """
                당신은 친절한 어시스턴트입니다.
                사용자의 질문에 대해 간단하고 명확하게 답변해주세요.
                """
            ),
            // 예시 1
            UserMessage("Python에서 문자열을 어떻게 합치나요?"),
            AssistantMessage("문자열은 + 연산자나 .join() 메서드를 사용하여 합칠 수 있습니다."),
            
            // 예시 2
            UserMessage("리스트를 어떻게 정렬하나요?"),
            AssistantMessage("리스트는 .sort() 메서드나 sorted() 함수를 사용하여 정렬할 수 있습니다."),
            
            // 실제 질문
            UserMessage(request.question)
        )
        
        val prompt = Prompt(messages)
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}

data class FewShotRequest(
    val question: String
)
```

### 3.3 대화 이력 관리

```kotlin
@RestController
class ConversationController(
    private val chatModel: ChatModel
) {
    private val conversations = mutableMapOf<String, MutableList<Message>>()
    
    @PostMapping("/conversation/{sessionId}")
    fun continueConversation(
        @PathVariable sessionId: String,
        @RequestBody request: MessageRequest
    ): ConversationResponse {
        // 세션별 대화 이력 가져오기 또는 생성
        val messages = conversations.getOrPut(sessionId) {
            mutableListOf(
                SystemMessage("당신은 친절한 어시스턴트입니다.")
            )
        }
        
        // 사용자 메시지 추가
        messages.add(UserMessage(request.message))
        
        // LLM 호출
        val prompt = Prompt(messages)
        val response = chatModel.call(prompt)
        val assistantText = response.results.firstOrNull()?.output?.text ?: "응답 없음"
        
        // 어시스턴트 응답을 대화 이력에 추가
        messages.add(AssistantMessage(assistantText))
        
        return ConversationResponse(
            message = assistantText,
            sessionId = sessionId
        )
    }
    
    @DeleteMapping("/conversation/{sessionId}")
    fun clearConversation(@PathVariable sessionId: String): Map<String, String> {
        conversations.remove(sessionId)
        return mapOf("status" to "cleared", "sessionId" to sessionId)
    }
}

data class MessageRequest(
    val message: String
)

data class ConversationResponse(
    val message: String,
    val sessionId: String
)
```

---

## 4. 메시지 조합 패턴

### 4.1 기본 패턴: SystemMessage + UserMessage

가장 일반적인 패턴으로, 역할을 정의하고 사용자 질문을 전달합니다.

```kotlin
val messages = listOf(
    SystemMessage("당신은 친절한 어시스턴트입니다."),
    UserMessage("안녕하세요!")
)
val prompt = Prompt(messages)
```

### 4.2 대화 패턴: SystemMessage + 대화 이력

이전 대화를 포함하여 연속적인 대화를 구현합니다.

```kotlin
val messages = listOf(
    SystemMessage("당신은 친절한 어시스턴트입니다."),
    UserMessage("안녕하세요!"),
    AssistantMessage("안녕하세요! 무엇을 도와드릴까요?"),
    UserMessage("Spring AI에 대해 설명해주세요")
)
val prompt = Prompt(messages)
```

### 4.3 Few-shot 패턴: 예시 포함

원하는 응답 형식을 예시로 제공합니다.

```kotlin
val messages = listOf(
    SystemMessage("당신은 코딩 도우미입니다."),
    UserMessage("Python에서 리스트를 만드는 방법은?"),
    AssistantMessage("리스트는 []를 사용합니다. 예: my_list = [1, 2, 3]"),
    UserMessage("Kotlin에서는?")
)
val prompt = Prompt(messages)
```

### 4.4 복합 패턴: 여러 SystemMessage

여러 시스템 지시를 결합하여 더 정교한 제어를 합니다.

```kotlin
val messages = listOf(
    SystemMessage("당신은 전문 소프트웨어 개발자입니다."),
    SystemMessage("코드는 항상 주석과 함께 작성해주세요."),
    SystemMessage("한국어로 설명해주세요."),
    UserMessage("의존성 주입에 대해 설명해주세요")
)
val prompt = Prompt(messages)
```

> 💡 **참고**: 여러 SystemMessage를 사용할 수 있지만, 일반적으로 하나의 SystemMessage에 모든 지시를 포함하는 것이 더 명확합니다.

---

## 5. 베스트 프랙티스

### 5.1 SystemMessage 작성 팁

#### ✅ 좋은 예: 명확하고 구체적

```kotlin
SystemMessage(
    """
    당신은 친절한 선생님입니다.
    
    다음 원칙을 따라주세요:
    1. 복잡한 개념을 쉽게 설명하기
    2. 예시를 통해 이해를 돕기
    3. 학생의 수준에 맞춰 설명하기
    
    응답 형식:
    - 간단한 요약
    - 자세한 설명
    - 실용적인 예시
    """
)
```

#### ❌ 나쁜 예: 모호하고 불명확

```kotlin
SystemMessage("좋은 선생님")  // 무엇이 좋은 선생님인지 불명확
```

### 5.2 메시지 순서

일반적으로 다음 순서를 권장합니다:

1. **SystemMessage**: 역할 정의 (최상단)
2. **UserMessage/AssistantMessage**: 대화 이력 (시간 순서)
3. **최신 UserMessage**: 현재 질문

```kotlin
val messages = listOf(
    // 1. 역할 정의
    SystemMessage("당신은 친절한 어시스턴트입니다."),
    
    // 2. 이전 대화 (선택적)
    UserMessage("이전 질문"),
    AssistantMessage("이전 응답"),
    
    // 3. 현재 질문
    UserMessage("현재 질문")
)
```

### 5.3 메시지 길이 관리

#### ✅ 좋은 예: 적절한 길이

```kotlin
SystemMessage(
    """
    당신은 코딩 도우미입니다.
    명확하고 실행 가능한 코드 예제를 제공해주세요.
    """
)
```

#### ❌ 나쁜 예: 너무 길거나 짧음

```kotlin
// 너무 짧음
SystemMessage("코딩 도우미")

// 너무 김 (불필요한 반복)
SystemMessage(
    """
    당신은 코딩 도우미입니다.
    당신은 코딩 도우미입니다.
    당신은 코딩 도우미입니다.
    ...
    """
)
```

### 5.4 역할 기반 챗봇 구현 패턴

```kotlin
@Service
class RoleService {
    fun createSystemMessage(role: String): SystemMessage {
        return when (role.lowercase()) {
            "teacher" -> SystemMessage(
                "당신은 친절한 선생님입니다. 교육적이고 이해하기 쉽게 설명해주세요."
            )
            "doctor" -> SystemMessage(
                "당신은 전문 의사입니다. 의학적 지식을 바탕으로 정확하게 답변해주세요."
            )
            else -> SystemMessage("당신은 도움이 되는 어시스턴트입니다.")
        }
    }
}

@RestController
class RoleChatController(
    private val chatModel: ChatModel,
    private val roleService: RoleService
) {
    @PostMapping("/chat/{role}")
    fun chat(
        @PathVariable role: String,
        @RequestBody request: MessageRequest
    ): String {
        val systemMessage = roleService.createSystemMessage(role)
        val prompt = Prompt(listOf(
            systemMessage,
            UserMessage(request.message)
        ))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}
```

---

## 6. 고급 활용 기법

### 6.1 동적 역할 설정

```kotlin
@RestController
class DynamicRoleController(
    private val chatModel: ChatModel
) {
    @PostMapping("/dynamic-role")
    fun dynamicRoleChat(@RequestBody request: DynamicRoleRequest): String {
        val systemMessage = SystemMessage(
            """
            당신은 ${request.role} 역할을 맡고 있습니다.
            ${request.instructions}
            
            다음 원칙을 따라주세요:
            ${request.principles.joinToString("\n") { "- $it" }}
            """
        )
        
        val prompt = Prompt(listOf(
            systemMessage,
            UserMessage(request.message)
        ))
        
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}

data class DynamicRoleRequest(
    val role: String,
    val instructions: String,
    val principles: List<String>,
    val message: String
)
```

### 6.2 컨텍스트 기반 메시지 구성

```kotlin
@RestController
class ContextBasedController(
    private val chatModel: ChatModel
) {
    @PostMapping("/context-based")
    fun contextBasedChat(@RequestBody request: ContextRequest): String {
        val messages = mutableListOf<Message>()
        
        // 기본 시스템 메시지
        messages.add(SystemMessage("당신은 친절한 어시스턴트입니다."))
        
        // 컨텍스트 정보 추가
        if (request.userInfo != null) {
            messages.add(SystemMessage(
                """
                사용자 정보:
                - 이름: ${request.userInfo.name}
                - 레벨: ${request.userInfo.level}
                - 관심사: ${request.userInfo.interests.joinToString(", ")}
                """
            ))
        }
        
        // 이전 대화 추가
        request.previousMessages?.forEach { prevMsg ->
            messages.add(UserMessage(prevMsg.userMessage))
            if (prevMsg.assistantMessage != null) {
                messages.add(AssistantMessage(prevMsg.assistantMessage))
            }
        }
        
        // 현재 질문
        messages.add(UserMessage(request.message))
        
        val prompt = Prompt(messages)
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}

data class ContextRequest(
    val userInfo: UserInfo?,
    val previousMessages: List<PreviousMessage>?,
    val message: String
)

data class UserInfo(
    val name: String,
    val level: String,
    val interests: List<String>
)

data class PreviousMessage(
    val userMessage: String,
    val assistantMessage: String?
)
```

---

## 7. 주의사항 및 트러블슈팅

### 7.1 일반적인 문제들

#### 문제 1: SystemMessage가 무시됨

**증상:**
```
LLM이 SystemMessage의 지시를 따르지 않음
```

**원인**: 
- SystemMessage가 UserMessage 이후에 위치
- SystemMessage가 너무 모호함

**해결책:**
```kotlin
// ✅ 올바른 순서
val messages = listOf(
    SystemMessage("당신은 선생님입니다."),  // 최상단
    UserMessage("질문")
)

// ❌ 잘못된 순서
val messages = listOf(
    UserMessage("질문"),
    SystemMessage("당신은 선생님입니다.")  // 너무 늦음
)
```

#### 문제 2: 대화 이력이 너무 길어짐

**증상:**
```
토큰 제한 초과 오류
```

**해결책:**
```kotlin
// 대화 이력 제한
fun limitConversationHistory(messages: List<Message>, maxMessages: Int = 10): List<Message> {
    // SystemMessage는 항상 유지
    val systemMessages = messages.filterIsInstance<SystemMessage>()
    val otherMessages = messages.filter { it !is SystemMessage }
    
    // 최근 메시지만 유지
    val recentMessages = otherMessages.takeLast(maxMessages - systemMessages.size)
    
    return systemMessages + recentMessages
}
```

#### 문제 3: 메시지 타입 혼동

**증상:**
```
컴파일 오류 또는 예상치 못한 동작
```

**해결책:**
```kotlin
// ✅ 명확한 타입 사용
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.messages.AssistantMessage

val messages = listOf(
    SystemMessage("역할 정의"),
    UserMessage("사용자 입력"),
    AssistantMessage("이전 응답")
)
```

---

## 8. 요약

### 8.1 핵심 내용 정리

1. **SystemMessage**: LLM의 역할과 행동 방식을 정의
2. **UserMessage**: 사용자의 입력
3. **AssistantMessage**: AI 어시스턴트의 응답 (대화 이력, Few-shot)
4. **메시지 순서**: SystemMessage → 대화 이력 → 최신 UserMessage
5. **Few-shot prompting**: 예시를 통해 원하는 응답 형식 유도

### 8.2 기본 패턴

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

### 8.3 다음 학습 내용

이제 역할 기반 메시지를 배웠으니, 다음 장에서는:
- **고급 PromptTemplate 기능**: 복잡한 템플릿 구조
- **메시지 시퀀스 최적화**: 효율적인 메시지 구성
- **대화 상태 관리**: 세션 관리 및 이력 저장

---

## 📚 참고 자료

- [Spring AI Messages 공식 문서](https://docs.spring.io/spring-ai/reference/api/messages.html)
- [Few-shot Prompting 가이드](https://platform.openai.com/docs/guides/prompt-engineering/strategy-write-clear-instructions)

---

## ❓ 학습 확인 문제

다음 질문에 답할 수 있는지 확인해보세요:

1. SystemMessage, UserMessage, AssistantMessage의 차이점은 무엇인가요?
2. Few-shot prompting이란 무엇이며, 어떻게 구현하나요?
3. 대화 이력을 관리하는 방법은?
4. 역할 기반 챗봇을 구현하는 패턴은?
5. 메시지 순서가 중요한 이유는 무엇인가요?

---

**다음 장**: [3.3: 고급 PromptTemplate 활용](../README.md#33-고급-prompttemplate-활용)

