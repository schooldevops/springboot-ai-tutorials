# 5.1: 임베딩의 개념과 EmbeddingClient

## 📖 학습 목표

이 강의를 마친 후 다음을 달성할 수 있습니다:
- **임베딩(Embedding)**의 개념과 필요성을 이해할 수 있습니다
- **벡터(Vector)**의 의미와 벡터 공간을 이해할 수 있습니다
- **EmbeddingClient**를 사용하여 텍스트를 벡터로 변환할 수 있습니다
- **시맨틱 검색(Semantic Search)**의 기본 원리를 이해할 수 있습니다
- **실제 사용 예제**를 통해 EmbeddingClient를 활용할 수 있습니다

---

## 🔑 핵심 키워드

이 장에서 다루는 핵심 키워드들:

1. **EmbeddingClient** - 텍스트를 벡터로 변환하는 Spring AI 인터페이스
2. **Vector** - 텍스트를 숫자로 표현한 배열 (보통 1536차원)
3. **Embedding** - 텍스트를 벡터로 변환한 결과
4. **시맨틱 검색(Semantic Search)** - 의미 기반 검색 (키워드가 아닌 의미로 검색)
5. **벡터 공간** - 벡터들이 존재하는 다차원 공간

---

## 1. 임베딩이란?

### 1.1 임베딩의 정의

**임베딩(Embedding)**은 텍스트, 이미지, 오디오 등의 데이터를 **벡터(Vector)**라는 숫자 배열로 변환하는 과정입니다.

#### 왜 임베딩이 필요한가?

```
일반 텍스트 검색:
"코딩"과 "프로그래밍" → 키워드가 다르므로 매칭 실패 ❌

임베딩 기반 검색:
"코딩" → [0.1, 0.2, 0.3, ...]
"프로그래밍" → [0.11, 0.19, 0.31, ...]
→ 벡터 간 거리가 가까워 의미가 비슷함 ✅
```

### 1.2 임베딩의 특징

#### 1.2.1 의미 보존

임베딩은 텍스트의 **의미를 보존**합니다:
- 비슷한 의미의 단어/문장 → 비슷한 벡터
- 다른 의미의 단어/문장 → 다른 벡터

#### 1.2.2 다차원 벡터

일반적으로 OpenAI의 `text-embedding-ada-002` 모델은 **1536차원** 벡터를 생성합니다:
- 각 차원은 텍스트의 특정 특성을 나타냄
- 예: 차원 1 = 감정, 차원 2 = 주제, 차원 3 = 문체 등

### 1.3 임베딩의 활용

1. **시맨틱 검색**: 의미 기반 검색 (RAG의 핵심)
2. **유사도 계산**: 두 텍스트의 의미적 유사성 측정
3. **클러스터링**: 비슷한 문서들을 그룹화
4. **추천 시스템**: 유사한 콘텐츠 추천

---

## 2. EmbeddingClient란?

### 2.1 EmbeddingClient의 정의

**EmbeddingClient**는 Spring AI에서 제공하는 인터페이스로, 텍스트를 벡터로 변환하는 기능을 제공합니다.

**주요 특징:**
- **통일된 인터페이스**: 다양한 임베딩 제공자를 동일한 방식으로 사용
- **자동 설정**: Spring Boot 설정만으로 자동 Bean 생성
- **간단한 API**: `.embed()` 메서드로 텍스트를 벡터로 변환

### 2.2 EmbeddingClient 사용 흐름

```
1. 텍스트 입력
   ↓
2. EmbeddingClient.embed()
   ↓
3. 벡터 반환 (List<Double>)
   ↓
4. 벡터 활용 (검색, 유사도 계산 등)
```

---

## 3. EmbeddingClient 기본 사용법

### 3.1 단계별 예제

#### 1단계: EmbeddingClient 주입

```kotlin
import org.springframework.ai.embedding.EmbeddingClient

@RestController
class EmbeddingController(
    private val embeddingClient: EmbeddingClient
) {
    // ...
}
```

#### 2단계: 텍스트 임베딩 생성

```kotlin
val text = "Spring AI는 Spring 프레임워크를 위한 AI 통합 라이브러리입니다."
val embedding = embeddingClient.embed(text)
// embedding: List<Double> (예: [0.1, 0.2, 0.3, ..., 1536개 요소])
```

#### 3단계: 여러 텍스트 임베딩 생성

```kotlin
val texts = listOf(
    "Spring AI 소개",
    "Kotlin 프로그래밍",
    "임베딩 활용"
)
val embeddings = embeddingClient.embed(texts)
// embeddings: List<List<Double>>
```

### 3.2 전체 코드 예제

