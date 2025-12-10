# 29. MCP Annotations

## 📖 학습 목표

- **MCP Annotations**의 개념과 활용법을 완벽히 이해합니다
- **Server Annotations**로 선언적 MCP 서버를 구축합니다
- **Client Annotations**로 MCP 클라이언트를 간편하게 구성합니다
- **Special Parameters**로 메타데이터와 컨텍스트를 활용합니다
- **실전 예제**로 통합 시나리오를 학습합니다

---

## 🔑 핵심 키워드

1. **@McpTool** - 실행 가능한 함수 제공 (AI가 호출)
2. **@McpResource** - 데이터 소스 제공 (AI가 읽기)
3. **@McpPrompt** - 프롬프트 템플릿 제공 (AI가 재사용)
4. **@McpClient** - MCP 클라이언트 주입 (서버 연결)
5. **Special Parameters** - ToolContext, Meta 접근

---

## 1. MCP Annotations란?

**MCP Annotations**는 선언적 방식으로 MCP 서버/클라이언트를 구성하는 Spring AI의 강력한 어노테이션 시스템입니다.

### 왜 Annotations를 사용하나요?

**기존 방식 (Programmatic)**
```kotlin
// 복잡한 설정 코드
val tool = ToolDefinition.builder()
    .name("calculate")
    .description("...")
    .build()
```

**Annotations 방식 (Declarative)**
```kotlin
// 간단한 어노테이션
@McpTool(name = "calculate", description = "...")
fun calculate(a: Int, b: Int): Int
```

### 주요 장점
- ✅ **간결성**: 보일러플레이트 코드 제거
- ✅ **가독성**: 의도가 명확한 선언적 코드
- ✅ **유지보수성**: 변경 사항 추적 용이
- ✅ **자동 등록**: Spring이 자동으로 MCP 서버에 등록

---

## 2. 샘플 구성

| Sample | Port | 주제 | 핵심 내용 |
|--------|------|------|-----------|
| **01** | 9900 | Server Annotations | @McpTool, @McpResource, @McpPrompt |
| **02** | 9901 | Client Annotations | @McpClient 활용 |
| **03** | 9902 | Special Parameters | ToolContext, Meta 접근 |
| **04** | 9903 | Complete Examples | 통합 실전 예제 |

---

## 3. Server Annotations 상세

### 3.1 @McpTool - 실행 가능한 함수

AI가 필요시 호출할 수 있는 함수를 정의합니다.

```kotlin
@McpTool(
    name = "calculate",
    description = "Perform arithmetic operations: add, subtract, multiply, divide"
)
fun calculate(a: Int, b: Int, operation: String): Int {
    return when (operation.lowercase()) {
        "add" -> a + b
        "subtract" -> a - b
        "multiply" -> a * b
        "divide" -> if (b != 0) a / b else 0
        else -> throw IllegalArgumentException("Unknown operation")
    }
}

@McpTool(
    name = "convertCase",
    description = "Convert text case: upper, lower, title"
)
fun convertCase(text: String, toCase: String): String {
    return when (toCase.lowercase()) {
        "upper" -> text.uppercase()
        "lower" -> text.lowercase()
        "title" -> text.split(" ").joinToString(" ") { it.capitalize() }
        else -> text
    }
}
```

**사용 시나리오:**
- 계산 함수 (수학, 통계)
- 변환 함수 (단위, 형식, 인코딩)
- 검증 함수 (유효성, 규칙)
- 외부 API 호출

---

### 3.2 @McpResource - 데이터 소스

AI가 읽을 수 있는 데이터를 제공합니다.

```kotlin
@McpResource(
    uri = "config://system",
    name = "System Configuration",
    description = "Application system settings"
)
fun getSystemInfo(): Map<String, Any> {
    return mapOf(
        "version" to "1.0.0",
        "environment" to "production",
        "maxConnections" to 100,
        "timeout" to 30
    )
}

@McpResource(
    uri = "user://{userId}",
    name = "User Profile",
    description = "User profile information"
)
fun getUserProfile(userId: String): Map<String, Any> {
    return mapOf(
        "id" to userId,
        "name" to "User $userId",
        "role" to "admin",
        "active" to true
    )
}
```

