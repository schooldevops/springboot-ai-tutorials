# 13장: [실전] 시맨틱 문서 검색 API

## 📚 학습 목표

5, 6장의 **EmbeddingClient**와 **VectorStore**를 활용하여, 여러 문서를 임베딩하여 저장하고 키워드 검색이 아닌 **'의미 기반'** 검색 API를 구현합니다.

## 🔑 핵심 키워드

- `EmbeddingClient`
- `VectorStore`
- `similaritySearch`
- 시맨틱 검색
- 벡터 임베딩
- 코사인 유사도

## 📖 개요

이 장에서는 Spring AI의 EmbeddingClient와 VectorStore를 사용하여 의미 기반 문서 검색 시스템을 구축합니다. 전통적인 키워드 매칭 방식이 아닌, 문서의 의미를 벡터로 변환하여 유사한 의미를 가진 문서를 찾는 시맨틱 검색을 구현합니다.

## 🎯 키워드 검색 vs 시맨틱 검색

### 키워드 검색 (전통적 방식)
```
질문: "자바 프레임워크"
결과: "자바", "프레임워크" 단어가 포함된 문서만 검색
한계: 동의어, 유사 개념 검색 불가
```

### 시맨틱 검색 (의미 기반)
```
질문: "자바 프레임워크"
결과: 
- "Spring Boot는 Java 기반 애플리케이션 개발 도구입니다"
- "Kotlin과 함께 사용하는 백엔드 프레임워크"
- "JVM 생태계의 웹 개발 솔루션"
장점: 의미가 유사한 모든 문서 검색 가능
```

## 🏗️ 프로젝트 구조

```
13.SymenticSearch/
├── README.md                          # 이 문서
├── QUICKSTART.md                      # 빠른 시작 가이드
└── sample/                            # 샘플 프로젝트
    ├── build.gradle.kts               # Gradle 빌드 설정
    ├── settings.gradle.kts            # Gradle 설정
    ├── test-requests.http             # HTTP 테스트 요청
    ├── sample-documents.md            # 샘플 문서 데이터
    └── src/
        └── main/
            ├── kotlin/com/example/semanticsearch/
            │   ├── SemanticSearchApplication.kt  # 메인 애플리케이션
            │   ├── config/
            │   │   └── VectorStoreConfig.kt      # VectorStore 설정
            │   ├── controller/
            │   │   └── DocumentSearchController.kt # REST 컨트롤러
            │   └── dto/
            │       ├── DocumentRequest.kt        # 요청 DTO
            │       └── SearchResponse.kt         # 응답 DTO
            └── resources/
                └── application.yml                # 설정 파일
```

## 💻 구현 상세

### 1. VectorStore 설정 (VectorStoreConfig.kt)

```kotlin
@Configuration
class VectorStoreConfig {
    
    @Bean
    fun vectorStore(embeddingClient: EmbeddingClient): VectorStore {
        // SimpleVectorStore: 인메모리 벡터 저장소
        return SimpleVectorStore(embeddingClient)
    }
}
```

### 2. 문서 DTO

```kotlin
// 문서 추가 요청
data class AddDocumentRequest(
    val content: String,
    val metadata: Map<String, Any> = emptyMap()
)

// 다중 문서 추가 요청
data class AddDocumentsRequest(
    val documents: List<DocumentItem>
)

data class DocumentItem(
    val content: String,
    val metadata: Map<String, Any> = emptyMap()
)

// 검색 요청
data class SearchRequest(
    val query: String,
    val topK: Int = 5
)

// 검색 결과
data class SearchResult(
    val rank: Int,
    val content: String,
    val metadata: Map<String, Any>,
    val score: Double? = null
)

data class SearchResponse(
    val query: String,
    val resultCount: Int,
    val results: List<SearchResult>
)
```

### 3. REST Controller