```kotlin
@RestController
class BasicEmbeddingController(
    private val embeddingClient: EmbeddingClient
) {
    @PostMapping("/embed")
    fun embedText(@RequestBody request: EmbedRequest): Map<String, Any> {
        val embedding = embeddingClient.embed(request.text)
        
        return mapOf(
            "text" to request.text,
            "dimension" to embedding.size,
            "embedding" to embedding
        )
    }
    
    @PostMapping("/embed-batch")
    fun embedBatch(@RequestBody request: BatchEmbedRequest): Map<String, Any> {
        val embeddings = embeddingClient.embed(request.texts)
        
        return mapOf(
            "texts" to request.texts,
            "count" to embeddings.size,
            "dimension" to embeddings.firstOrNull()?.size ?: 0,
            "embeddings" to embeddings
        )
    }
}

data class EmbedRequest(
    val text: String
)

data class BatchEmbedRequest(
    val texts: List<String>
)
```

---

## 4. Embedding 구조 이해하기

### 4.1 Embedding 객체

Spring AI의 `embed()` 메서드는 `List<Double>`을 반환합니다:

```kotlin
val embedding: List<Double> = embeddingClient.embed("텍스트")
```

**특징:**
- **차원 수**: 모델에 따라 다름 (OpenAI: 1536차원)
- **값 범위**: 일반적으로 -1 ~ 1 사이의 실수
- **정규화**: 벡터의 길이가 정규화되어 있음

### 4.2 벡터 차원 확인

```kotlin
@RestController
class EmbeddingInfoController(
    private val embeddingClient: EmbeddingClient
) {
    @GetMapping("/info")
    fun getEmbeddingInfo(): Map<String, Any> {
        val testText = "테스트"
        val embedding = embeddingClient.embed(testText)
        
        return mapOf(
            "dimension" to embedding.size,
            "sample" to embedding.take(5), // 처음 5개 값만 표시
            "min" to embedding.minOrNull(),
            "max" to embedding.maxOrNull(),
            "average" to embedding.average()
        )
    }
}
```

---

## 5. 실전 활용 예제

### 5.1 단일 텍스트 임베딩

```kotlin
@RestController
class SingleEmbeddingController(
    private val embeddingClient: EmbeddingClient
) {
    @PostMapping("/embed-single")
    fun embedSingle(@RequestBody request: EmbedRequest): Map<String, Any> {
        val embedding = embeddingClient.embed(request.text)
        
        return mapOf(
            "text" to request.text,
            "dimension" to embedding.size,
            "embedding" to embedding,
            "first5" to embedding.take(5) // 샘플로 처음 5개만
        )
    }
}
```

### 5.2 배치 임베딩 생성

```kotlin
@RestController
class BatchEmbeddingController(
    private val embeddingClient: EmbeddingClient
) {
    @PostMapping("/embed-batch")
    fun embedBatch(@RequestBody request: BatchEmbedRequest): Map<String, Any> {
        val embeddings = embeddingClient.embed(request.texts)
        
        return mapOf(
            "count" to embeddings.size,
            "dimension" to embeddings.firstOrNull()?.size ?: 0,
            "results" to request.texts.zip(embeddings).map { (text, embedding) ->
                mapOf(
                    "text" to text,
                    "dimension" to embedding.size,
                    "sample" to embedding.take(5)
                )
            }
        )
    }
}
```

### 5.3 문서 분할 및 임베딩

```kotlin
@RestController
class DocumentEmbeddingController(
    private val embeddingClient: EmbeddingClient
) {
    @PostMapping("/embed-document")
    fun embedDocument(@RequestBody request: DocumentRequest): Map<String, Any> {
        // 문서를 청크로 분할 (간단한 예제)
        val chunks = splitIntoChunks(request.text, request.chunkSize)
        
        // 각 청크를 임베딩
        val embeddings = embeddingClient.embed(chunks)
        
        return mapOf(
            "originalLength" to request.text.length,
            "chunkCount" to chunks.size,
            "chunkSize" to request.chunkSize,
            "chunks" to chunks.zip(embeddings).map { (chunk, embedding) ->
                mapOf(
                    "text" to chunk,
                    "embedding" to embedding.take(5) // 샘플
                )
            }
        )
    }
    
    private fun splitIntoChunks(text: String, chunkSize: Int): List<String> {
        val chunks = mutableListOf<String>()
        var start = 0
        
        while (start < text.length) {
            val end = minOf(start + chunkSize, text.length)
            chunks.add(text.substring(start, end))
            start = end
        }
        
        return chunks
    }
}

data class DocumentRequest(
    val text: String,
    val chunkSize: Int = 500
)
```

---

## 6. 시맨틱 검색 기초

### 6.1 시맨틱 검색의 개념

**시맨틱 검색(Semantic Search)**은 키워드 매칭이 아닌 **의미 기반 검색**입니다.

