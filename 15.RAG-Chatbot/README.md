# 15장: [실전] RAG 챗봇 고도화 (데이터 파이프라인)

## 📚 학습 목표

14장의 RAG 챗봇에 **ETL 파이프라인**을 구축합니다. 애플리케이션 시작 시 자동으로 문서를 로드하고, 중복을 체크하며, 벡터 저장소를 업데이트하는 프로세스를 구현합니다.

## 🔑 핵심 키워드

- `ETL` (Extract, Transform, Load)
- `@PostConstruct`
- 데이터 파이프라인
- 문서 관리
- 중복 검사
- 증분 업데이트

## 📖 개요

14장에서 구현한 RAG 챗봇을 고도화하여, 수동 문서 인제스트가 아닌 **자동화된 ETL 파이프라인**을 구축합니다. 애플리케이션 시작 시 자동으로 문서를 로드하고, 파일 변경을 감지하여 증분 업데이트를 수행합니다.

## 🔄 ETL 파이프라인

### 14장 vs 15장

| 구분 | 14장 (기본) | 15장 (고도화) |
|------|-----------|-------------|
| 문서 로딩 | 수동 API 호출 | 자동 (시작 시) |
| 중복 처리 | 없음 (매번 재처리) | 파일 해시 기반 검사 |
| 업데이트 | 전체 재로드 | 증분 업데이트 |
| 모니터링 | 없음 | 상태 추적 |
| 관리 | 수동 | 자동화 |

### ETL 프로세스

```
애플리케이션 시작
     ↓
1. EXTRACT (추출)
   - 문서 디렉토리 스캔
   - 파일 목록 수집
   - 메타데이터 추출
     ↓
2. TRANSFORM (변환)
   - 파일 해시 계산
   - 중복 검사
   - 변경 감지
   - 텍스트 분할
     ↓
3. LOAD (적재)
   - 신규/변경 문서만 임베딩
   - VectorStore 업데이트
   - 상태 저장
     ↓
RAG 서비스 준비 완료
```

## 🏗️ 프로젝트 구조

```
15.RAG-Chatbot/
├── README.md
├── QUICKSTART.md
└── sample/
    ├── build.gradle.kts
    ├── settings.gradle.kts
    ├── test-requests.http
    ├── wiki-documents/           # 자동 로드 대상
    │   ├── company-policy.md
    │   ├── tech-stack.md
    │   └── ...
    └── src/main/
        ├── kotlin/com/example/ragchatbot/
        │   ├── RAGChatbotApplication.kt
        │   ├── config/
        │   │   └── VectorStoreConfig.kt
        │   ├── service/
        │   │   ├── ETLPipelineService.kt      # ETL 파이프라인
        │   │   ├── DocumentLoaderService.kt   # 문서 로더
        │   │   ├── DocumentTracker.kt         # 문서 추적
        │   │   └── RAGService.kt              # RAG 서비스
        │   ├── controller/
        │   │   └── RAGChatController.kt
        │   └── dto/
        │       └── ChatDTO.kt
        └── resources/
            └── application.yml
```

## 💻 구현 상세

### 1. 문서 추적 (DocumentTracker)

```kotlin
@Component
class DocumentTracker {
    private val documentHashes = ConcurrentHashMap<String, String>()
    
    fun calculateFileHash(file: File): String {
        val md = MessageDigest.getInstance("MD5")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var read: Int
            while (input.read(buffer).also { read = it } > 0) {
                md.update(buffer, 0, read)
            }
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }
    
    fun isDocumentChanged(filePath: String, currentHash: String): Boolean {
        val previousHash = documentHashes[filePath]
        return previousHash == null || previousHash != currentHash
    }
    
    fun updateDocumentHash(filePath: String, hash: String) {
        documentHashes[filePath] = hash
    }
}
```

### 2. ETL 파이프라인 서비스

