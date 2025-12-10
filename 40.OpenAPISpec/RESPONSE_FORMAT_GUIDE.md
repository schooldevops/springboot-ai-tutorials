# Response Format 기능 가이드

## 개요

검색 API에 `responseFormat` 파라미터를 추가하여 두 가지 형식으로 결과를 받을 수 있습니다:

1. **`yaml`**: 원본 OpenAPI YAML 파일에서 검색된 부분을 추출하여 반환
2. **`meta`**: 벡터 검색 정보와 생성된 OpenAPI spec 반환 (기본값)

## API 사용법

### 검색 요청 형식

```json
{
  "query": "검색 쿼리",
  "topK": 5,
  "includeRelatedSchemas": true,
  "responseFormat": "yaml"  // "yaml" 또는 "meta"
}
```

### 1. YAML 형식 응답 (`responseFormat: "yaml"`)

원본 OpenAPI YAML 파일에서 검색된 부분을 정확하게 추출하여 **text/plain 형식**으로 반환합니다.

**요청 예시:**
```http
POST http://localhost:8090/api/spec/search
Content-Type: application/json

{
  "query": "How to get inventory",
  "topK": 2,
  "responseFormat": "yaml"
}
```

**응답 (Content-Type: text/plain):**
```yaml
openapi: 3.0.0
info:
  title: OpenAPI Petstore
  version: 1.0.0
paths:
  /store/inventory:
    get:
      operationId: getInventory
      summary: Returns pet inventories by status
      description: Returns a map of status codes to quantities
      tags:
      - store
      responses:
        '200':
          description: successful operation
          content:
            application/json:
              schema:
                type: object
                additionalProperties:
                  type: integer
                  format: int32
      security:
      - api_key: []
---
openapi: 3.0.0
info:
  title: OpenAPI Petstore
  version: 1.0.0
paths:
  /store/order:
    post:
      operationId: placeOrder
      summary: Place an order for a pet
      ...
```

**특징:**
- ✅ **실제 개행과 들여쓰기가 적용된 YAML** (이스케이프 없음)
- ✅ Content-Type: `text/plain`으로 반환
- ✅ 여러 결과는 `---`로 구분
- ✅ 원본 YAML 파일의 정확한 구조 유지
- ✅ 바로 `.yaml` 파일로 저장 가능
- ✅ 복사-붙여넣기로 즉시 사용 가능

### 2. Meta 형식 응답 (`responseFormat: "meta"`, 기본값)

벡터 검색 메타데이터와 생성된 OpenAPI spec을 반환합니다.

**요청 예시:**
```http
POST http://localhost:8090/api/spec/search
Content-Type: application/json

{
  "query": "How to get inventory",
  "topK": 1,
  "responseFormat": "meta"
}
```

**응답:**
```json
{
  "type": "path",
  "method": "GET",
  "path": "/store/inventory",
  "operationId": "getInventory",
  "summary": "Returns pet inventories by status",
  "tags": ["store"],
  "parameters": [],
  "responseSchemas": {"200": "object"},
  "relatedSchemas": [],
  "openApiSpec": "openapi: 3.0.0\ninfo:\n  title: API Specification\n  version: 1.0.0\npaths:\n  /store/inventory:\n    get:\n      operationId: getInventory\n      summary: Returns pet inventories by status\n      tags:\n        - store\n      responses:\n        '200':\n          description: Response\n          content:\n            application/json:\n              schema:\n                type: object\n"
}
```

**특징:**
- ✅ 벡터 검색 메타데이터 포함 (operationId, summary, tags 등)
- ✅ 구조화된 정보 제공
- ✅ 생성된 OpenAPI spec 포함
- ✅ 관련 스키마 정보 포함

## 비교

| 항목 | YAML 형식 | Meta 형식 |
|------|-----------|-----------|
| 응답 타입 | `text/plain` | `application/json` |
| 응답 형식 | 순수 YAML 텍스트 | `List<SearchResult>` JSON |
| 개행/들여쓰기 | ✅ 실제 적용 | N/A |
| 원본 유지 | ✅ 원본 그대로 | ❌ 생성된 spec |
| 메타데이터 | ❌ 없음 | ✅ 상세 정보 |
| 재사용성 | ✅ 바로 사용 가능 | ⚠️ 참고용 |
| 정확성 | ✅ 100% 정확 | ⚠️ 생성된 내용 |
| 파일 저장 | ✅ 즉시 가능 | ❌ 가공 필요 |

## 사용 시나리오

### YAML 형식을 사용해야 할 때:
- 원본 OpenAPI spec의 정확한 내용이 필요할 때
- 검색 결과를 다른 도구에 바로 import해야 할 때
- 원본 문서의 주석이나 설명이 중요할 때

### Meta 형식을 사용해야 할 때:
- 검색 결과의 메타데이터가 필요할 때
- 구조화된 정보로 분석해야 할 때
- 벡터 검색 점수나 관련 정보가 필요할 때

## 테스트 예시

### test.http 파일

```http
### 1. Upload OpenAPI Spec
POST http://localhost:8090/api/spec/upload-content
Content-Type: text/plain

< ./src/main/resources/petstore.yaml

### 2. Search with YAML format
POST http://localhost:8090/api/spec/search
Content-Type: application/json

{
  "query": "How to get pets",
  "topK": 1,
  "responseFormat": "yaml"
}

### 3. Search with Meta format (default)
POST http://localhost:8090/api/spec/search
Content-Type: application/json

{
  "query": "How to get pets",
  "topK": 1,
  "responseFormat": "meta"
}

### 4. Search without responseFormat (defaults to meta)
POST http://localhost:8090/api/spec/search
Content-Type: application/json

{
  "query": "How to get pets",
  "topK": 1
}
```

## 구현 세부사항

### YamlExtractor 서비스
- 원본 YAML에서 특정 path 또는 component 추출
- 참조된 스키마 자동 포함
- SnakeYAML 라이브러리 사용

### SpecDocumentService
- 원본 YAML 저장 (`originalYaml` 필드)
- `storeSpec()` 메서드에 `yamlContent` 파라미터 추가

### SpecSearchService
- `responseFormat` 파라미터 추가
- `extractOriginalYamlFragments()` 메서드로 원본 추출
- 조건에 따라 yaml 또는 meta 형식 반환

## 주의사항

1. **원본 YAML 필요**: `yaml` 형식을 사용하려면 반드시 spec을 업로드해야 합니다
2. **메모리 사용**: 원본 YAML이 메모리에 저장되므로 큰 파일은 주의
3. **단일 Spec**: 현재는 하나의 OpenAPI spec만 저장 가능 (마지막 업로드가 유지됨)
