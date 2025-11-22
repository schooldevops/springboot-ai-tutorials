# 12장: [실전] 이력서 분석 및 JSON 추출기

## 📚 학습 목표

4장의 **BeanOutputParser**와 **Kotlin data class**를 활용하여, 비정형 텍스트(이력서)에서 이름, 경력, 스킬 등을 구조화된 JSON으로 추출하는 API를 구현합니다.

## 🔑 핵심 키워드

- `BeanOutputParser`
- `data class`
- 정형 데이터 추출
- REST API
- JSON 파싱
- 구조화된 출력

## 📖 개요

이 장에서는 Spring AI의 BeanOutputParser를 사용하여 비정형 텍스트 데이터를 구조화된 Kotlin data class로 변환하는 실전 애플리케이션을 구축합니다. 이력서 텍스트를 입력받아 이름, 연락처, 경력, 학력, 스킬 등을 자동으로 추출하고 JSON 형태로 반환하는 API를 만들어봅니다.

## 🎯 구현할 기능

### 1. 기본 이력서 파싱
- 이력서 텍스트에서 기본 정보 추출
- 이름, 이메일, 전화번호, 주소 파싱

### 2. 상세 이력서 분석
- 경력 사항 추출 (회사명, 직책, 기간, 업무 내용)
- 학력 정보 추출 (학교명, 전공, 학위, 졸업년도)
- 스킬 및 기술 스택 추출

### 3. 스킬 분석
- 기술 스택 카테고리별 분류
- 숙련도 수준 파악

## 🏗️ 프로젝트 구조

```
12.AnalysysResumeAndJson/
├── README.md                          # 이 문서
├── QUICKSTART.md                      # 빠른 시작 가이드
└── sample/                            # 샘플 프로젝트
    ├── build.gradle.kts               # Gradle 빌드 설정
    ├── settings.gradle.kts            # Gradle 설정
    ├── test-requests.http             # HTTP 테스트 요청
    ├── sample-resumes.md              # 샘플 이력서 데이터
    └── src/
        └── main/
            ├── kotlin/com/example/resumeanalyzer/
            │   ├── ResumeAnalyzerApplication.kt  # 메인 애플리케이션
            │   ├── controller/
            │   │   └── ResumeAnalyzerController.kt  # REST 컨트롤러
            │   ├── model/
            │   │   └── ResumeInfo.kt             # 이력서 데이터 모델
            │   ├── dto/
            │   │   └── ResumeRequest.kt          # 요청 DTO
            │   └── util/
            │       └── BeanOutputParser.kt       # 파서 유틸리티
            └── resources/
                └── application.yml                # 설정 파일
```

## 💻 구현 상세

### 1. 데이터 모델 설계 (ResumeInfo.kt)

```kotlin
// 경력 정보
data class WorkExperience(
    val company: String,
    val position: String,
    val startDate: String,
    val endDate: String?,
    val description: String?
)

// 학력 정보
data class Education(
    val school: String,
    val degree: String,
    val major: String,
    val graduationYear: Int?
)

// 스킬 정보
data class Skill(
    val name: String,
    val category: String,
    val proficiency: String?
)

// 전체 이력서 정보
data class ResumeInfo(
    val name: String,
    val email: String?,
    val phone: String?,
    val address: String?,
    val summary: String?,
    val workExperiences: List<WorkExperience>,
    val educations: List<Education>,
    val skills: List<Skill>
)
```

### 2. BeanOutputParser 유틸리티

```kotlin
class BeanOutputParser<T>(private val clazz: Class<T>) {
    private val objectMapper = ObjectMapper().apply {
        registerKotlinModule()
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
    
    val format: String
        get() = generateJsonSchema(clazz)
    
    fun parse(text: String): T {
        val cleanedJson = cleanJsonResponse(text)
        return objectMapper.readValue(cleanedJson, clazz)
    }
    
    private fun cleanJsonResponse(text: String): String {
        // JSON 코드 블록 제거
        var cleaned = text.trim()
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.removePrefix("```json").removeSuffix("```").trim()
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.removePrefix("```").removeSuffix("```").trim()
        }
        return cleaned
    }
    
    private fun generateJsonSchema(clazz: Class<*>): String {
        // 간단한 JSON 스키마 생성
        return """
        JSON 형식으로 응답해주세요.
        클래스: ${clazz.simpleName}
        """.trimIndent()
    }
}
```