```kotlin
@RestController
@RequestMapping("/api/search")
class DocumentSearchController(
    private val vectorStore: VectorStore,
    private val embeddingClient: EmbeddingClient
) {
    
    // 단일 문서 추가
    @PostMapping("/documents")
    fun addDocument(@RequestBody request: AddDocumentRequest): Map<String, Any> {
        val document = Document(request.content, request.metadata)
        vectorStore.add(listOf(document))
        
        return mapOf(
            "status" to "success",
            "message" to "문서가 추가되었습니다",
            "documentId" to document.id
        )
    }
    
    // 다중 문서 추가
    @PostMapping("/documents/batch")
    fun addDocuments(@RequestBody request: AddDocumentsRequest): Map<String, Any> {
        val documents = request.documents.map { item ->
            Document(item.content, item.metadata)
        }
        
        vectorStore.add(documents)
        
        return mapOf(
            "status" to "success",
            "message" to "${documents.size}개 문서가 추가되었습니다",
            "count" to documents.size
        )
    }
    
    // 시맨틱 검색
    @PostMapping("/query")
    fun search(@RequestBody request: SearchRequest): SearchResponse {
        val results = vectorStore.similaritySearch(
            SearchRequest.query(request.query)
                .withTopK(request.topK)
        )
        
        val searchResults = results.mapIndexed { index, doc ->
            SearchResult(
                rank = index + 1,
                content = doc.content,
                metadata = doc.metadata
            )
        }
        
        return SearchResponse(
            query = request.query,
            resultCount = searchResults.size,
            results = searchResults
        )
    }
    
    // GET 방식 검색
    @GetMapping("/query")
    fun searchGet(
        @RequestParam query: String,
        @RequestParam(defaultValue = "5") topK: Int
    ): SearchResponse {
        return search(SearchRequest(query, topK))
    }
}
```

## 🧪 테스트 방법

### 1. 애플리케이션 실행

```bash
cd sample

# OpenAI API 키 설정
export OPENAI_API_KEY=your-api-key-here

# 애플리케이션 실행
./gradlew bootRun
```

### 2. 문서 추가

#### 단일 문서 추가
```bash
curl -X POST http://localhost:9000/api/search/documents \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Spring Boot는 Java 기반의 웹 애플리케이션 프레임워크입니다.",
    "metadata": {
      "category": "framework",
      "language": "java"
    }
  }'
```

#### 다중 문서 일괄 추가
```bash
curl -X POST http://localhost:9000/api/search/documents/batch \
  -H "Content-Type: application/json" \
  -d '{
    "documents": [
      {
        "content": "Kotlin은 JVM에서 실행되는 현대적인 프로그래밍 언어입니다.",
        "metadata": {"category": "language"}
      },
      {
        "content": "PostgreSQL은 오픈소스 관계형 데이터베이스입니다.",
        "metadata": {"category": "database"}
      },
      {
        "content": "Docker는 컨테이너 기반 가상화 플랫폼입니다.",
        "metadata": {"category": "devops"}
      }
    ]
  }'
```

### 3. 시맨틱 검색

```bash
# POST 방식
curl -X POST http://localhost:9000/api/search/query \
  -H "Content-Type: application/json" \
  -d '{
    "query": "백엔드 개발 도구",
    "topK": 3
  }'

# GET 방식
curl "http://localhost:9000/api/search/query?query=데이터베이스&topK=3"
```

**예상 응답:**
```json
{
  "query": "백엔드 개발 도구",
  "resultCount": 3,
  "results": [
    {
      "rank": 1,
      "content": "Spring Boot는 Java 기반의 웹 애플리케이션 프레임워크입니다.",
      "metadata": {
        "category": "framework",
        "language": "java"
      }
    },
    {
      "rank": 2,
      "content": "Kotlin은 JVM에서 실행되는 현대적인 프로그래밍 언어입니다.",
      "metadata": {
        "category": "language"
      }
    },
    {
      "rank": 3,
      "content": "PostgreSQL은 오픈소스 관계형 데이터베이스입니다.",
      "metadata": {
        "category": "database"
      }
    }
  ]
}
```

## 📝 주요 개념 설명

### EmbeddingClient

텍스트를 고차원 벡터로 변환하는 클라이언트입니다.

```kotlin
val embedding = embeddingClient.embed("Spring Boot는 프레임워크입니다")
// 결과: [0.123, -0.456, 0.789, ...] (1536차원 벡터)
```

**특징:**
- 의미가 유사한 텍스트는 유사한 벡터를 생성
- OpenAI의 text-embedding-ada-002 모델 사용
- 1536차원 벡터 생성

### VectorStore

벡터화된 문서를 저장하고 검색하는 저장소입니다.

