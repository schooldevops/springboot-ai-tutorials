# BeanOutputParser와 Kotlin Data Class 샘플 프로젝트

이 프로젝트는 Spring AI에서 LLM 응답을 Kotlin Data Class로 자동 파싱하는 방법을 보여줍니다.

## 📁 프로젝트 구조

```
sample/
├── src/main/kotlin/com/example/springai/
│   ├── BeanParserApplication.kt         # 메인 애플리케이션
│   ├── controller/
│   │   ├── BasicParserController.kt     # 기본 파싱 예제
│   │   ├── AdvancedParserController.kt  # 고급 파싱 예제
│   │   ├── SafeParserController.kt      # 안전한 파싱 (에러 처리)
│   │   └── ServiceBasedParserController.kt # 서비스 기반 사용
│   ├── service/
│   │   └── ParsingService.kt             # 범용 파싱 서비스
│   ├── model/
│   │   ├── DataModels.kt                # Data Class 정의들
│   │   └── CommonModels.kt               # 공통 모델
│   └── util/
│       ├── BeanOutputParser.kt           # BeanOutputParser 구현
│       └── JsonCleaner.kt                # JSON 정리 유틸리티
└── src/main/resources/
    └── application.yml                   # 설정 파일
```

## 🚀 빠른 시작

### 1. 환경 변수 설정

```bash
export OPENAI_API_KEY="sk-your-api-key-here"
```

### 2. 실행

```bash
./gradlew bootRun
```

### 3. 테스트

#### 기본 파싱 예제

```bash
# 사용자 정보 파싱
curl -X POST http://localhost:8080/api/basic-parser/user-info \
  -H "Content-Type: application/json" \
  -d '{
    "question": "사용자 정보를 제공해주세요. 이름은 홍길동, 나이는 30, 이메일은 hong@example.com입니다."
  }'

# 프로필 정보 파싱 (Nullable 필드 포함)
curl -X POST http://localhost:8080/api/basic-parser/profile \
  -H "Content-Type: application/json" \
  -d '{
    "question": "홍길동에 대한 프로필 정보를 제공해주세요."
  }'
```

#### 고급 파싱 예제

```bash
# 제품 정보 파싱 (리스트 포함)
curl -X POST http://localhost:8080/api/advanced-parser/product \
  -H "Content-Type: application/json" \
  -d '{
    "description": "스마트폰 제품입니다. 가격은 100만원입니다. 카메라, 배터리, 디스플레이 기능이 있습니다."
  }'

# 회사 정보 파싱 (중첩 구조)
curl -X POST http://localhost:8080/api/advanced-parser/company \
  -H "Content-Type: application/json" \
  -d '{
    "question": "Spring Corp 회사 정보를 제공해주세요. 주소는 서울시 강남구 테헤란로, 우편번호는 06142, 국가는 한국입니다. 직원 수는 100명입니다."
  }'
```

#### 안전한 파싱 예제

```bash
# 에러 처리 포함
curl -X POST http://localhost:8080/api/safe-parser/parse \
  -H "Content-Type: application/json" \
  -d '{
    "question": "사용자 정보를 제공해주세요."
  }'
```

#### 서비스 기반 예제

```bash
# 서비스를 통한 사용자 정보 파싱
curl -X POST http://localhost:8080/api/service-parser/user \
  -H "Content-Type: application/json" \
  -d '{
    "question": "사용자 정보를 추출해주세요."
  }'

# 레시피 파싱
curl -X POST http://localhost:8080/api/service-parser/recipe \
  -H "Content-Type: application/json" \
  -d '{
    "question": "김치찌개 레시피를 제공해주세요."
  }'
```

## 📝 주요 예제 설명

### 1. BasicParserController

**기본 사용자 정보 파싱:**
- `/api/basic-parser/user-info`: UserInfo Data Class로 파싱

**프로필 정보 파싱:**
- `/api/basic-parser/profile`: Nullable 필드를 포함한 UserProfile 파싱

### 2. AdvancedParserController

**리스트 포함 파싱:**
- `/api/advanced-parser/product`: Product (features 리스트 포함)

**중첩 구조 파싱:**
- `/api/advanced-parser/company`: CompanyInfo (Address 중첩 객체 포함)

### 3. SafeParserController

**에러 처리:**
- `/api/safe-parser/parse`: 파싱 실패 시 에러 정보 반환

### 4. ParsingService

범용 파싱 서비스로 다양한 타입의 객체를 파싱할 수 있습니다:
- 제네릭 메서드로 재사용 가능
- JSON 정리 기능 포함

### 5. BeanOutputParser (직접 구현)

Spring AI 1.0.0-M6에서는 BeanOutputParser가 기본 포함되지 않을 수 있어 Jackson을 사용하여 직접 구현했습니다.

**주요 기능:**
- `.format`: JSON 스키마 생성
- `.parse()`: JSON 문자열을 객체로 파싱

## 💡 학습 포인트

이 샘플 프로젝트를 통해 학습할 수 있는 내용:

1. **BeanOutputParser 이해**
   - LLM 응답을 구조화된 객체로 파싱
   - JSON 스키마를 LLM에 전달하여 형식 지정

2. **Kotlin Data Class 활용**
   - 불변 데이터 클래스 정의
   - Nullable 필드 처리
   - 중첩 구조 처리

3. **에러 처리**
   - 파싱 실패 시 안전한 처리
   - JSON 정리 유틸리티

4. **서비스 레이어 분리**
   - 재사용 가능한 파싱 서비스
   - 제네릭을 활용한 범용 메서드

## 🔧 핵심 패턴

```kotlin
// 1. Parser 생성
val parser = BeanOutputParser(UserInfo::class.java)
val format = parser.format

// 2. Prompt 생성 (Format 포함)
val prompt = Prompt(
    listOf(
        SystemMessage("다음 형식으로 응답해주세요:\n$format"),
        UserMessage("사용자 정보를 제공해주세요")
    )
)

// 3. LLM 호출
val response = chatModel.call(prompt)
val text = response.results.firstOrNull()?.output?.text ?: ""

// 4. 파싱
val userInfo: UserInfo = parser.parse(text)
```

## 📚 참고사항

### BeanOutputParser 직접 구현

이 프로젝트에서는 BeanOutputParser를 직접 구현했습니다. Spring AI의 공식 BeanOutputParser를 사용하려면:

1. Spring AI Core 의존성 확인
2. 올바른 패키지 import 확인
3. 필요시 공식 문서 참조

### JSON 정리

LLM이 반환하는 JSON에 마크다운 코드 블록이 포함될 수 있습니다:
- ````json` 제거
- 주석 제거
- 불필요한 텍스트 제거

이 작업은 `JsonCleaner` 유틸리티가 자동으로 처리합니다.

---

**다음 학습**: [4.2: 리스트 및 맵 파싱](../../README.md#42-리스트-및-맵-파싱)

