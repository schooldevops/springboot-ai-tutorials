# 14장: [실전] 사내 위키 기반 RAG 챗봇 (기초)

## 📚 학습 목표

7, 8장의 **RAG** 개념을 적용하여, 로컬의 Markdown/PDF 문서를 기반으로 질문에 답변하는 챗봇 API를 구현합니다. **(데이터 로딩, 분할, 저장, 검색, 답변)**

## 🔑 핵심 키워드

- `RAG` (Retrieval-Augmented Generation)
- `DocumentLoader`
- `TextSplitter`
- `VectorStore`
- `ChatModel`

## 📖 개요

이 장에서는 Spring AI의 RAG 패턴을 사용하여 사내 위키 문서를 기반으로 질문에 답변하는 챗봇을 구축합니다. 단순히 LLM의 사전 학습 지식에만 의존하는 것이 아니라, 실제 문서를 검색하여 그 내용을 바탕으로 답변을 생성합니다.

## 🎯 RAG란?

**RAG (Retrieval-Augmented Generation)**는 외부 지식 베이스에서 관련 정보를 검색(Retrieve)한 후, 그 정보를 활용하여 답변을 생성(Generate)하는 기법입니다.

### 일반 LLM vs RAG

| 특징 | 일반 LLM | RAG |
|------|---------|-----|
| 지식 출처 | 사전 학습 데이터 | 실시간 문서 검색 |
| 최신 정보 | ❌ 학습 시점까지만 | ✅ 문서 업데이트 시 반영 |
| 사내 정보 | ❌ 모름 | ✅ 사내 문서 활용 |
| 환각(Hallucination) | 높음 | 낮음 (문서 기반) |
| 답변 근거 | 제공 불가 | 출처 문서 제공 |

## 🔄 RAG 워크플로우

```
1. LOAD (로딩)
   ↓
   문서 파일 읽기 (Markdown, PDF, TXT 등)
   
2. SPLIT (분할)
   ↓
   긴 문서를 작은 청크로 분할
   
3. STORE (저장)
   ↓
   청크를 임베딩하여 VectorStore에 저장
   
4. RETRIEVE (검색)
   ↓
   질문과 유사한 청크 검색
   
5. GENERATE (생성)
   ↓
   검색된 청크를 컨텍스트로 답변 생성
```

## 🏗️ 프로젝트 구조

```
14.WikiKMS/
├── README.md                          # 이 문서
├── QUICKSTART.md                      # 빠른 시작 가이드
└── sample/                            # 샘플 프로젝트
    ├── build.gradle.kts               # Gradle 빌드 설정
    ├── settings.gradle.kts            # Gradle 설정
    ├── test-requests.http             # HTTP 테스트 요청
    ├── wiki-documents/                # 샘플 위키 문서
    │   ├── company-policy.md
    │   ├── tech-stack.md
    │   ├── development-guide.md
    │   └── faq.md
    └── src/
        └── main/
            ├── kotlin/com/example/wikichatbot/
            │   ├── WikiChatbotApplication.kt  # 메인 애플리케이션
            │   ├── config/
            │   │   └── VectorStoreConfig.kt   # VectorStore 설정
            │   ├── service/
            │   │   ├── DocumentService.kt     # 문서 로딩/분할
            │   │   └── RAGService.kt          # RAG 로직
            │   ├── controller/
            │   │   └── WikiChatController.kt  # REST 컨트롤러
            │   └── dto/
            │       ├── ChatRequest.kt         # 요청 DTO
            │       └── ChatResponse.kt        # 응답 DTO
            └── resources/
                └── application.yml             # 설정 파일
```

## 💻 구현 상세

### 1. 문서 로딩 및 분할 (DocumentService)

```kotlin
@Service
class DocumentService {
    
    fun loadAndSplitMarkdownFile(filePath: String): List<Document> {
        // 1. 파일 읽기
        val content = File(filePath).readText()
        
        // 2. Document 생성
        val document = Document(
            content,
            mapOf(
                "source" to filePath,
                "type" to "markdown"
            )
        )
        
        // 3. 텍스트 분할
        val textSplitter = TokenTextSplitter(
            defaultChunkSize = 500,  // 청크 크기
            minChunkSizeChars = 100,
            minChunkLengthToEmbed = 50,
            maxNumChunks = 10000
        )
        
        return textSplitter.split(listOf(document))
    }
}
```

**주요 개념:**
- **TokenTextSplitter**: 토큰 기반 텍스트 분할
- **Chunk Size**: 각 청크의 크기 (토큰 수)
- **Overlap**: 청크 간 중복 (컨텍스트 유지)

### 2. RAG 서비스 (RAGService)

