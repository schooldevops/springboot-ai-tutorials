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
cd 11.SimpleChatBot/sample

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
curl http://localhost:8080/api/chat/health
```

Expected response:
```json
{
  "status": "UP",
  "service": "Simple ChatBot API",
  "timestamp": "1763735369702"
}
```

## Testing the API

### Simple Chat

```bash
curl -X POST http://localhost:8080/api/chat/simple \
  -H "Content-Type: application/json" \
  -d '{"message": "Spring AI가 무엇인가요?"}'
```

### Template-Based Chat

```bash
curl -X POST http://localhost:8080/api/chat/template \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Kotlin의 주요 특징은 무엇인가요?",
    "topic": "Kotlin 프로그래밍"
  }'
```

### Role-Based Chat

```bash
curl -X POST http://localhost:8080/api/chat/role \
  -H "Content-Type: application/json" \
  -d '{
    "message": "REST API 설계 시 주의할 점은?",
    "role": "당신은 20년 경력의 백엔드 아키텍트입니다."
  }'
```

## Using the HTTP Test File

If you're using IntelliJ IDEA or VS Code with REST Client extension:

1. Open `test-requests.http`
2. Click the "Run" button next to any request
3. View the response in the editor

## Troubleshooting

### Error: "Unauthorized" or "Invalid API key"

- Check that your API key is correct
- Ensure the key starts with `sk-`
- Verify the environment variable is set: `echo $OPENAI_API_KEY`

### Error: "Port 8080 already in use"

Change the port in `application.yml`:

```yaml
server:
  port: 8081  # or any available port
```

### Build Fails

```bash
# Clean and rebuild
./gradlew clean build

# If Gradle wrapper is missing
gradle wrapper --gradle-version 8.5
```

## Next Steps

- 📖 Read the full [README.md](README.md) for detailed explanations
- 🧪 Try all test cases in `test-requests.http`
- 🔧 Modify the code to add your own endpoints
- 📚 Proceed to Chapter 12 for more advanced features

## API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/chat/health` | Health check |
| POST | `/api/chat/simple` | Simple chat |
| POST | `/api/chat/template` | Template-based chat |
| POST | `/api/chat/role` | Role-based chat |

## Support

- 📚 [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- 💬 [OpenAI API Documentation](https://platform.openai.com/docs)
- 🐛 Check the application logs for detailed error messages
