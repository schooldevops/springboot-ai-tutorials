# Sample 03: FactCheckingEvaluator

`FactCheckingEvaluator`를 사용하여 AI 응답의 사실성을 검증하고 환각(hallucination)을 탐지하는 방법을 학습합니다.

## 📚 학습 목표

- `FactCheckingEvaluator` 사용법 이해
- AI 환각(hallucination) 탐지
- 주장(claim)과 문서(document) 검증
- 사실성 평가 패턴
- 다중 주장 검증

## 🎯 핵심 개념

### FactCheckingEvaluator

AI 응답의 사실적 정확성을 검증하여 환각을 탐지하는 평가자입니다.

**평가 방식:**
```
Document: {document}
Claim: {claim}
```

- **Document**: 사실의 근거가 되는 참조 문서
- **Claim**: 검증할 주장 또는 AI 응답

### Grounded Factuality

문서에 기반한 사실성 검증(grounded factuality)에 특화되어 있습니다.

- ✅ **Grounded**: 제공된 문서를 기반으로 사실 확인
- ❌ **Closed-book**: 일반 지식 없이 평가 (이 evaluator의 목적이 아님)

## 🔍 주요 구현

### 1. 기본 사실 확인

```kotlin
fun checkFact(
    document: String,
    claim: String
): EvaluationResponse {
    val evaluator = FactCheckingEvaluator(chatClientBuilder)
    
    val request = EvaluationRequest(
        document,      // 참조 문서
        emptyList(),   // dataList는 사용하지 않음
        claim          // 검증할 주장
    )
    
    return evaluator.evaluate(request)
}
```

### 2. 환각 탐지

```kotlin
fun detectHallucination(
    sourceDocument: String,
    aiResponse: String
): HallucinationResult {
    val evaluationResult = checkFact(sourceDocument, aiResponse)
    
    return HallucinationResult(
        isHallucination = !evaluationResult.isPass,
        sourceDocument = sourceDocument,
        aiResponse = aiResponse
    )
}
```

### 3. 다중 주장 검증

```kotlin
fun checkMultipleClaims(
    document: String,
    claims: List<String>
): List<Boolean> {
    return claims.map { claim ->
        checkFact(document, claim).isPass
    }
}
```

## 🧪 테스트 케이스

### 올바른 사실 검증

```kotlin
@Test
fun `should verify correct fact as supported`() {
    val document = "The Earth is the third planet from the Sun."
    val claim = "The Earth is the third planet from the Sun."
    
    val result = factCheckingService.checkFact(document, claim)
    
    assertThat(result.isPass).isTrue()
}
```

### 잘못된 사실 탐지

```kotlin
@Test
fun `should detect incorrect fact as not supported`() {
    val document = "The Earth is the third planet from the Sun."
    val claim = "The Earth is the fourth planet from the Sun."
    
    val result = factCheckingService.checkFact(document, claim)
    
    assertThat(result.isPass).isFalse()
}
```

### 환각 탐지

```kotlin
@Test
fun `should detect hallucination in AI response`() {
    val sourceDocument = "Spring AI는 2023년에 출시된 프레임워크입니다."
    val aiResponse = "Spring AI는 2020년에 출시되었습니다."
    
    val result = factCheckingService.detectHallucination(sourceDocument, aiResponse)
    
    assertThat(result.isHallucination).isTrue()
}
```

### 다중 주장 검증

```kotlin
@Test
fun `should check multiple claims against document`() {
    val document = """
        Spring Boot는 2014년에 출시되었습니다.
        Spring Boot는 자동 설정 기능을 제공합니다.
    """.trimIndent()
    
    val claims = listOf(
        "Spring Boot는 2014년에 출시되었습니다.",     // ✅ 사실
        "Spring Boot는 2010년에 출시되었습니다.",     // ❌ 거짓
        "Spring Boot는 자동 설정을 제공합니다."       // ✅ 사실
    )
    
    val results = factCheckingService.checkMultipleClaims(document, claims)
    
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

1. **환각 탐지**: AI가 생성한 잘못된 정보를 자동으로 탐지
2. **사실 검증**: 주장이 문서에 의해 뒷받침되는지 확인
3. **품질 보증**: RAG 시스템의 신뢰성 향상
4. **배치 검증**: 여러 주장을 효율적으로 검증
5. **Grounded Factuality**: 문서 기반 사실성 검증

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
          temperature: 0.0  # 일관된 평가를 위해 0.0 사용
```

## 📊 평가 시나리오

### 시나리오 1: 정확한 사실

- **문서**: "The Earth is the third planet from the Sun."
- **주장**: "The Earth is the third planet from the Sun."
- **결과**: ✅ PASS (사실로 뒷받침됨)

### 시나리오 2: 잘못된 사실

- **문서**: "The Earth is the third planet from the Sun."
- **주장**: "The Earth is the fourth planet from the Sun."
- **결과**: ❌ FAIL (사실과 다름)

### 시나리오 3: 환각 탐지

- **원본**: "Spring AI는 2023년에 출시되었습니다."
- **AI 응답**: "Spring AI는 2020년에 출시되었습니다."
- **결과**: ⚠️ HALLUCINATION (환각 탐지)

### 시나리오 4: 부분적 사실

- **문서**: "RAG는 Retrieval, Augmentation, Generation으로 구성됩니다."
- **주장**: "RAG는 Retrieval과 Generation으로 구성됩니다."
- **결과**: 평가 모델에 따라 다를 수 있음 (불완전한 정보)

## 📖 참고 사항

### 효율적인 평가 모델

Spring AI 문서에서는 사실 확인을 위해 더 작고 효율적인 모델 사용을 권장합니다:

- **Bespoke Minicheck**: 사실 확인에 특화된 소형 모델
- **Ollama 지원**: 로컬에서 실행 가능
- **비용 절감**: GPT-4 같은 대형 모델 대비 저렴

### Temperature 설정

- **0.0**: 일관된 평가를 위해 권장
- **결정론적**: 같은 입력에 대해 같은 결과 보장

### 제한사항

- **Grounded Factuality**: 제공된 문서만 기반으로 평가
- **일반 지식 테스트 불가**: Closed-book 평가는 다른 방법 필요

## 다음 단계

[Sample 04: RAG Evaluation Integration](../sample04-rag-evaluation)에서 완전한 RAG 시스템의 통합 평가를 학습합니다.
