# 리스트 및 맵 파싱 샘플 프로젝트

이 프로젝트는 Spring AI에서 ListOutputParser와 MapOutputParser를 사용하여 LLM 응답을 리스트 및 맵 형식으로 파싱하는 방법을 보여줍니다.

## 📁 프로젝트 구조

```
sample/
├── src/main/kotlin/com/example/springai/
│   ├── ListMapParserApplication.kt       # 메인 애플리케이션
│   ├── controller/
│   │   ├── ListParserController.kt      # 리스트 파싱 예제
│   │   ├── MapParserController.kt       # 맵 파싱 예제
│   │   └── ComplexParserController.kt    # 복합 형식 파싱 예제
│   ├── util/
│   │   ├── ListOutputParser.kt           # ListOutputParser 구현
│   │   └── MapOutputParser.kt            # MapOutputParser 구현
│   └── model/
│       └── CommonModels.kt                # 공통 모델
└── src/main/resources/
    └── application.yml                     # 설정 파일
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

#### 리스트 파싱 예제

```bash
# 기본 리스트 파싱
curl -X POST http://localhost:8080/api/list-parser/parse \
  -H "Content-Type: application/json" \
  -d '{
    "question": "5가지 프로그래밍 언어를 나열해주세요"
  }'

# CSV 형식 파싱
curl -X POST http://localhost:8080/api/list-parser/csv \
  -H "Content-Type: application/json" \
  -d '{
    "question": "5가지 프레임워크를 쉼표로 구분하여 나열해주세요"
  }'

# 안전한 파싱 (에러 처리)
curl -X POST http://localhost:8080/api/list-parser/safe \
  -H "Content-Type: application/json" \
  -d '{
    "question": "3가지 데이터베이스를 나열해주세요"
  }'
```

#### 맵 파싱 예제

```bash
# 기본 맵 파싱
curl -X POST http://localhost:8080/api/map-parser/parse \
  -H "Content-Type: application/json" \
  -d '{
    "question": "프로그래밍 언어별 특징을 Key-Value 형식으로 제공해주세요"
  }'

# 안전한 맵 파싱
curl -X POST http://localhost:8080/api/map-parser/safe \
  -H "Content-Type: application/json" \
  -d '{
    "question": "프레임워크별 장점을 Key-Value 형식으로 제공해주세요"
  }'
```

#### 복합 형식 파싱 예제

```bash
# 리스트와 맵 조합
curl -X POST http://localhost:8080/api/complex-parser/combined \
  -H "Content-Type: application/json" \
  -d '{
    "question": "프로그래밍 언어 카테고리를 나열하고, 각 언어의 특징을 설명해주세요"
  }'

# 구조화된 카테고리별 데이터
curl -X POST http://localhost:8080/api/complex-parser/structured \
  -H "Content-Type: application/json" \
  -d '{
    "question": "프론트엔드와 백엔드 카테고리별로 프레임워크를 나열해주세요"
  }'
```

## 📝 주요 예제 설명

### 1. ListParserController

**기본 리스트 파싱:**
- `/api/list-parser/parse`: 줄바꿈으로 구분된 리스트 파싱

**CSV 형식 파싱:**
- `/api/list-parser/csv`: 쉼표로 구분된 리스트 파싱

**안전한 파싱:**
- `/api/list-parser/safe`: 에러 처리 포함

### 2. MapParserController

**기본 맵 파싱:**
- `/api/map-parser/parse`: Key-Value 형식 파싱

**안전한 파싱:**
- `/api/map-parser/safe`: 에러 처리 포함

### 3. ComplexParserController

**복합 형식 파싱:**
- `/api/complex-parser/combined`: 리스트와 맵을 함께 파싱
- `/api/complex-parser/structured`: 구조화된 카테고리별 데이터 파싱

## 💡 학습 포인트

이 샘플 프로젝트를 통해 학습할 수 있는 내용:

1. **ListOutputParser 이해**
   - LLM 응답을 리스트로 자동 파싱
   - 다양한 구분자 지원 (줄바꿈, 쉼표 등)

2. **MapOutputParser 이해**
   - LLM 응답을 Key-Value 맵으로 파싱
   - 구조화된 데이터 추출

3. **CSV 형식 처리**
   - 쉼표로 구분된 값 파싱
   - 실제 데이터 처리 시나리오

4. **복합 형식 파싱**
   - 리스트와 맵 조합
   - 구조화된 데이터 처리

## 🔧 핵심 패턴

```kotlin
// ListOutputParser
val parser = ListOutputParser()
val format = parser.format

val prompt = Prompt(
    listOf(
        SystemMessage("다음 형식으로 응답해주세요:\n$format"),
        UserMessage("항목을 나열해주세요")
    )
)

val response = chatModel.call(prompt)
val text = response.results.firstOrNull()?.output?.text ?: ""
val items: List<String> = parser.parse(text)

// MapOutputParser
val mapParser = MapOutputParser()
val format = mapParser.format

val prompt = Prompt(
    listOf(
        SystemMessage("다음 형식으로 응답해주세요:\n$format"),
        UserMessage("Key-Value 쌍을 제공해주세요")
    )
)

val response = chatModel.call(prompt)
val text = response.results.firstOrNull()?.output?.text ?: ""
val map: Map<String, String> = mapParser.parse(text)
```

## 📚 참고사항

### ListOutputParser와 MapOutputParser 직접 구현

이 프로젝트에서는 ListOutputParser와 MapOutputParser를 직접 구현했습니다. Spring AI의 공식 파서를 사용하려면:

1. Spring AI Core 의존성 확인
2. 올바른 패키지 import 확인
3. 필요시 공식 문서 참조

### 구분자 선택

- **줄바꿈 (`\n`)**: 기본 구분자, 가독성 좋음
- **쉼표 (`,`)**: CSV 형식에 적합
- **세미콜론 (`;`)**: 항목에 쉼표 포함 가능

---

**다음 학습**: [5.1: 임베딩의 개념과 EmbeddingClient](../../README.md#51-임베딩의-개념과-embeddingclient)