### 3. REST Controller

```kotlin
@RestController
@RequestMapping("/api/resume")
class ResumeAnalyzerController(
    private val chatModel: ChatModel
) {
    
    @PostMapping("/analyze")
    fun analyzeResume(@RequestBody request: ResumeRequest): ResumeInfo {
        val parser = BeanOutputParser(ResumeInfo::class.java)
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    당신은 이력서 분석 전문가입니다.
                    주어진 이력서 텍스트를 분석하여 다음 JSON 형식으로 정보를 추출해주세요:
                    
                    $format
                    
                    - 정보가 없는 필드는 null로 설정
                    - 날짜는 "YYYY-MM" 형식 사용
                    - 현재 재직중이면 endDate는 null
                    - 반드시 유효한 JSON 형식으로 응답
                    """.trimIndent()
                ),
                UserMessage(request.resumeText)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.result.output.content
        
        return parser.parse(text)
    }
    
    @PostMapping("/extract-skills")
    fun extractSkills(@RequestBody request: ResumeRequest): List<Skill> {
        val parser = BeanOutputParser(Array<Skill>::class.java)
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    이력서에서 기술 스택과 스킬을 추출하여 JSON 배열로 반환해주세요.
                    각 스킬은 다음 형식을 따릅니다:
                    {
                      "name": "스킬명",
                      "category": "카테고리 (예: Backend, Frontend, Database, DevOps)",
                      "proficiency": "숙련도 (Beginner/Intermediate/Advanced/Expert)"
                    }
                    """.trimIndent()
                ),
                UserMessage(request.resumeText)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.result.output.content
        
        return parser.parse(text).toList()
    }
}
```

## 🧪 테스트 방법

### 1. 애플리케이션 실행

```bash
cd sample

# OpenAI API 키 설정
export OPENAI_API_KEY=your-api-key-here

# 애플리케이션 실행
./gradlew bootRun
```

### 2. 샘플 이력서로 테스트

#### 전체 이력서 분석
```bash
curl -X POST http://localhost:9000/api/resume/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "resumeText": "홍길동\n이메일: hong@example.com\n전화: 010-1234-5678\n\n경력:\n- ABC 회사, 백엔드 개발자, 2020-01 ~ 2023-12\n  Spring Boot와 Kotlin을 사용한 REST API 개발\n\n학력:\n- 서울대학교, 컴퓨터공학과, 학사, 2019년 졸업\n\n기술:\nKotlin, Spring Boot, PostgreSQL, Docker"
  }'
```

**예상 응답:**
```json
{
  "name": "홍길동",
  "email": "hong@example.com",
  "phone": "010-1234-5678",
  "address": null,
  "summary": null,
  "workExperiences": [
    {
      "company": "ABC 회사",
      "position": "백엔드 개발자",
      "startDate": "2020-01",
      "endDate": "2023-12",
      "description": "Spring Boot와 Kotlin을 사용한 REST API 개발"
    }
  ],
  "educations": [
    {
      "school": "서울대학교",
      "degree": "학사",
      "major": "컴퓨터공학과",
      "graduationYear": 2019
    }
  ],
  "skills": [
    {
      "name": "Kotlin",
      "category": "Backend",
      "proficiency": "Advanced"
    },
    {
      "name": "Spring Boot",
      "category": "Backend",
      "proficiency": "Advanced"
    },
    {
      "name": "PostgreSQL",
      "category": "Database",
      "proficiency": "Intermediate"
    },
    {
      "name": "Docker",
      "category": "DevOps",
      "proficiency": "Intermediate"
    }
  ]
}
```

#### 스킬만 추출
```bash
curl -X POST http://localhost:9000/api/resume/extract-skills \
  -H "Content-Type: application/json" \
  -d '{
    "resumeText": "기술 스택: Java, Kotlin, Spring Boot, React, PostgreSQL, MongoDB, Docker, Kubernetes, AWS"
  }'
```

## 📝 주요 개념 설명

### BeanOutputParser

Spring AI의 BeanOutputParser는 LLM의 비정형 텍스트 응답을 Kotlin/Java 객체로 자동 변환해주는 도구입니다.

**주요 기능:**
1. **스키마 생성**: data class 구조를 기반으로 JSON 스키마 자동 생성
2. **파싱**: LLM 응답을 객체로 자동 변환
3. **타입 안전성**: 컴파일 타임에 타입 체크

### Kotlin Data Class의 장점