#### 전통적 검색 vs 시맨틱 검색

```
전통적 검색:
검색어: "코딩"
→ "코딩" 키워드가 포함된 문서만 찾음
→ "프로그래밍", "개발" 같은 유사어는 찾지 못함 ❌

시맨틱 검색:
검색어: "코딩"
→ "코딩"의 임베딩 생성
→ 유사한 임베딩을 가진 문서 모두 찾음
→ "프로그래밍", "개발" 관련 문서도 찾음 ✅
```

### 6.2 간단한 시맨틱 검색 구현

```kotlin
@Service
class SimpleSemanticSearchService(
    private val embeddingClient: EmbeddingClient
) {
    // 문서 저장 (실제로는 DB에 저장)
    private val documents = mutableListOf<Document>()
    
    fun addDocument(text: String, id: String) {
        val embedding = embeddingClient.embed(text)
        documents.add(Document(id, text, embedding))
    }
    
    fun search(query: String, topK: Int = 5): List<SearchResult> {
        // 검색어 임베딩 생성
        val queryEmbedding = embeddingClient.embed(query)
        
        // 모든 문서와의 유사도 계산
        val results = documents.map { doc ->
            val similarity = cosineSimilarity(queryEmbedding, doc.embedding)
            SearchResult(doc.id, doc.text, similarity)
        }
        
        // 유사도 순으로 정렬하여 상위 K개 반환
        return results.sortedByDescending { it.similarity }.take(topK)
    }
    
    // 코사인 유사도 계산 (5.2장에서 자세히 다룸)
    private fun cosineSimilarity(a: List<Double>, b: List<Double>): Double {
        if (a.size != b.size) return 0.0
        
        val dotProduct = a.zip(b).sumOf { (x, y) -> x * y }
        val normA = kotlin.math.sqrt(a.sumOf { it * it })
        val normB = kotlin.math.sqrt(b.sumOf { it * it })
        
        return if (normA > 0.0 && normB > 0.0) {
            dotProduct / (normA * normB)
        } else {
            0.0
        }
    }
}

data class Document(
    val id: String,
    val text: String,
    val embedding: List<Double>
)

data class SearchResult(
    val id: String,
    val text: String,
    val similarity: Double
)
```

---

## 7. EmbeddingClient 설정

### 7.1 application.yml 설정

```yaml
spring:
  application:
    name: embedding-demo
  
  # OpenAI Embedding 설정
  ai:
    openai:
      api-key: ${OPENAI_API_KEY:your-api-key-here}
      embedding:
        options:
          model: text-embedding-ada-002  # 임베딩 모델
          # 또는 text-embedding-3-small, text-embedding-3-large
```

### 7.2 모델 선택

#### OpenAI 임베딩 모델

1. **text-embedding-ada-002** (기본)
   - 차원: 1536
   - 성능: 좋음
   - 비용: 중간

2. **text-embedding-3-small**
   - 차원: 1536
   - 성능: ada-002보다 향상
   - 비용: 비슷

3. **text-embedding-3-large**
   - 차원: 3072
   - 성능: 최고
   - 비용: 높음

### 7.3 모델별 설정 예제

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      embedding:
        options:
          model: text-embedding-3-small
          # 또는 text-embedding-3-large (더 높은 차원)
```

---

## 8. 베스트 프랙티스

### 8.1 텍스트 길이 관리

#### ✅ 좋은 예: 적절한 길이

```kotlin
// 일반적으로 500-1000자 단위로 분할
val chunks = splitIntoChunks(text, chunkSize = 500)
val embeddings = embeddingClient.embed(chunks)
```

#### ❌ 나쁜 예: 너무 긴 텍스트

```kotlin
// 너무 긴 텍스트는 비용 증가 및 정확도 저하
val embedding = embeddingClient.embed(veryLongText) // 10,000자 이상
```

### 8.2 배치 처리 활용

```kotlin
// ✅ 좋은 예: 여러 텍스트를 한 번에 처리
val texts = listOf("텍스트1", "텍스트2", "텍스트3")
val embeddings = embeddingClient.embed(texts)