**SimpleVectorStore:**
- 인메모리 벡터 저장소
- 개발 및 테스트용
- 애플리케이션 재시작 시 데이터 소실

**프로덕션 환경:**
- PGVector (PostgreSQL)
- Pinecone
- Weaviate
- Milvus

### Similarity Search

코사인 유사도를 사용하여 가장 유사한 문서를 찾습니다.

```kotlin
val results = vectorStore.similaritySearch(
    SearchRequest.query("프로그래밍 언어")
        .withTopK(5)
        .withSimilarityThreshold(0.7)
)
```

**파라미터:**
- `query`: 검색 질의
- `topK`: 반환할 최대 결과 수
- `similarityThreshold`: 최소 유사도 임계값 (0.0 ~ 1.0)

## 🎓 학습 포인트

1. **임베딩의 이해**: 텍스트를 벡터로 변환하는 원리
2. **VectorStore 활용**: 벡터 저장 및 검색
3. **시맨틱 검색**: 의미 기반 검색의 장점
4. **메타데이터 활용**: 문서 분류 및 필터링
5. **유사도 계산**: 코사인 유사도의 이해

## 💡 실전 활용 사례

### 1. 문서 검색 시스템
- 사내 위키 검색
- 기술 문서 검색
- FAQ 자동 매칭

### 2. 추천 시스템
- 유사 상품 추천
- 관련 기사 추천
- 콘텐츠 추천

### 3. 고객 지원
- 유사 문의 검색
- 자동 답변 매칭
- 티켓 분류

## 🚀 다음 단계

- **14장**: RAG 패턴을 적용한 사내 위키 챗봇
- **15장**: RAG 챗봇 고도화 (데이터 파이프라인)

## 📚 참고 자료

- [Spring AI 공식 문서](https://docs.spring.io/spring-ai/reference/)
- [Embedding API](https://docs.spring.io/spring-ai/reference/api/embedding.html)
- [VectorStore API](https://docs.spring.io/spring-ai/reference/api/vectordb.html)

## 💡 팁

> [!TIP]
> **임베딩 비용 최적화**: 동일한 텍스트는 한 번만 임베딩하고 결과를 캐싱하세요.

> [!TIP]
> **메타데이터 활용**: 문서에 카테고리, 날짜, 작성자 등의 메타데이터를 추가하면 검색 결과를 필터링할 수 있습니다.

> [!TIP]
> **적절한 topK 설정**: 너무 많은 결과는 정확도를 떨어뜨립니다. 보통 3-10개가 적당합니다.

> [!WARNING]
> **SimpleVectorStore 한계**: 프로덕션 환경에서는 PGVector 등 영구 저장소를 사용하세요.

## 🔧 고급 기능

### 1. 메타데이터 필터링

```kotlin
val results = vectorStore.similaritySearch(
    SearchRequest.query("프로그래밍")
        .withTopK(5)
        .withFilterExpression("category == 'language'")
)
```

### 2. 유사도 임계값 설정

```kotlin
val results = vectorStore.similaritySearch(
    SearchRequest.query("데이터베이스")
        .withTopK(10)
        .withSimilarityThreshold(0.75) // 75% 이상 유사한 것만
)
```

### 3. 문서 삭제

```kotlin
@DeleteMapping("/documents/{id}")
fun deleteDocument(@PathVariable id: String): Map<String, Any> {
    vectorStore.delete(listOf(id))
    return mapOf("status" to "success", "deletedId" to id)
}
```

### 4. 벡터 저장소 초기화

```kotlin
@DeleteMapping("/documents")
fun clearAll(): Map<String, Any> {
    // SimpleVectorStore의 경우 재생성
    return mapOf("status" to "success", "message" to "모든 문서가 삭제되었습니다")
}
```

## 📊 성능 고려사항

### 임베딩 생성 시간
- 텍스트 길이에 비례
- API 호출 시간 포함
- 배치 처리로 최적화 가능

### 검색 속도
- SimpleVectorStore: O(n) - 모든 벡터와 비교
- PGVector: O(log n) - 인덱스 활용
- 대규모 데이터는 전용 벡터 DB 권장

### 메모리 사용량
- 문서당 ~6KB (1536차원 float 벡터)
- 10,000개 문서 ≈ 60MB
- 프로덕션: 외부 저장소 필수
