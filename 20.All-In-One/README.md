# 20장: [종합] Spring Boot + Kotlin + Spring AI 풀스택 챗봇

## 📚 학습 목표

지금까지 배운 모든 기술(**RAG, Function Calling, Chat Memory**)을 통합하여, 실제 작동하는 **RAG 기반의 대화형 AI 챗봇 서비스**를 완성합니다.

## 🔑 핵심 키워드

- `RAG (Retrieval-Augmented Generation)`
- `Function Calling`
- `Chat Memory`
- `Vector Store`
- `Document Processing`
- 통합 아키텍처
- TDD

## 📖 개요

이 장에서는 모든 Spring AI 기능을 통합한 종합 챗봇을 구축합니다:
- **RAG**: 문서 기반 질의응답
- **Function Calling**: 외부 API 호출
- **Chat Memory**: 대화 기록 관리

**TDD 방식**으로 개발합니다.

## 🏗️ 통합 아키텍처

```
┌─────────────────────────────────────────┐
│         All-In-One Chatbot              │
├─────────────────────────────────────────┤
│                                         │
│  ┌──────────────┐  ┌─────────────────┐ │
│  │ Chat Memory  │  │ Function Calling│ │
│  │  (History)   │  │   (Weather)     │ │
│  └──────────────┘  └─────────────────┘ │
│                                         │
│  ┌──────────────────────────────────┐  │
│  │         RAG System               │  │
│  │  ┌──────────┐  ┌──────────────┐ │  │
│  │  │Documents │→ │Vector Store  │ │  │
│  │  └──────────┘  └──────────────┘ │  │
│  └──────────────────────────────────┘  │
│                                         │
│  ┌──────────────────────────────────┐  │
│  │      AI Model (Llama 3.2)       │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

## 💻 구현 상세 (TDD 방식)

### 1. 통합 챗봇 서비스

**AllInOneChatServiceTest.kt:**
```kotlin
@Test
fun `should answer from documents using RAG`() {
    val response = chatService.chat("user-1", "회사 정책은?")
    assertTrue(response.contains("문서"))
}

@Test
fun `should call weather function`() {
    val response = chatService.chat("user-1", "서울 날씨 알려줘")
    assertTrue(response.contains("날씨") || response.contains("온도"))
}

@Test
fun `should remember conversation history`() {
    chatService.chat("user-1", "내 이름은 김철수야")
    val response = chatService.chat("user-1", "내 이름이 뭐였지?")
    assertTrue(response.contains("김철수"))
}
```

**AllInOneChatService.kt:**
```kotlin
@Service
class AllInOneChatService(
    private val chatModel: ChatModel,
    private val vectorStore: VectorStore,
    private val chatHistoryService: ChatHistoryService
) {
    fun chat(sessionId: String, userMessage: String): String {
        // 1. Add to history
        chatHistoryService.addMessage(sessionId, UserMessage(userMessage))
        
        // 2. RAG: Search relevant documents
        val similarDocs = vectorStore.similaritySearch(
            SearchRequest.query(userMessage).withTopK(3)
        )
        
        // 3. Build context
        val context = similarDocs.joinToString("\n") { it.content }
        val systemMessage = SystemMessage(
            "다음 문서를 참고하여 답변하세요:\n$context"
        )
        
        // 4. Get history
        val history = chatHistoryService.getHistory(sessionId)
        
        // 5. Call AI with Function Calling enabled
        val options = OllamaOptions.builder()
            .withFunction("getWeather")
            .build()
        
        val messages = listOf(systemMessage) + history
        val response = chatModel.call(Prompt(messages, options))
        
        // 6. Save response
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

### 3. 통합 테스트

```bash
# RAG 테스트
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "user-1", "message": "회사 정책은?"}'

# Function Calling 테스트
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "user-1", "message": "서울 날씨 알려줘"}'

# Chat Memory 테스트
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "user-1", "message": "내 이름은 김철수야"}'

curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "user-1", "message": "내 이름이 뭐였지?"}'
```

## 🎓 학습 포인트

1. **RAG Integration** - 문서 기반 답변
2. **Function Calling** - 외부 API 호출
3. **Chat Memory** - 대화 기록 유지
4. **Unified Architecture** - 모든 기능 통합
5. **TDD** - 테스트 주도 개발

## 💡 실전 활용 사례

### 1. 기업 내부 AI 어시스턴트
- 사내 문서 검색 (RAG)
- 날씨/일정 조회 (Function Calling)
- 연속 대화 (Chat Memory)

### 2. 고객 지원 챗봇
- FAQ 문서 기반 답변
- 주문 조회 기능
- 대화 맥락 유지

### 3. 교육 플랫폼
- 학습 자료 검색
- 진도 확인 기능
- 학습 이력 추적

## 🔧 고급 기능

### 1. 하이브리드 검색

```kotlin
fun hybridSearch(query: String): List<Document> {
    // RAG + Keyword search
    val ragResults = vectorStore.similaritySearch(query)
    val keywordResults = documentRepository.findByKeyword(query)
    return (ragResults + keywordResults).distinctBy { it.id }
}
```

### 2. 조건부 Function Calling

```kotlin
val options = if (requiresExternalData(userMessage)) {
    OllamaOptions.builder()
        .withFunction("getWeather")
        .withFunction("getOrderStatus")
        .build()
} else {
    OllamaOptions.builder().build()
}
```

### 3. 스마트 컨텍스트 관리

```kotlin
fun buildContext(sessionId: String, query: String): String {
    val recentHistory = chatHistoryService.getHistory(sessionId).takeLast(5)
    val relevantDocs = vectorStore.similaritySearch(query)
    
    return """
        대화 기록: ${recentHistory.joinToString()}
        관련 문서: ${relevantDocs.joinToString()}
    """.trimIndent()
}
```

## ✨ 통합의 장점

1. **지능적 답변** - RAG로 정확한 정보 제공
2. **실시간 데이터** - Function Calling으로 최신 정보
3. **자연스러운 대화** - Chat Memory로 문맥 유지
4. **확장 가능** - 모듈식 아키텍처

## 🚀 프로덕션 배포 고려사항

1. **Vector Store**: SimpleVectorStore → PostgreSQL with pgvector
2. **Chat History**: In-memory → Redis/Database
3. **Caching**: 자주 묻는 질문 캐싱
4. **Monitoring**: 응답 시간, 정확도 추적
5. **Security**: API 인증, Rate limiting

## 🎯 성공 기준

- ✅ RAG 기반 문서 검색 동작
- ✅ Function Calling 정상 작동
- ✅ Chat Memory 대화 기록 유지
- ✅ 모든 기능 통합 테스트 통과
- ✅ TDD 방식 개발 완료
