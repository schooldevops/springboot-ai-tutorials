# 18장: [실전] 상품 이미지 태그 생성기

## 📚 학습 목표

10장의 **멀티모달 기능**을 활용하여, 상품 이미지를 업로드하면 AI가 이미지의 특징, 색상, 스타일 등을 분석하여 **마케팅 태그(JSON 형식)**를 생성하는 API를 구현합니다.

## 🔑 핵심 키워드

- `Vision API`
- `BeanOutputParser`
- 이미지 분석
- 상품 태깅
- Multimodal AI
- TDD

## 📖 개요

이 장에서는 Llama 3.2 Vision 모델을 사용하여 상품 이미지를 분석하고, BeanOutputParser를 통해 구조화된 JSON 형식의 마케팅 태그를 생성합니다. **TDD 방식**으로 개발합니다.

## 🎯 Multimodal AI란?

**Multimodal AI**는 텍스트뿐만 아니라 이미지, 오디오 등 여러 형태의 데이터를 함께 처리할 수 있는 AI입니다.

### Text-only vs Multimodal

| 특징 | Text-only | Multimodal |
|------|-----------|-----------|
| 입력 | 텍스트만 | 텍스트 + 이미지 |
| 이미지 분석 | ❌ 불가능 | ✅ 가능 |
| 활용 | 대화, 문서 | 이미지 분석, 태깅 |

## 🔄 Image Tag Generation 워크플로우

```
상품 이미지 업로드
       ↓
Base64 인코딩
       ↓
Multimodal AI (Llama 3.2 Vision)
       ↓
이미지 분석
  - 색상 추출
  - 스타일 파악
  - 특징 인식
       ↓
BeanOutputParser
       ↓
구조화된 JSON 태그
{
  "colors": ["빨강", "검정"],
  "style": "모던",
  "features": ["심플", "고급스러움"],
  "category": "의류",
  "tags": ["#레드", "#모던스타일"]
}
```

## 💻 구현 상세 (TDD 방식)

### 1. 데이터 모델 (BeanOutputParser용)

```kotlin
data class ProductTags(
    val colors: List<String>,
    val style: String,
    val features: List<String>,
    val category: String,
    val tags: List<String>,
    val description: String
)
```

### 2. 이미지 분석 서비스 (테스트 먼저)

**ImageAnalysisServiceTest.kt:**
```kotlin
@Test
fun `should analyze image and return tags`() {
    val imageBytes = loadTestImage()
    val tags = imageAnalysisService.analyzePro ductImage(imageBytes)
    
    assertNotNull(tags)
    assertTrue(tags.colors.isNotEmpty())
    assertNotNull(tags.style)
}
```

**ImageAnalysisService.kt:**
```kotlin
@Service
class ImageAnalysisService(
    private val chatModel: ChatModel
) {
    fun analyzeProductImage(imageBytes: ByteArray): ProductTags {
        val base64Image = Base64.getEncoder().encodeToString(imageBytes)
        
        val userMessage = UserMessage(
            "이 상품 이미지를 분석하여 색상, 스타일, 특징을 JSON 형식으로 추출해주세요.",
            listOf(Media(MimeTypeUtils.IMAGE_PNG, base64Image))
        )
        
        val outputParser = BeanOutputParser(ProductTags::class.java)
        val prompt = Prompt(
            listOf(SystemMessage(outputParser.format), userMessage)
        )
        
        val response = chatModel.call(prompt)
        return outputParser.parse(response.result.output.content)
    }
}
```

### 3. REST Controller (파일 업로드)

```kotlin
@RestController
@RequestMapping("/api/images")
class ImageController(
    private val imageAnalysisService: ImageAnalysisService
) {
    
    @PostMapping("/analyze", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun analyzeImage(@RequestParam("file") file: MultipartFile): ProductTags {
        val imageBytes = file.bytes
        return imageAnalysisService.analyzeProductImage(imageBytes)
    }
}
```

## 🧪 테스트 방법

### 1. 단위 테스트

```bash
./gradlew test
```

### 2. 애플리케이션 실행

```bash
ollama serve
ollama pull llama3.2-vision

./gradlew bootRun
```

### 3. 이미지 업로드 테스트

```bash
curl -X POST http://localhost:8080/api/images/analyze \
  -F "file=@product.jpg"
```

**응답:**
```json
{
  "colors": ["빨강", "검정", "흰색"],
  "style": "모던",
  "features": ["심플", "고급스러움", "미니멀"],
  "category": "의류",
  "tags": ["#레드", "#모던스타일", "#심플"],
  "description": "빨간색과 검정색이 조화를 이루는 모던한 스타일의 의류"
}
```

## 🎓 학습 포인트

1. **Multimodal AI** - 이미지와 텍스트 동시 처리
2. **Vision API** - 이미지 분석 기능
3. **BeanOutputParser** - 구조화된 JSON 출력
4. **File Upload** - MultipartFile 처리
5. **TDD** - 테스트 먼저 작성

## 💡 실전 활용 사례

### 1. E-commerce
- 상품 자동 태깅
- 카테고리 자동 분류
- 검색 최적화

### 2. 마케팅
- 자동 해시태그 생성
- 상품 설명 자동 작성
- SNS 콘텐츠 생성

### 3. 재고 관리
- 이미지 기반 분류
- 유사 상품 검색
- 품질 검사

## 🚀 다음 단계

- **19장**: 대화형 챗봇 (채팅 기록 관리)

## 📚 참고 자료

- [Spring AI Multimodal](https://docs.spring.io/spring-ai/reference/api/multimodal.html)
- [BeanOutputParser](https://docs.spring.io/spring-ai/reference/api/output-parser.html)

## 💡 팁

> [!TIP]
> **Vision Model**: Llama 3.2 Vision은 이미지 분석에 특화된 모델입니다.

> [!TIP]
> **BeanOutputParser**: 복잡한 JSON 구조도 Kotlin 데이터 클래스로 자동 파싱됩니다.

> [!WARNING]
> **이미지 크기**: 너무 큰 이미지는 Base64 인코딩 시 메모리 문제가 발생할 수 있습니다.

## 🔧 고급 기능

### 1. 배치 처리

```kotlin
fun analyzeBatch(files: List<MultipartFile>): List<ProductTags> {
    return files.map { analyzeProductImage(it.bytes) }
}
```

### 2. 캐싱

```kotlin
@Cacheable("image-tags")
fun analyzeProductImage(imageBytes: ByteArray): ProductTags
```

### 3. 비동기 처리

```kotlin
@Async
fun analyzeProductImageAsync(imageBytes: ByteArray): CompletableFuture<ProductTags>
```
