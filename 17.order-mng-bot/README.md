# 17장: [실전] AI 에이전트: 주문 관리 봇

## 📚 학습 목표

Function Calling을 심화하여, '주문 상태 조회', '배송지 변경' 등 여러 Kotlin 함수(Service Bean)를 AI가 상황에 맞게 선택하고 호출하는 **멀티-턴(Multi-turn)** 에이전트를 구현합니다.

## 🔑 핵심 키워드

- `AI Agent`
- `Multi-turn`
- `Function Calling`
- Spring Service 연동
- TDD (Test-Driven Development)

## 📖 개요

이 장에서는 Spring AI의 Function Calling을 활용하여 여러 함수를 등록하고, AI가 대화 맥락에 따라 적절한 함수를 선택하여 호출하는 주문 관리 봇을 구축합니다. **TDD 방식**으로 개발합니다.

## 🎯 Multi-turn Function Calling이란?

**Multi-turn**은 여러 차례의 대화를 통해 AI가 상황에 맞는 함수를 선택하고 호출하는 것입니다.

### Single-turn vs Multi-turn

| 특징 | Single-turn | Multi-turn |
|------|------------|-----------|
| 대화 횟수 | 1회 | 여러 회 |
| 컨텍스트 | 유지 안 됨 | 유지됨 |
| 함수 선택 | 단일 함수 | 상황별 함수 |
| 사용 예 | 날씨 조회 | 주문 관리 |

## 🔄 Multi-turn 워크플로우

```
Turn 1:
User: "주문 12345 상태 알려줘"
  ↓
AI: getOrderStatus(12345) 호출
  ↓
Response: "배송 중입니다"

Turn 2:
User: "배송지 변경하고 싶어"
  ↓
AI: changeDeliveryAddress(12345, new_address) 호출
  ↓
Response: "배송지가 변경되었습니다"

Turn 3:
User: "취소할래"
  ↓
AI: cancelOrder(12345) 호출
  ↓
Response: "주문이 취소되었습니다"
```

## 💻 구현 상세 (TDD 방식)

### 1. 주문 서비스 (테스트 먼저)

**OrderServiceTest.kt:**
```kotlin
@Test
fun `should get order status`() {
    val status = orderService.getOrderStatus("12345")
    assertEquals("배송 중", status.status)
}

@Test
fun `should change delivery address`() {
    val result = orderService.changeDeliveryAddress("12345", "새 주소")
    assertTrue(result.success)
}
```

**OrderService.kt:**
```kotlin
@Service
class OrderService {
    private val orders = mutableMapOf<String, Order>()
    
    fun getOrderStatus(orderId: String): OrderStatus {
        val order = orders[orderId] ?: throw OrderNotFoundException()
        return OrderStatus(orderId, order.status)
    }
    
    fun changeDeliveryAddress(orderId: String, newAddress: String): Result {
        val order = orders[orderId] ?: throw OrderNotFoundException()
        order.deliveryAddress = newAddress
        return Result(success = true)
    }
    
    fun cancelOrder(orderId: String): Result {
        orders.remove(orderId)
        return Result(success = true)
    }
}
```

### 2. 여러 함수 등록

```kotlin
@Configuration
class FunctionConfig(
    private val orderService: OrderService
) {
    
    @Bean
    @Description("Get order status by order ID")
    fun getOrderStatus(): Function<OrderRequest, OrderStatus> {
        return Function { request ->
            orderService.getOrderStatus(request.orderId)
        }
    }
    
    @Bean
    @Description("Change delivery address for an order")
    fun changeDeliveryAddress(): Function<AddressChangeRequest, Result> {
        return Function { request ->
            orderService.changeDeliveryAddress(request.orderId, request.newAddress)
        }
    }
    
    @Bean
    @Description("Cancel an order")
    fun cancelOrder(): Function<OrderRequest, Result> {
        return Function { request ->
            orderService.cancelOrder(request.orderId)
        }
    }
}
```

### 3. AI 에이전트 (Multi-turn)