```kotlin
@Service
class RAGService(
    private val chatModel: ChatModel,
    private val vectorStore: VectorStore
) {
    
    fun askQuestion(question: String, topK: Int = 3): RAGResponse {
        // 1. 관련 문서 검색 (RETRIEVE)
        val searchRequest = SearchRequest.query(question)
            .withTopK(topK)
        
        val relevantDocs = vectorStore.similaritySearch(searchRequest)
        
        // 2. 컨텍스트 구성
        val context = relevantDocs.joinToString("\n\n") { doc ->
            "문서: ${doc.metadata["source"]}\n${doc.content}"
        }
        
        // 3. 프롬프트 생성
        val prompt = """
            다음 문서들을 참고하여 질문에 답변해주세요.
            
            [참고 문서]
            $context
            
            [질문]
            $question
            
            [답변 규칙]
            - 참고 문서의 내용만을 기반으로 답변하세요
            - 문서에 없는 내용은 "문서에서 해당 정보를 찾을 수 없습니다"라고 답변하세요
            - 답변 시 어느 문서를 참고했는지 명시하세요
        """.trimIndent()
        
        // 4. 답변 생성 (GENERATE)
        val response = chatModel.call(prompt)
        
        return RAGResponse(
            question = question,
            answer = response,
            sources = relevantDocs.map { it.metadata["source"] as String },
            context = context
        )
    }
}
```

### 3. REST Controller

```kotlin
@RestController
@RequestMapping("/api/wiki")
class WikiChatController(
    private val documentService: DocumentService,
    private val ragService: RAGService,
    private val vectorStore: VectorStore
) {
    
    // 문서 인제스트
    @PostMapping("/ingest")
    fun ingestDocuments(@RequestParam directory: String): Map<String, Any> {
        val files = File(directory).listFiles { file ->
            file.extension == "md"
        } ?: emptyArray()
        
        var totalChunks = 0
        files.forEach { file ->
            val chunks = documentService.loadAndSplitMarkdownFile(file.absolutePath)
            vectorStore.add(chunks)
            totalChunks += chunks.size
        }
        
        return mapOf(
            "status" to "success",
            "filesProcessed" to files.size,
            "totalChunks" to totalChunks
        )
    }
    
    // RAG 기반 Q&A
    @PostMapping("/ask")
    fun ask(@RequestBody request: ChatRequest): ChatResponse {
        val result = ragService.askQuestion(request.question, request.topK ?: 3)
        
        return ChatResponse(
            question = result.question,
            answer = result.answer,
            sources = result.sources,
            timestamp = System.currentTimeMillis()
        )
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

### 2. 문서 인제스트

```bash
curl -X POST "http://localhost:9000/api/wiki/ingest?directory=wiki-documents"
```

**응답:**
```json
{
  "status": "success",
  "filesProcessed": 4,
  "totalChunks": 23
}
```

### 3. 질문하기

```bash
curl -X POST http://localhost:9000/api/wiki/ask \
  -H "Content-Type: application/json" \
  -d '{
    "question": "회사의 재택근무 정책은 무엇인가요?",
    "topK": 3
  }'
```

**응답:**
```json
{
  "question": "회사의 재택근무 정책은 무엇인가요?",
  "answer": "company-policy.md 문서에 따르면, 직원들은 주 2회까지 재택근무를 할 수 있습니다. 사전에 팀장의 승인을 받아야 하며, 업무 시간 동안 온라인 상태를 유지해야 합니다.",
  "sources": [
    "wiki-documents/company-policy.md"
  ],
  "timestamp": 1763737000000
}
```

## 📝 주요 개념 설명

### DocumentLoader

문서 파일을 읽어서 Document 객체로 변환합니다.

**지원 형식:**
- Markdown (`.md`)
- PDF (`.pdf`)
- Text (`.txt`)
- JSON (`.json`)

### TextSplitter

긴 문서를 작은 청크로 분할합니다.

**종류:**
- **TokenTextSplitter**: 토큰 기반 분할
- **CharacterTextSplitter**: 문자 기반 분할
- **RecursiveCharacterTextSplitter**: 계층적 분할

**파라미터:**
```kotlin
TokenTextSplitter(
    defaultChunkSize = 500,      // 청크 크기 (토큰)
    minChunkSizeChars = 100,     // 최소 문자 수
    minChunkLengthToEmbed = 50,  // 임베딩 최소 길이
    maxNumChunks = 10000         // 최대 청크 수
)
```

### VectorStore

문서 청크를 벡터로 변환하여 저장하고 검색합니다.

**주요 메서드:**
```kotlin
// 문서 추가
vectorStore.add(documents)

// 유사도 검색
val results = vectorStore.similaritySearch(
    SearchRequest.query("질문")
        .withTopK(3)
)
```

### ChatModel

검색된 컨텍스트를 바탕으로 답변을 생성합니다.

```kotlin
val prompt = """
    [컨텍스트]
    $context
    
    [질문]
    $question
""".trimIndent()

