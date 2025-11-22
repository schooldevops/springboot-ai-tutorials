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
cd 12.AnalysysResumeAndJson/sample

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
curl http://localhost:9000/api/resume/health
```

Expected response:
```json
{
  "status": "UP",
  "service": "Resume Analyzer API",
  "timestamp": "1763736310820"
}
```

## Testing the API

### Basic Resume Info

```bash
curl -X POST http://localhost:9000/api/resume/basic \
  -H "Content-Type: application/json" \
  -d '{
    "resumeText": "홍길동\n이메일: hong@example.com\n전화: 010-1234-5678\n\n경력: ABC 회사, 백엔드 개발자, 2020-01 ~ 현재"
  }'
```

### Full Resume Analysis

```bash
curl -X POST http://localhost:9000/api/resume/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "resumeText": "홍길동\n이메일: hong@example.com\n\n경력:\nABC 테크, 시니어 개발자, 2021-03 ~ 현재\n\n학력:\n서울대학교, 컴퓨터공학과, 학사, 2018\n\n기술: Kotlin, Spring Boot, PostgreSQL"
  }'
```

### Extract Skills Only

```bash
curl -X POST http://localhost:9000/api/resume/extract-skills \
  -H "Content-Type: application/json" \
  -d '{
    "resumeText": "기술 스택: Kotlin, Java, Spring Boot, PostgreSQL, Docker, Kubernetes, AWS"
  }'
```

## Using the HTTP Test File

If you're using IntelliJ IDEA or VS Code with REST Client extension:

1. Open `test-requests.http`
2. Click the "Run" button next to any request
3. View the response in the editor

## Sample Resume Data

Check `sample-resumes.md` for various resume examples:
- Backend Developer (detailed)
- Full-stack Developer
- Junior Developer
- Data Engineer
- Mobile Developer

## Troubleshooting

### Error: "Unauthorized" or "Invalid API key"

- Check that your API key is correct
- Ensure the key starts with `sk-`
- Verify the environment variable is set: `echo $OPENAI_API_KEY`

### Error: "JSON 파싱 실패"

- The LLM response format may vary
- Try adjusting the temperature in `application.yml` (lower = more consistent)
- Check the application logs for the raw response

### Port 9000 already in use

Change the port in `application.yml`:

```yaml
server:
  port: 9001  # or any available port
```

## Next Steps

- 📖 Read the full [README.md](README.md) for detailed explanations
- 🧪 Try all test cases in `test-requests.http`
- 📝 Test with your own resume data
- 🔧 Customize the data models for your needs
- 📚 Proceed to Chapter 13 for semantic search

## API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/resume/health` | Health check |
| POST | `/api/resume/basic` | Extract basic info only |
| POST | `/api/resume/analyze` | Full resume analysis |
| POST | `/api/resume/extract-skills` | Extract skills only |
| POST | `/api/resume/extract-experience` | Extract work experience only |

## Tips

💡 **Temperature Setting**: Set to 0.3 for more consistent JSON parsing

💡 **Data Validation**: Add validation logic to check required fields after parsing

💡 **Error Handling**: Implement retry logic for failed parsing attempts

💡 **Privacy**: Be mindful of personal data when using external APIs

## Support

- 📚 [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- 📖 [Output Parser Reference](https://docs.spring.io/spring-ai/reference/api/output-parser.html)
- 💬 [OpenAI API Documentation](https://platform.openai.com/docs)
