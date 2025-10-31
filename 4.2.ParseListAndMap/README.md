# 4.2: 리스트 및 맵 파싱

## 📖 학습 목표

이 강의를 마친 후 다음을 달성할 수 있습니다:
- **ListOutputParser**를 사용하여 LLM 응답을 리스트 형식으로 파싱할 수 있습니다
- **MapOutputParser**를 사용하여 LLM 응답을 Key-Value 맵 형식으로 파싱할 수 있습니다
- **CSV 형식**의 응답을 파싱하여 리스트로 변환할 수 있습니다
- **복합 형식** (리스트와 맵 조합)을 파싱할 수 있습니다
- **실제 사용 예제**를 통해 ListOutputParser와 MapOutputParser를 활용할 수 있습니다

---

## 🔑 핵심 키워드

이 장에서 다루는 핵심 키워드들:

1. **ListOutputParser** - LLM 응답을 리스트로 파싱하는 파서
2. **MapOutputParser** - LLM 응답을 Key-Value 맵으로 파싱하는 파서
3. **CSV** - 쉼표로 구분된 값 형식
4. **Map** - Key-Value 쌍의 데이터 구조
5. **구조화된 파싱** - 일정한 형식의 데이터를 자동으로 파싱

---

## 1. ListOutputParser란?

### 1.1 ListOutputParser의 필요성

#### 문제: 비정형 리스트 응답의 한계

```kotlin
// ❌ 문제: LLM 응답이 일관되지 않은 형식
val prompt = Prompt(UserMessage("5가지 프로그래밍 언어를 나열해주세요"))
val response = chatModel.call(prompt)
val text = response.results.firstOrNull()?.output?.text ?: ""

// 문제점:
// - 수동으로 리스트 추출 필요
// - 형식이 일관되지 않을 수 있음
// - 파싱 로직 복잡
```

#### 해결: ListOutputParser 사용

```kotlin
// ✅ 해결: 자동으로 리스트로 파싱
val parser = ListOutputParser()
val format = parser.format

val prompt = Prompt(
    listOf(
        SystemMessage("다음 형식으로 응답해주세요:\n$format"),
        UserMessage("5가지 프로그래밍 언어를 나열해주세요")
    )
)

val response = chatModel.call(prompt)
val text = response.results.firstOrNull()?.output?.text ?: ""
val languages: List<String> = parser.parse(text)

// 장점:
// - 자동 파싱
// - 일관된 형식
// - 간단한 사용
```

### 1.2 ListOutputParser의 정의

**ListOutputParser**는 LLM의 텍스트 응답을 리스트로 자동 파싱하는 파서입니다.

**주요 특징:**
- **자동 파싱**: 텍스트를 리스트로 변환
- **형식 지시**: `.format`으로 LLM에 응답 형식 요구
- **유연성**: 다양한 구분자 지원 (줄바꿈, 쉼표 등)

---

## 2. MapOutputParser란?

### 2.1 MapOutputParser의 필요성

#### 문제: Key-Value 형식 응답 파싱의 어려움

```kotlin
// ❌ 문제: 수동으로 Key-Value 추출 필요
val prompt = Prompt(UserMessage("언어별 특징을 Key-Value 형식으로 나열해주세요"))
val response = chatModel.call(prompt)
val text = response.results.firstOrNull()?.output?.text ?: ""

// 문제점:
// - 수동 파싱 필요
// - 형식 일관성 보장 어려움
// - 에러 처리 복잡
```

#### 해결: MapOutputParser 사용

```kotlin
// ✅ 해결: 자동으로 Map으로 파싱
val parser = MapOutputParser()
val format = parser.format

val prompt = Prompt(
    listOf(
        SystemMessage("다음 형식으로 응답해주세요:\n$format"),
        UserMessage("언어별 특징을 제공해주세요")
    )
)

val response = chatModel.call(prompt)
val text = response.results.firstOrNull()?.output?.text ?: ""
val languageFeatures: Map<String, String> = parser.parse(text)
```

### 2.2 MapOutputParser의 정의

**MapOutputParser**는 LLM의 텍스트 응답을 Key-Value 맵으로 자동 파싱하는 파서입니다.

**주요 특징:**
- **자동 파싱**: 텍스트를 Map으로 변환
- **형식 지시**: `.format`으로 LLM에 응답 형식 요구
- **Key-Value 구조**: 명확한 구조화된 데이터

---

## 3. ListOutputParser 기본 사용법

### 3.1 단계별 예제

#### 1단계: ListOutputParser 생성

```kotlin
import com.example.springai.util.ListOutputParser

val parser = ListOutputParser()
```

#### 2단계: Format 문자열 가져오기

```kotlin
val format = parser.format  // LLM에 전달할 형식 설명
```

#### 3단계: Prompt에 Format 포함