**사용 시나리오:**
- 시스템 설정 조회
- 사용자 정보 제공
- 데이터베이스 읽기
- 파일 시스템 접근
- 외부 API 데이터

---

### 3.3 @McpPrompt - 프롬프트 템플릿

AI가 재사용할 수 있는 템플릿을 제공합니다.

```kotlin
@McpPrompt(
    name = "greeting",
    description = "Welcome greeting template"
)
fun greetingPrompt(name: String): String {
    return "Hello {name}, welcome to our service!"
}

@McpPrompt(
    name = "notification",
    description = "Notification message template"
)
fun notificationPrompt(): String {
    return "Hi {name}, you have {count} new messages."
}

// 템플릿 채우기 헬퍼 함수
fun fillTemplate(template: String, params: Map<String, String>): String {
    var result = template
    params.forEach { (key, value) ->
        result = result.replace("{$key}", value)
    }
    return result
}
```

**사용 시나리오:**
- 이메일 템플릿
- 알림 메시지
- 응답 패턴
- 다국어 메시지
- 일관된 포맷

---

## 4. Client Annotations

### 4.1 @McpClient - MCP 서버 연결

```kotlin
@Configuration
class McpClientConfig {
    
    @Bean
    @McpClient("weatherServer")
    fun weatherClient(): McpSyncClient {
        // 자동으로 MCP 서버에 연결
        return McpSyncClient.builder()
            .transport(StdioTransport("weather-server"))
            .build()
    }
}

@Service
class WeatherService(
    @McpClient("weatherServer") 
    private val mcpClient: McpSyncClient
) {
    fun getWeather(city: String): String {
        return mcpClient.callTool("getWeather", mapOf("city" to city))
    }
}
```

**특징:**
- 자동 연결 관리
- 의존성 주입
- 타입 안전성
- 리소스 정리

---

## 5. Special Parameters

### 5.1 ToolContext 접근

```kotlin
@McpTool(name = "contextAware")
fun contextAwareTool(
    param: String,
    @ToolContext context: Map<String, Any>
): String {
    val userId = context["userId"] as? String
    val sessionId = context["sessionId"] as? String
    
    return "Processing $param for user $userId in session $sessionId"
}
```

### 5.2 Meta 정보 접근

```kotlin
@McpTool(name = "metaAware")
fun metaAwareTool(
    param: String,
    @Meta("requestId") requestId: String?,
    @Meta("timestamp") timestamp: Long?
): String {
    return "Request $requestId at $timestamp: $param"
}
```

**활용:**
- 사용자 컨텍스트
- 세션 정보
- 요청 메타데이터
- 추적 및 로깅

---

## 6. 실전 예제

### 6.1 계산기 서비스

```kotlin
@Service
class CalculatorService {
    
    @McpTool(
        name = "calculate",
        description = "Perform arithmetic: add, subtract, multiply, divide"
    )
    fun calculate(a: Int, b: Int, operation: String): Int {
        return when (operation) {
            "add" -> a + b
            "subtract" -> a - b
            "multiply" -> a * b
            "divide" -> if (b != 0) a / b else 0
            else -> throw IllegalArgumentException("Unknown: $operation")
        }
    }
}
```

**AI 사용 예:**
```
User: "15 더하기 27은?"
AI: [calculate(15, 27, "add") 호출]
Tool: 42
AI: "42입니다"
```

### 6.2 사용자 정보 서비스

```kotlin
@Service
class UserService {
    
    @McpResource(
        uri = "user://{userId}",
        name = "User Profile"
    )
    fun getUserProfile(userId: String): Map<String, Any> {
        return mapOf(
            "id" to userId,
            "name" to "User $userId",
            "role" to "admin"
        )
    }
}
```

**AI 사용 예:**
```
User: "사용자 123의 정보는?"
AI: [getUserProfile("123") 호출]
Resource: {id: "123", name: "User 123", role: "admin"}
AI: "사용자 123은 관리자 역할입니다"
```

