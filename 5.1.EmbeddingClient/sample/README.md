# EmbeddingClient 샘플 프로젝트

이 프로젝트는 Spring AI에서 EmbeddingModel을 사용하여 텍스트를 벡터로 변환하는 방법을 보여줍니다.

## 📁 프로젝트 구조

```
sample/
├── src/main/kotlin/com/example/springai/
│   ├── EmbeddingClientApplication.kt        # 메인 애플리케이션
│   ├── controller/
│   │   ├── BasicEmbeddingController.kt     # 기본 임베딩 예제
│   │   ├── DocumentEmbeddingController.kt  # 문서 분할 및 임베딩
│   │   ├── SafeEmbeddingController.kt      # 안전한 임베딩 (에러 처리)
│   │   └── SemanticSearchController.kt      # 시맨틱 검색 예제
│   ├── service/
│   │   └── SimpleSemanticSearchService.kt   # 시맨틱 검색 서비스
│   └── model/
│       └── CommonModels.kt                 # 공통 모델
└── src/main/resources/
    └── application.yml                      # 설정 파일
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

#### 기본 임베딩 예제

```bash
# 단일 텍스트 임베딩
curl -X POST http://localhost:8080/api/embedding/embed \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Spring AI는 Spring 프레임워크를 위한 AI 통합 라이브러리입니다."
  }'

# 배치 임베딩 생성
curl -X POST http://localhost:8080/api/embedding/embed-batch \
  -H "Content-Type: application/json" \
  -d '{
    "texts": ["텍스트1", "텍스트2", "텍스트3"]
  }'

# 임베딩 정보 확인
curl http://localhost:8080/api/embedding/info
```

#### 문서 분할 및 임베딩

```bash
curl -X POST http://localhost:8080/api/document-embedding/embed-document \
  -H "Content-Type: application/json" \
  -d '{
    "text": "긴 문서 텍스트...",
    "chunkSize": 500
  }'
```

#### 시맨틱 검색 예제

```bash
# 문서 추가
curl -X POST http://localhost:8080/api/semantic-search/add \
  -H "Content-Type: application/json" \
  -d '{
    "id": "doc1",
    "text": "Spring AI는 Spring 프레임워크를 위한 AI 통합 라이브러리입니다."
  }'

# 검색
curl -X POST http://localhost:8080/api/semantic-search/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "프로그래밍",
    "topK": 5
  }'
```

## 📝 주요 예제 설명

### 1. BasicEmbeddingController

**기본 임베딩:**
- `/api/embedding/embed`: 단일 텍스트 임베딩 생성
- `/api/embedding/embed-batch`: 여러 텍스트 배치 임베딩
- `/api/embedding/info`: 임베딩 정보 확인 (차원, 통계 등)

### 2. DocumentEmbeddingController

**문서 분할 및 임베딩:**
- `/api/document-embedding/embed-document`: 긴 문서를 청크로 분할하여 임베딩

### 3. SimpleSemanticSearchService

**시맨틱 검색 서비스:**
- 문서 추가, 검색, 삭제 기능
- 코사인 유사도를 이용한 의미 기반 검색

### 4. SafeEmbeddingController

**안전한 임베딩:**
- `/api/safe-embedding/embed`: 에러 처리 포함

## 💡 학습 포인트

이 샘플 프로젝트를 통해 학습할 수 있는 내용:

1. **EmbeddingModel 이해**
   - 텍스트를 벡터로 변환
   - FloatArray 타입의 임베딩 벡터

2. **배치 처리**
   - 여러 텍스트를 한 번에 임베딩
   - 효율적인 처리

3. **문서 분할**
   - 긴 문서를 청크로 분할
   - 각 청크를 개별적으로 임베딩

4. **시맨틱 검색**
   - 코사인 유사도 계산
   - 의미 기반 검색 구현

## 🔧 핵심 패턴

```kotlin
// 1. EmbeddingModel 주입
@RestController
class EmbeddingController(
    private val embeddingModel: EmbeddingModel
) {
    // 2. 단일 텍스트 임베딩
    @PostMapping("/embed")
    fun embed(@RequestBody request: EmbedRequest): Map<String, Any> {
        val embedding = embeddingModel.embed(request.text)
        // embedding: FloatArray
        
        return mapOf(
            "dimension" to embedding.size,
            "embedding" to embedding.map { it.toDouble() }
        )
    }
    
    // 3. 배치 임베딩
    @PostMapping("/embed-batch")
    fun embedBatch(@RequestBody request: BatchEmbedRequest): Map<String, Any> {
        val embeddings = embeddingModel.embed(request.texts)
        // embeddings: List<FloatArray>
        
        return mapOf(
            "count" to embeddings.size
        )
    }
}
```

## 📚 참고사항

### EmbeddingModel vs EmbeddingClient

Spring AI 1.0.0-M6에서는 `EmbeddingModel` 인터페이스를 사용합니다.
- `embed(text: String)`: FloatArray 반환
- `embed(texts: List<String>)`: List<FloatArray> 반환

### FloatArray 처리

임베딩 벡터는 `FloatArray` 타입으로 반환됩니다.
JSON 응답을 위해 필요시 `List<Double>`로 변환:
```kotlin
embedding.map { it.toDouble() }
```

### 코사인 유사도 계산

```kotlin
private fun cosineSimilarity(a: FloatArray, b: FloatArray): Double {
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

---

**다음 학습**: [5.2: 텍스트 유사도 계산](../../README.md#52-텍스트-유사도-계산)

