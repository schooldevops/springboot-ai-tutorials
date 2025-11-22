# 🚀 Quick Start Guide

## Prerequisites

- ☕ Java 17 or higher
- 🐘 Gradle (or use included wrapper)
- 🔑 OpenAI API key ([Get one here](https://platform.openai.com/api-keys))

## Setup Steps

### 1. Get OpenAI API Key

1. Visit https://platform.openai.com/api-keys
2. Sign in or create an account
3. Click "Create new secret key"
4. Copy the key (starts with `sk-`)

### 2. Configure API Key

Choose one of the following methods:

#### Method A: Environment Variable (Recommended)

**macOS/Linux:**
```bash
export OPENAI_API_KEY=sk-your-actual-api-key-here
```

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="sk-your-actual-api-key-here"
```

**Windows (CMD):**
```cmd
set OPENAI_API_KEY=sk-your-actual-api-key-here
```

#### Method B: Edit application.yml

Edit `src/main/resources/application.yml`:

```yaml
spring:
  ai:
    openai:
      api-key: sk-your-actual-api-key-here  # Replace with your actual key
```

> ⚠️ **Security Warning**: Never commit API keys to version control!

### 3. Run the Application

```bash
# Navigate to sample directory
cd 14.WikiKMS/sample

# Run the application
./gradlew bootRun
```

**Windows:**
```cmd
gradlew.bat bootRun
```

### 4. Verify It's Running

Open a new terminal and test the health endpoint:

```bash
curl http://localhost:9000/api/wiki/health
```

Expected response:
```json
{
  "status": "UP",
  "service": "Wiki Chatbot API",
  "timestamp": "1763738059677"
}
```

## Using the RAG Chatbot

### Step 1: Ingest Documents

First, load the wiki documents into the vector store:

```bash
curl -X POST http://localhost:9000/api/wiki/ingest \
  -H "Content-Type: application/json" \
  -d '{"directory": "wiki-documents"}'
```

**Expected Response:**
```json
{
  "status": "success",
  "message": "문서 인제스트 완료",
  "directory": "wiki-documents",
  "filesProcessed": 4,
  "files": [
    "company-policy.md",
    "tech-stack.md",
    "development-guide.md",
    "faq.md"
  ],
  "totalChunks": 23
}
```

### Step 2: Ask Questions

Now you can ask questions about the documents:

```bash
curl -X POST http://localhost:9000/api/wiki/ask \
  -H "Content-Type: application/json" \
  -d '{
    "question": "회사의 재택근무 정책은 무엇인가요?",
    "topK": 3
  }'
```

**Expected Response:**
```json
{
  "question": "회사의 재택근무 정책은 무엇인가요?",
  "answer": "company-policy.md 문서에 따르면, 직원들은 주 2회까지 재택근무를 할 수 있습니다. 재택근무를 원하는 경우 사전에 팀장의 승인을 받아야 하며, 업무 시간 동안 온라인 상태를 유지해야 합니다. 또한 정기 회의에는 반드시 참석해야 하고, 업무 진행 상황을 매일 보고해야 합니다.",
  "sources": [
    "company-policy.md"
  ],
  "timestamp": 1763738100000
}
```

## Sample Questions

Try these questions to test the RAG chatbot:

### Company Policy
```bash
# 휴가 정책
curl -X POST http://localhost:9000/api/wiki/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "연차는 몇 일이고 어떻게 사용하나요?"}'

# 복지 제도
curl -X POST http://localhost:9000/api/wiki/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "회사의 복지 제도는 무엇이 있나요?"}'
```

### Tech Stack
```bash
# 백엔드 기술
curl -X POST http://localhost:9000/api/wiki/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "우리 회사에서 사용하는 백엔드 기술 스택은 무엇인가요?"}'

# 데이터베이스
curl -X POST http://localhost:9000/api/wiki/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "어떤 데이터베이스를 사용하나요?"}'
```

### Development Guide
```bash
# 코딩 컨벤션
curl -X POST http://localhost:9000/api/wiki/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "Kotlin 코딩 컨벤션에서 클래스 네이밍 규칙은?"}'

# Git 커밋 메시지
curl -X POST http://localhost:9000/api/wiki/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "Git 커밋 메시지 형식은 어떻게 작성하나요?"}'
```

### FAQ
```bash
# 출장 경비
curl -X POST http://localhost:9000/api/wiki/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "출장 시 경비 처리는 어떻게 하나요?"}'

# 장비 지원
curl -X POST http://localhost:9000/api/wiki/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "입사 시 어떤 장비를 지급받나요?"}'
```

## Using the HTTP Test File

If you're using IntelliJ IDEA or VS Code with REST Client extension:

1. Open `test-requests.http`
2. Click the "Run" button next to any request
3. View the response in the editor

The file contains 15 pre-configured test scenarios.

## Understanding RAG Workflow

```
1. LOAD
   ↓
   wiki-documents/*.md 파일 읽기
   
2. SPLIT
   ↓
   긴 문서를 작은 청크로 분할
   
3. STORE
   ↓
   청크를 임베딩하여 VectorStore에 저장
   
4. RETRIEVE
   ↓
   질문과 유사한 청크 검색 (topK개)
   
5. GENERATE
   ↓
   검색된 청크를 컨텍스트로 답변 생성
```

## Troubleshooting

### Error: "Unauthorized" or "Invalid API key"

- Check that your API key is correct
- Ensure the key starts with `sk-`
- Verify the environment variable is set: `echo $OPENAI_API_KEY`

### Error: "관련 문서를 찾을 수 없습니다"

- Make sure you've ingested documents first
- Check that the ingest endpoint returned success
- Verify files exist in `wiki-documents/` directory

### Error: "파일을 찾을 수 없습니다"

- Check the directory path is correct
- Ensure you're running from the `sample/` directory
- Verify `wiki-documents/` folder exists

### Port 9000 already in use

Change the port in `application.yml`:

```yaml
server:
  port: 9001  # or any available port
```

## Advanced Usage

### Ingest Single File

```bash
curl -X POST http://localhost:9000/api/wiki/ingest/file \
  -H "Content-Type: application/json" \
  -d '{"filePath": "wiki-documents/company-policy.md"}'
```

### Adjust TopK

Retrieve more or fewer documents:

```bash
# Get top 5 documents
curl -X POST http://localhost:9000/api/wiki/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "프로덕션 장애 대응 절차는?", "topK": 5}'
```

### GET Method

```bash
curl "http://localhost:9000/api/wiki/ask?question=회사의%20복지%20제도는?&topK=3"
```

## Next Steps

- 📖 Read the full [README.md](README.md) for detailed explanations
- 🧪 Try all test cases in `test-requests.http`
- 📝 Add your own wiki documents
- 🔧 Customize chunk size in `application.yml`
- 📚 Proceed to Chapter 15 for RAG pipeline automation

## API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/wiki/health` | Health check |
| POST | `/api/wiki/ingest` | Ingest directory of documents |
| POST | `/api/wiki/ingest/file` | Ingest single file |
| POST | `/api/wiki/ask` | RAG-based Q&A (POST) |
| GET | `/api/wiki/ask` | RAG-based Q&A (GET) |

## Tips

💡 **Document Quality**: Better organized documents = better answers

💡 **TopK Setting**: 3-5 is usually optimal. Too many = noise, too few = missing info

💡 **Chunk Size**: Adjust in `application.yml` based on your document structure

💡 **Source Attribution**: Answers include source documents for verification

> [!WARNING]
> **In-Memory Storage**: SimpleVectorStore loses data on restart. Re-ingest documents after each restart.

> [!TIP]
> **Cost Optimization**: Embedding generation costs money. Ingest documents once, ask many questions.

## Support

- 📚 [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- 📖 [RAG Pattern](https://docs.spring.io/spring-ai/reference/concepts.html#_retrieval_augmented_generation)
- 📖 [Document Readers](https://docs.spring.io/spring-ai/reference/api/document-readers.html)
- 📖 [Text Splitters](https://docs.spring.io/spring-ai/reference/api/text-splitters.html)