```kotlin
@Service
class ETLPipelineService(
    private val documentLoader: DocumentLoaderService,
    private val vectorStore: VectorStore,
    private val documentTracker: DocumentTracker
) {
    
    @PostConstruct
    fun initializePipeline() {
        logger.info("ETL 파이프라인 시작...")
        loadAndProcessDocuments()
    }
    
    fun loadAndProcessDocuments() {
        val directory = File("wiki-documents")
        if (!directory.exists()) {
            logger.warn("문서 디렉토리가 없습니다: ${directory.absolutePath}")
            return
        }
        
        // 1. EXTRACT: 파일 수집
        val files = directory.listFiles { file ->
            file.extension.lowercase() == "md"
        } ?: emptyArray()
        
        logger.info("발견된 문서: ${files.size}개")
        
        var newCount = 0
        var updatedCount = 0
        var skippedCount = 0
        
        files.forEach { file ->
            // 2. TRANSFORM: 해시 계산 및 중복 검사
            val currentHash = documentTracker.calculateFileHash(file)
            
            if (documentTracker.isDocumentChanged(file.absolutePath, currentHash)) {
                // 3. LOAD: 문서 로드 및 저장
                val documents = documentLoader.loadAndSplitMarkdownFile(file.absolutePath)
                vectorStore.add(documents)
                
                documentTracker.updateDocumentHash(file.absolutePath, currentHash)
                
                if (documentTracker.documentHashes[file.absolutePath] == null) {
                    newCount++
                } else {
                    updatedCount++
                }
                
                logger.info("처리 완료: ${file.name} (${documents.size} 청크)")
            } else {
                skippedCount++
                logger.debug("건너뜀 (변경 없음): ${file.name}")
            }
        }
        
        logger.info("ETL 완료 - 신규: $newCount, 업데이트: $updatedCount, 건너뜀: $skippedCount")
    }
}
```

### 3. REST Controller (모니터링 포함)

```kotlin
@RestController
@RequestMapping("/api/rag")
class RAGChatController(
    private val ragService: RAGService,
    private val etlPipelineService: ETLPipelineService,
    private val documentTracker: DocumentTracker
) {
    
    // RAG Q&A
    @PostMapping("/ask")
    fun ask(@RequestBody request: ChatRequest): ChatResponse {
        return ragService.askQuestion(request.question, request.topK ?: 3)
    }
    
    // 문서 상태 조회
    @GetMapping("/documents/status")
    fun getDocumentStatus(): Map<String, Any> {
        return mapOf(
            "totalDocuments" to documentTracker.documentHashes.size,
            "documents" to documentTracker.documentHashes.keys
        )
    }
    
    // 수동 리프레시
    @PostMapping("/refresh")
    fun refreshDocuments(): Map<String, Any> {
        etlPipelineService.loadAndProcessDocuments()
        return mapOf(
            "status" to "success",
            "message" to "문서 리프레시 완료"
        )
    }
}
```

## 🧪 테스트 방법

### 1. 애플리케이션 시작 (자동 로드)

```bash
cd sample
export OPENAI_API_KEY=your-api-key-here
./gradlew bootRun
```

**로그 확인:**
```
ETL 파이프라인 시작...
발견된 문서: 4개
처리 완료: company-policy.md (5 청크)
처리 완료: tech-stack.md (6 청크)
처리 완료: development-guide.md (7 청크)
처리 완료: faq.md (5 청크)
ETL 완료 - 신규: 4, 업데이트: 0, 건너뜀: 0
```

### 2. 문서 상태 확인

```bash
curl http://localhost:9000/api/rag/documents/status
```

**응답:**
```json
{
  "totalDocuments": 4,
  "documents": [
    "wiki-documents/company-policy.md",
    "wiki-documents/tech-stack.md",
    "wiki-documents/development-guide.md",
    "wiki-documents/faq.md"
  ]
}
```

### 3. 질문하기

```bash
curl -X POST http://localhost:9000/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{
    "question": "회사의 재택근무 정책은?",
    "topK": 3
  }'
```

