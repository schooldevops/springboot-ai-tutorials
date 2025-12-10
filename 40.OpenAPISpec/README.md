# OpenAPI Spec Parser with VectorDB

Spring AI와 Kotlin을 활용하여 OpenAPI 명세를 파싱하고 VectorDB에 저장하여 자연어 검색을 가능하게 하는 애플리케이션입니다.

## 📚 개요

이 애플리케이션은 OpenAPI 명세 파일(YAML/JSON)을 파싱하여 API 경로(paths)와 컴포넌트 스키마(components)를 분해하고, 각각을 VectorDB에 저장합니다. 자연어 쿼리를 통해 관련 API 엔드포인트와 스키마 정보를 함께 검색할 수 있습니다.

## 🎯 주요 기능

### 1. OpenAPI 명세 파싱
- **swagger-parser** 라이브러리를 사용한 강력한 파싱
- Paths, Operations, Parameters 추출
- Component Schemas, Properties 추출
- 메타데이터 자동 추출

### 2. VectorDB 저장
- **경로별 분해**: 각 API 경로 operation을 개별 Document로 저장
- **컴포넌트별 분해**: 각 스키마를 개별 Document로 저장
- **풍부한 메타데이터**: method, path, parameters, schemas 등
- **자동 링크**: 경로와 관련 스키마 자동 연결

### 3. 자연어 검색
- **의미 기반 검색**: 자연어 쿼리로 API 검색
- **통합 결과**: 경로와 관련 스키마를 함께 반환
- **컨텍스트 제공**: 완전한 API 사용 정보 제공

## 🏗️ 아키텍처

```
OpenAPI YAML/JSON
        ↓
OpenAPISpecParser (swagger-parser)
        ↓
ParsedSpec (paths + components)
        ↓
SpecDocumentService
        ↓
VectorDB Documents (with metadata)
        ↓
SpecSearchService (natural language)
        ↓
SearchResults (paths + related schemas)
```

## 📊 VectorDB 스키마 설계

### Path Documents

각 API operation은 하나의 Document로 저장됩니다:

```kotlin
Document(
    content = "GET /pets - List all pets. Parameters: limit (query, integer). Responses: 200 (Pet[])",
    metadata = mapOf(
        "type" to "path",
        "method" to "GET",
        "path" to "/pets",
        "operationId" to "listPets",
        "tags" to "pets",
        "parameters" to "limit:integer:query:false",
        "responseSchemas" to "200:Pet[]"
    )
)
```

### Component Documents

각 스키마는 하나의 Document로 저장됩니다:

```kotlin
Document(
    content = "Pet schema. Properties: id: integer, int64 (required), name: string (required), tag: string",
    metadata = mapOf(
        "type" to "component",
        "componentType" to "schema",
        "name" to "Pet",
        "properties" to "id:integer:int64:true;name:string::true;tag:string::false",
        "required" to "id,name"
    )
)
```

## 🚀 사용 방법

### 1. OpenAPI 명세 업로드

```http
POST /api/spec/upload
Content-Type: multipart/form-data

file: petstore.yaml
```

### 2. 자연어 검색

```http
POST /api/spec/search
Content-Type: application/json

{
  "query": "How do I get a list of pets?",
  "topK": 5,
  "includeRelatedSchemas": true
}
```

**응답 예시:**
```json
[
  {
    "type": "path",
    "method": "GET",
    "path": "/pets",
    "operationId": "listPets",
    "summary": "List all pets",
    "parameters": ["limit:integer:query:false"],
    "responseSchemas": {"200": "Pet[]"},
    "relatedSchemas": [
      {
        "name": "Pet",
        "properties": ["id:integer:int64:true", "name:string::true"],
        "required": ["id", "name"]
      }
    ]
  }
]
```

### 3. 통계 조회

```http
GET /api/spec/stats
```

## 🧪 테스트

### 전체 테스트 실행

```bash
./gradlew clean test
```

### 테스트 커버리지

- **OpenAPISpecParser**: 8개 테스트
  - 명세 파싱, 경로 추출, 파라미터 추출, 스키마 추출
- **SpecDocumentService**: 6개 테스트
  - Document 생성, 메타데이터 검증, 저장/조회
