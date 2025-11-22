# 19장: [실전] 대화형 챗봇 (채팅 기록 관리)

## 📚 학습 목표

ChatModel에 이전 대화 기록(List<Message>)을 함께 전달하여, 문맥을 기억하고 연속적인 대화가 가능한 챗봇을 구현합니다. (Spring AI의 ChatMemory 활용 또는 수동 관리)

## 🔑 핵심 키워드

- `ChatMemory`
- `List<Message>`
- 대화 컨텍스트 유지
- 세션 관리
- TDD

## 📖 개요

이 장에서는 대화 이력을 관리하여 AI가 이전 대화를 기억하고 문맥에 맞는 응답을 생성하는 대화형 챗봇을 구축합니다. **TDD 방식**으로 개발합니다.

## 🔄 대화 기록 관리 워크플로우

```
Turn 1:
User: "내 이름은 김철수야"
AI: "안녕하세요 김철수님!"
  ↓ [대화 기록 저장]

Turn 2:
User: "내 이름이 뭐였지?"
AI: "김철수님이라고 하셨습니다."
  ↓ [이전 대화 참조]

Turn 3:
User: "고마워"
AI: "천만에요, 김철수님!"
```

## 💻 구현 상세 (TDD 방식)

### 1. 대화 기록 관리 서비스

**ChatHistoryServiceTest.kt:**
```kotlin
@Test
fun `should store and retrieve chat history`() {
    val sessionId = "user-123"
    chatHistoryService.addMessage(sessionId, UserMessage("안녕"))
    
    val history = chatHistoryService.getHistory(sessionId)
    assertEquals(1, history.size)
}
```

**ChatHistoryService.kt:**
```kotlin
@Service
class ChatHistoryService {
    private val sessions = ConcurrentHashMap<String, MutableList<Message>>()
    
    fun addMessage(sessionId: String, message: Message) {
        sessions.computeIfAbsent(sessionId) { mutableListOf() }.add(message)
    }
    
    fun getHistory(sessionId: String): List<Message> {
        return sessions[sessionId] ?: emptyList()
    }
    
    fun clearHistory(sessionId: String) {
        sessions.remove(sessionId)
    }
}
```

### 2. 대화형 챗봇 서비스

```kotlin
@Service
class ConversationalChatService(
    private val chatModel: ChatModel,
    private val chatHistoryService: ChatHistoryService
) {
    fun chat(sessionId: String, userMessage: String): String {
        // 1. 사용자 메시지 추가
        chatHistoryService.addMessage(sessionId, UserMessage(userMessage))
        
        // 2. 전체 대화 기록 가져오기
        val history = chatHistoryService.getHistory(sessionId)
        
        // 3. AI 호출 (대화 기록 포함)
        val prompt = Prompt(history)
        val response = chatModel.call(prompt)
        
        // 4. AI 응답 저장
        chatHistoryService.addMessage(sessionId, response.result.output)
        
        return response.result.output.content
    }
}
```

## 🧪 테스트 방법

### 1. 단위 테스트

```bash
./gradlew test
```

### 2. 애플리케이션 실행

```bash
ollama serve
ollama pull llama3.2
./gradlew bootRun
```

### 3. 대화 테스트

```bash
# Turn 1
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "user-123", "message": "내 이름은 김철수야"}'

# Turn 2
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "user-123", "message": "내 이름이 뭐였지?"}'
```

## 🎓 학습 포인트

1. **Chat History** - 대화 기록 저장 및 관리
2. **Context Awareness** - 이전 대화 참조
3. **Session Management** - 사용자별 세션 분리
4. **TDD** - 테스트 먼저 작성

## 💡 실전 활용 사례

### 1. 고객 상담 챗봇
- 이전 문의 기억
- 연속적인 상담
- 개인화된 응답

### 2. 교육 챗봇
- 학습 진도 추적
- 이전 질문 참조
- 맞춤형 설명

### 3. 개인 비서
- 일정 관리
- 선호도 기억
- 연속적인 대화

## 🔧 고급 기능

### 1. 대화 기록 제한

```kotlin
fun addMessage(sessionId: String, message: Message) {
    val history = sessions.computeIfAbsent(sessionId) { mutableListOf() }
    history.add(message)
    
    // 최근 20개만 유지
    if (history.size > 20) {
        history.removeAt(0)
    }
}
```

### 2. 세션 만료

```kotlin
@Scheduled(fixedRate = 3600000) // 1시간마다
fun cleanupExpiredSessions() {
    val now = System.currentTimeMillis()
    sessions.entries.removeIf { (_, history) ->
        val lastMessage = history.lastOrNull()
        lastMessage == null || (now - lastMessage.timestamp) > 3600000
    }
}
```

### 3. 영구 저장

```kotlin
@Service
class PersistentChatHistoryService(
    private val chatHistoryRepository: ChatHistoryRepository
) {
    fun saveHistory(sessionId: String, messages: List<Message>) {
        chatHistoryRepository.save(ChatHistory(sessionId, messages))
    }
}
```
