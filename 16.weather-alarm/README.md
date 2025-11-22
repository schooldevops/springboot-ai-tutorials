# 16장: [실전] AI 기반 스마트 날씨 알리미

## 📚 학습 목표

9장의 **Function Calling**을 활용합니다. 사용자가 "오늘 서울 날씨 어때?"라고 물으면, LLM이 이를 '날씨 조회 함수'로 인식하고, 실제 외부 날씨 API를 호출한 결과를 바탕으로 답변을 생성합니다.

## 🔑 핵심 키워드

- `Function Calling`
- 외부 API 연동
- `RestTemplate` / `WebClient`
- TDD (Test-Driven Development)
- Structured Output

## 📖 개요

이 장에서는 Spring AI의 Function Calling 기능을 사용하여 AI가 자동으로 외부 날씨 API를 호출하고, 그 결과를 바탕으로 사용자에게 맞춤형 답변을 제공하는 스마트 날씨 알리미를 구축합니다. **TDD 방식**으로 개발하여 테스트 커버리지를 확보합니다.

## 🎯 Function Calling이란?

**Function Calling**은 LLM이 사용자의 질문을 이해하고, 필요한 경우 미리 정의된 함수를 자동으로 호출하는 기능입니다.

### 일반 LLM vs Function Calling

| 특징 | 일반 LLM | Function Calling |
|------|---------|------------------|
| 실시간 데이터 | ❌ 학습 데이터만 | ✅ 외부 API 호출 |
| 날씨 정보 | ❌ 오래된 정보 | ✅ 실시간 날씨 |
| 정확성 | 낮음 (추측) | 높음 (실제 데이터) |
| 함수 호출 | 불가능 | 자동 호출 |

## 🔄 Function Calling 워크플로우

```
사용자 질문: "오늘 서울 날씨 어때?"
       ↓
1. LLM 분석
   - 날씨 정보 필요 감지
   - 함수 호출 결정
       ↓
2. Function Call
   - getWeather("서울") 호출
   - 외부 API 요청
       ↓
3. API 응답
   - 온도: 15°C
   - 날씨: 맑음
   - 습도: 60%
       ↓
4. LLM 답변 생성
   - 함수 결과 활용
   - 자연스러운 답변 생성
       ↓
"서울의 현재 날씨는 맑고 기온은 15도입니다. 
외출하기 좋은 날씨네요!"
```

## 🧪 TDD (Test-Driven Development)

### TDD 사이클

```
1. RED (실패하는 테스트 작성)
   ↓
2. GREEN (테스트 통과하는 최소 코드)
   ↓
3. REFACTOR (코드 개선)
   ↓
반복
```

### TDD의 장점

- ✅ 버그 조기 발견
- ✅ 리팩토링 안전성
- ✅ 문서화 효과
- ✅ 설계 개선

## 🏗️ 프로젝트 구조

```
16.weather-alarm/
├── README.md
├── QUICKSTART.md
└── sample/
    ├── build.gradle.kts
    ├── settings.gradle.kts
    ├── test-requests.http
    └── src/
        ├── main/
        │   ├── kotlin/com/example/weatheralarm/
        │   │   ├── WeatherAlarmApplication.kt
        │   │   ├── config/
        │   │   │   └── FunctionConfig.kt
        │   │   ├── service/
        │   │   │   ├── WeatherService.kt
        │   │   │   └── WeatherFunction.kt
        │   │   ├── controller/
        │   │   │   └── WeatherController.kt
        │   │   └── dto/
        │   │       ├── WeatherRequest.kt
        │   │       └── WeatherResponse.kt
        │   └── resources/
        │       └── application.yml
        └── test/
            └── kotlin/com/example/weatheralarm/
                ├── service/
                │   ├── WeatherServiceTest.kt
                │   └── WeatherFunctionTest.kt
                ├── controller/
                │   └── WeatherControllerTest.kt
                └── IntegrationTest.kt
```

## 💻 구현 상세 (TDD 방식)

### 1. 테스트 먼저 작성 (RED)

```kotlin
@Test
fun `should fetch weather data for given city`() {
    // given
    val city = "서울"
    
    // when
    val weather = weatherService.getWeather(city)
    
    // then
    assertThat(weather.city).isEqualTo("서울")
    assertThat(weather.temperature).isNotNull()
}
```

### 2. 구현 (GREEN)

```kotlin
@Service
class WeatherService(
    private val restTemplate: RestTemplate
) {
    fun getWeather(city: String): WeatherData {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey"
        val response = restTemplate.getForObject(url, WeatherApiResponse::class.java)
        
        return WeatherData(
            city = city,
            temperature = response?.main?.temp ?: 0.0,
            description = response?.weather?.firstOrNull()?.description ?: "",
            humidity = response?.main?.humidity ?: 0
        )
    }
}
```

### 3. Function Calling 설정

```kotlin
@Configuration
class FunctionConfig {
    
    @Bean
    @Description("Get current weather for a city")
    fun getWeather(weatherService: WeatherService): (WeatherRequest) -> WeatherData {
        return { request ->
            weatherService.getWeather(request.city)
        }
    }
}
```

### 4. AI 통합

