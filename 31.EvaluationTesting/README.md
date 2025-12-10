# Evaluation Testing Tutorial

Spring AI의 평가(Evaluation) 및 테스팅 기능을 활용하여 AI 애플리케이션의 품질을 검증하는 방법을 학습합니다.

## 📚 개요

AI 애플리케이션은 비결정적(non-deterministic) 특성으로 인해 전통적인 `assertEquals` 방식의 테스팅이 어렵습니다. Spring AI는 **LLM-as-a-Judge** 패턴을 활용한 평가 프레임워크를 제공하여 AI 응답의 품질을 체계적으로 검증할 수 있습니다.

## 🎯 핵심 개념

### 1. Evaluator Interface

Spring AI의 평가 시스템의 핵심 인터페이스입니다:

```kotlin
@FunctionalInterface
interface Evaluator {
    fun evaluate(evaluationRequest: EvaluationRequest): EvaluationResponse
}
```

### 2. EvaluationRequest

평가에 필요한 정보를 담는 객체:

- **userText**: 사용자의 원본 질문
- **dataList**: RAG에서 검색된 컨텍스트 데이터
- **responseContent**: AI 모델의 응답 내용

### 3. EvaluationResponse

평가 결과를 담는 객체:

- **isPass**: 평가 통과 여부 (Boolean)
- **score**: 평가 점수 (선택적)
- **feedback**: 평가 피드백 (선택적)

### 4. LLM-as-a-Judge Pattern

AI 모델 자체를 평가자로 활용하는 패턴입니다. 응답을 생성한 모델과 다른 (더 강력한) 모델을 평가에 사용할 수 있습니다.

## 🔍 주요 Evaluator 구현체

### RelevancyEvaluator

AI 응답이 사용자 질문과 제공된 컨텍스트에 관련성이 있는지 평가합니다.

**사용 사례:**
- RAG 시스템의 응답 품질 검증
- 검색된 문서와 응답의 일관성 확인
- 컨텍스트 기반 응답의 적절성 평가

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

### FactCheckingEvaluator

AI 응답의 사실적 정확성을 검증하여 환각(hallucination)을 탐지합니다.

**사용 사례:**
- AI 환각(hallucination) 탐지
- 주장(claim)과 문서(document) 간 사실 검증
- 정보의 정확성 보장

**평가 방식:**
```
Document: {document}
Claim: {claim}
```

문서에 기반한 사실성 검증(grounded factuality)에 특화되어 있습니다.

## 📂 샘플 구성

### [Sample 01: Basic Evaluation Testing](./sample01-basic-evaluation)

`Evaluator` 인터페이스와 `EvaluationRequest/Response` 패턴의 기본 사용법을 학습합니다.

**학습 내용:**
- Evaluator 인터페이스 이해
- EvaluationRequest 생성
- EvaluationResponse 해석
- 커스텀 평가자 구현

### [Sample 02: RelevancyEvaluator](./sample02-relevancy-evaluator)

AI 응답의 관련성을 평가하는 방법을 학습합니다.

**학습 내용:**
- RelevancyEvaluator 사용법
- 커스텀 프롬프트 템플릿 작성
- RAG 응답 검증
- 컨텍스트 기반 평가

### [Sample 03: FactCheckingEvaluator](./sample03-factchecking-evaluator)

사실 확인 및 환각 탐지 방법을 학습합니다.

**학습 내용:**
- FactCheckingEvaluator 사용법
- 환각(hallucination) 탐지
- 주장과 문서 검증
- 사실성 평가 패턴

### [Sample 04: RAG Evaluation Integration](./sample04-rag-evaluation)

완전한 RAG 시스템의 통합 평가를 학습합니다.

**학습 내용:**
- 엔드투엔드 RAG 평가
- 다중 평가자 조합
- 통합 테스트 작성
- ChatResponse 메타데이터 활용

## 🎓 평가 테스팅 모범 사례

### 1. 적절한 평가 모델 선택

- 평가용 모델은 응답 생성 모델과 다를 수 있음
- 더 강력한 모델을 평가에 사용하여 정확도 향상
- 비용 효율성을 위해 특화된 소형 모델 활용 (예: Bespoke Minicheck)

### 2. RAG 시스템 평가 전략

**검색(Retrieval) 평가:**
- 관련 문서 검색 정확도
- 검색 순위 품질 (MRR, NDCG)
- 컨텍스트 충분성

**생성(Generation) 평가:**
- 응답 관련성 (RelevancyEvaluator)
- 사실적 정확성 (FactCheckingEvaluator)
- 환각 탐지

### 3. 테스트 데이터셋 구축

- 다양한 질문 유형 포함
- 정답 또는 관련 문서 명시 (Ground Truth)
- 엣지 케이스 및 오류 시나리오 포함

### 4. 통합 테스트 환경

- Testcontainers로 격리된 테스트 환경 구성
- 실제 Vector Store 사용
- 일관된 평가 기준 적용

### 5. 메타데이터 활용

RAG 응답의 컨텍스트 추출:
```kotlin
val context = chatResponse.metadata
    .get(RetrievalAugmentationAdvisor.DOCUMENT_CONTEXT)
```

## 🔧 기술 스택

- **Spring AI**: 1.0.0-M4
- **Spring Boot**: 3.4.0
- **Kotlin**: 2.1.0
- **OpenAI API**: GPT-4o-mini
- **Testing**: JUnit 5, AssertJ

## 📖 참고 자료

- [Spring AI Evaluation Testing Documentation](https://docs.spring.io/spring-ai/reference/api/testing.html)
- [Spring AI GitHub - Integration Tests](https://github.com/spring-projects/spring-ai/tree/main/spring-ai-integration-tests)
- [RAG Evaluation Best Practices](https://docs.spring.io/spring-ai/reference/api/advisors.html)

## 🚀 시작하기

각 샘플은 독립적으로 실행 가능하며, TDD 방식으로 구현되어 있습니다:

```bash
cd sample0X-*
./gradlew clean test
./gradlew bootRun
```

## 💡 주요 학습 포인트

1. **비결정적 AI 응답 테스팅**: 전통적인 assertEquals 대신 평가 기반 접근
2. **품질 보증**: 환각 탐지 및 사실성 검증으로 신뢰성 향상
3. **RAG 검증**: 검색과 생성 품질을 체계적으로 평가
4. **자동화된 평가**: CI/CD 파이프라인에 통합 가능한 테스트 작성

---

**Note**: 모든 샘플은 OpenAI API 키가 필요합니다. `application.yml`에서 설정하거나 환경 변수로 제공하세요.
