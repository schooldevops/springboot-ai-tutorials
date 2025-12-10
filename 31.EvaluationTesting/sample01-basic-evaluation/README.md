# Sample 01: Basic Evaluation Testing

Spring AI의 `Evaluator` 인터페이스와 `EvaluationRequest/Response` 패턴의 기본 사용법을 학습합니다.

## 📚 학습 목표

- `Evaluator` 인터페이스 이해
- `EvaluationRequest` 생성 방법
- `EvaluationResponse` 해석
- 커스텀 평가자 구현
- TDD 방식의 평가 테스트 작성

## 🎯 핵심 개념

### Evaluator Interface

```kotlin
@FunctionalInterface
interface Evaluator {
    fun evaluate(evaluationRequest: EvaluationRequest): EvaluationResponse
}
```

Spring AI의 모든 평가자는 이 인터페이스를 구현합니다.

### EvaluationRequest

평가에 필요한 3가지 정보를 담습니다:

```kotlin
EvaluationRequest(
    userText: String,           // 사용자 질문
    dataList: List<Content>,    // 컨텍스트 데이터
    responseContent: String     // AI 응답
)
```

### EvaluationResponse

평가 결과를 담습니다:

```kotlin
class EvaluationResponse(
    val isPass: Boolean  // 평가 통과 여부
)
```

## 🔍 주요 구현

### BasicEvaluationService

```kotlin
@Service
class BasicEvaluationService(
    private val chatModel: ChatModel
) {
    fun evaluateRelevance(
        userQuestion: String,
        aiResponse: String,
        context: List<String>
    ): EvaluationResponse {
        val chatClientBuilder = ChatClient.builder(chatModel)
        val evaluator = RelevancyEvaluator(chatClientBuilder)
        
        val dataList = context.map { Document(it) }
        val request = EvaluationRequest(userQuestion, dataList, aiResponse)
        
        return evaluator.evaluate(request)
    }
}
```

## 🧪 테스트 케이스

### 1. 관련성 있는 응답 평가

```kotlin
@Test
fun `should evaluate simple response as relevant`() {
    val userQuestion = "What is the capital of France?"
    val aiResponse = "The capital of France is Paris."
    val context = listOf("France is a country in Europe. Its capital city is Paris.")
    
    val result = evaluationService.evaluateRelevance(userQuestion, aiResponse, context)
    
    assertThat(result.isPass).isTrue()
}
```

### 2. 관련성 없는 응답 평가

```kotlin
@Test
fun `should evaluate irrelevant response as not relevant`() {
    val userQuestion = "What is the capital of France?"
    val aiResponse = "The weather is nice today."
    val context = listOf("France is a country in Europe. Its capital city is Paris.")
    
    val result = evaluationService.evaluateRelevance(userQuestion, aiResponse, context)
    
    assertThat(result.isPass).isFalse()
}
```

### 3. 커스텀 평가자 생성

```kotlin
fun createCustomEvaluator(chatClient: ChatClient): Evaluator {
    return object : Evaluator {
        override fun evaluate(request: EvaluationRequest): EvaluationResponse {
            val isPass = request.responseContent.isNotBlank()
            return EvaluationResponse(isPass)
        }
    }
}
```

## 🚀 실행 방법

### 테스트 실행

```bash
./gradlew clean test
```

### 애플리케이션 실행

```bash
./gradlew bootRun
```

## 📊 평가 프로세스

```
1. EvaluationRequest 생성
   ├─ userText: 사용자 질문
   ├─ dataList: 컨텍스트 (Document 리스트)
   └─ responseContent: AI 응답

2. Evaluator 선택
   └─ RelevancyEvaluator 사용

3. 평가 수행
   └─ evaluator.evaluate(request)

4. EvaluationResponse 반환
   └─ isPass: true/false
```

## 💡 주요 학습 포인트

1. **Evaluator 인터페이스**: 모든 평가자의 기본 계약
2. **EvaluationRequest**: 평가에 필요한 정보 캡슐화
3. **RelevancyEvaluator**: 관련성 평가를 위한 기본 구현체
4. **Document 변환**: 문자열 컨텍스트를 Document로 변환
5. **TDD 접근**: 테스트 우선 개발로 신뢰성 확보

## 🔧 설정

### application.yml

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o-mini
          temperature: 0.7
```

## 📖 참고 사항

- **LLM-as-a-Judge**: AI 모델 자체를 평가자로 활용
- **비결정성**: AI 응답의 비결정적 특성으로 인해 평가 기반 테스팅 필요
- **평가 모델**: 응답 생성 모델과 다른 모델을 평가에 사용 가능

## 다음 단계

[Sample 02: RelevancyEvaluator](../sample02-relevancy-evaluator)에서 더 상세한 관련성 평가와 커스텀 프롬프트 템플릿을 학습합니다.