```kotlin
val prompt = Prompt(
    listOf(
        SystemMessage(
            """
            다음 형식으로 응답해주세요:
            $format
            
            각 항목은 줄바꿈으로 구분해주세요.
            """.trimIndent()
        ),
        UserMessage("5가지 프로그래밍 언어를 나열해주세요")
    )
)
```

#### 4단계: LLM 호출 및 파싱

```kotlin
val response = chatModel.call(prompt)
val text = response.results.firstOrNull()?.output?.text ?: ""
val items: List<String> = parser.parse(text)
```

### 3.2 전체 코드 예제

```kotlin
@RestController
class ListParserController(
    private val chatModel: ChatModel
) {
    @PostMapping("/parse-list")
    fun parseList(@RequestBody request: ParseRequest): Map<String, List<String>> {
        val parser = ListOutputParser()
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    다음 형식으로 응답해주세요:
                    $format
                    """.trimIndent()
                ),
                UserMessage(request.question)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.results.firstOrNull()?.output?.text 
            ?: throw IllegalStateException("응답 없음")
        
        val items = parser.parse(text)
        
        return mapOf(
            "items" to items,
            "count" to items.size
        )
    }
}
```

---

## 4. MapOutputParser 기본 사용법

### 4.1 단계별 예제

#### 1단계: MapOutputParser 생성

```kotlin
import com.example.springai.util.MapOutputParser

val parser = MapOutputParser()
```

#### 2단계: Format 문자열 가져오기

```kotlin
val format = parser.format
```

#### 3단계: Prompt에 Format 포함

```kotlin
val prompt = Prompt(
    listOf(
        SystemMessage(
            """
            다음 형식으로 응답해주세요:
            $format
            """.trimIndent()
        ),
        UserMessage("언어별 특징을 제공해주세요")
    )
)
```

#### 4단계: LLM 호출 및 파싱

```kotlin
val response = chatModel.call(prompt)
val text = response.results.firstOrNull()?.output?.text ?: ""
val map: Map<String, String> = parser.parse(text)
```

### 4.2 전체 코드 예제

```kotlin
@RestController
class MapParserController(
    private val chatModel: ChatModel
) {
    @PostMapping("/parse-map")
    fun parseMap(@RequestBody request: ParseRequest): Map<String, Any> {
        val parser = MapOutputParser()
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    다음 형식으로 응답해주세요:
                    $format
                    """.trimIndent()
                ),
                UserMessage(request.question)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.results.firstOrNull()?.output?.text 
            ?: throw IllegalStateException("응답 없음")
        
        val resultMap = parser.parse(text)
        
        return mapOf(
            "data" to resultMap,
            "count" to resultMap.size
        )
    }
}
```

---

## 5. 실전 활용 예제

### 5.1 CSV 형식 파싱

```kotlin
@RestController
class CsvParserController(
    private val chatModel: ChatModel
) {
    @PostMapping("/parse-csv")
    fun parseCsv(@RequestBody request: ParseRequest): Map<String, List<String>> {
        val parser = ListOutputParser(separator = ",")
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    다음 CSV 형식으로 응답해주세요:
                    $format
                    
                    각 항목은 쉼표(,)로 구분해주세요.
                    """.trimIndent()
                ),
                UserMessage(request.question)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.results.firstOrNull()?.output?.text 
            ?: throw IllegalStateException("응답 없음")
        
        val items = parser.parse(text)
        
        return mapOf(
            "items" to items,
            "count" to items.size
        )
    }
}
```

### 5.2 복합 형식 파싱 (리스트와 맵 조합)

```kotlin
@RestController
class ComplexParserController(
    private val chatModel: ChatModel
) {
    @PostMapping("/parse-complex")
    fun parseComplex(@RequestBody request: ParseRequest): Map<String, Any> {
        // 1. 리스트 파싱
        val listParser = ListOutputParser()
        val listFormat = listParser.format
        
        // 2. 맵 파싱
        val mapParser = MapOutputParser()
        val mapFormat = mapParser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    다음 형식으로 응답해주세요:
                    
                    1. 카테고리 목록 ($listFormat):
                    [카테고리 목록]
                    
                    2. 카테고리별 세부사항 ($mapFormat):
                    [카테고리별 세부사항]
                    """.trimIndent()
                ),
                UserMessage(request.question)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.results.firstOrNull()?.output?.text 
            ?: throw IllegalStateException("응답 없음")
        
        // 텍스트를 두 부분으로 분리하여 파싱
        val parts = text.split("\n\n")
        val categories = if (parts.isNotEmpty()) {
            listParser.parse(parts[0])
        } else {
            emptyList()
        }
        
        val details = if (parts.size > 1) {
            mapParser.parse(parts[1])
        } else {
            emptyMap()
        }
        
        return mapOf(
            "categories" to categories,
            "details" to details
        )
    }
}
```

