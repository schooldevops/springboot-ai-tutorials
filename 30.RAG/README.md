# 30. RAG (Retrieval-Augmented Generation)

## 📖 학습 목표

- **RAG의 개념**과 필요성을 완벽히 이해합니다
- **ETL Pipeline**으로 문서를 처리하고 벡터화합니다
- **Vector Store**로 임베딩을 저장하고 검색합니다
- **QuestionAnswerAdvisor**로 RAG 시스템을 구축합니다
- **실전 예제**로 완전한 RAG 애플리케이션을 만듭니다

---

## 🔑 핵심 키워드

1. **RAG** - Retrieval-Augmented Generation
2. **ETL Pipeline** - Extract, Transform, Load
3. **Vector Store** - 벡터 데이터베이스
4. **Embedding** - 텍스트 벡터화
5. **QuestionAnswerAdvisor** - RAG 통합

---

## 1. RAG란?

**RAG (Retrieval-Augmented Generation)**는 외부 지식을 검색하여 AI 응답의 정확성을 높이는 기술입니다.

### 왜 RAG가 필요한가?

**RAG 없이 (일반 LLM)**
```
User: "2024년 우리 회사 매출은?"
AI: "죄송하지만 그 정보는 모릅니다" ❌
```

**RAG 사용**
```
User: "2024년 우리 회사 매출은?"
AI: [회사 문서 검색] → "2024년 매출은 100억원입니다" ✅
```

### RAG의 장점
- ✅ **최신 정보**: 학습 데이터 이후의 정보도 활용
- ✅ **정확성**: 실제 문서 기반 답변
- ✅ **투명성**: 출처 추적 가능
- ✅ **비용 효율**: 모델 재학습 불필요

---

## 2. RAG 아키텍처

```
┌─────────────────────────────────────────────────────┐
│                  RAG 시스템 흐름                      │
└─────────────────────────────────────────────────────┘

1️⃣ ETL Pipeline (문서 처리)
   ┌──────────┐    ┌───────────┐    ┌──────────┐
   │ Extract  │ →  │ Transform │ →  │   Load   │
   │ (문서읽기)│    │ (벡터화)   │    │(저장)    │
   └──────────┘    └───────────┘    └──────────┘
        ↓               ↓                 ↓
   PDF, TXT       Chunking          Vector Store
   DOCX, HTML     Embedding         (ChromaDB, etc)

2️⃣ Query Time (질문 응답)
   ┌──────────┐    ┌───────────┐    ┌──────────┐
   │  Query   │ →  │ Retrieve  │ →  │ Generate │
   │ (질문)    │    │ (검색)     │    │ (생성)   │
   └──────────┘    └───────────┘    └──────────┘
        ↓               ↓                 ↓
   사용자 질문      관련 문서 검색      AI 답변 생성
```

---

## 3. ETL Pipeline 상세

### 3.1 Extract (추출)

문서를 읽어들이는 단계입니다.

```kotlin
// PDF 문서 읽기
val pdfReader = PagePdfDocumentReader("document.pdf")
val documents = pdfReader.get()

// 텍스트 파일 읽기
val textReader = TextReader(Resource("data.txt"))
val documents = textReader.get()

// 웹 페이지 읽기
val webReader = WebDocumentReader("https://example.com")
val documents = webReader.get()
```

**지원 형식:**
- PDF, DOCX, TXT
- HTML, Markdown
- JSON, CSV
- 웹 페이지

---

### 3.2 Transform (변환)

문서를 작은 청크로 나누고 벡터화합니다.

```kotlin
// 1. 문서 분할 (Chunking)
val splitter = TokenTextSplitter(
    defaultChunkSize = 800,
    minChunkSizeChars = 350,
    minChunkLengthToEmbed = 5,
    maxNumChunks = 10000
)
val chunks = splitter.split(documents)

// 2. 임베딩 생성
val embeddingModel = OpenAiEmbeddingModel(apiKey)
val embeddings = embeddingModel.embed(chunks)
```

**Chunking 전략:**
- **Token-based**: 토큰 수 기준 분할
- **Sentence-based**: 문장 단위 분할
- **Paragraph-based**: 단락 단위 분할
- **Semantic**: 의미 단위 분할

---

### 3.3 Load (적재)

벡터를 Vector Store에 저장합니다.

```kotlin
// Vector Store에 저장
val vectorStore = SimpleVectorStore(embeddingModel)
vectorStore.add(documents)

// 또는 영구 저장소 사용
val chromaStore = ChromaVectorStore(embeddingModel)
chromaStore.add(documents)
```