- **SpecSearchService**: 6개 테스트
  - 자연어 검색, 스키마 링크, 파라미터 검색

**총 20개 테스트** - 모두 TDD 방식으로 작성

## 📁 프로젝트 구조

```
40.OpenAPISpec/
├── src/main/kotlin/com/example/openapi/
│   ├── parser/
│   │   └── OpenAPISpecParser.kt      # OpenAPI 파싱
│   ├── service/
│   │   ├── SpecDocumentService.kt    # VectorDB 저장
│   │   └── SpecSearchService.kt      # 자연어 검색
│   ├── controller/
│   │   └── SpecController.kt         # REST API
│   └── model/
│       ├── ParsedSpec.kt             # 파싱 결과 모델
│       └── SearchResult.kt           # 검색 결과 모델
├── src/test/kotlin/                  # TDD 테스트
├── src/main/resources/
│   ├── application.yml
│   └── petstore.yaml                 # 샘플 OpenAPI 명세
└── test.http                         # HTTP 테스트 파일
```

## 💡 핵심 구현 사항

### 1. 경로와 컴포넌트 분해

모든 API operation과 스키마를 개별 Document로 분해하여 저장:
- 세밀한 검색 가능
- 정확한 매칭
- 효율적인 임베딩

### 2. 메타데이터 기반 링크

경로 Document의 메타데이터에 참조 스키마 정보 저장:
- `responseSchemas`: 응답 스키마 참조
- `requestSchema`: 요청 스키마 참조
- 검색 시 자동으로 관련 스키마 조회

### 3. 자연어 검색

VectorDB의 의미 기반 검색 활용:
- "How do I get pets?" → GET /pets 찾기
- "Pet model structure" → Pet 스키마 찾기
- 자동으로 관련 정보 결합

## 🔧 기술 스택

- **Spring AI**: 1.0.0-M4
- **Spring Boot**: 3.4.0
- **Kotlin**: 2.1.0
- **swagger-parser**: 2.1.20 (OpenAPI 파싱)
- **OpenAI Embeddings**: text-embedding-3-small
- **VectorDB**: SimpleVectorStore

## 📖 API 엔드포인트

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/spec/upload` | OpenAPI 명세 파일 업로드 |
| POST | `/api/spec/upload-content` | OpenAPI 명세 내용 업로드 |
| POST | `/api/spec/search` | 자연어 검색 |
| GET | `/api/spec/stats` | 저장된 명세 통계 |
| DELETE | `/api/spec/clear` | 모든 명세 삭제 |
| GET | `/api/spec/health` | 헬스 체크 |

## 🎓 학습 포인트

1. **OpenAPI 파싱**: swagger-parser를 사용한 명세 파싱
2. **Document 분해**: 큰 명세를 작은 검색 가능한 단위로 분해
3. **메타데이터 설계**: 효과적인 필터링과 링크를 위한 메타데이터
4. **자연어 검색**: VectorDB를 활용한 의미 기반 검색
5. **TDD 개발**: 테스트 우선 개발로 신뢰성 확보

## 🔍 검색 예시

### 예시 1: API 엔드포인트 찾기

**쿼리**: "How do I get a list of pets?"

**결과**: GET /pets 엔드포인트 + Pet 스키마 정보

### 예시 2: 스키마 구조 확인

**쿼리**: "What is the Pet model structure?"

**결과**: Pet 스키마의 모든 속성과 타입 정보

### 예시 3: 특정 기능 검색

**쿼리**: "Create a new pet"

**결과**: POST /pets 엔드포인트 + 요청/응답 스키마

## 🚦 실행 방법

```bash
# 애플리케이션 실행
./gradlew bootRun

# 테스트 실행
./gradlew test

# 빌드
./gradlew build
```

## 📝 샘플 데이터

프로젝트에 포함된 `petstore.yaml`은 OpenAPI 공식 예제입니다:
- 3개의 API 경로
- 2개의 컴포넌트 스키마 (Pet, Error)
- 다양한 파라미터와 응답 타입

---

**Note**: 이 애플리케이션은 TDD 방식으로 개발되었으며, 모든 핵심 기능에 대한 테스트가 포함되어 있습니다.
