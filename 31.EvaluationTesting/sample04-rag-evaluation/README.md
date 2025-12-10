# Sample 04: RAG Evaluation Integration

완전한 RAG 시스템과 통합 평가 기능을 학습합니다. `RelevancyEvaluator`와 `FactCheckingEvaluator`를 함께 사용하여 RAG 응답의 품질을 종합적으로 평가합니다.

## 📚 학습 목표

- 엔드투엔드 RAG 시스템 구축
- 다중 평가자 조합 사용
- 통합 테스트 작성
- ChatResponse 메타데이터 활용
- 품질 점수 계산

## 🎯 핵심 개념

### 통합 평가 시스템

RAG 시스템의 품질을 보장하기 위해 두 가지 평가자를 함께 사용합니다:

1. **RelevancyEvaluator**: 응답이 질문과 컨텍스트에 관련이 있는지 평가
2. **FactCheckingEvaluator**: 응답이 검색된 문서에 기반한 사실인지 검증

### 평가 프로세스

```
1. 사용자 질문 입력
   ↓
2. VectorStore에서 관련 문서 검색
   ↓
3. QuestionAnswerAdvisor로 RAG 응답 생성
   ↓
4. RelevancyEvaluator로 관련성 평가
   ↓
5. FactCheckingEvaluator로 사실성 검증
   ↓
6. 품질 점수 계산 및 리포트 생성
```

## 🔍 주요 구현

### 1. RAG 시스템 초기화

```kotlin
@PostConstruct
fun init() {
    vectorStore = SimpleVectorStore(embeddingModel)
    loadSampleDocuments()
    
    chatClient = chatClientBuilder
        .defaultAdvisors(QuestionAnswerAdvisor(vectorStore))
        .build()
    
    relevancyEvaluator = RelevancyEvaluator(chatClientBuilder)
    factCheckingEvaluator = FactCheckingEvaluator(chatClientBuilder)
}
```

### 2. 질문 및 평가

```kotlin
fun askAndEvaluate(question: String): RagEvaluationResult {
    // RAG 응답 생성
    val chatResponse = chatClient.prompt()
        .user(question)
        .call()
        .chatResponse()
    
    val answer = chatResponse.result.output.content
    val retrievedDocuments = vectorStore.similaritySearch(question)
    
    // 관련성 평가
    val relevanceEvaluation = relevancyEvaluator.evaluate(
        EvaluationRequest(question, retrievedDocuments, answer)
    )
    
    // 사실성 검증
    val factCheckEvaluation = factCheckingEvaluator.evaluate(
        EvaluationRequest(combinedDocument, emptyList(), answer)
    )
    
    return RagEvaluationResult(
        question, answer, documentContents,
        relevanceEvaluation, factCheckEvaluation
    )
}
```

### 3. 품질 점수 계산

```kotlin
fun calculateQualityScore(result: RagEvaluationResult): Double {
    val relevanceScore = if (result.relevanceEvaluation.isPass) 1.0 else 0.0
    val factCheckScore = if (result.factCheckEvaluation.isPass) 1.0 else 0.0
    
    // 가중 평균 (관련성 60%, 사실성 40%)
    return (relevanceScore * 0.6) + (factCheckScore * 0.4)
}
```

## 🧪 테스트 케이스

### RAG 응답 생성

```kotlin
@Test
fun `should answer question using RAG`() {
    val question = "Spring AI란 무엇인가요?"
    val answer = ragEvaluationService.askQuestion(question)
    
    assertThat(answer).containsIgnoringCase("Spring AI")
}
```

### 통합 평가

```kotlin
@Test
fun `should evaluate RAG response with both evaluators`() {
    val question = "Vector Store의 역할은?"
    val result = ragEvaluationService.askAndEvaluate(question)
    
    assertThat(result.answer).isNotBlank()
    assertThat(result.relevanceEvaluation).isNotNull
    assertThat(result.factCheckEvaluation).isNotNull
}
```

### 배치 평가

```kotlin
@Test
fun `should evaluate multiple questions`() {
    val questions = listOf(
        "Spring AI란?",
        "RAG의 구성요소는?",
        "Vector Store란?"
    )
    
    val results = ragEvaluationService.evaluateMultipleQuestions(questions)
    
    assertThat(results).hasSize(3)
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

### HTTP 요청 테스트

`test.http` 파일을 사용하여 다양한 엔드포인트를 테스트할 수 있습니다.

## 📊 API 엔드포인트

### 1. 기본 질문

```http
POST /api/rag-evaluation/ask
Content-Type: application/json

{
  "question": "Spring AI란 무엇인가요?"
}
```

### 2. 질문 및 평가

```http
POST /api/rag-evaluation/ask-and-evaluate
Content-Type: application/json

{
  "question": "RAG의 주요 구성요소는?"
}
```

**응답 예시:**
```json
{
  "question": "RAG의 주요 구성요소는?",
  "answer": "RAG는 Retrieval, Augmentation, Generation으로 구성됩니다.",
  "retrievedDocuments": ["..."],
  "relevancePass": true,
  "factCheckPass": true,
  "qualityScore": 1.0
}
```

### 3. 상세 리포트

```http
POST /api/rag-evaluation/detailed-report
```

### 4. 배치 평가

```http
POST /api/rag-evaluation/evaluate-batch
Content-Type: application/json

{
  "questions": [
    "Spring AI란?",
    "RAG란?",
    "Vector Store란?"
  ]
}
```

## 💡 주요 학습 포인트

1. **통합 평가**: 여러 평가자를 조합하여 종합적인 품질 평가
2. **RAG 파이프라인**: 검색, 증강, 생성의 전체 흐름 구현
3. **메타데이터 활용**: ChatResponse에서 컨텍스트 정보 추출
4. **품질 점수**: 평가 결과를 정량화하여 시스템 개선에 활용
5. **REST API**: 평가 기능을 API로 제공하여 실용성 향상

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
          temperature: 0.7  # 응답 생성용
      embedding:
        options:
          model: text-embedding-3-small
```

## 📈 평가 메트릭

### 품질 점수 계산

```
Quality Score = (Relevance × 0.6) + (FactCheck × 0.4)
```

- **1.0**: 완벽한 응답 (관련성 ✅, 사실성 ✅)
- **0.6**: 관련성만 통과 (관련성 ✅, 사실성 ❌)
- **0.4**: 사실성만 통과 (관련성 ❌, 사실성 ✅)
- **0.0**: 품질 미달 (관련성 ❌, 사실성 ❌)

## 📖 참고 사항

### ChatResponse 메타데이터

Spring AI의 `ChatResponse`에서 RAG 컨텍스트를 추출할 수 있습니다:

```kotlin
val context = chatResponse.metadata
    .get(RetrievalAugmentationAdvisor.DOCUMENT_CONTEXT)
```

### 평가 전략

1. **개발 단계**: 모든 응답을 평가하여 품질 확인
2. **프로덕션**: 샘플링 평가로 시스템 모니터링
3. **CI/CD**: 자동화된 평가 테스트로 품질 보장

## 🎓 실전 활용

이 샘플은 다음과 같은 실전 시나리오에 적용할 수 있습니다:

- **고객 지원 챗봇**: 응답 품질 자동 검증
- **문서 Q&A 시스템**: 정확성 보장
- **지식 관리 시스템**: 정보 신뢰성 확보
- **교육 플랫폼**: 학습 콘텐츠 검증

---

**축하합니다!** 🎉 Evaluation Testing 튜토리얼을 완료했습니다. 이제 Spring AI의 평가 프레임워크를 활용하여 신뢰할 수 있는 AI 애플리케이션을 개발할 수 있습니다.
