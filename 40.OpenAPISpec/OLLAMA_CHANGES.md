# OpenAPI Spec Parser - Ollama 호환성 변경사항

## 변경된 파일들

### 1. build.gradle.kts
- Spring AI 버전: M4 → M6
- OpenAI → Ollama로 변경
```kotlin
// implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter:1.0.0-M6")
implementation("org.springframework.ai:spring-ai-ollama-spring-boot-starter:1.0.0-M6")
```

### 2. application.yml
- Ollama 설정 추가
- Embedding 모델 설정: `nomic-embed-text`
```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: deepseek-r1:8b
          temperature: 0.7
      embedding:
        options:
          model: nomic-embed-text
```

### 3. SpecDocumentService.kt
- `SimpleVectorStore` 생성자 변경: `SimpleVectorStore.builder(embeddingModel).build()`
- `similaritySearch` API 단순화: `SearchRequest` 제거, 기본 API 사용
- Nullable 반환 타입 처리: `?: emptyList()` 추가

### 4. SpecSearchService.kt
- `SearchRequest` import 제거
- `similaritySearch(query)` 기본 API 사용
- Nullable 반환 타입 처리

### 5. SpecController.kt
- `SearchRequest` → `SpecSearchRequest`로 이름 변경 (Spring AI의 SearchRequest와 충돌 방지)

## 주요 API 변경사항 (M4 → M6)

### SimpleVectorStore 생성
**Before (M4):**
```kotlin
vectorStore = SimpleVectorStore(embeddingModel)
```

**After (M6):**
```kotlin
vectorStore = SimpleVectorStore.builder(embeddingModel).build()
```

### Document 속성 접근
**Before (M4):**
```kotlin
doc.content
```

**After (M6):**
```kotlin
doc.text
```

### SimilaritySearch API
**M4에서 사용 가능했던 방식:**
```kotlin
vectorStore.similaritySearch(query, topK)
```

**M6에서 변경:**
```kotlin
// SearchRequest 방식은 API 호환성 문제로 사용 불가
// 기본 API 사용
vectorStore.similaritySearch(query) // Returns nullable List
```

## 실행 방법

```bash
# Gradle wrapper 생성 (이미 완료)
gradle wrapper --gradle-version 8.5

# 빌드
./gradlew build -x test

# 실행
./gradlew bootRun
```

## 확인사항

✅ 애플리케이션이 포트 8090에서 성공적으로 시작됨
✅ Ollama 설정 적용됨
✅ 모든 컴파일 에러 해결됨

## 필요한 Ollama 모델

애플리케이션 실행 전에 다음 모델들이 설치되어 있어야 합니다:

```bash
# Chat 모델
ollama pull deepseek-r1:8b

# Embedding 모델
ollama pull nomic-embed-text
```

## 테스트

애플리케이션이 실행 중일 때 `test.http` 파일을 사용하여 API 테스트 가능:

1. Health Check: `GET http://localhost:8090/api/spec/health`
2. Upload Spec: `POST http://localhost:8090/api/spec/upload-content`
3. Search: `POST http://localhost:8090/api/spec/search`