### 5.3 구조화된 데이터 추출

```kotlin
data class CategoryItem(
    val name: String,
    val items: List<String>
)

@RestController
class StructuredParserController(
    private val chatModel: ChatModel
) {
    @PostMapping("/parse-structured")
    fun parseStructured(@RequestBody request: ParseRequest): List<CategoryItem> {
        // 각 카테고리별로 리스트 파싱
        val parser = ListOutputParser()
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    카테고리별로 항목을 나열해주세요.
                    각 카테고리는 다음과 같은 형식으로:
                    
                    카테고리명:
                    $format
                    """.trimIndent()
                ),
                UserMessage(request.question)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.results.firstOrNull()?.output?.text 
            ?: throw IllegalStateException("응답 없음")
        
        // 카테고리별로 분리하여 파싱
        val categories = mutableListOf<CategoryItem>()
        val lines = text.lines()
        var currentCategory = ""
        var currentItems = mutableListOf<String>()
        
        for (line in lines) {
            if (line.endsWith(":")) {
                if (currentCategory.isNotEmpty()) {
                    categories.add(CategoryItem(currentCategory, currentItems))
                }
                currentCategory = line.removeSuffix(":").trim()
                currentItems = mutableListOf()
            } else if (line.trim().isNotEmpty()) {
                currentItems.add(line.trim())
            }
        }
        
        if (currentCategory.isNotEmpty()) {
            categories.add(CategoryItem(currentCategory, currentItems))
        }
        
        return categories
    }
}
```

---

## 6. 고급 활용 기법

### 6.1 커스텀 구분자 사용

```kotlin
@RestController
class CustomSeparatorController(
    private val chatModel: ChatModel
) {
    @PostMapping("/parse-custom")
    fun parseWithCustomSeparator(@RequestBody request: ParseRequest): List<String> {
        // 세미콜론으로 구분
        val parser = ListOutputParser(separator = ";")
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    다음 형식으로 응답해주세요:
                    $format
                    
                    각 항목은 세미콜론(;)으로 구분해주세요.
                    """.trimIndent()
                ),
                UserMessage(request.question)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.results.firstOrNull()?.output?.text 
            ?: throw IllegalStateException("응답 없음")
        
        return parser.parse(text)
    }
}
```

### 6.2 에러 처리 포함

```kotlin
@RestController
class SafeListParserController(
    private val chatModel: ChatModel
) {
    @PostMapping("/safe-parse-list")
    fun safeParseList(@RequestBody request: ParseRequest): Map<String, Any> {
        val parser = ListOutputParser()
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage("다음 형식으로 응답해주세요:\n$format"),
                UserMessage(request.question)
            )
        )
        
        return try {
            val response = chatModel.call(prompt)
            val text = response.results.firstOrNull()?.output?.text 
                ?: throw IllegalStateException("응답 없음")
            
            val items = parser.parse(text)
            
            mapOf(
                "success" to true,
                "items" to items,
                "count" to items.size
            )
        } catch (e: Exception) {
            mapOf(
                "success" to false,
                "error" to (e.message ?: "알 수 없는 오류")
            )
        }
    }
}
```

---

## 7. ListOutputParser와 MapOutputParser 메서드 상세

### 7.1 ListOutputParser 메서드

#### .format 속성

**format**은 LLM이 따라야 할 리스트 형식을 반환합니다.

```kotlin
val parser = ListOutputParser()
val format = parser.format

// 예시 출력:
// 각 항목을 줄바꿈으로 구분하여 나열해주세요.
```

#### .parse() 메서드

**parse()**는 텍스트를 리스트로 변환합니다.

```kotlin
val text = """
항목1
항목2
항목3
""".trimIndent()

val items = parser.parse(text)
// 결과: ["항목1", "항목2", "항목3"]
```

### 7.2 MapOutputParser 메서드

#### .format 속성

**format**은 LLM이 따라야 할 Key-Value 형식을 반환합니다.

```kotlin
val parser = MapOutputParser()
val format = parser.format

// 예시 출력:
// Key: Value 형식으로 응답해주세요.
```

#### .parse() 메서드

**parse()**는 텍스트를 Map으로 변환합니다.

```kotlin
val text = """
Key1: Value1
Key2: Value2
Key3: Value3
""".trimIndent()

val map = parser.parse(text)
// 결과: {"Key1": "Value1", "Key2": "Value2", "Key3": "Value3"}
```

---

## 8. 베스트 프랙티스

### 8.1 형식 명확히 지정

#### ✅ 좋은 예: 명확한 형식 지시

```kotlin
val prompt = Prompt(
    listOf(
        SystemMessage(
            """
            다음 형식으로 응답해주세요:
            $format
            
            각 항목은 줄바꿈으로 구분해주세요.
            추가 설명은 포함하지 마세요.
            """.trimIndent()
        ),
        UserMessage("5가지 항목을 나열해주세요")
    )
)
```

