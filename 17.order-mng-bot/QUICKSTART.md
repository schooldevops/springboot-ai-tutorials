# 🚀 Quick Start Guide

## Prerequisites

- ☕ Java 17 or higher
- 🐘 Gradle (or use included wrapper)
- 🦙 Ollama with Llama model

## Setup Steps

### 1. Install and Start Ollama

```bash
# macOS
brew install ollama

# Start Ollama
ollama serve

# Pull Llama model
ollama pull llama3.2
```

### 2. Run Tests (TDD Verification)

```bash
cd 17.order-mng-bot/sample

# Run all tests
./gradlew test

# Expected output:
# OrderServiceTest > should get order status PASSED
# OrderServiceTest > should change delivery address PASSED
# OrderServiceTest > should cancel order PASSED
# BUILD SUCCESSFUL
# 5 tests completed
```

### 3. Run the Application

```bash
./gradlew bootRun
```

## Testing Multi-turn Conversation

### Scenario 1: Order Status → Address Change → Cancel

```bash
# Turn 1: Check order status
curl -X POST http://localhost:8080/api/orders/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "주문 12345 상태 알려줘"}'

# Response: "주문 12345는 현재 배송 중입니다..."

# Turn 2: Change delivery address
curl -X POST http://localhost:8080/api/orders/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "배송지를 제주시 제주대로 999로 변경해줘"}'

# Response: "배송지가 변경되었습니다..."

# Turn 3: Cancel order
curl -X POST http://localhost:8080/api/orders/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "이 주문 취소할래"}'

# Response: "주문이 취소되었습니다..."
```

### Scenario 2: Context-aware Conversation

```bash
# Reset conversation
curl -X POST http://localhost:8080/api/orders/reset

# Turn 1: Check order
curl -X POST http://localhost:8080/api/orders/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "주문 67890 어떻게 되고 있어?"}'

# Turn 2: AI remembers order ID from previous turn
curl -X POST http://localhost:8080/api/orders/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "배송지 바꾸고 싶어"}'
# AI will use order ID 67890 from context
```

## Key Features Demonstrated

### ✅ Multi-turn Conversation

AI maintains conversation context across multiple turns.

### ✅ Multiple Functions

AI selects appropriate function based on user intent:
- `getOrderStatus` - For status queries
- `changeDeliveryAddress` - For address changes
- `cancelOrder` - For cancellations

### ✅ Context Awareness

AI remembers previous conversation (e.g., order ID) and uses it in subsequent turns.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/orders/health` | Health check with conversation count |
| POST | `/api/orders/chat` | Multi-turn chat |
| POST | `/api/orders/reset` | Reset conversation history |
| GET | `/api/orders/history` | View conversation history |

## Conversation Management

### View History

```bash
curl http://localhost:8080/api/orders/history
```

### Reset Conversation

```bash
curl -X POST http://localhost:8080/api/orders/reset
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

### AI Not Calling Functions

**Check:**
1. Function descriptions are clear
2. Ollama is running
3. Llama model is pulled

## Understanding Multi-turn

```
Turn 1: User asks about order → AI calls getOrderStatus
Turn 2: User wants to change address → AI calls changeDeliveryAddress
Turn 3: User wants to cancel → AI calls cancelOrder

AI remembers context from previous turns!
```

## Next Steps

- 📖 Read the full [README.md](README.md)
- 🧪 Run tests to see TDD in action
- 🔧 Add more functions (refund, track shipping, etc.)
- 📝 Test different conversation scenarios

## Tips

💡 **Clear Descriptions**: Use detailed @Description for each function

💡 **Context Management**: Reset conversation when starting new topic

💡 **Test Coverage**: Write tests for each function and multi-turn scenarios

## Support

- 📚 [Spring AI Multi-turn](https://docs.spring.io/spring-ai/reference/concepts.html#_conversations)
- 🦙 [Ollama Documentation](https://ollama.com/docs)
