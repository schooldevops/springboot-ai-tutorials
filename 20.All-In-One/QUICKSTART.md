# 🚀 Quick Start Guide

## Prerequisites

- ☕ Java 17 or higher
- 🦙 Ollama with Llama and embedding models

## Setup Steps

### 1. Install and Start Ollama

```bash
ollama serve
ollama pull llama3.2
ollama pull nomic-embed-text
```

### 2. Run the Application

```bash
cd 20.All-In-One/sample
./gradlew bootRun
```

## Testing All Features

### Feature 1: RAG (Document-based Q&A)

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "user-1", "message": "회사 정책은?"}'

# Response: "재택근무는 주 2회 가능합니다..." (from documents)
```

### Feature 2: Function Calling (Weather API)

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "user-1", "message": "서울 날씨 알려줘"}'

# Response: AI calls getWeather function
```

### Feature 3: Chat Memory (Context Awareness)

```bash
# Turn 1
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "user-1", "message": "내 이름은 김철수야"}'

# Turn 2
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "user-1", "message": "내 이름이 뭐였지?"}'

# Response: "김철수님이라고 하셨습니다." (remembers from history)
```

## All-In-One Integration

```bash
# Complex query using all features
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "user-1", "message": "회사 휴가 규정 알려주고, 서울 날씨도 확인해줘"}'

# AI will:
# 1. Search documents for vacation policy (RAG)
# 2. Call weather function (Function Calling)
# 3. Remember this conversation (Chat Memory)
```

## Key Features

✅ **RAG** - Document-based answers
✅ **Function Calling** - External API calls
✅ **Chat Memory** - Conversation history
✅ **Integrated** - All features work together

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/chat/health` | Health check |
| POST | `/api/chat` | Send message (all features) |
| DELETE | `/api/chat/{sessionId}` | Clear history |

## Architecture

```
User Message
    ↓
┌───────────────────┐
│ Chat Memory       │ → Load history
└───────────────────┘
    ↓
┌───────────────────┐
│ RAG System        │ → Search documents
└───────────────────┘
    ↓
┌───────────────────┐
│ AI Model          │ → Generate response
│ + Function Call   │ → Call external APIs
└───────────────────┘
    ↓
Save to history
```

## Troubleshooting

### Ollama Connection Error
```bash
ollama serve
```

### Embedding Model Not Found
```bash
ollama pull nomic-embed-text
```

## Next Steps

- 📖 Read the full [README.md](README.md)
- 🧪 Test all three features
- 🔧 Add more documents
- 📝 Add more functions

## Tips

💡 **Integration**: All features work together seamlessly

💡 **Scalability**: Easy to add more documents and functions

💡 **Production**: Consider PostgreSQL + pgvector for production