// ❌ 나쁜 예: 하나씩 처리 (비효율적)
val embeddings = texts.map { embeddingClient.embed(it) }
```

### 8.3 캐싱 전략

```kotlin
@Service
class CachedEmbeddingService(
    private val embeddingClient: EmbeddingClient
) {
    private val cache = mutableMapOf<String, List<Double>>()
    
    fun getEmbedding(text: String): List<Double> {
        return cache.getOrPut(text) {
            embeddingClient.embed(text)
        }
    }
}
```

### 8.4 에러 처리

```kotlin
@RestController
class SafeEmbeddingController(
    private val embeddingClient: EmbeddingClient
) {
    @PostMapping("/safe-embed")
    fun safeEmbed(@RequestBody request: EmbedRequest): Map<String, Any> {
        return try {
            val embedding = embeddingClient.embed(request.text)
            mapOf(
                "success" to true,
                "dimension" to embedding.size,
                "embedding" to embedding
            )
        } catch (e: Exception) {
            mapOf(
                "success" to false,
                "error" to (e.message ?: "알 수 없는 오류")
            )
        }
    }
}
```

---

## 9. 주의사항 및 트러블슈팅

### 9.1 일반적인 문제들

#### 문제 1: API Key 오류

**증상:**
```
No qualifying bean of type 'EmbeddingClient' available
```

**원인**: 
- EmbeddingClient Bean이 생성되지 않음
- API Key 설정 오류

**해결책:**
```yaml
# application.yml 확인
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}  # 환경 변수 확인
```

#### 문제 2: 차원 불일치

**증상:**
```
벡터 차원이 예상과 다름
```

**원인**: 모델 변경 또는 버전 차이

**해결책:**
```kotlin
// 차원 확인
val embedding = embeddingClient.embed("테스트")
println("Dimension: ${embedding.size}")
```

#### 문제 3: 비용 증가

**증상:**
```
API 사용량이 예상보다 많음
```

**해결책:**
- 캐싱 적용
- 배치 처리 활용
- 문서 분할 최적화

### 9.2 성능 최적화

#### 배치 처리 활용

```kotlin
// ✅ 효율적: 배치 처리
val texts = listOf("텍스트1", "텍스트2", "텍스트3")
val embeddings = embeddingClient.embed(texts)

// ❌ 비효율적: 개별 처리
val embeddings = texts.map { embeddingClient.embed(it) }
```

#### 비동기 처리

```kotlin
@Service
class AsyncEmbeddingService(
    private val embeddingClient: EmbeddingClient
) {
    @Async
    suspend fun embedAsync(text: String): List<Double> {
        return embeddingClient.embed(text)
    }
}
```

---

## 10. 요약

### 10.1 핵심 내용 정리

1. **임베딩**: 텍스트를 벡터(숫자 배열)로 변환
2. **EmbeddingClient**: Spring AI의 임베딩 생성 인터페이스
3. **벡터**: 텍스트의 의미를 숫자로 표현 (일반적으로 1536차원)
4. **시맨틱 검색**: 의미 기반 검색 (키워드가 아닌 의미로 검색)
5. **배치 처리**: 여러 텍스트를 한 번에 임베딩 생성

### 10.2 기본 패턴

```kotlin
// 1. EmbeddingClient 주입
@RestController
class EmbeddingController(
    private val embeddingClient: EmbeddingClient
) {
    // 2. 단일 텍스트 임베딩
    @PostMapping("/embed")
    fun embed(@RequestBody request: EmbedRequest): Map<String, Any> {
        val embedding = embeddingClient.embed(request.text)
        
        return mapOf(
            "text" to request.text,
            "dimension" to embedding.size,
            "embedding" to embedding
        )
    }
    
    // 3. 배치 임베딩
    @PostMapping("/embed-batch")
    fun embedBatch(@RequestBody request: BatchEmbedRequest): Map<String, Any> {
        val embeddings = embeddingClient.embed(request.texts)
        
        return mapOf(
            "count" to embeddings.size,
            "dimension" to embeddings.firstOrNull()?.size ?: 0,
            "embeddings" to embeddings
        )
    }
}
```

### 10.3 다음 학습 내용

이제 EmbeddingClient 기본 사용법을 배웠으니, 다음 장에서는:
- **텍스트 유사도 계산**: 코사인 유사도 등
- **벡터 저장소**: 대량의 벡터 저장 및 검색
- **RAG 구현**: 임베딩을 활용한 검색 기반 생성

---

## 📚 참고 자료

- [Spring AI Embedding 공식 문서](https://docs.spring.io/spring-ai/reference/api/embedding.html)
- [OpenAI Embeddings](https://platform.openai.com/docs/guides/embeddings)
- [Vector Embeddings 이해하기](https://platform.openai.com/docs/guides/embeddings/what-are-embeddings)

---

## ❓ 학습 확인 문제

다음 질문에 답할 수 있는지 확인해보세요:

1. 임베딩이란 무엇이며, 왜 필요한가요?
2. EmbeddingClient를 사용하는 방법은?
3. 벡터의 차원이 무엇을 의미하나요?
4. 시맨틱 검색이 전통적 검색과 어떻게 다른가요?
5. 배치 처리를 사용하는 이유는?

---

**다음 장**: [5.2: 텍스트 유사도 계산](../README.md#52-텍스트-유사도-계산)