val answer = chatModel.call(prompt)
```

## 🎓 학습 포인트

1. **RAG 워크플로우**: Load → Split → Store → Retrieve → Generate
2. **문서 분할**: 적절한 청크 크기 설정의 중요성
3. **컨텍스트 구성**: 검색된 문서를 프롬프트에 포함
4. **답변 근거**: 출처 문서 제공으로 신뢰성 향상
5. **환각 방지**: 문서 기반 답변으로 정확도 향상

## 💡 실전 활용 사례

### 1. 사내 위키 챗봇
- 회사 정책, 절차 문서 기반 Q&A
- 신입 사원 온보딩 지원
- HR 문의 자동 응답

### 2. 기술 문서 검색
- API 문서 검색
- 코딩 가이드 참조
- 트러블슈팅 가이드

### 3. 고객 지원
- 제품 매뉴얼 기반 답변
- FAQ 자동 응답
- 기술 지원 티켓 처리

### 4. 법률/규정 검색
- 계약서 검토
- 규정 준수 확인
- 법률 자문 지원

## 🚀 다음 단계

- **15장**: RAG 챗봇 고도화 (ETL 파이프라인, 자동 업데이트)

## 📚 참고 자료

- [Spring AI 공식 문서](https://docs.spring.io/spring-ai/reference/)
- [RAG 패턴](https://docs.spring.io/spring-ai/reference/concepts.html#_retrieval_augmented_generation)
- [Document Readers](https://docs.spring.io/spring-ai/reference/api/document-readers.html)
- [Text Splitters](https://docs.spring.io/spring-ai/reference/api/text-splitters.html)

## 💡 팁

> [!TIP]
> **청크 크기 설정**: 너무 작으면 컨텍스트 부족, 너무 크면 관련 없는 정보 포함. 보통 300-800 토큰이 적당합니다.

> [!TIP]
> **TopK 설정**: 3-5개가 적당합니다. 너무 많으면 노이즈 증가, 너무 적으면 정보 부족.

> [!TIP]
> **메타데이터 활용**: 문서 출처, 작성일, 카테고리 등을 메타데이터로 저장하면 필터링에 유용합니다.

> [!WARNING]
> **토큰 제한**: ChatModel의 컨텍스트 윈도우를 초과하지 않도록 주의하세요. GPT-4o-mini는 128K 토큰까지 지원합니다.

## 🔧 고급 기능

### 1. 문서 메타데이터 활용

```kotlin
val document = Document(
    content,
    mapOf(
        "source" to filePath,
        "type" to "markdown",
        "category" to "policy",
        "lastModified" to File(filePath).lastModified(),
        "author" to "HR Team"
    )
)
```

### 2. 청크 오버랩

```kotlin
val textSplitter = TokenTextSplitter(
    defaultChunkSize = 500,
    defaultChunkOverlap = 50  // 청크 간 50 토큰 중복
)
```

### 3. 필터링 검색

```kotlin
val searchRequest = SearchRequest.query(question)
    .withTopK(5)
    .withFilterExpression("category == 'policy'")
```

### 4. 답변 품질 향상

```kotlin
val prompt = """
    당신은 사내 위키 전문가입니다.
    다음 문서들을 참고하여 정확하고 상세하게 답변해주세요.
    
    [참고 문서]
    $context
    
    [질문]
    $question
    
    [답변 규칙]
    1. 참고 문서의 내용만 사용
    2. 출처 문서명 명시
    3. 불확실한 경우 명시적으로 표현
    4. 간결하고 명확하게 작성
""".trimIndent()
```

## 📊 성능 고려사항

### 문서 로딩 시간
- Markdown: 빠름 (~10ms/파일)
- PDF: 중간 (~100ms/파일)
- 대용량 파일: 분할 처리 권장

### 임베딩 생성 시간
- 청크당 ~100-500ms
- 배치 처리로 최적화 가능
- 비용: $0.0001/1K 토큰

### 검색 성능
- SimpleVectorStore: O(n)
- PGVector: O(log n)
- 1000개 청크: SimpleVectorStore 충분
- 10000개 이상: PGVector 권장

### 메모리 사용량
- 청크당 ~6KB (벡터)
- 1000개 청크: ~6MB
- 10000개 청크: ~60MB

## 🎯 Best Practices

1. **문서 구조화**
   - 명확한 제목과 섹션
   - 일관된 포맷
   - 메타데이터 포함

2. **청크 크기 최적화**
   - 문서 특성에 맞게 조정
   - A/B 테스트로 검증
   - 오버랩 활용

3. **프롬프트 엔지니어링**
   - 명확한 지시사항
   - 출처 명시 요구
   - 환각 방지 규칙

4. **모니터링**
   - 답변 품질 추적
   - 검색 정확도 측정
   - 사용자 피드백 수집
