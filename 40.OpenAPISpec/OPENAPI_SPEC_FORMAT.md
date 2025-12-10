# OpenAPI Spec 검색 결과 형식 개선

## 변경사항

검색 결과에 완벽한 OpenAPI spec YAML 형식을 포함하도록 수정했습니다.

### 1. SearchResult 모델 업데이트

`openApiSpec` 필드 추가:
```kotlin
data class SearchResult(
    // ... 기존 필드들 ...
    
    // Complete OpenAPI spec in YAML format
    val openApiSpec: String? = null
)
```

### 2. OpenAPISpecFormatter 서비스 생성

완벽한 OpenAPI YAML을 생성하는 전용 서비스:

**Path 검색 결과 예시:**
```yaml
openapi: 3.0.0
info:
  title: API Specification
  version: 1.0.0
paths:
  /pets:
    get:
      operationId: listPets
      summary: List all pets
      tags:
        - pets
      parameters:
        - name: limit
          in: query
          required: false
          schema:
            type: integer
      responses:
        '200':
          description: Response
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Pet'
components:
  schemas:
    Pet:
      type: object
      properties:
        id:
          type: integer
          format: int64
        name:
          type: string
      required:
        - id
        - name
```

**Component 검색 결과 예시:**
```yaml
openapi: 3.0.0
info:
  title: API Specification
  version: 1.0.0
components:
  schemas:
    Pet:
      type: object
      description: A pet in the store
      properties:
        id:
          type: integer
          format: int64
        name:
          type: string
        tag:
          type: string
      required:
        - id
        - name
```

### 3. 검색 결과 구조

이제 검색 API 응답에 다음이 포함됩니다:

```json
{
  "type": "path",
  "method": "GET",
  "path": "/pets",
  "operationId": "listPets",
  "summary": "List all pets",
  "tags": ["pets"],
  "parameters": ["limit:integer:query:false"],
  "responseSchemas": {"200": "Pet[]"},
  "relatedSchemas": [
    {
      "name": "Pet",
      "properties": ["id:integer:int64:true", "name:string::true"],
      "required": ["id", "name"]
    }
  ],
  "openApiSpec": "openapi: 3.0.0\ninfo:\n  title: API Specification\n..."
}
```

## 사용 방법

### 1. petstore.yaml 업로드

```bash
curl -X POST http://localhost:8090/api/spec/upload-content \
  -H "Content-Type: text/plain" \
  --data-binary @src/main/resources/petstore.yaml
```

### 2. 검색 (완벽한 OpenAPI spec 포함)

```bash
curl -X POST http://localhost:8090/api/spec/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "How do I get a list of pets?",
    "topK": 3,
    "includeRelatedSchemas": true
  }'
```

### 3. 응답에서 OpenAPI spec 추출

응답의 `openApiSpec` 필드에 완벽한 OpenAPI YAML이 포함되어 있습니다:

```javascript
const result = response[0];
const openApiYaml = result.openApiSpec;

// 이 YAML을 파일로 저장하거나 직접 사용 가능
fs.writeFileSync('api-spec.yaml', openApiYaml);
```

## 장점

1. **완벽한 OpenAPI 형식**: 검색 결과를 바로 OpenAPI 도구에서 사용 가능
2. **자동 스키마 포함**: Path 검색 시 관련 Component 스키마가 자동으로 포함
3. **표준 준수**: OpenAPI 3.0.0 표준 형식 준수
4. **재사용 가능**: 검색 결과를 다른 API 도구에 바로 import 가능

## 테스트

`test.http` 파일에서 다음 엔드포인트 테스트:

```http
### Search with OpenAPI Spec
POST http://localhost:8090/api/spec/search
Content-Type: application/json

{
  "query": "How to get pets",
  "topK": 1,
  "includeRelatedSchemas": true
}
```

응답의 `openApiSpec` 필드를 확인하세요!