```kotlin
@Service
class WeatherChatService(
    private val chatModel: ChatModel
) {
    
    fun chat(userMessage: String): String {
        val userPrompt = Prompt(
            userMessage,
            OllamaOptions.builder()
                .withFunction("getWeather")
                .build()
        )
        
        val response = chatModel.call(userPrompt)
        return response.result.output.content
    }
}
```

## 🧪 테스트 방법

### 1. 단위 테스트 실행

```bash
cd sample
./gradlew test
```

**출력:**
```
WeatherServiceTest > should fetch weather data for given city PASSED
WeatherFunctionTest > should call weather function PASSED
WeatherControllerTest > should return weather response PASSED

BUILD SUCCESSFUL
```

### 2. 통합 테스트

```bash
./gradlew integrationTest
```

### 3. 애플리케이션 실행

```bash
# Ollama 시작
ollama serve
ollama pull llama3.2

# 애플리케이션 실행
export WEATHER_API_KEY=your-api-key-here
./gradlew bootRun
```

### 4. API 테스트

```bash
curl -X POST http://localhost:8080/api/weather/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "오늘 서울 날씨 어때?"
  }'
```

**응답:**
```json
{
  "message": "서울의 현재 날씨는 맑고 기온은 15도입니다. 습도는 60%로 쾌적합니다. 외출하기 좋은 날씨네요!",
  "functionCalled": "getWeather",
  "weatherData": {
    "city": "서울",
    "temperature": 15.0,
    "description": "맑음",
    "humidity": 60
  }
}
```

## 📝 주요 개념 설명

### Function Calling

LLM이 자동으로 함수를 호출하도록 설정합니다.

```kotlin
@Bean
@Description("Get current weather for a city")
fun getWeather(): (WeatherRequest) -> WeatherData {
    return { request ->
        // 실제 날씨 API 호출
        weatherService.getWeather(request.city)
    }
}
```

### RestTemplate

외부 API 호출을 위한 Spring의 HTTP 클라이언트입니다.

```kotlin
val response = restTemplate.getForObject(
    url,
    WeatherApiResponse::class.java
)
```

### TDD 테스트 작성

```kotlin
@SpringBootTest
class WeatherServiceTest {
    
    @Autowired
    lateinit var weatherService: WeatherService
    
    @Test
    fun `should return weather data`() {
        val result = weatherService.getWeather("서울")
        assertThat(result.city).isEqualTo("서울")
    }
}
```

## 🎓 학습 포인트

1. **Function Calling**: LLM이 자동으로 함수 호출
2. **외부 API 연동**: RestTemplate을 사용한 HTTP 통신
3. **TDD**: 테스트 먼저 작성하는 개발 방식
4. **Structured Output**: 함수 결과를 구조화된 형태로 반환
5. **AI 통합**: 실시간 데이터와 AI 답변 결합

## 💡 실전 활용 사례

### 1. 스마트 날씨 알림
- 출근 시간 날씨 알림
- 우산 필요 여부 판단
- 옷차림 추천

### 2. 여행 플래너
- 목적지 날씨 확인
- 여행 일정 추천
- 준비물 안내

### 3. 농업 지원
- 작물 관리 조언
- 관개 시기 추천
- 병충해 예측

## 🚀 다음 단계

- **17장**: AI 에이전트: 주문 관리 봇 (Multi-turn Function Calling)

## 📚 참고 자료

- [Spring AI Function Calling](https://docs.spring.io/spring-ai/reference/api/functions.html)
- [OpenWeatherMap API](https://openweathermap.org/api)
- [TDD by Example](https://www.amazon.com/Test-Driven-Development-Kent-Beck/dp/0321146530)

## 💡 팁

> [!TIP]
> **Function Description**: @Description 어노테이션으로 함수 설명을 명확히 작성하면 LLM이 더 정확하게 함수를 선택합니다.

> [!TIP]
> **TDD 실천**: 테스트를 먼저 작성하면 API 설계가 더 명확해집니다.

> [!WARNING]
> **API 키 보안**: 환경 변수로 API 키를 관리하고, 절대 코드에 하드코딩하지 마세요.

## 🔧 고급 기능

### 1. 여러 함수 등록

```kotlin
@Bean
fun getWeather(): Function<WeatherRequest, WeatherData>

@Bean
fun getAirQuality(): Function<CityRequest, AirQualityData>

@Bean
fun getUVIndex(): Function<LocationRequest, UVData>
```

### 2. 조건부 함수 호출

```kotlin
OllamaOptions.builder()
    .withFunction("getWeather")
    .withFunction("getAirQuality")
    .build()
```

### 3. 캐싱

```kotlin
@Cacheable("weather")
fun getWeather(city: String): WeatherData {
    // API 호출
}
```

## 📊 테스트 커버리지

```bash
./gradlew test jacocoTestReport
```

**목표:**
- Line Coverage: > 80%
- Branch Coverage: > 70%
- Method Coverage: > 90%

## ✨ Best Practices

1. **테스트 먼저 작성**
   - RED → GREEN → REFACTOR
   - 명확한 테스트 케이스

2. **함수 설명 명확히**
   - @Description 활용
   - 파라미터 설명 포함

3. **에러 처리**
   - API 실패 시 대응
   - 타임아웃 설정
   - 재시도 로직

4. **모니터링**
   - 함수 호출 로깅
   - 성능 측정
   - 에러 추적
