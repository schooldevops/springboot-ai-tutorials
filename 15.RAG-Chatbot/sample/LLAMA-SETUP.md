# Llama 모델 사용 가이드

## Ollama 설치 및 설정

### 1. Ollama 설치

**macOS:**
```bash
brew install ollama
```

**Linux:**
```bash
curl -fsSL https://ollama.com/install.sh | sh
```

**Windows:**
https://ollama.com/download 에서 다운로드

### 2. Ollama 서비스 시작

```bash
ollama serve
```

기본적으로 `http://localhost:11434`에서 실행됩니다.

### 3. Llama 모델 다운로드

**Chat 모델 (Llama 3.2):**
```bash
ollama pull llama3.2
```

**Embedding 모델 (Nomic Embed Text):**
```bash
ollama pull nomic-embed-text
```

### 4. 모델 확인

```bash
ollama list
```

예상 출력:
```
NAME                    ID              SIZE    MODIFIED
llama3.2:latest         a80c4f17acd5    2.0 GB  2 hours ago
nomic-embed-text:latest 0a109f422b47    274 MB  2 hours ago
```

## 애플리케이션 실행

### 기본 설정으로 실행

```bash
cd 15.RAG-Chatbot/sample
./gradlew bootRun
```

### 커스텀 모델 사용

```bash
# 다른 Llama 모델 사용
export OLLAMA_CHAT_MODEL=llama3.2:3b
export OLLAMA_EMBEDDING_MODEL=nomic-embed-text

./gradlew bootRun
```

### Ollama 서버 URL 변경

```bash
export OLLAMA_BASE_URL=http://your-ollama-server:11434
./gradlew bootRun
```

## 사용 가능한 모델

### Chat 모델
- `llama3.2` (권장) - 최신 Llama 모델
- `llama3.2:1b` - 경량 버전 (1B 파라미터)
- `llama3.2:3b` - 중간 버전 (3B 파라미터)
- `llama3.1` - 이전 버전
- `llama2` - Llama 2 모델

### Embedding 모델
- `nomic-embed-text` (권장) - 고품질 임베딩
- `mxbai-embed-large` - 대용량 임베딩
- `all-minilm` - 경량 임베딩

## 테스트

### 1. Health Check

```bash
curl http://localhost:9000/api/rag/health
```

### 2. 문서 상태 확인

```bash
curl http://localhost:9000/api/rag/documents/status
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

## OpenAI vs Ollama 비교

| 특징 | OpenAI | Ollama (Llama) |
|------|--------|----------------|
| 비용 | 유료 (API 호출당) | 무료 (로컬 실행) |
| 속도 | 빠름 (클라우드) | 중간 (로컬 하드웨어 의존) |
| 프라이버시 | 외부 전송 | 완전 로컬 |
| 설정 | API 키만 필요 | Ollama 설치 필요 |
| 인터넷 | 필수 | 불필요 |
| 품질 | 매우 높음 | 높음 |

## 문제 해결

### Ollama 연결 실패

```
Connection refused: localhost:11434
```

**해결:**
```bash
# Ollama 서비스 시작
ollama serve
```

### 모델을 찾을 수 없음

```
model 'llama3.2' not found
```

**해결:**
```bash
ollama pull llama3.2
ollama pull nomic-embed-text
```

### 메모리 부족

Llama 모델은 메모리를 많이 사용합니다.

**해결:**
- 더 작은 모델 사용: `llama3.2:1b`
- 시스템 메모리 확인: 최소 8GB RAM 권장

## 성능 최적화

### GPU 가속

Ollama는 자동으로 GPU를 감지하고 사용합니다.

**확인:**
```bash
ollama ps
```

### 동시 요청 제한

로컬 실행 시 동시 요청을 제한하세요:

```yaml
server:
  tomcat:
    threads:
      max: 10
```

## 추가 정보

- [Ollama 공식 문서](https://ollama.com/docs)
- [Llama 모델 목록](https://ollama.com/library)
- [Spring AI Ollama](https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html)