**Vector Store 옵션:**
- **In-Memory**: SimpleVectorStore
- **ChromaDB**: 오픈소스 벡터 DB
- **Pinecone**: 클라우드 벡터 DB
- **Weaviate**: 엔터프라이즈 벡터 DB
- **Redis**: Redis Stack

---

## 4. RAG Advisors

### 4.1 QuestionAnswerAdvisor

가장 간단한 RAG 구현입니다.

```kotlin
val vectorStore = SimpleVectorStore(embeddingModel)
vectorStore.add(documents)

val chatClient = ChatClient.builder(chatModel)
    .defaultAdvisors(
        QuestionAnswerAdvisor(vectorStore)
    )
    .build()

// 사용
val response = chatClient.prompt()
    .user("2024년 매출은?")
    .call()
    .content()
```

**동작 방식:**
1. 질문을 임베딩으로 변환
2. Vector Store에서 유사 문서 검색
3. 검색된 문서를 컨텍스트로 추가
4. AI가 컨텍스트 기반 답변 생성

---

### 4.2 RetrievalAugmentationAdvisor

더 세밀한 제어가 가능한 RAG입니다.

```kotlin
val advisor = RetrievalAugmentationAdvisor.builder()
    .documentRetriever(vectorStore)
    .queryTransformer { query -> 
        // 질문 변환 로직
        "관련 정보: $query"
    }
    .documentFilter { docs ->
        // 문서 필터링
        docs.filter { it.score > 0.7 }
    }
    .build()

val chatClient = ChatClient.builder(chatModel)
    .defaultAdvisors(advisor)
    .build()
```

**커스터마이징:**
- Query Transformation: 질문 재작성
- Document Filtering: 관련성 필터링
- Re-ranking: 문서 재정렬
- Context Compression: 컨텍스트 압축

---

## 5. 샘플 구성

| Sample | Port | 주제 | 핵심 내용 |
|--------|------|------|-----------|
| **01** | 10000 | Basic RAG | QuestionAnswerAdvisor 기본 |
| **02** | 10001 | ETL Pipeline | 문서 처리 파이프라인 |
| **03** | 10002 | Vector Store | 다양한 Vector Store |
| **04** | 10003 | Complete RAG | 통합 RAG 시스템 |

---

## 6. 실전 예제

### 6.1 회사 문서 QA 시스템

```kotlin
@Service
class CompanyDocumentService(
    private val embeddingModel: EmbeddingModel,
    private val chatModel: ChatModel
) {
    private val vectorStore = SimpleVectorStore(embeddingModel)
    
    // 문서 로드
    fun loadDocuments() {
        // 1. PDF 읽기
        val pdfReader = PagePdfDocumentReader("company-report.pdf")
        val documents = pdfReader.get()
        
        // 2. 청크 분할
        val splitter = TokenTextSplitter()
        val chunks = splitter.split(documents)
        
        // 3. Vector Store에 저장
        vectorStore.add(chunks)
    }
    
    // 질문 응답
    fun ask(question: String): String {
        val chatClient = ChatClient.builder(chatModel)
            .defaultAdvisors(QuestionAnswerAdvisor(vectorStore))
            .build()
        
        return chatClient.prompt()
            .user(question)
            .call()
            .content()
    }
}
```

**사용 예:**
```kotlin
service.loadDocuments()
val answer = service.ask("2024년 매출은?")
// "2024년 매출은 100억원입니다"
```

---

### 6.2 기술 문서 검색

```kotlin
@Service
class TechDocService(
    private val embeddingModel: EmbeddingModel,
    private val chatModel: ChatModel
) {
    private val vectorStore = ChromaVectorStore(embeddingModel)
    
    fun indexDocumentation(urls: List<String>) {
        urls.forEach { url ->
            val reader = WebDocumentReader(url)
            val documents = reader.get()
            vectorStore.add(documents)
        }
    }
    
    fun search(query: String): List<Document> {
        return vectorStore.similaritySearch(
            SearchRequest.query(query).withTopK(5)
        )
    }
}
```

---

## 7. RAG 모듈

### 7.1 Pre-Retrieval (검색 전)

질문을 개선하여 검색 품질을 높입니다.

```kotlin
// Query Expansion (질문 확장)
val expandedQuery = queryExpander.expand("RAG란?")
// "RAG란? Retrieval Augmented Generation이란?"

// Query Rewriting (질문 재작성)
val rewrittenQuery = queryRewriter.rewrite("작년 매출")
// "2024년 회사 매출액"
```

---

### 7.2 Retrieval (검색)

관련 문서를 검색합니다.

