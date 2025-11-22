# 🚀 Quick Start Guide

## Prerequisites

- ☕ Java 17 or higher
- 🦙 Ollama with Llama model

## Setup Steps

### 1. Install and Start Ollama

```bash
ollama serve
ollama pull llama3.2
```

### 2. Run Tests (TDD Verification)

```bash
cd 19.ConversationalChatBot/sample
./gradlew test

# Expected output:
# ChatHistoryServiceTest > should store and retrieve chat history PASSED
# ChatHistoryServiceTest > should maintain separate histories for different sessions PASSED
# ChatHistoryServiceTest > should clear history for specific session PASSED
# ChatHistoryServiceTest > should return empty list for non-existent session PASSED
# BUILD SUCCESSFUL
# 4 tests completed
```

### 3. Run the Application

```bash
./gradlew bootRun
```

## Testing Conversational Chat

### Scenario: Context-aware Conversation

```bash
# Turn 1: Introduce yourself
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "user-123", "message": "내 이름은 김철수야"}'

# Response: "안녕하세요 김철수님!"

# Turn 2: Ask about name (AI remembers from Turn 1)
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "user-123", "message": "내 이름이 뭐였지?"}'

# Response: "김철수님이라고 하셨습니다."

# Turn 3: Continue conversation
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "user-123", "message": "고마워"}'

# Response: "천만에요, 김철수님!"
```

## Key Features Demonstrated

### ✅ Chat History Management

AI remembers previous messages in the conversation.

### ✅ Session Isolation

Different users have separate conversation histories.

### ✅ Context Awareness

AI uses previous context to generate relevant responses.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/chat/health` | Health check with active sessions |
| POST | `/api/chat` | Send message (with history) |
| GET | `/api/chat/{sessionId}/history` | View conversation history |
| DELETE | `/api/chat/{sessionId}` | Clear conversation history |

## Managing Chat History

### View History

```bash
curl http://localhost:8080/api/chat/user-123/history
```

**Response:**
```json
{
  "sessionId": "user-123",
  "messageCount": 6,
  "messages": [
    {"content": "내 이름은 김철수야"},
    {"content": "안녕하세요 김철수님!"},
    {"content": "내 이름이 뭐였지?"},
    {"content": "김철수님이라고 하셨습니다."}
  ]
}
```

### Clear History

```bash
curl -X DELETE http://localhost:8080/api/chat/user-123
```

## Troubleshooting

### Ollama Connection Error

```
Connection refused: localhost:11434
```

**Solution:**
```bash
ollama serve
```

### AI Not Remembering Context

**Check:**
1. Using same sessionId for all requests
2. History is being saved correctly
3. Ollama is running

## Next Steps

- 📖 Read the full [README.md](README.md)
- 🧪 Test different conversation scenarios
- 🔧 Implement session expiration
- 📝 Add persistent storage

## Tips

💡 **Session Management**: Use unique sessionId for each user

💡 **History Limit**: Consider limiting history to recent N messages

💡 **Persistence**: Implement database storage for production

## Support

- 📚 [Spring AI ChatMemory](https://docs.spring.io/spring-ai/reference/concepts.html#_conversations)
