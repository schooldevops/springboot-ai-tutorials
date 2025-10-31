# 텍스트 유사도 계산 샘플 프로젝트

이 프로젝트는 Spring AI에서 EmbeddingModel을 사용하여 텍스트 간 유사도를 계산하는 방법을 보여줍니다.

## 📁 프로젝트 구조

```
sample/
├── src/main/kotlin/com/example/springai/
│   ├── TextSimilarityApplication.kt       # 메인 애플리케이션
│   ├── controller/
│   │   ├── BasicSimilarityController.kt           # 기본 유사도 계산
│   │   ├── MultipleSimilarityController.kt        # 여러 텍스트 비교
│   │   ├── PairwiseSimilarityController.kt       # 모든 쌍 비교
│   │   ├── ThresholdSimilarityController.kt      # 임계값 필터링
│   │   ├── TopKSimilarityController.kt          # Top-K 검색
│   │   └── AdvancedSimilarityController.kt       # 고급 기능
│   ├── service/
│   │   ├── DuplicateDetectionService.kt         # 중복 검사
│   │   └── TextClusteringService.kt              # 텍스트 클러스터링
│   ├── util/
│   │   └── SimilarityUtils.kt                   # 유사도 계산 유틸리티
│   └── model/
│       └── CommonModels.kt                       # 공통 모델
└── src/main/resources/
    └── application.yml                           # 설정 파일
```

## 🚀 빠른 시작

### 1. 환경 변수 설정

```bash
export OPENAI_API_KEY="sk-your-api-key-here"
```

### 2. 실행

```bash
./gradlew bootRun
```

### 3. 테스트

#### 기본 유사도 계산

```bash
# 두 텍스트 간 유사도
curl -X POST http://localhost:8080/api/similarity/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "text1": "Spring AI는 프레임워크입니다.",
    "text2": "Spring AI는 라이브러리입니다."
  }'
```

#### 여러 텍스트 비교

```bash
curl -X POST http://localhost:8080/api/similarity/multiple/compare \
  -H "Content-Type: application/json" \
  -d '{
    "query": "프로그래밍",
    "texts": ["코딩", "개발", "요리"]
  }'
```

#### Top-K 유사 텍스트 찾기

```bash
curl -X POST http://localhost:8080/api/similarity/topk/find \
  -H "Content-Type: application/json" \
  -d '{
    "query": "인공지능",
    "texts": ["AI", "머신러닝", "딥러닝", "데이터 과학"],
    "topK": 3
  }'
```

## 📝 주요 예제 설명

### 1. BasicSimilarityController

**기본 유사도 계산:**
- `/api/similarity/calculate`: 두 텍스트 간 유사도 계산

### 2. MultipleSimilarityController

**여러 텍스트 비교:**
- `/api/similarity/multiple/compare`: 하나의 쿼리와 여러 텍스트 비교

### 3. PairwiseSimilarityController

**모든 쌍 비교:**
- `/api/similarity/pairwise/calculate`: 모든 텍스트 쌍의 유사도 계산

### 4. ThresholdSimilarityController

**임계값 필터링:**
- `/api/similarity/threshold/filter`: 임계값 이상의 유사 텍스트 찾기

### 5. TopKSimilarityController

**Top-K 검색:**
- `/api/similarity/topk/find`: 유사도 높은 상위 K개 찾기

### 6. AdvancedSimilarityController

**고급 기능:**
- `/api/similarity/advanced/duplicates`: 중복 문서 검사
- `/api/similarity/advanced/cluster`: 텍스트 클러스터링

## 💡 학습 포인트

이 샘플 프로젝트를 통해 학습할 수 있는 내용:

1. **코사인 유사도 이해**
   - 두 벡터 간 각도로 유사도 측정
   - 0.0 ~ 1.0 범위의 유사도 점수

2. **유사도 계산 구현**
   - 내적과 벡터 크기를 이용한 계산
   - FloatArray 타입 처리

3. **유사도 해석**
   - 점수에 따른 의미 해석
   - 임계값 기반 필터링

4. **실전 활용**
   - 중복 검사
   - 텍스트 클러스터링
   - 유사 문서 검색

## 🔧 핵심 패턴

```kotlin
// 1. 텍스트 임베딩 생성
val embedding1 = embeddingModel.embed(text1)
val embedding2 = embeddingModel.embed(text2)

// 2. 코사인 유사도 계산
val similarity = SimilarityUtils.cosineSimilarity(embedding1, embedding2)

// 3. 결과 해석
val interpretation = SimilarityUtils.interpretSimilarity(similarity)
```

## 📚 참고사항

### 코사인 유사도 계산

```kotlin
fun cosineSimilarity(a: FloatArray, b: FloatArray): Double {
    if (a.size != b.size) return 0.0
    
    val dotProduct = a.zip(b).sumOf { (x, y) -> (x * y).toDouble() }
    val normA = kotlin.math.sqrt(a.sumOf { (it * it).toDouble() })
    val normB = kotlin.math.sqrt(b.sumOf { (it * it).toDouble() })
    
    return if (normA > 0.0 && normB > 0.0) {
        dotProduct / (normA * normB)
    } else {
        0.0
    }
}
```

### 유사도 해석 가이드

| 유사도 점수 | 해석 |
|------------|------|
| 0.9 ~ 1.0 | 매우 유사 (거의 동일) |
| 0.7 ~ 0.9 | 유사 (비슷한 의미) |
| 0.5 ~ 0.7 | 보통 (약간 관련) |
| 0.3 ~ 0.5 | 다소 다름 (약간 관련) |
| 0.0 ~ 0.3 | 다름 (관련 없음) |

---

**다음 학습**: [6.1: 벡터 저장소의 필요성](../../README.md#61-벡터-저장소의-필요성)