```kotlin
@Service
class OrderAgent(
    private val chatModel: ChatModel
) {
    private val conversationHistory = mutableListOf<Message>()
    
    fun chat(userMessage: String): String {
        conversationHistory.add(UserMessage(userMessage))
        
        val options = OllamaOptions.builder()
            .withFunction("getOrderStatus")
            .withFunction("changeDeliveryAddress")
            .withFunction("cancelOrder")
            .build()
        
        val prompt = Prompt(conversationHistory, options)
        val response = chatModel.call(prompt)
        
        conversationHistory.add(response.result.output)
        
        return response.result.output.content
    }
}
```

## 🧪 테스트 방법

### 1. 단위 테스트

```bash
./gradlew test
```

**출력:**
```
OrderServiceTest > should get order status PASSED
OrderServiceTest > should change delivery address PASSED
OrderServiceTest > should cancel order PASSED
OrderAgentTest > should handle multi-turn conversation PASSED

BUILD SUCCESSFUL
```

### 2. 애플리케이션 실행

```bash
ollama serve
ollama pull llama3.2

./gradlew bootRun
```

### 3. Multi-turn 대화 테스트

```bash
# Turn 1: 주문 상태 조회
curl -X POST http://localhost:8080/api/orders/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "주문 12345 상태 알려줘"}'

# Turn 2: 배송지 변경
curl -X POST http://localhost:8080/api/orders/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "배송지를 서울시 강남구로 변경해줘"}'

# Turn 3: 주문 취소
curl -X POST http://localhost:8080/api/orders/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "주문 취소할래"}'
```

## 🎓 학습 포인트

1. **Multi-turn Conversation** - 대화 맥락 유지
2. **Multiple Functions** - 여러 함수 등록 및 선택
3. **Conversation History** - 이전 대화 기억
4. **Context-aware** - 상황에 맞는 함수 호출
5. **TDD** - 테스트 먼저 작성

## 💡 실전 활용 사례

### 1. 고객 서비스 봇
- 주문 조회
- 배송 추적
- 반품/교환 처리

### 2. 예약 관리 시스템
- 예약 확인
- 일정 변경
- 예약 취소

### 3. 계정 관리
- 정보 조회
- 설정 변경
- 계정 삭제

## 🚀 다음 단계

- **18장**: 상품 이미지 태그 생성기 (Multimodal)

## 📚 참고 자료

- [Spring AI Function Calling](https://docs.spring.io/spring-ai/reference/api/functions.html)
- [Multi-turn Conversations](https://docs.spring.io/spring-ai/reference/concepts.html#_conversations)

## 💡 팁

> [!TIP]
> **Conversation History**: 대화 이력을 유지하면 AI가 이전 맥락을 이해하고 더 자연스러운 응답을 생성합니다.

> [!TIP]
> **Function Description**: 각 함수의 @Description을 명확히 작성하면 AI가 올바른 함수를 선택합니다.

> [!WARNING]
> **메모리 관리**: 대화 이력이 너무 길어지면 메모리 문제가 발생할 수 있습니다. 적절히 정리하세요.

## 🔧 고급 기능

### 1. 대화 이력 관리

```kotlin
fun clearHistory() {
    conversationHistory.clear()
}

fun getHistory(): List<Message> {
    return conversationHistory.toList()
}
```

### 2. 조건부 함수 활성화

```kotlin
val options = if (userRole == "admin") {
    OllamaOptions.builder()
        .withFunction("getOrderStatus")
        .withFunction("changeDeliveryAddress")
        .withFunction("cancelOrder")
        .withFunction("refundOrder")  // Admin only
        .build()
} else {
    OllamaOptions.builder()
        .withFunction("getOrderStatus")
        .build()
}
```

### 3. 함수 호출 로깅

```kotlin
@Aspect
class FunctionCallLogger {
    @Around("@annotation(Description)")
    fun logFunctionCall(joinPoint: ProceedingJoinPoint): Any? {
        logger.info("Function called: ${joinPoint.signature.name}")
        return joinPoint.proceed()
    }
}
```

## ✨ Best Practices

1. **명확한 함수 설명**
   - @Description 상세히 작성
   - 파라미터 설명 포함

2. **대화 이력 관리**
   - 적절한 길이 유지
   - 세션별 분리

3. **에러 처리**
   - 함수 실패 시 대응
   - 사용자 친화적 메시지

4. **테스트 커버리지**
   - 각 함수별 테스트
   - Multi-turn 시나리오 테스트
