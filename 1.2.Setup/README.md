# 1.2: Spring AI 개발 환경 구축

## 📖 학습 목표

이 강의를 마친 후 다음을 달성할 수 있습니다:
- **Spring Initializr**를 사용하여 Kotlin 기반 Spring Boot 프로젝트를 생성할 수 있습니다
- **Spring AI 의존성**을 올바르게 추가하고 설정할 수 있습니다
- **API Key 관리** 방법을 이해하고 안전하게 설정할 수 있습니다
- 프로젝트를 **실행**하고 기본적인 동작을 확인할 수 있습니다
- **Gradle과 Maven**의 차이를 이해하고 선택할 수 있습니다

---

## 🔑 핵심 키워드

이 장에서 다루는 핵심 키워드들:

1. **start.spring.io** (Spring Initializr)
2. **Gradle / Maven** (빌드 도구)
3. **spring-ai-openai-starter** (OpenAI 통합)
4. **spring-ai-anthropic-starter** (Anthropic 통합)
5. **spring-ai-ollama-starter** (Ollama 통합)
6. **API Key 관리** (환경 변수, application.yml)

---

## 1. 사전 준비사항

### 1.1 필요한 도구

프로젝트를 시작하기 전에 다음 도구들이 설치되어 있어야 합니다:

- **JDK 17 이상** (권장: JDK 17, 21)
- **IDE**: IntelliJ IDEA (권장) 또는 VS Code
- **Git** (버전 관리)
- **터미널/명령 프롬프트** 접근 권한

### 1.2 JDK 설치 확인

터미널에서 다음 명령어로 JDK 버전을 확인하세요:

```bash
java -version
# 예상 출력: openjdk version "17.0.x" 또는 "21.0.x"
```

JDK가 설치되어 있지 않다면:
- **macOS**: `brew install openjdk@17`
- **Windows**: Oracle JDK 또는 OpenJDK 다운로드
- **Linux**: `sudo apt-get install openjdk-17-jdk`

---

## 2. Spring Initializr 소개

### 2.1 Spring Initializr란?

**Spring Initializr**는 Spring Boot 프로젝트를 빠르게 생성할 수 있게 해주는 웹 기반 도구입니다.

