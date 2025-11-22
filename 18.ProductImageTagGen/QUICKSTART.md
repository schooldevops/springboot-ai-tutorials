# 🚀 Quick Start Guide

## Prerequisites

- ☕ Java 17 or higher
- 🐘 Gradle (or use included wrapper)
- 🦙 Ollama with Llama 3.2 Vision model

## Setup Steps

### 1. Install and Start Ollama

```bash
# macOS
brew install ollama

# Start Ollama
ollama serve

# Pull Llama 3.2 Vision model
ollama pull llama3.2-vision
```

### 2. Run Tests (TDD Verification)

```bash
cd 18.ProductImageTagGen/sample

# Run all tests
./gradlew test

# Expected output:
# ImageAnalysisServiceTest > should analyze image and return product tags PASSED
# ImageAnalysisServiceTest > should handle empty image PASSED
# BUILD SUCCESSFUL
# 2 tests completed
```

### 3. Run the Application

```bash
./gradlew bootRun
```

## Testing Image Analysis

### Upload Product Image

```bash
curl -X POST http://localhost:8080/api/images/analyze \
  -F "file=@product.jpg"
```

**Response:**
```json
{
  "productTags": {
    "colors": ["빨강", "검정", "흰색"],
    "style": "모던",
    "features": ["심플", "고급스러움", "미니멀"],
    "category": "의류",
    "tags": ["#레드", "#모던스타일", "#심플"],
    "description": "빨간색과 검정색이 조화를 이루는 모던한 스타일의 의류"
  },
  "processingTime": 2500
}
```

## Key Features Demonstrated

### ✅ Multimodal AI

AI analyzes both image and text prompts together.

### ✅ Structured JSON Output

Automatic parsing to ProductTags data class.

### ✅ File Upload

MultipartFile handling for image uploads.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/images/health` | Health check |
| POST | `/api/images/analyze` | Analyze product image |

## Troubleshooting

### Ollama Connection Error

```
Connection refused: localhost:11434
```

**Solution:**
```bash
ollama serve
```

### Vision Model Not Found

```
model 'llama3.2-vision' not found
```

**Solution:**
```bash
ollama pull llama3.2-vision
```

### File Too Large

```
Maximum upload size exceeded
```

**Solution:** Check `application.yml`:
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
```

## Next Steps

- 📖 Read the full [README.md](README.md)
- 🧪 Test with different product images
- 🔧 Customize tag generation prompts
- 📝 Add more analysis features

## Tips

💡 **Image Quality**: Higher quality images produce better analysis results

💡 **Prompt Engineering**: Customize system prompt for specific product types

💡 **Caching**: Consider caching results for identical images

## Support

- 📚 [Spring AI Multimodal](https://docs.spring.io/spring-ai/reference/api/multimodal.html)
- 🦙 [Ollama Vision Models](https://ollama.com/library/llama3.2-vision)