### 4. 문서 수정 후 리프레시

```bash
# 1. 문서 수정
echo "## 새로운 정책" >> wiki-documents/company-policy.md

# 2. 수동 리프레시
curl -X POST http://localhost:9000/api/rag/refresh

# 3. 로그 확인
# 처리 완료: company-policy.md (6 청크)
# ETL 완료 - 신규: 0, 업데이트: 1, 건너뜀: 3
```

## 📝 주요 개념 설명

### @PostConstruct

Spring Bean 초기화 후 자동 실행되는 메서드를 지정합니다.

```kotlin
@PostConstruct
fun initializePipeline() {
    // 애플리케이션 시작 시 자동 실행
    loadAndProcessDocuments()
}
```

### 파일 해시 (MD5)

파일 내용의 변경을 감지하기 위해 해시값을 계산합니다.

```kotlin
fun calculateFileHash(file: File): String {
    val md = MessageDigest.getInstance("MD5")
    // 파일 내용을 읽어 해시 계산
    return md.digest().joinToString("") { "%02x".format(it) }
}
```

### 증분 업데이트

변경된 문서만 처리하여 효율성을 높입니다.

```kotlin
if (documentTracker.isDocumentChanged(filePath, currentHash)) {
    // 변경된 문서만 처리
    vectorStore.add(documents)
}
```

## 🎓 학습 포인트

1. **ETL 파이프라인**: Extract → Transform → Load 프로세스
2. **자동화**: @PostConstruct를 사용한 시작 시 초기화
3. **중복 검사**: 파일 해시 기반 변경 감지
4. **증분 업데이트**: 변경된 문서만 처리
5. **모니터링**: 문서 상태 추적 및 조회
6. **수동 제어**: 필요 시 수동 리프레시

## 💡 실전 활용 사례

### 1. 사내 위키 자동 동기화
- 위키 문서 변경 시 자동 반영
- 야간 배치로 정기 업데이트
- 변경 이력 추적

### 2. 문서 버전 관리
- Git 연동으로 변경 감지
- 커밋 시 자동 업데이트
- 롤백 지원

### 3. 대용량 문서 처리
- 증분 업데이트로 효율성 향상
- 배치 처리로 부하 분산
- 우선순위 기반 처리

## 🚀 다음 단계

- **16장**: AI 기반 스마트 날씨 알리미

## 📚 참고 자료

- [Spring @PostConstruct](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/PostConstruct.html)
- [ETL 패턴](https://en.wikipedia.org/wiki/Extract,_transform,_load)

## 💡 팁

> [!TIP]
> **파일 감시**: Spring Boot DevTools나 WatchService를 사용하면 파일 변경 시 자동 리프레시 가능

> [!TIP]
> **배치 처리**: 대량 문서는 배치로 나누어 처리하여 메모리 효율성 향상

> [!WARNING]
> **동시성**: 여러 인스턴스 실행 시 중복 처리 방지 필요 (분산 락 사용)

## 🔧 고급 기능

### 1. 스케줄링

```kotlin
@Scheduled(cron = "0 0 2 * * *")  // 매일 새벽 2시
fun scheduledRefresh() {
    etlPipelineService.loadAndProcessDocuments()
}
```

### 2. 파일 감시

```kotlin
val watchService = FileSystems.getDefault().newWatchService()
directory.register(watchService, ENTRY_MODIFY)
```

### 3. 우선순위 처리

```kotlin
val priorityFiles = files.sortedBy { 
    it.lastModified() 
}.reversed()
```

## 📊 성능 최적화

### 해시 계산 캐싱
- 파일 수정 시간 먼저 확인
- 변경 없으면 해시 계산 생략

### 병렬 처리
```kotlin
files.parallelStream().forEach { file ->
    processDocument(file)
}
```

### 배치 임베딩
```kotlin
val allDocuments = files.flatMap { loadDocuments(it) }
vectorStore.add(allDocuments)  // 한 번에 처리
```
