# 5.2: 텍스트 유사도 계산

## 📖 학습 목표

이 강의를 마친 후 다음을 달성할 수 있습니다:
- **코사인 유사도(Cosine Similarity)**의 개념과 계산 방법을 이해할 수 있습니다
- **두 텍스트 간의 유사도**를 계산하여 의미적 유사성을 비교할 수 있습니다
- **여러 텍스트 간의 유사도**를 비교하고 정렬할 수 있습니다
- **유사도 임계값**을 설정하여 유사한 텍스트를 필터링할 수 있습니다
- **실제 사용 예제**를 통해 텍스트 유사도 계산을 활용할 수 있습니다

---

## 🔑 핵심 키워드

이 장에서 다루는 핵심 키워드들:

1. **코사인 유사도(Cosine Similarity)** - 두 벡터 간의 각도를 이용한 유사도 측정
2. **유사도 점수** - 0.0 ~ 1.0 사이의 값 (1.0에 가까울수록 유사)
3. **임베딩 벡터** - 텍스트를 숫자 배열로 변환한 벡터
4. **의미적 유사성** - 키워드가 아닌 의미 기반의 유사성
5. **유사도 임계값** - 유사도 필터링을 위한 기준값

---

## 1. 텍스트 유사도란?

### 1.1 유사도의 정의

**텍스트 유사도**는 두 텍스트가 의미적으로 얼마나 유사한지를 수치로 표현한 것입니다.

#### 왜 유사도 계산이 필요한가?

```
키워드 기반 매칭:
"코딩"과 "프로그래밍" → 키워드가 다르므로 매칭 실패 ❌

의미 기반 유사도:
"코딩" → [0.1, 0.2, 0.3, ...]
"프로그래밍" → [0.11, 0.19, 0.31, ...]
→ 코사인 유사도: 0.85 (매우 유사함) ✅
```

### 1.2 유사도 측정 방법

#### 주요 유사도 측정 방법들

1. **코사인 유사도 (Cosine Similarity)** ⭐ 가장 많이 사용
   - 벡터 간 각도로 측정
   - 범위: -1.0 ~ 1.0 (일반적으로 0.0 ~ 1.0)
   - **장점**: 벡터 크기에 영향받지 않음

2. **유클리드 거리 (Euclidean Distance)**
   - 벡터 간 실제 거리
   - 범위: 0 ~ ∞ (작을수록 유사)
   - **장점**: 직관적

3. **내적 (Dot Product)**
   - 벡터 간 내적
   - 범위: -∞ ~ ∞
   - **단점**: 벡터 크기에 영향받음

---

## 2. 코사인 유사도(Cosine Similarity)

### 2.1 코사인 유사도의 개념

**코사인 유사도**는 두 벡터 간의 각도를 코사인 값으로 나타낸 것입니다.

#### 수학적 정의

```
코사인 유사도 = (A · B) / (||A|| × ||B||)

A · B: 내적 (Dot Product)
||A||: 벡터 A의 크기 (Norm)
||B||: 벡터 B의 크기 (Norm)
```

#### 특징

- **범위**: -1.0 ~ 1.0
  - **1.0**: 완전히 동일한 방향 (매우 유사)
  - **0.0**: 수직 관계 (관련 없음)
  - **-1.0**: 반대 방향 (반대 의미)
- **정규화**: 벡터 크기에 영향받지 않음
- **효과적**: 임베딩 벡터에 가장 적합

### 2.2 코사인 유사도 계산 예제

```kotlin
fun cosineSimilarity(a: FloatArray, b: FloatArray): Double {
    if (a.size != b.size) return 0.0
    
    // 내적 계산
    val dotProduct = a.zip(b).sumOf { (x, y) -> (x * y).toDouble() }
    
    // 벡터 크기 계산
    val normA = kotlin.math.sqrt(a.sumOf { (it * it).toDouble() })
    val normB = kotlin.math.sqrt(b.sumOf { (it * it).toDouble() })
    
    // 코사인 유사도 계산
    return if (normA > 0.0 && normB > 0.0) {
        dotProduct / (normA * normB)
    } else {
        0.0
    }
}
```

---

## 3. 두 텍스트 간 유사도 계산

### 3.1 기본 유사도 계산

#### 단계별 예제

**1단계: 두 텍스트 임베딩 생성**

```kotlin
val text1 = "Spring AI는 Spring 프레임워크를 위한 AI 통합 라이브러리입니다."
val text2 = "Spring AI는 인공지능 기능을 제공하는 프레임워크입니다."

val embedding1 = embeddingModel.embed(text1)
val embedding2 = embeddingModel.embed(text2)
```

**2단계: 코사인 유사도 계산**

```kotlin
val similarity = cosineSimilarity(embedding1, embedding2)
// similarity: 0.85 (예시)
```