```kotlin
data class ResumeInfo(
    val name: String,
    val email: String?,  // Nullable
    val skills: List<Skill> = emptyList()  // 기본값
)
```

- **간결성**: 자동으로 equals, hashCode, toString 생성
- **Null 안전성**: Nullable 타입 명시적 선언
- **불변성**: val 사용으로 불변 객체 생성
- **구조 분해**: `val (name, email) = resume` 가능

### JSON 응답 정제

LLM이 반환하는 응답은 종종 다음과 같은 형태입니다:

```
```json
{
  "name": "홍길동"
}
```
```

BeanOutputParser는 이러한 마크다운 코드 블록을 자동으로 제거하고 순수 JSON만 추출합니다.

## 🎓 학습 포인트

1. **BeanOutputParser 활용**: 비정형 텍스트를 구조화된 데이터로 변환
2. **복잡한 데이터 모델**: 중첩된 객체와 리스트 처리
3. **Nullable 처리**: Kotlin의 null 안전성 활용
4. **프롬프트 엔지니어링**: 정확한 JSON 추출을 위한 프롬프트 설계
5. **에러 처리**: JSON 파싱 실패 시 대응 방법

## 💡 실전 활용 사례

### 1. 채용 플랫폼
- 지원자 이력서 자동 파싱
- 구조화된 데이터로 검색 및 필터링
- 매칭 알고리즘 적용

### 2. HR 시스템
- 직원 정보 자동 입력
- 이력서 데이터베이스 구축
- 스킬 매트릭스 생성

### 3. 헤드헌팅 서비스
- 후보자 프로필 자동 생성
- 포지션 매칭
- 스킬 갭 분석

## 🚀 다음 단계

- **13장**: VectorStore를 활용한 시맨틱 문서 검색 API
- **14장**: RAG 패턴을 적용한 사내 위키 챗봇

## 📚 참고 자료

- [Spring AI 공식 문서](https://docs.spring.io/spring-ai/reference/)
- [Output Parsers](https://docs.spring.io/spring-ai/reference/api/output-parser.html)
- [Kotlin Data Classes](https://kotlinlang.org/docs/data-classes.html)

## 💡 팁

> [!TIP]
> **프롬프트 최적화**: BeanOutputParser를 사용할 때는 SystemMessage에서 명확한 JSON 스키마와 예시를 제공하면 파싱 성공률이 높아집니다.

> [!TIP]
> **Nullable 필드**: 이력서에서 모든 정보를 추출할 수 없으므로, data class의 필드는 가능한 nullable로 설계하세요.

> [!TIP]
> **검증 로직**: 파싱 후 필수 필드(이름, 연락처 등)가 있는지 검증하는 로직을 추가하세요.

> [!WARNING]
> **개인정보 보호**: 실제 서비스에서는 이력서 데이터를 외부 API로 전송하기 전에 개인정보 처리 동의를 받아야 합니다.

## 🔧 고급 기능

### 1. 에러 처리

```kotlin
@PostMapping("/analyze")
fun analyzeResume(@RequestBody request: ResumeRequest): ResponseEntity<*> {
    return try {
        val result = analyzeResumeInternal(request)
        ResponseEntity.ok(result)
    } catch (e: JsonProcessingException) {
        ResponseEntity.badRequest()
            .body(mapOf("error" to "JSON 파싱 실패", "message" to e.message))
    } catch (e: Exception) {
        ResponseEntity.internalServerError()
            .body(mapOf("error" to "이력서 분석 실패", "message" to e.message))
    }
}
```

### 2. 검증 로직

```kotlin
fun validateResumeInfo(resume: ResumeInfo): List<String> {
    val errors = mutableListOf<String>()
    
    if (resume.name.isBlank()) {
        errors.add("이름은 필수입니다")
    }
    
    if (resume.email == null && resume.phone == null) {
        errors.add("이메일 또는 전화번호 중 하나는 필수입니다")
    }
    
    return errors
}
```

### 3. 재시도 로직

```kotlin
fun parseWithRetry(text: String, maxRetries: Int = 3): ResumeInfo {
    repeat(maxRetries) { attempt ->
        try {
            return parser.parse(text)
        } catch (e: Exception) {
            if (attempt == maxRetries - 1) throw e
            // 재시도 전 대기
            Thread.sleep(1000)
        }
    }
    throw IllegalStateException("파싱 실패")
}
```
