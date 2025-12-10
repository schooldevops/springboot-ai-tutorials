# Sample 02: RelevancyEvaluator

`RelevancyEvaluator`를 사용하여 AI 응답의 관련성을 평가하는 방법을 학습합니다.

## 📚 학습 목표

- `RelevancyEvaluator` 사용법 이해
- RAG 응답 품질 검증
- 커스텀 프롬프트 템플릿 작성
- VectorStore와 통합한 평가
- 다중 응답 평가

## 🎯 핵심 개념

### RelevancyEvaluator

AI 응답이 사용자 질문과 제공된 컨텍스트에 관련성이 있는지 평가하는 평가자입니다.

**기본 프롬프트 템플릿:**
```
Your task is to evaluate if the response for the query 
is in line with the context information provided.
You have two options to answer. Either YES or NO.

Query: {query}
Response: {response}
Context: {context}
Answer:
```

### 평가 프로세스

```
1. VectorStore에서 관련 문서 검색
   └─ similaritySearch(question)

2. EvaluationRequest 생성
   ├─ userText: 사용자 질문
   ├─ dataList: 검색된 문서들
   └─ responseContent: AI 응답

3. RelevancyEvaluator로 평가
   └─ evaluate(request)

4. EvaluationResponse 반환
   └─ isPass: true/false
```

## 🔍 주요 구현

### 1. RAG 응답 평가

```kotlin
fun evaluateRagResponse(
    question: String,
    response: String
): EvaluationResponse {
    // VectorStore에서 관련 문서 검색
    val similarDocuments = vectorStore.similaritySearch(question)
    
    // RelevancyEvaluator 생성
    val evaluator = RelevancyEvaluator(chatClientBuilder)
    
    // 평가 수행
    val request = EvaluationRequest(question, similarDocuments, response)
    return evaluator.evaluate(request)
}
```

### 2. 컨텍스트 기반 평가

```kotlin
fun evaluateWithContext(
    question: String,
    response: String,
    context: List<String>
): EvaluationResponse {
    val evaluator = RelevancyEvaluator(chatClientBuilder)
    val dataList = context.map { Document(it) }
    val request = EvaluationRequest(question, dataList, response)
    
    return evaluator.evaluate(request)
}
```

### 3. 다중 응답 평가

```kotlin
fun evaluateMultipleResponses(
    question: String,
    responses: List<String>
): List<Boolean> {
    return responses.map { response ->
        evaluateRagResponse(question, response).isPass
    }
}
```

## 🧪 테스트 케이스

### 관련성 있는 응답

```kotlin
@Test
fun `should evaluate RAG response as relevant`() {
    val question = "Spring AI란 무엇인가요?"
    val response = "Spring AI는 AI 애플리케이션 개발을 위한 Spring 프레임워크입니다."
    
    val result = relevancyService.evaluateRagResponse(question, response)
    
    assertThat(result.isPass).isTrue()
}
```

### 관련성 없는 응답

```kotlin
@Test
fun `should evaluate irrelevant RAG response as not relevant`() {
    val question = "Spring AI란 무엇인가요?"
    val response = "오늘 날씨가 좋습니다."
    
    val result = relevancyService.evaluateRagResponse(question, response)
    
    assertThat(result.isPass).isFalse()
}
```

### 다중 응답 평가

```kotlin
@Test
fun `should evaluate multiple responses`() {
    val question = "Spring Boot의 특징은?"
    val responses = listOf(
        "Spring Boot는 자동 설정을 제공합니다.",      // 관련성 있음
        "오늘은 월요일입니다.",                        // 관련성 없음
        "Spring Boot는 독립 실행 가능한 애플리케이션을 만듭니다." // 관련성 있음
    )
    
    val results = relevancyService.evaluateMultipleResponses(question, responses)
    
    assertThat(results[0]).isTrue()
    assertThat(results[1]).isFalse()
    assertThat(results[2]).isTrue()
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

## 💡 주요 학습 포인트

1. **VectorStore 통합**: 관련 문서를 자동으로 검색하여 컨텍스트로 사용
2. **관련성 평가**: 응답이 질문과 컨텍스트에 부합하는지 검증
3. **RAG 품질 보증**: RAG 시스템의 응답 품질을 체계적으로 평가
4. **배치 평가**: 여러 응답을 효율적으로 평가
5. **LLM-as-a-Judge**: AI 모델을 평가자로 활용

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
          temperature: 0.0  # 일관된 평가를 위해 낮은 temperature
      embedding:
        options:
          model: text-embedding-3-small
```

## 📊 평가 시나리오

### 시나리오 1: 정확한 응답

- **질문**: "Spring AI란 무엇인가요?"
- **응답**: "Spring AI는 AI 애플리케이션 개발을 위한 Spring 프레임워크입니다."
- **평가**: ✅ PASS (관련성 있음)

### 시나리오 2: 부정확한 응답

- **질문**: "Spring AI란 무엇인가요?"
- **응답**: "오늘 날씨가 좋습니다."
- **평가**: ❌ FAIL (관련성 없음)

### 시나리오 3: 부분적으로 관련된 응답

- **질문**: "RAG의 주요 구성요소는?"
- **응답**: "RAG는 검색 기능을 사용합니다."
- **평가**: 평가 모델에 따라 다를 수 있음

## 📖 참고 사항

- **Temperature 설정**: 평가의 일관성을 위해 낮은 temperature (0.0) 사용 권장
- **평가 모델**: 응답 생성 모델과 다른 모델을 평가에 사용 가능
- **컨텍스트 품질**: 검색된 문서의 품질이 평가 정확도에 영향

## 다음 단계

[Sample 03: FactCheckingEvaluator](../sample03-factchecking-evaluator)에서 사실 확인 및 환각 탐지를 학습합니다.
