# Chapter 15 - Llama 변환 요약

## 변경 사항

### 1. build.gradle.kts
**변경 전:**
```kotlin
implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter")
```

**변경 후:**
```kotlin
implementation("org.springframework.ai:spring-ai-ollama-spring-boot-starter")
```

### 2. application.yml
**변경 전:**
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY:your-api-key-here}
      embedding:
        options:
          model: text-embedding-ada-002
      chat:
        options:
          model: gpt-4o-mini
```

**변경 후:**
```yaml
spring:
  ai:
    ollama:
      base-url: ${OLLAMA_BASE_URL:http://localhost:11434}
      embedding:
        options:
          model: ${OLLAMA_EMBEDDING_MODEL:nomic-embed-text}
      chat:
        options:
          model: ${OLLAMA_CHAT_MODEL:llama3.2}
```

## 사용 방법

### 1. Ollama 설치 및 모델 다운로드

```bash
# Ollama 설치 (macOS)
brew install ollama

# Ollama 서비스 시작
ollama serve

# 새 터미널에서 모델 다운로드
ollama pull llama3.2
ollama pull nomic-embed-text
```

### 2. 애플리케이션 실행

```bash
cd 15.RAG-Chatbot/sample
./gradlew bootRun
```

### 3. 테스트

```bash
# Health check
curl http://localhost:9000/api/rag/health

# 문서 상태
curl http://localhost:9000/api/rag/documents/status

# 질문하기
curl -X POST http://localhost:9000/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "회사의 재택근무 정책은?", "topK": 3}'
```

## 장점

✅ **무료** - API 비용 없음
✅ **프라이버시** - 모든 데이터가 로컬에서 처리
✅ **오프라인** - 인터넷 연결 불필요
✅ **커스터마이징** - 다양한 Llama 모델 선택 가능

## 주의사항

⚠️ **시스템 요구사항** - 최소 8GB RAM 권장
⚠️ **속도** - OpenAI보다 느릴 수 있음 (하드웨어 의존)
⚠️ **초기 설정** - Ollama 설치 및 모델 다운로드 필요

## 추가 문서

자세한 내용은 `LLAMA-SETUP.md` 파일을 참고하세요.