**3단계: 결과 해석**

```kotlin
when {
    similarity > 0.9 -> "매우 유사"
    similarity > 0.7 -> "유사"
    similarity > 0.5 -> "보통"
    else -> "다름"
}
```

### 3.2 전체 코드 예제

```kotlin
@RestController
class SimilarityController(
    private val embeddingModel: EmbeddingModel
) {
    @PostMapping("/similarity")
    fun calculateSimilarity(@RequestBody request: SimilarityRequest): Map<String, Any> {
        val embedding1 = embeddingModel.embed(request.text1)
        val embedding2 = embeddingModel.embed(request.text2)
        
        val similarity = cosineSimilarity(embedding1, embedding2)
        
        return mapOf(
            "text1" to request.text1,
            "text2" to request.text2,
            "similarity" to similarity,
            "similarityPercent" to (similarity * 100),
            "interpretation" to interpretSimilarity(similarity)
        )
    }
    
    private fun interpretSimilarity(similarity: Double): String {
        return when {
            similarity >= 0.9 -> "매우 유사"
            similarity >= 0.7 -> "유사"
            similarity >= 0.5 -> "보통"
            similarity >= 0.3 -> "다소 다름"
            else -> "다름"
        }
    }
}

data class SimilarityRequest(
    val text1: String,
    val text2: String
)
```

---

## 4. 여러 텍스트 간 유사도 비교

### 4.1 하나의 텍스트와 여러 텍스트 비교

```kotlin
@RestController
class MultipleSimilarityController(
    private val embeddingModel: EmbeddingModel
) {
    @PostMapping("/similarity-multiple")
    fun calculateMultipleSimilarity(
        @RequestBody request: MultipleSimilarityRequest
    ): Map<String, Any> {
        val queryEmbedding = embeddingModel.embed(request.query)
        
        val results = request.texts.mapIndexed { index, text ->
            val textEmbedding = embeddingModel.embed(text)
            val similarity = cosineSimilarity(queryEmbedding, textEmbedding)
            
            SimilarityResult(
                index = index,
                text = text,
                similarity = similarity
            )
        }
        
        val sortedResults = results.sortedByDescending { it.similarity }
        
        return mapOf(
            "query" to request.query,
            "results" to sortedResults.map { result ->
                mapOf(
                    "index" to result.index,
                    "text" to result.text,
                    "similarity" to result.similarity,
                    "similarityPercent" to (result.similarity * 100)
                )
            },
            "mostSimilar" to sortedResults.firstOrNull()?.let {
                mapOf(
                    "text" to it.text,
                    "similarity" to it.similarity
                )
            }
        )
    }
}

data class MultipleSimilarityRequest(
    val query: String,
    val texts: List<String>
)

data class SimilarityResult(
    val index: Int,
    val text: String,
    val similarity: Double
)
```

### 4.2 모든 텍스트 쌍의 유사도 계산

```kotlin
@RestController
class PairwiseSimilarityController(
    private val embeddingModel: EmbeddingModel
) {
    @PostMapping("/similarity-pairwise")
    fun calculatePairwiseSimilarity(
        @RequestBody request: PairwiseSimilarityRequest
    ): Map<String, Any> {
        val embeddings = embeddingModel.embed(request.texts)
        
        val pairs = mutableListOf<SimilarityPair>()
        
        for (i in request.texts.indices) {
            for (j in (i + 1) until request.texts.size) {
                val similarity = cosineSimilarity(embeddings[i], embeddings[j])
                pairs.add(
                    SimilarityPair(
                        text1 = request.texts[i],
                        text2 = request.texts[j],
                        similarity = similarity
                    )
                )
            }
        }
        
        val sortedPairs = pairs.sortedByDescending { it.similarity }
        
        return mapOf(
            "totalPairs" to pairs.size,
            "pairs" to sortedPairs.map { pair ->
                mapOf(
                    "text1" to pair.text1,
                    "text2" to pair.text2,
                    "similarity" to pair.similarity
                )
            }
        )
    }
}

data class PairwiseSimilarityRequest(
    val texts: List<String>
)

data class SimilarityPair(
    val text1: String,
    val text2: String,
    val similarity: Double
)
```

---

## 5. 유사도 임계값 필터링

### 5.1 임계값 기반 필터링