**주요 특징:**
- 웹 인터페이스 제공 (https://start.spring.io/)
- IDE 통합 (IntelliJ IDEA, VS Code)
- CLI 도구 제공
- 프로젝트 템플릿 생성

### 2.2 Spring Initializr 접근 방법

1. **웹 브라우저**: https://start.spring.io/
2. **IntelliJ IDEA**: New Project → Spring Initializr
3. **VS Code**: Spring Initializr 확장 프로그램
4. **CLI**: `curl` 또는 HTTPie 사용

---

## 3. 프로젝트 생성 방법

### 3.1 방법 1: 웹 브라우저 사용 (초보자 권장)

#### 3.1.1 단계별 가이드

1. **브라우저에서 https://start.spring.io/ 접속**

2. **프로젝트 설정 입력**

   ```
   Project: Gradle Project (또는 Maven Project)
   Language: Kotlin
   Spring Boot: 3.3.0 (또는 최신 버전)
   Project Metadata:
     Group: com.example
     Artifact: spring-ai-app
     Name: spring-ai-app
     Description: Spring AI with Kotlin Application
     Package name: com.example.springai
     Packaging: Jar
     Java: 17 (또는 21)
   ```

3. **의존성 추가**

   "Add Dependencies" 버튼 클릭 후 다음을 검색하여 추가:
   - **Spring Web** (REST API 개발용)
   - **Spring AI OpenAI** (OpenAI 통합)
   - 또는 **Spring AI Anthropic** (Claude 통합)
   - 또는 **Spring AI Ollama** (로컬 LLM 통합)

4. **프로젝트 다운로드**

   "Generate" 버튼을 클릭하여 ZIP 파일 다운로드

5. **압축 해제 및 열기**

   ```bash
   unzip spring-ai-app.zip
   cd spring-ai-app
   ```

### 3.2 방법 2: IntelliJ IDEA 사용 (개발자 권장)

#### 3.2.1 프로젝트 생성 단계

1. **IntelliJ IDEA 실행** → `File` → `New` → `Project`

2. **Spring Initializr 선택**

   - Name: `spring-ai-app`
   - Location: 원하는 디렉토리 선택
   - Language: **Kotlin**
   - Type: **Gradle** (권장) 또는 Maven
   - JDK: 17 이상
   - Spring Boot: 최신 버전

3. **의존성 선택**

   다음 의존성을 검색하여 추가:
   - `Spring Web`
   - `Spring AI OpenAI` (또는 다른 LLM 통합)

4. **프로젝트 생성 완료**

   Finish 버튼 클릭 후 IntelliJ가 자동으로 프로젝트를 구성합니다.

### 3.3 방법 3: CLI 사용 (고급)

#### 3.3.1 cURL을 사용한 프로젝트 생성

```bash
curl https://start.spring.io/starter.zip \
  -d type=gradle-project-kotlin \
  -d language=kotlin \
  -d bootVersion=3.3.0 \
  -d baseDir=spring-ai-app \
  -d groupId=com.example \
  -d artifactId=spring-ai-app \
  -d name=spring-ai-app \
  -d description="Spring AI with Kotlin" \
  -d packageName=com.example.springai \
  -d packaging=jar \
  -d javaVersion=17 \
  -d dependencies=web,spring-ai-openai-starter \
  -o spring-ai-app.zip

unzip spring-ai-app.zip
cd spring-ai-app
```

---

## 4. 프로젝트 구조 이해하기

### 4.1 생성된 프로젝트 구조

```
spring-ai-app/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/example/springai/
│   │   │       └── SpringAiAppApplication.kt
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application.yml (또는)
│   └── test/
│       └── kotlin/
│           └── com/example/springai/
│               └── SpringAiAppApplicationTests.kt
├── build.gradle.kts (Gradle) 또는 pom.xml (Maven)
├── settings.gradle.kts
└── README.md
```

### 4.2 주요 파일 설명

#### 4.2.1 build.gradle.kts (Gradle 프로젝트)

```kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    // Spring AI OpenAI
    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter:1.0.0-M6")
    
    // Kotlin 관련
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // Jackson (JSON 처리)
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    
    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

#### 4.2.2 application.yml (설정 파일)

```yaml
spring:
  application:
    name: spring-ai-app
  
  # Spring AI OpenAI 설정
  ai:
    openai:
      api-key: ${OPENAI_API_KEY:your-api-key-here}
      chat:
        options:
          model: gpt-4
          temperature: 0.7
```

#### 4.2.3 SpringAiAppApplication.kt (메인 클래스)

```kotlin
package com.example.springai

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringAiAppApplication

fun main(args: Array<String>) {
    runApplication<SpringAiAppApplication>(*args)
}
```

---

## 5. Gradle vs Maven 선택

### 5.1 Gradle vs Maven 비교

| 특징 | Gradle | Maven |
|------|--------|-------|
| **빌드 스크립트** | Kotlin DSL / Groovy | XML |
| **빌드 속도** | 빠름 (증분 빌드) | 상대적으로 느림 |
| **의존성 해결** | 빠름 | 보통 |
| **설정 파일** | build.gradle.kts | pom.xml |
| **학습 곡선** | 중간 | 낮음 |
| **유연성** | 높음 | 낮음 |

### 5.2 선택 가이드

**Gradle 선택 시기:**
- ✅ Kotlin 개발자 (DSL 일관성)
- ✅ 복잡한 빌드 작업이 필요한 경우
- ✅ 빠른 빌드 속도가 중요한 경우
- ✅ 멀티 모듈 프로젝트

**Maven 선택 시기:**
- ✅ XML에 익숙한 경우
- ✅ 표준화된 구조 선호
- ✅ 기존 Maven 프로젝트와 통합

> 💡 **권장**: Kotlin 프로젝트이므로 Gradle (Kotlin DSL)을 권장합니다.

---

## 6. Spring AI 의존성 설정

### 6.1 사용 가능한 Spring AI Starter

Spring AI는 여러 LLM 제공자를 지원합니다:

#### 6.1.1 OpenAI (ChatGPT)

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter:1.0.0-M6")
}
```

**특징:**
- GPT-3.5, GPT-4, GPT-4 Turbo 지원
- Vision 모델 지원 (GPT-4o)
- 널리 사용되는 모델
- 유료 서비스 (API 키 필요)

#### 6.1.2 Anthropic (Claude)

```kotlin
dependencies {
    implementation("org.springframework.ai:spring-ai-anthropic-spring-boot-starter:1.0.0-M6")
}
```

**특징:**
- Claude 3 (Sonnet, Opus, Haiku) 지원
- 긴 컨텍스트 윈도우
- 안전성 중심 설계
- 유료 서비스 (API 키 필요)

#### 6.1.3 Ollama (로컬 LLM)

```kotlin
dependencies {
    implementation("org.springframework.ai:spring-ai-ollama-spring-boot-starter:1.0.0-M6")
}
```

**특징:**
- 무료 (로컬 실행)
- Llama, Mistral, CodeLlama 등 지원
- 인터넷 연결 불필요
- 로컬에 Ollama 설치 필요

#### 6.1.4 Azure OpenAI

```kotlin
dependencies {
    implementation("org.springframework.ai:spring-ai-azure-openai-spring-boot-starter:1.0.0-M6")
}
```

#### 6.1.5 Vertex AI (Google)

```kotlin
dependencies {
    implementation("org.springframework.ai:spring-ai-vertex-ai-gemini-spring-boot-starter:1.0.0-M6")
}
```

### 6.2 여러 LLM 동시 사용

여러 LLM을 하나의 프로젝트에서 사용할 수도 있습니다:

```kotlin
dependencies {
    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter:1.0.0-M6")
    implementation("org.springframework.ai:spring-ai-ollama-spring-boot-starter:1.0.0-M6")
}
```

> ⚠️ **주의**: 여러 Starter를 사용할 때는 `@Primary` 어노테이션으로 기본 ChatModel을 지정해야 합니다.

> 💡 **참고**: 이 가이드는 Spring AI 1.0.0-M6 버전을 기준으로 작성되었습니다. 최신 버전의 경우 API가 다를 수 있으니 공식 문서를 참고하세요.

---

## 7. API Key 관리

### 7.1 API Key 발급 방법

#### 7.1.1 OpenAI API Key

1. **https://platform.openai.com/** 접속
2. 계정 생성 또는 로그인
3. `API keys` 섹션으로 이동
4. "Create new secret key" 클릭
5. 키 복사 및 안전하게 보관

#### 7.1.2 Anthropic API Key

1. **https://console.anthropic.com/** 접속
2. 계정 생성 또는 로그인
3. `API Keys` 섹션으로 이동
4. "Create Key" 클릭
5. 키 복사 및 안전하게 보관

### 7.2 API Key 설정 방법

#### 7.2.1 방법 1: 환경 변수 사용 (권장 ✅)

**macOS / Linux:**
```bash
export OPENAI_API_KEY="sk-..."
```

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="sk-..."
```

**Windows (CMD):**
```cmd
set OPENAI_API_KEY=sk-...
```

**영구적으로 설정 (macOS/Linux):**
```bash
# ~/.zshrc 또는 ~/.bashrc에 추가
echo 'export OPENAI_API_KEY="sk-..."' >> ~/.zshrc
source ~/.zshrc
```

**application.yml:**
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
```

#### 7.2.2 방법 2: application.yml 직접 설정 (개발 환경만)

⚠️ **주의**: 절대 Git에 커밋하지 마세요!

```yaml
spring:
  ai:
    openai:
      api-key: sk-your-actual-api-key-here
```

**Git 제외 설정 (.gitignore):**
```
application-local.yml
application-secret.yml
```

#### 7.2.3 방법 3: Spring Cloud Config / Vault (프로덕션)

프로덕션 환경에서는 Spring Cloud Config나 HashiCorp Vault 같은 외부 설정 관리 도구를 사용하세요.

### 7.3 API Key 보안 베스트 프랙티스

✅ **해야 할 것:**
- 환경 변수 사용
- `.gitignore`에 API 키 포함 파일 추가
- 프로필 분리 (`application-dev.yml`, `application-prod.yml`)
- API 키 로테이션 주기적 실행

❌ **하지 말아야 할 것:**
- API 키를 코드에 하드코딩
- Git 저장소에 API 키 커밋
- 공개 저장소에 API 키 노출
- 화면 공유 시 API 키 노출

---

## 8. 첫 번째 Spring AI 애플리케이션 만들기

### 8.1 기본 프로젝트 확인

프로젝트가 정상적으로 생성되었는지 확인:

```bash
cd spring-ai-app
./gradlew build  # 또는 gradle build
```

### 8.2 간단한 ChatModel 테스트

#### 8.2.1 RestController 생성

`src/main/kotlin/com/example/springai/controller/ChatController.kt` 생성:

```kotlin
package com.example.springai.controller

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatModel: ChatModel
) {
    
    /**
     * 간단한 테스트 엔드포인트
     * GET http://localhost:8080/api/chat/test
     */
    @GetMapping("/test")
    fun test(): String {
        val prompt = Prompt(UserMessage("안녕하세요! 간단히 자기소개 해주세요."))
        val response = chatModel.call(prompt)
        return response.result?.output?.text ?: "응답을 생성할 수 없습니다."
    }
    
    /**
     * 메시지를 받아서 AI에게 질문하는 엔드포인트
     * POST http://localhost:8080/api/chat
     * Body: {"message": "Spring AI에 대해 설명해주세요"}
     */
    @PostMapping
    fun chat(@RequestBody request: ChatRequest): ChatResponse {
        val prompt = Prompt(UserMessage(request.message))
        val response = chatModel.call(prompt)
        
        return ChatResponse(
            message = response.result?.output?.text ?: "응답을 생성할 수 없습니다."
        )
    }
}

data class ChatRequest(
    val message: String
)

data class ChatResponse(
    val message: String
)
```

> 💡 **주의**: Spring AI 1.0.0-M6에서는 `ChatModel` 인터페이스를 사용하며, 응답의 텍스트는 `response.result.output.text`로 접근합니다.

### 8.3 애플리케이션 실행

#### 8.3.1 IDE에서 실행

**IntelliJ IDEA:**
1. `SpringAiAppApplication.kt` 파일 열기
2. 메인 함수 옆의 실행 버튼 클릭

**VS Code:**
1. Spring Boot Dashboard 사용
2. 또는 터미널에서 실행

#### 8.3.2 터미널에서 실행

```bash
# Gradle
./gradlew bootRun

# Maven
./mvnw spring-boot:run
```

### 8.4 테스트

애플리케이션이 실행되면 브라우저 또는 curl로 테스트:

```bash
# GET 요청
curl http://localhost:8080/api/chat/test

# POST 요청
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Spring AI에 대해 간단히 설명해주세요"}'
```

---

## 9. 프로젝트 설정 상세

### 9.1 application.yml 완전판

```yaml
spring:
  application:
    name: spring-ai-app
  
  # OpenAI 설정
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4          # 또는 gpt-3.5-turbo
          temperature: 0.7      # 0.0 ~ 2.0 (창의성 조절)
          max-tokens: 1000      # 최대 토큰 수
          top-p: 1.0            # Nucleus sampling
          frequency-penalty: 0.0
          presence-penalty: 0.0

server:
  port: 8080

logging:
  level:
    com.example.springai: DEBUG
    org.springframework.ai: DEBUG
```

### 9.2 Anthropic 설정 예시

```yaml
spring:
  ai:
    anthropic:
      api-key: ${ANTHROPIC_API_KEY}
      chat:
        options:
          model: claude-3-sonnet-20240229
          temperature: 0.7
          max-tokens: 1000
```

### 9.3 Ollama 설정 예시

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama2        # 또는 mistral, codellama 등
          temperature: 0.7
```

---

## 10. 트러블슈팅

### 10.1 일반적인 문제들

#### 문제 1: API Key를 찾을 수 없음

```
Error: API key not found
```

**해결책:**
- 환경 변수가 설정되었는지 확인: `echo $OPENAI_API_KEY`
- application.yml에서 `api-key: ${OPENAI_API_KEY}` 형식 확인
- 애플리케이션 재시작

#### 문제 2: 의존성을 찾을 수 없음

```
Could not resolve: spring-ai-openai-spring-boot-starter
```

**해결책:**
- Spring AI 버전 확인 (이 가이드에서 사용하는 버전: 1.0.0-M6)
- Maven Central 저장소 연결 확인
- `./gradlew clean build --refresh-dependencies`

#### 문제 3: Kotlin 컴파일 오류

```
Kotlin version mismatch
```

**해결책:**
- `build.gradle.kts`에서 Kotlin 버전 확인
- Spring Boot와 호환되는 Kotlin 버전 사용
- `kotlin("jvm") version "1.9.24"` 이상

#### 문제 4: 포트가 이미 사용 중

```
Port 8080 is already in use
```

**해결책:**
- 다른 애플리케이션 종료
- 또는 `application.yml`에서 포트 변경:
  ```yaml
  server:
    port: 8081
  ```

### 10.2 디버깅 팁

**로깅 레벨 설정:**
```yaml
logging:
  level:
    org.springframework.ai: DEBUG
    org.springframework.web: DEBUG
```

**환경 변수 확인:**
```kotlin
@Configuration
class Config {
    @PostConstruct
    fun checkEnv() {
        println("API Key: ${System.getenv("OPENAI_API_KEY")?.take(7)}...")
    }
}
```

---

## 11. 다음 단계 준비

### 11.1 프로젝트 확인 체크리스트

프로젝트가 올바르게 설정되었는지 확인:

- [ ] 프로젝트가 빌드됨 (`./gradlew build`)
- [ ] 애플리케이션이 실행됨 (`./gradlew bootRun`)
- [ ] API Key가 올바르게 설정됨
- [ ] 테스트 엔드포인트가 응답함 (`/api/chat/test`)
- [ ] ChatModel이 정상 작동함

### 11.2 샘플 프로젝트

이제 [sample/](./sample/) 디렉토리의 예제 코드를 참고하여 학습을 진행하세요.

---

## 12. 요약

### 12.1 핵심 내용 정리

1. **Spring Initializr**로 Kotlin 기반 Spring Boot 프로젝트 생성
2. **Spring AI Starter** 의존성 추가 (OpenAI, Anthropic, Ollama 등)
3. **API Key**를 환경 변수로 안전하게 관리
4. **Gradle (Kotlin DSL)** 또는 Maven 선택
5. 간단한 **ChatController**로 첫 번째 AI 애플리케이션 실행

> 💡 **중요**: Spring AI 1.0.0-M6 버전에서는 `ChatModel` 인터페이스를 사용하며, 응답 접근 방식이 `response.result.output.text`입니다.

### 12.2 주요 명령어

```bash
# 프로젝트 생성 (CLI)
curl https://start.spring.io/starter.zip ... -o project.zip

# 빌드
./gradlew build

# 실행
./gradlew bootRun

# 테스트
./gradlew test
```

### 12.3 다음 학습 내용

이제 환경이 준비되었으니 다음 장인 **2장: LLM과 대화하기 (ChatClient)**에서 실제로 LLM과 대화하는 방법을 배워봅시다!

---

## 📚 참고 자료

- [Spring Initializr](https://start.spring.io/)
- [Spring AI 공식 레퍼런스](https://docs.spring.io/spring-ai/reference/)
- [Gradle Kotlin DSL 가이드](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)

---

## ❓ 학습 확인 문제

다음 질문에 답할 수 있는지 확인해보세요:

1. Spring Initializr에서 Kotlin 프로젝트를 생성하는 방법은?
2. OpenAI API Key를 안전하게 관리하는 방법은?
3. Gradle과 Maven의 주요 차이점은?
4. 여러 LLM Provider를 동시에 사용하는 방법은?
5. 첫 번째 Spring AI 애플리케이션을 실행하는 방법은?

---

**다음 장**: [2장: LLM과 대화하기 (ChatClient)](../README.md#2장-llm과-대화하기-chatclient)