```kotlin
// 유사도 검색
val results = vectorStore.similaritySearch(
    SearchRequest.query("RAG")
        .withTopK(5)
        .withSimilarityThreshold(0.7)
)

// 하이브리드 검색 (벡터 + 키워드)
val hybridResults = vectorStore.hybridSearch(
    query = "RAG",
    keywords = listOf("retrieval", "generation")
)
```

---

### 7.3 Post-Retrieval (검색 후)

검색 결과를 개선합니다.

```kotlin
// Re-ranking (재정렬)
val reranked = reranker.rerank(results, query)

// Filtering (필터링)
val filtered = results.filter { it.score > 0.8 }

// Compression (압축)
val compressed = compressor.compress(results)
```

---

### 7.4 Generation (생성)

최종 답변을 생성합니다.

```kotlin
val chatClient = ChatClient.builder(chatModel)
    .defaultAdvisors(
        QuestionAnswerAdvisor(vectorStore)
    )
    .build()

val response = chatClient.prompt()
    .user("질문")
    .call()
    .content()
```

---

## 8. 비교표

### Vector Store 비교

| Store | 타입 | 특징 | 사용 시나리오 |
|-------|------|------|---------------|
| SimpleVectorStore | In-Memory | 빠름, 휘발성 | 개발/테스트 |
| ChromaDB | 영구 저장 | 오픈소스, 로컬 | 소규모 운영 |
| Pinecone | 클라우드 | 관리형, 확장성 | 대규모 운영 |
| Weaviate | 엔터프라이즈 | 고급 기능 | 엔터프라이즈 |

### Chunking 전략 비교

| 전략 | 장점 | 단점 | 사용 시나리오 |
|------|------|------|---------------|
| Token-based | 정확한 크기 | 문맥 분리 | 일반적 |
| Sentence-based | 문맥 유지 | 크기 불균일 | 대화형 |
| Semantic | 의미 보존 | 복잡함 | 고급 |

---

## 9. 실행 방법

### Sample 01: Basic RAG
```bash
cd sample01-basic-rag
./gradlew bootRun

# 테스트
curl -X POST http://localhost:10000/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "RAG란 무엇인가요?"}'
```

### Sample 02: ETL Pipeline
```bash
cd sample02-etl-pipeline
./gradlew bootRun
```

### Sample 03: Vector Store
```bash
cd sample03-vector-store
./gradlew bootRun
```

### Sample 04: Complete RAG
```bash
cd sample04-complete-rag
./gradlew bootRun
```

---

## 10. 모범 사례

### ✅ DO

```kotlin
// 적절한 청크 크기
val splitter = TokenTextSplitter(
    defaultChunkSize = 800,  // 너무 크지도 작지도 않게
    minChunkSizeChars = 350
)

// 메타데이터 추가
val document = Document(
    content = text,
    metadata = mapOf(
        "source" to "report.pdf",
        "page" to 1,
        "date" to "2024-01-01"
    )
)

// 유사도 임계값 설정
val results = vectorStore.similaritySearch(
    SearchRequest.query(query)
        .withSimilarityThreshold(0.7)  // 관련성 높은 것만
)
```

### ❌ DON'T

```kotlin
// 너무 큰 청크
val splitter = TokenTextSplitter(
    defaultChunkSize = 5000  // ❌ 너무 큼
)

// 메타데이터 없음
val document = Document(text)  // ❌ 출처 추적 불가

// 임계값 없음
val results = vectorStore.similaritySearch(
    SearchRequest.query(query)  // ❌ 관련 없는 문서도 포함
)
```

---

## 11. 문제 해결

### Q: 검색 결과가 관련 없어요
```kotlin
// 유사도 임계값 조정
SearchRequest.query(query)
    .withSimilarityThreshold(0.8)  // 높이기
```

### Q: 답변이 부정확해요
```kotlin
// 더 많은 문서 검색
SearchRequest.query(query)
    .withTopK(10)  // 5 → 10
```

### Q: 성능이 느려요
```kotlin
// 청크 크기 조정
TokenTextSplitter(
    defaultChunkSize = 500  // 줄이기
)
```

---

## 12. 다음 단계

1. ✅ **Sample 01** - Basic RAG 기본
2. ✅ **Sample 02** - ETL Pipeline 구축
3. ✅ **Sample 03** - Vector Store 통합
4. ✅ **Sample 04** - Complete RAG 시스템

---

**시작하기**: [Sample 01: Basic RAG](./sample01-basic-rag/)

**관련 문서**:
- [Spring AI RAG Reference](https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html)
- [Vector Stores](https://docs.spring.io/spring-ai/reference/api/vectordbs.html)
- [Document Readers](https://docs.spring.io/spring-ai/reference/api/documentreaders.html)
- [ETL Pipeline](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html)