---

## 7. 아키텍처

```
┌─────────────────────────────────────────┐
│           AI Model (ChatGPT)            │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│         MCP Client (Spring AI)          │
│  - @McpClient                           │
│  - Auto-configuration                   │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│         MCP Server (Your App)           │
│  - @McpTool                             │
│  - @McpResource                         │
│  - @McpPrompt                           │
└─────────────────────────────────────────┘
```

---

## 8. 비교표

### Annotation 비교

| Annotation | 목적 | 반환 타입 | AI 동작 |
|------------|------|-----------|---------|
| @McpTool | 함수 실행 | Any | 호출 및 결과 사용 |
| @McpResource | 데이터 읽기 | Any | 읽기 전용 접근 |
| @McpPrompt | 템플릿 제공 | String | 템플릿 재사용 |

### 사용 시나리오 비교

| 시나리오 | 적합한 Annotation | 예시 |
|----------|-------------------|------|
| 계산 수행 | @McpTool | calculate(10, 5, "add") |
| 설정 조회 | @McpResource | getSystemConfig() |
| 메시지 생성 | @McpPrompt | greetingTemplate() |

---

## 9. 실행 방법

### Sample 01: Server Annotations
```bash
cd sample01-server-annotations
./gradlew bootRun

# 테스트
curl -X POST http://localhost:9900/api/mcp/tool/calculate \
  -H "Content-Type: application/json" \
  -d '{"a": 15, "b": 3, "operation": "multiply"}'
```

### Sample 02: Client Annotations
```bash
cd sample02-client-annotations
./gradlew bootRun
```

### Sample 03: Special Parameters
```bash
cd sample03-special-parameters
./gradlew bootRun
```

### Sample 04: Complete Examples
```bash
cd sample04-complete-examples
./gradlew bootRun
```

---

## 10. 모범 사례

### ✅ DO

```kotlin
// 명확한 이름과 설명
@McpTool(
    name = "calculateSum",
    description = "Calculate the sum of two integers"
)
fun calculateSum(a: Int, b: Int): Int

// 타입 안전성
@McpResource(uri = "user://{id}")
fun getUser(id: String): UserProfile

// 에러 처리
@McpTool(name = "divide")
fun divide(a: Int, b: Int): Int {
    if (b == 0) throw IllegalArgumentException("Division by zero")
    return a / b
}
```

### ❌ DON'T

```kotlin
// 모호한 이름
@McpTool(name = "do")
fun doSomething(x: Any): Any

// 부작용 있는 Resource
@McpResource(uri = "data")
fun getData(): String {
    database.delete() // ❌ Resource는 읽기 전용
    return "data"
}

// 에러 처리 없음
@McpTool(name = "divide")
fun divide(a: Int, b: Int) = a / b // ❌ b=0 처리 없음
```

---

## 11. 문제 해결

### Q: Annotation이 인식되지 않아요
```kotlin
// Component Scan 확인
@SpringBootApplication
@ComponentScan(basePackages = ["com.example.annotations"])
class Application
```

### Q: Tool이 호출되지 않아요
```kotlin
// MCP Server 활성화 확인
spring:
  ai:
    mcp:
      server:
        enabled: true
```

### Q: 파라미터가 전달되지 않아요
```kotlin
// 파라미터 이름 명시
@McpTool(name = "calculate")
fun calculate(
    @Param("a") first: Int,
    @Param("b") second: Int
): Int
```

---

## 12. 다음 단계

1. ✅ **Sample 01** - Server Annotations 기본
2. ✅ **Sample 02** - Client Annotations 연동
3. ✅ **Sample 03** - Special Parameters 활용
4. ✅ **Sample 04** - Complete Examples 통합

---

**시작하기**: [Sample 01: Server Annotations](./sample01-server-annotations/)

**관련 문서**:
- [MCP Protocol](https://modelcontextprotocol.io)
- [Spring AI Reference](https://docs.spring.io/spring-ai/reference/)
- [MCP Annotations Guide](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-annotations-overview.html)
