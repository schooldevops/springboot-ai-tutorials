# 🚀 Quick Start Guide

## Prerequisites

- ☕ Java 17 or higher
- 🐘 Gradle (or use included wrapper)
- 🦙 Ollama with Llama model
- 🌤️ OpenWeatherMap API key ([Get one here](https://openweathermap.org/api))

## Setup Steps

### 1. Get OpenWeatherMap API Key

1. Visit https://openweathermap.org/api
2. Sign up for free account
3. Get your API key from dashboard

### 2. Install and Start Ollama

```bash
# macOS
brew install ollama

# Start Ollama
ollama serve

# Pull Llama model
ollama pull llama3.2
```

### 3. Configure API Key

```bash
export WEATHER_API_KEY=your-weather-api-key-here
```

### 4. Run Tests (TDD Verification)

```bash
cd 16.weather-alarm/sample

# Run all tests
./gradlew test

# Expected output:
# BUILD SUCCESSFUL
# 5 tests completed
```

### 5. Run the Application

```bash
./gradlew bootRun
```

## Testing the Application

### 1. Health Check

```bash
curl http://localhost:8080/api/weather/health
```

**Response:**
```json
{
  "status": "UP",
  "service": "Weather Alarm API"
}
```

### 2. Ask About Weather

```bash
curl -X POST http://localhost:8080/api/weather/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "오늘 서울 날씨 어때?"
  }'
```

**Expected Response:**
```json
{
  "message": "서울의 현재 날씨는 맑고 기온은 15도입니다. 습도는 60%로 쾌적합니다. 외출하기 좋은 날씨네요!",
  "functionCalled": "getWeather"
}
```

## TDD Workflow Demonstrated

### 1. Test First (RED)

```kotlin
@Test
fun `should fetch weather data for given city`() {
    val result = weatherService.getWeather("서울")
    assertEquals("서울", result.city)
}
```

### 2. Implementation (GREEN)

```kotlin
fun getWeather(city: String): WeatherData {
    // Implementation to pass the test
}
```

### 3. Refactor

Improve code quality while keeping tests green.

## Sample Questions

Try these questions to test Function Calling:

```bash
# 날씨 조회
curl -X POST http://localhost:8080/api/weather/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "부산 날씨 알려줘"}'

# 우산 필요 여부
curl -X POST http://localhost:8080/api/weather/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "서울 날씨 보고 우산 필요한지 알려줘"}'

# 옷차림 추천
curl -X POST http://localhost:8080/api/weather/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "오늘 서울 날씨에 맞는 옷차림 추천해줘"}'
```

## Troubleshooting

### Ollama Connection Error

```
Connection refused: localhost:11434
```

**Solution:**
```bash
ollama serve
```

### Weather API Error

```
Failed to fetch weather: 401 Unauthorized
```

**Solution:** Check your `WEATHER_API_KEY` environment variable.

### Tests Failing

```bash
# Clean and rebuild
./gradlew clean test

# Check test reports
open build/reports/tests/test/index.html
```

## Understanding Function Calling

When you ask "오늘 서울 날씨 어때?", the LLM:

1. **Recognizes** the need for weather data
2. **Calls** the `getWeather` function automatically
3. **Receives** real weather data from API
4. **Generates** natural language response

## Next Steps

- 📖 Read the full [README.md](README.md)
- 🧪 Run tests to see TDD in action
- 🔧 Modify tests and watch them fail/pass
- 📝 Add more weather functions

## Tips

💡 **TDD Practice**: Try adding a new test first, watch it fail, then implement

💡 **Function Description**: Clear @Description helps LLM choose the right function

💡 **Test Coverage**: Run `./gradlew test jacocoTestReport` to see coverage

## Support

- 📚 [Spring AI Function Calling](https://docs.spring.io/spring-ai/reference/api/functions.html)
- 🌤️ [OpenWeatherMap API Docs](https://openweathermap.org/api)
- 🦙 [Ollama Documentation](https://ollama.com/docs)