#### ❌ 나쁜 예: 모호한 형식 지시

```kotlin
val prompt = Prompt(
    listOf(
        SystemMessage("리스트 형식으로 응답해주세요"),  // 너무 모호함
        UserMessage("항목을 나열해주세요")
    )
)
```

### 8.2 구분자 선택

#### 줄바꿈 구분자 (기본)
- **장점**: 가독성 좋음
- **단점**: 항목이 여러 줄일 수 있음

#### 쉼표 구분자
- **장점**: 간결함
- **단점**: 항목에 쉼표가 포함될 수 있음

#### 세미콜론 구분자
- **장점**: 항목에 쉼표 포함 가능
- **단점**: 가독성 약간 저하

### 8.3 에러 처리

```kotlin
fun safeParseList(text: String): List<String> {
    return try {
        parser.parse(text)
    } catch (e: Exception) {
        // 기본값 반환 또는 로깅
        emptyList()
    }
}
```

---

## 9. 주의사항 및 트러블슈팅

### 9.1 일반적인 문제들

#### 문제 1: 빈 리스트 반환

**증상:**
```
파싱 결과가 빈 리스트
```

**원인**: LLM이 지정된 형식을 따르지 않음

**해결책:**
```kotlin
// Format 메시지를 더 명확하게
val formatInstructions = """
다음 형식으로만 응답해주세요:
$format

예시:
항목1
항목2
항목3
""".trimIndent()
```

#### 문제 2: 구분자 인식 실패

**증상:**
```
리스트가 하나의 항목으로 파싱됨
```

**원인**: LLM이 다른 구분자를 사용

**해결책:**
```kotlin
// 구분자를 명확히 지정
val parser = ListOutputParser(separator = "\n")
val format = parser.format

val prompt = Prompt(
    listOf(
        SystemMessage(
            """
            다음 형식으로 응답해주세요:
            $format
            
            반드시 줄바꿈으로 구분해주세요.
            """.trimIndent()
        ),
        UserMessage(request.question)
    )
)
```

#### 문제 3: 맵 파싱 실패

**증상:**
```
Map이 비어있거나 키-값 쌍이 누락됨
```

**해결책:**
```kotlin
// 형식을 더 명확히
val formatInstructions = """
다음 형식으로 응답해주세요:
$format

예시:
Key1: Value1
Key2: Value2

콜론(:) 앞이 Key, 뒤가 Value입니다.
""".trimIndent()
```

---

## 10. 요약

### 10.1 핵심 내용 정리

1. **ListOutputParser**: LLM 응답을 리스트로 자동 파싱
2. **MapOutputParser**: LLM 응답을 Key-Value 맵으로 자동 파싱
3. **.format**: LLM에 전달할 형식 설명
4. **.parse()**: 텍스트를 리스트/맵으로 변환
5. **구분자 선택**: 줄바꿈, 쉼표, 세미콜론 등

### 10.2 기본 패턴

```kotlin
// ListOutputParser
val listParser = ListOutputParser()
val format = listParser.format

val prompt = Prompt(
    listOf(
        SystemMessage("다음 형식으로 응답해주세요:\n$format"),
        UserMessage("항목을 나열해주세요")
    )
)

val response = chatModel.call(prompt)
val text = response.results.firstOrNull()?.output?.text ?: ""
val items: List<String> = listParser.parse(text)

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

> 💡 **중요**: Spring AI 1.0.0-M6에서 응답은 `response.results.firstOrNull()?.output?.text`로 접근합니다.

### 10.3 다음 학습 내용

이제 리스트 및 맵 파싱을 배웠으니, 다음 장에서는:
- **임베딩의 개념**: 텍스트를 벡터로 변환
- **EmbeddingClient**: 임베딩 생성 및 사용
- **시맨틱 검색**: 의미 기반 검색

---

## 📚 참고 자료

- [Spring AI OutputParser 공식 문서](https://docs.spring.io/spring-ai/reference/api/output-parser.html)
- [Kotlin Collections](https://kotlinlang.org/docs/collections-overview.html)

---

## ❓ 학습 확인 문제

다음 질문에 답할 수 있는지 확인해보세요:

1. ListOutputParser를 사용하는 이유는 무엇인가요?
2. MapOutputParser와 BeanOutputParser의 차이는?
3. CSV 형식의 응답을 파싱하는 방법은?
4. 복합 형식(리스트와 맵 조합)을 파싱하는 방법은?
5. 구분자를 선택할 때 고려사항은?

---

**다음 장**: [5.1: 임베딩의 개념과 EmbeddingClient](../README.md#51-임베딩의-개념과-embeddingclient)