```kotlin
@RestController
class ThresholdSimilarityController(
    private val embeddingModel: EmbeddingModel
) {
    @PostMapping("/similarity-threshold")
    fun findSimilarTexts(
        @RequestBody request: ThresholdSimilarityRequest
    ): Map<String, Any> {
        val queryEmbedding = embeddingModel.embed(request.query)
        
        val allResults = request.texts.map { text ->
            val textEmbedding = embeddingModel.embed(text)
            val similarity = cosineSimilarity(queryEmbedding, textEmbedding)
            
            SimilarityResult(text = text, similarity = similarity)
        }
        
        // 임계값 이상만 필터링
        val filteredResults = allResults
            .filter { it.similarity >= request.threshold }
            .sortedByDescending { it.similarity }
        
        return mapOf(
            "query" to request.query,
            "threshold" to request.threshold,
            "totalTexts" to request.texts.size,
            "filteredCount" to filteredResults.size,
            "results" to filteredResults.map { result ->
                mapOf(
                    "text" to result.text,
                    "similarity" to result.similarity
                )
            }
        )
    }
}

data class ThresholdSimilarityRequest(
    val query: String,
    val texts: List<String>,
    val threshold: Double = 0.7  // 기본값 0.7 (70% 이상 유사)
)
```

### 5.2 Top-K 유사 텍스트 찾기

```kotlin
@RestController
class TopKSimilarityController(
    private val embeddingModel: EmbeddingModel
) {
    @PostMapping("/similarity-topk")
    fun findTopKSimilar(
        @RequestBody request: TopKSimilarityRequest
    ): Map<String, Any> {
        val queryEmbedding = embeddingModel.embed(request.query)
        
        val allResults = request.texts.map { text ->
            val textEmbedding = embeddingModel.embed(text)
            val similarity = cosineSimilarity(queryEmbedding, textEmbedding)
            
            SimilarityResult(text = text, similarity = similarity)
        }
        
        val topK = allResults
            .sortedByDescending { it.similarity }
            .take(request.topK)
        
        return mapOf(
            "query" to request.query,
            "topK" to request.topK,
            "results" to topK.mapIndexed { index, result ->
                mapOf(
                    "rank" to (index + 1),
                    "text" to result.text,
                    "similarity" to result.similarity,
                    "similarityPercent" to (result.similarity * 100)
                )
            }
        )
    }
}

data class TopKSimilarityRequest(
    val query: String,
    val texts: List<String>,
    val topK: Int = 5
)
```

---

## 6. 실전 활용 예제

### 6.1 문서 중복 검사

```kotlin
@Service
class DuplicateDetectionService(
    private val embeddingModel: EmbeddingModel
) {
    fun detectDuplicates(
        texts: List<String>,
        threshold: Double = 0.95
    ): List<DuplicatePair> {
        val embeddings = embeddingModel.embed(texts)
        
        val duplicates = mutableListOf<DuplicatePair>()
        
        for (i in texts.indices) {
            for (j in (i + 1) until texts.size) {
                val similarity = cosineSimilarity(embeddings[i], embeddings[j])
                
                if (similarity >= threshold) {
                    duplicates.add(
                        DuplicatePair(
                            text1 = texts[i],
                            text2 = texts[j],
                            similarity = similarity
                        )
                    )
                }
            }
        }
        
        return duplicates.sortedByDescending { it.similarity }
    }
}

data class DuplicatePair(
    val text1: String,
    val text2: String,
    val similarity: Double
)
```

### 6.2 유사 문서 그룹화

```kotlin
@Service
class TextClusteringService(
    private val embeddingModel: EmbeddingModel
) {
    fun clusterTexts(
        texts: List<String>,
        similarityThreshold: Double = 0.7
    ): List<TextCluster> {
        val embeddings = embeddingModel.embed(texts)
        val clusters = mutableListOf<TextCluster>()
        val used = BooleanArray(texts.size)
        
        for (i in texts.indices) {
            if (used[i]) continue
            
            val cluster = mutableListOf<Int>()
            cluster.add(i)
            used[i] = true
            
            for (j in (i + 1) until texts.size) {
                if (used[j]) continue
                
                val similarity = cosineSimilarity(embeddings[i], embeddings[j])
                
                if (similarity >= similarityThreshold) {
                    cluster.add(j)
                    used[j] = true
                }
            }
            
            clusters.add(
                TextCluster(
                    texts = cluster.map { texts[it] },
                    centerIndex = i,
                    size = cluster.size
                )
            )
        }
        
        return clusters.sortedByDescending { it.size }
    }
}

data class TextCluster(
    val texts: List<String>,
    val centerIndex: Int,
    val size: Int
)
```

---

## 7. 유사도 해석 가이드

### 7.1 유사도 점수 해석

| 유사도 점수 | 해석 | 활용 |
|------------|------|------|
| **0.9 ~ 1.0** | 매우 유사 (거의 동일) | 중복 검사, 동의어 찾기 |
| **0.7 ~ 0.9** | 유사 (비슷한 의미) | 유사 문서 검색, 추천 시스템 |
| **0.5 ~ 0.7** | 보통 (약간 관련) | 관련 문서 검색 |
| **0.3 ~ 0.5** | 다소 다름 (약간 관련) | 관련성 낮은 검색 |
| **0.0 ~ 0.3** | 다름 (관련 없음) | 필터링 대상 |

