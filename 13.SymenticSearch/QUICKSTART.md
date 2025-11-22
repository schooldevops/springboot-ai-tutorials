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
cd 13.SymenticSearch/sample

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
curl http://localhost:9000/api/search/health
```

Expected response:
```json
{
  "status": "UP",
  "service": "Semantic Search API",
  "timestamp": "1763736918901"
}
```

## Testing the API

### Step 1: Add Documents

#### Add Single Document
```bash
curl -X POST http://localhost:9000/api/search/documents \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Spring Boot는 Java 기반의 웹 애플리케이션 프레임워크입니다.",
    "metadata": {"category": "framework"}
  }'
```

#### Add Multiple Documents (Batch)
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

### Step 2: Perform Semantic Search

#### POST Method
```bash
curl -X POST http://localhost:9000/api/search/query \
  -H "Content-Type: application/json" \
  -d '{
    "query": "백엔드 개발 도구",
    "topK": 3
  }'
```

#### GET Method
```bash
curl "http://localhost:9000/api/search/query?query=데이터베이스&topK=3"
```

**Expected Response:**
```json
{
  "query": "백엔드 개발 도구",
  "resultCount": 3,
  "results": [
    {
      "rank": 1,
      "content": "Spring Boot는 Java 기반의 웹 애플리케이션 프레임워크입니다.",
      "metadata": {"category": "framework"}
    },
    {
      "rank": 2,
      "content": "Kotlin은 JVM에서 실행되는 현대적인 프로그래밍 언어입니다.",
      "metadata": {"category": "language"}
    },
    {
      "rank": 3,
      "content": "PostgreSQL은 오픈소스 관계형 데이터베이스입니다.",
      "metadata": {"category": "database"}
    }
  ]
}
```

## Using the HTTP Test File

If you're using IntelliJ IDEA or VS Code with REST Client extension:

1. Open `test-requests.http`
2. Click the "Run" button next to any request
3. View the response in the editor

## Sample Document Data

Check `sample-documents.md` for various document examples:
- Programming Languages (Kotlin, Java, Python, TypeScript)
- Frameworks (Spring Boot, React, Django, Express)
- Databases (PostgreSQL, MongoDB, Redis, MySQL)
- DevOps Tools (Docker, Kubernetes, Jenkins, GitHub Actions)
- Cloud Services (AWS, GCP, Azure)

## Testing Workflow

### 1. Start Fresh
```bash
# Restart the application to clear in-memory store
./gradlew bootRun
```

### 2. Load Sample Documents
Use the batch endpoint to load multiple documents at once:
```bash
# Load programming languages
curl -X POST http://localhost:9000/api/search/documents/batch \
  -H "Content-Type: application/json" \
  -d @sample-data/languages.json

# Load frameworks
curl -X POST http://localhost:9000/api/search/documents/batch \
  -H "Content-Type: application/json" \
  -d @sample-data/frameworks.json
```

### 3. Test Semantic Search
Try different queries to see semantic matching:

```bash
# Query: "백엔드 개발"
# Should match: Spring Boot, Kotlin, PostgreSQL

# Query: "컨테이너 기술"
# Should match: Docker, Kubernetes

# Query: "NoSQL"
# Should match: MongoDB, Redis
```

## Troubleshooting

### Error: "Unauthorized" or "Invalid API key"

- Check that your API key is correct
- Ensure the key starts with `sk-`
- Verify the environment variable is set: `echo $OPENAI_API_KEY`

### Error: "Connection refused"

- Make sure the application is running
- Check the port is 9000: `lsof -i :9000`
- Verify no firewall is blocking the connection

### Empty Search Results

- Make sure you've added documents first
- Check that documents were successfully added
- Try lowering the similarity threshold
- Increase topK to see more results

### Port 9000 already in use

Change the port in `application.yml`:

```yaml
server:
  port: 9001  # or any available port
```

## Understanding Semantic Search

### Example: Keyword vs Semantic

**Keyword Search:**
- Query: "자바 프레임워크"
- Matches: Only documents with exact words "자바" or "프레임워크"

**Semantic Search:**
- Query: "자바 프레임워크"
- Matches: 
  - "Spring Boot는 Java 기반..." ✅
  - "Kotlin은 JVM에서..." ✅ (related concept)
  - "백엔드 개발 도구..." ✅ (similar meaning)

### How It Works

1. **Embedding**: Text → Vector (1536 dimensions)
   ```
   "Spring Boot" → [0.123, -0.456, 0.789, ...]
   ```

2. **Storage**: Vectors stored in VectorStore
   ```
   SimpleVectorStore (in-memory)
   ```

3. **Search**: Query vector compared with all stored vectors
   ```
   Cosine similarity: 0.0 (different) ~ 1.0 (identical)
   ```

4. **Results**: Top K most similar documents returned

## Next Steps

- 📖 Read the full [README.md](README.md) for detailed explanations
- 🧪 Try all test cases in `test-requests.http`
- 📝 Test with your own document data
- 🔧 Experiment with different topK and threshold values
- 📚 Proceed to Chapter 14 for RAG chatbot

## API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/search/health` | Health check |
| POST | `/api/search/documents` | Add single document |
| POST | `/api/search/documents/batch` | Add multiple documents |
| POST | `/api/search/query` | Semantic search (POST) |
| GET | `/api/search/query` | Semantic search (GET) |

## Tips

💡 **Batch Loading**: Use batch endpoint for better performance when adding multiple documents

💡 **Metadata**: Add category, tags, dates to enable filtering

💡 **TopK Setting**: Start with 3-5 results for best relevance

💡 **Threshold**: Use 0.7-0.8 for high precision, 0.5-0.6 for high recall

> [!WARNING]
> **In-Memory Storage**: SimpleVectorStore loses data on restart. For production, use PGVector or similar.

> [!TIP]
> **Cost Optimization**: Embedding API calls cost money. Cache embeddings when possible.

## Support

- 📚 [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- 📖 [Embedding API Reference](https://docs.spring.io/spring-ai/reference/api/embedding.html)
- 📖 [VectorStore API Reference](https://docs.spring.io/spring-ai/reference/api/vectordb.html)
- 💬 [OpenAI Embeddings Documentation](https://platform.openai.com/docs/guides/embeddings)
