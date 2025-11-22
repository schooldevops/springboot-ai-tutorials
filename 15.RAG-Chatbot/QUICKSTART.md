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

**macOS/Linux:**
```bash
export OPENAI_API_KEY=sk-your-actual-api-key-here
```

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="sk-your-actual-api-key-here"
```

### 3. Run the Application

```bash
cd 15.RAG-Chatbot/sample
./gradlew bootRun
```

**Watch the ETL Pipeline in Action:**
```
=== ETL 파이프라인 시작 ===
발견된 문서: 4개
✓ 신규: company-policy.md (5 청크)
✓ 신규: tech-stack.md (6 청크)
✓ 신규: development-guide.md (7 청크)
✓ 신규: faq.md (5 청크)
ETL 완료 - 신규: 4, 업데이트: 0, 건너뜀: 0, 총: 4
=== ETL 파이프라인 완료 ===
Tomcat started on port 9000
```

## Testing the Application

### 1. Check Health

```bash
curl http://localhost:9000/api/rag/health
```

**Response:**
```json
{
  "status": "UP",
  "service": "RAG Chatbot API",
  "documentsLoaded": 4,
  "timestamp": "1763780626258"
}
```

### 2. Check Document Status

```bash
curl http://localhost:9000/api/rag/documents/status
```

**Response:**
```json
{
  "totalDocuments": 4,
  "documents": [
    "wiki-documents/company-policy.md",
    "wiki-documents/development-guide.md",
    "wiki-documents/faq.md",
    "wiki-documents/tech-stack.md"
  ]
}
```

### 3. Ask Questions

```bash
curl -X POST http://localhost:9000/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{
    "question": "회사의 재택근무 정책은?",
    "topK": 3
  }'
```

## Testing Incremental Updates

### 1. Modify a Document

```bash
echo "\n## 새로운 정책\n- 추가 내용" >> wiki-documents/company-policy.md
```

### 2. Trigger Manual Refresh

```bash
curl -X POST http://localhost:9000/api/rag/refresh
```

**Response:**
```json
{
  "status": "success",
  "message": "문서 리프레시 완료",
  "new": 0,
  "updated": 1,
  "skipped": 3,
  "total": 4
}
```

**Log Output:**
```
✓ 업데이트: company-policy.md (6 청크)
○ 건너뜀 (변경 없음): tech-stack.md
○ 건너뜀 (변경 없음): development-guide.md
○ 건너뜀 (변경 없음): faq.md
ETL 완료 - 신규: 0, 업데이트: 1, 건너뜀: 3
```

### 3. Add New Document

```bash
cat > wiki-documents/new-policy.md << 'EOF'
# 신규 정책

## 하이브리드 근무제
- 주 3일 사무실, 주 2일 재택
EOF

curl -X POST http://localhost:9000/api/rag/refresh
```

**Response:**
```json
{
  "new": 1,
  "updated": 0,
  "skipped": 4,
  "total": 5
}
```

## Key Features Demonstrated

### ✅ Automatic Loading on Startup

Documents are automatically loaded when the application starts via `@PostConstruct`.

### ✅ Duplicate Detection

Files are tracked using MD5 hash. Only changed files are reprocessed.

### ✅ Incremental Updates

Manual refresh processes only new or modified documents, skipping unchanged ones.

### ✅ Monitoring

Real-time status of loaded documents via `/api/rag/documents/status`.

## API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/rag/health` | Health check with document count |
| GET | `/api/rag/documents/status` | List all tracked documents |
| POST | `/api/rag/refresh` | Manual document refresh |
| POST | `/api/rag/ask` | RAG-based Q&A |
| GET | `/api/rag/ask` | RAG-based Q&A (GET) |

## Troubleshooting

### Documents Not Loading

Check the logs for:
```
발견된 문서: 0개
```

**Solution**: Ensure `wiki-documents/` directory exists and contains `.md` files.

### All Documents Skipped

```
ETL 완료 - 신규: 0, 업데이트: 0, 건너뜀: 4
```

**This is normal** on subsequent restarts if files haven't changed!

### API Key Error

```
HttpRetryException: cannot retry due to server authentication
```

**Solution**: Set valid `OPENAI_API_KEY` environment variable.

## Configuration

Edit `application.yml` to customize:

```yaml
etl:
  documents:
    directory: wiki-documents  # Document directory
    auto-load: true            # Auto-load on startup
```

## Tips

💡 **First Run**: All documents are marked as "신규" (new)

💡 **Subsequent Runs**: Unchanged documents are "건너뜀" (skipped)

💡 **Manual Refresh**: Use `/api/rag/refresh` to check for new/modified documents

💡 **Performance**: Incremental updates are much faster than full reload

## Next Steps

- 📖 Read the full [README.md](README.md)
- 🧪 Try modifying documents and refreshing
- 📝 Add your own wiki documents
- 🔧 Customize ETL settings in `application.yml`

## Support

- 📚 [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- 📖 [@PostConstruct](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/PostConstruct.html)