### 7.2 임계값 선택 가이드

#### 용도별 권장 임계값

- **중복 검사**: 0.95 이상
- **유사 문서 검색**: 0.7 이상
- **관련 문서 검색**: 0.5 이상
- **관련성 필터링**: 0.3 이상

---

## 8. 성능 최적화

### 8.1 배치 임베딩 활용

```kotlin
// ✅ 좋은 예: 배치 처리
val embeddings = embeddingModel.embed(texts)
val queryEmbedding = embeddingModel.embed(query)

// 모든 텍스트와 유사도 계산
val similarities = embeddings.map { 
    cosineSimilarity(queryEmbedding, it) 
}

// ❌ 나쁜 예: 개별 처리
val similarities = texts.map { text ->
    val textEmbedding = embeddingModel.embed(text)
    cosineSimilarity(queryEmbedding, textEmbedding)
}
```

### 8.2 캐싱 활용

```kotlin
@Service
class CachedSimilarityService(
    private val embeddingModel: EmbeddingModel
) {
    private val embeddingCache = mutableMapOf<String, FloatArray>()
    
    fun getEmbedding(text: String): FloatArray {
        return embeddingCache.getOrPut(text) {
            embeddingModel.embed(text)
        }
    }
    
    fun calculateSimilarity(text1: String, text2: String): Double {
        val embedding1 = getEmbedding(text1)
        val embedding2 = getEmbedding(text2)
        
        return cosineSimilarity(embedding1, embedding2)
    }
}
```

---

## 9. 주의사항 및 트러블슈팅

### 9.1 일반적인 문제들

#### 문제 1: 유사도가 항상 낮음

**증상:**
```
모든 유사도가 0.5 이하
```

**원인**: 
- 임베딩 모델 문제
- 텍스트가 실제로 다름

**해결책:**
- 동일한 텍스트로 테스트
- 다른 임베딩 모델 시도
- 임계값 조정

#### 문제 2: 유사도 계산 오류

**증상:**
```
NaN 또는 Infinity 값
```

**원인**: 벡터 크기가 0

**해결책:**
```kotlin
if (normA > 0.0 && normB > 0.0) {
    dotProduct / (normA * normB)
} else {
    0.0  // 또는 예외 처리
}
```

#### 문제 3: 성능 저하

**증상:**
```
대량의 텍스트 비교 시 느림
```

**해결책:**
- 배치 임베딩 활용
- 캐싱 적용
- 병렬 처리 고려

---

## 10. 요약

### 10.1 핵심 내용 정리

1. **코사인 유사도**: 두 벡터 간 각도로 유사도 측정 (0.0 ~ 1.0)
2. **유사도 계산**: 내적 / (벡터 크기 × 벡터 크기)
3. **유사도 해석**: 점수에 따른 의미 해석
4. **임계값 필터링**: 특정 임계값 이상만 필터링
5. **Top-K 검색**: 유사도 높은 상위 K개 선택

### 10.2 기본 패턴

```kotlin
// 1. 텍스트 임베딩 생성
val embedding1 = embeddingModel.embed(text1)
val embedding2 = embeddingModel.embed(text2)

// 2. 코사인 유사도 계산
val similarity = cosineSimilarity(embedding1, embedding2)

// 3. 결과 해석
when {
    similarity >= 0.9 -> "매우 유사"
    similarity >= 0.7 -> "유사"
    similarity >= 0.5 -> "보통"
    else -> "다름"
}
```

### 10.3 코사인 유사도 계산 함수

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

### 10.4 다음 학습 내용

이제 텍스트 유사도 계산을 배웠으니, 다음 장에서는:
- **벡터 저장소**: 대량의 벡터 저장 및 검색
- **RAG 구현**: 임베딩과 유사도 검색을 활용한 검색 기반 생성
- **고급 검색**: 하이브리드 검색, 재랭킹 등

---

## 📚 참고 자료

- [코사인 유사도 위키피디아](https://en.wikipedia.org/wiki/Cosine_similarity)
- [벡터 유사도 측정 방법](https://www.pinecone.io/learn/vector-similarity/)
- [Spring AI Embedding 공식 문서](https://docs.spring.io/spring-ai/reference/api/embedding.html)

---

## ❓ 학습 확인 문제

다음 질문에 답할 수 있는지 확인해보세요:

1. 코사인 유사도란 무엇이며, 왜 사용하나요?
2. 두 텍스트 간 유사도를 계산하는 방법은?
3. 여러 텍스트 중 가장 유사한 텍스트를 찾는 방법은?
4. 유사도 임계값을 어떻게 선택하나요?
5. 코사인 유사도 계산의 성능을 최적화하는 방법은?

---

**다음 장**: [6.1: 벡터 저장소의 필요성](../README.md#61-벡터-저장소의-필요성)

