# 📚 Spring AI with Kotlin: AI 애플리케이션 개발 마스터 과정 (1~10장)

## 1장: Spring AI와 Kotlin 소개

### 1.1: 과정 소개 및 로드맵

*학습 목표*: Spring AI의 역할과 Kotlin을 함께 사용했을 때의 이점을 이해하고, 전체 과정의 학습 목표를 설정합니다.

*핵심 키워드*: Generative AI, LLM, Spring AI, Kotlin, Spring Boot

### 1.2: Spring AI 개발 환경 구축

*학습 목표*: Spring Initializr와 Kotlin, Spring AI 의존성을 설정하여 프로젝트를 생성하고 실행할 수 있습니다.

*핵심 키워드*: start.spring.io, Gradle/Maven, spring-ai-openai-starter (또는 Anthropic/Ollama), API Key 관리

#### 중요 기능 및 참고 자료:

- Spring AI 공식 레퍼런스: https://docs.spring.io/spring-ai/reference/
- Spring Initializr: https://start.spring.io/

## 2장: LLM과 대화하기 (ChatModel)

### 2.1: ChatModel의 이해와 활용

*학습 목표*: Spring AI의 핵심 인터페이스인 ChatModel을 이해하고, 간단한 프롬프트를 전송하여 응답을 받아옵니다.

*핵심 키워드*: ChatModel, .call(), Prompt, ChatResponse, Generation

> 💡 **참고**: 이 과정은 Spring AI 1.0.0-M6 버전을 기준으로 합니다. 이 버전에서는 `ChatModel` 인터페이스를 사용하며, 응답은 `response.result.output.text`로 접근합니다.

### 2.2: 다양한 LLM 모델 연동하기

*학습 목표*: OpenAI(ChatGPT) 외 Ollama, Anthropic(Claude) 등 다양한 모델을 Spring AI 설정으로 연동하고 교체해 봅니다.

*핵심 키워드*: application.yml, 프로퍼티 설정, ChatModel

#### 중요 기능 및 참고 자료:

- Chat API: https://docs.spring.io/spring-ai/reference/api/chat.html

## 3장: 효과적인 프롬프트 엔지니어링 (PromptTemplate)

### 3.1: 기본 PromptTemplate 활용

*학습 목표*: PromptTemplate을 사용하여 동적인 값을 프롬프트에 주입하는 방법을 학습합니다.

*핵심 키워드*: PromptTemplate, .add(key, value), .render()

### 3.2: 역할 기반 메시지 (Message Types)

*학습 목표*: System, User, AI 메시지 역할을 구분하여 LLM에 더 정교한 컨텍스트와 지시를 전달하는 방법을 배웁니다.

*핵심 키워드*: SystemMessage, UserMessage, AssistantMessage, few-shot-prompting

#### 중요 기능 및 참고 자료:

- Prompts: https://docs.spring.io/spring-ai/reference/api/prompts.html

## 4장: LLM 응답 구조화 (OutputParser)

### 4.1: BeanOutputParser와 Kotlin Data Class

*학습 목표*: LLM의 비정형 텍스트 응답을 Kotlin의 data class로 자동 파싱하는 방법을 학습합니다.

*핵심 키워드*: BeanOutputParser, data class, .getFormat(), JSON 응답

### 4.2: 리스트 및 맵 파싱

*학습 목표*: ListOutputParser, MapOutputParser를 사용해 특정 형식의 목록이나 Key-Value 데이터를 추출합니다.

*핵심 키워드*: ListOutputParser, MapOutputParser, CSV, Map

#### 중요 기능 및 참고 자료:

- Output Parsers: https://docs.spring.io/spring-ai/reference/api/output-parser.html

## 5장: 임베딩과 시맨틱 검색 (EmbeddingClient)

### 5.1: 임베딩의 개념과 EmbeddingClient

*학습 목표*: 텍스트를 벡터로 변환하는 임베딩의 개념을 이해하고, EmbeddingClient를 사용해 텍스트를 벡터화합니다.

*핵심 키워드*: EmbeddingClient, Vector, Embedding, 시맨틱 검색(Semantic Search)

### 5.2: 텍스트 유사도 계산

*학습 목표*: 두 개 이상의 텍스트를 임베딩하고 벡터 간의 유사도를 계산하여 의미적 유사성을 비교합니다.

*핵심 키워드*: 코사인 유사도(Cosine Similarity), List<Double>

#### 중요 기능 및 참고 자료:

- Embedding API: https://docs.spring.io/spring-ai/reference/api/embedding.html

## 6장: 벡터 저장소 (VectorStore)

### 6.1: 벡터 저장소의 필요성

*학습 목표*: 대규모 벡터 데이터를 효율적으로 저장하고 검색하기 위한 벡터 저장소의 필요성을 이해합니다.

*핵심 키워드*: Vector Database, HNSW, ANN (Approximate Nearest Neighbor)

### 6.2: Spring AI VectorStore 추상화 (In-Memory)

*학습 목표*: VectorStore 인터페이스를 이해하고, 테스트 및 개발을 위해 SimpleVectorStore (In-Memory)를 사용해 봅니다.

*핵심 키워드*: VectorStore, Document, add(), similaritySearch()

### 6.3: 외부 벡터 저장소 연동 (PostgreSQL/PGVector)

*학습 목표*: PGVector와 같은 외부 벡터 저장소를 Spring AI에 연동하고 설정하는 방법을 학습합니다.

*핵심 키워드*: PGVector, Docker, spring-ai-pgvector-starter

#### 중요 기능 및 참고 자료:

- VectorStore API: https://docs.spring.io/spring-ai/reference/api/vectordb.html

## 7장: RAG (검색 증강 생성) - 기본

### 7.1: RAG 패턴의 이해

*학습 목표*: LLM의 환각(Hallucination)을 줄이고 최신 정보를 제공하기 위한 RAG 패턴의 원리를 이해합니다.

*핵심 키워드*: RAG (Retrieval-Augmented Generation), Grounding, Context

### 7.2: 간단한 RAG 파이프라인 구현

*학습 목표*: VectorStore에서 관련 문서를 검색(Retrieval)하고, 검색된 내용을 PromptTemplate에 주입하여 ChatModel에 전달하는 과정을 구현합니다.

*핵심 키워드*: VectorStore.similaritySearch(), Context 주입, PromptTemplate, ChatModel

#### 중요 기능 및 참고 자료:

- RAG 패턴: https://docs.spring.io/spring-ai/reference/patterns/rag.html

## 8장: RAG (검색 증강 생성) - 심화 (데이터 처리)

### 8.1: 문서 로딩 (Document Loaders)

*학습 목표*: PDF, Txt, Markdown 등 다양한 형식의 외부 문서를 Spring AI의 ResourceReader를 통해 로드합니다.

*핵심 키워드*: ResourceReader, TextReader, PdfDocumentReader

### 8.2: 문서 분할 (Document Transformers)

*학습 목표*: LLM의 토큰 제한과 임베딩 효율성을 위해 긴 문서를 의미 있는 단위로 분할(Splitting)하는 방법을 학습합니다.

*핵심 키워드*: TokenTextSplitter, Chunking, Overlap

#### 중요 기능 및 참고 자료:

- 데이터 엔지니어링 (ETL): https://docs.spring.io/spring-ai/reference/patterns/etl-pipeline.html

## 9장: AI가 Kotlin 함수 호출하기 (Function Calling)

### 9.1: Function Calling 개념과 활용

*학습 목표*: LLM이 사용자의 요청을 분석하여 개발자가 정의한 Kotlin 함수(Spring Bean)를 호출하도록 설정하는 방법을 배웁니다.

*핵심 키워드*: Function Calling, Tool, @Bean 등록, ChatOptions

ㅊㅇ

*학습 목표*: '날씨 묻기', '주문 상태 확인' 등 LLM이 직접 처리할 수 없는 요청을 Function Calling을 통해 외부 API(또는 서비스)와 연동하여 처리합니다.

*핵심 키워드*: java.util.function.Function, @Description, API 통합

#### 중요 기능 및 참고 자료:

- Function Calling: https://docs.spring.io/spring-ai/reference/api/function-calling.html

## 10장: 멀티모달 (Multi-modality)

### 10.1: Vision API와 이미지 인식

*학습 목표*: GPT-4o, Claude 3 등 Vision을 지원하는 모델에 텍스트와 함께 이미지를 전송하여 분석 및 설명을 요청합니다.

*핵심 키워드*: Multi-modality, Vision, UserMessage, Media, MimeType

### 10.2: 이미지 업로드 및 분석

*학습 목표*: Spring Boot API를 통해 이미지를 업로드하고, 이 이미지를 Spring AI를 통해 분석하는 간단한 엔드포인트를 구현합니다.

*핵심 키워드*: MultipartFile, Base64, 이미지 분석

#### 중요 기능 및 참고 자료:

- Multimodality: https://docs.spring.io/spring-ai/reference/api/multimodal.html

# 🚀 Spring AI with Kotlin: 실전 프로젝트 (11~20장)

## 11장: [실전] 간단한 Q&A 챗봇 API 구현

*학습 목표*: 1~3장에서 배운 ChatModel과 PromptTemplate을 활용하여 간단한 질의응답이 가능한 Spring Boot REST API를 구현합니다.

*핵심 키워드*: @RestController, @PostMapping, ChatModel, API 엔드포인트

## 12장: [실전] 이력서 분석 및 JSON 추출기

*학습 목표*: 4장의 BeanOutputParser와 Kotlin data class를 활용하여, 비정형 텍스트(이력서)에서 이름, 경력, 스킬 등을 구조화된 JSON으로 추출하는 API를 구현합니다.

*핵심 키워드*: BeanOutputParser, data class, 정형 데이터 추출, REST API

## 13장: [실전] 시맨틱 문서 검색 API

*학습 목표*: 5, 6장의 EmbeddingClient와 VectorStore를 활용하여, 여러 문서를 임베딩하여 저장하고 키워드 검색이 아닌 '의미 기반' 검색 API를 구현합니다.

*핵심 키워드*: EmbeddingClient, VectorStore, similaritySearch, 시맨틱 검색

## 14장: [실전] 사내 위키 기반 RAG 챗봇 (기초)

*학습 목표*: 7, 8장의 RAG 개념을 적용하여, 로컬의 Markdown/PDF 문서를 기반으로 질문에 답변하는 챗봇 API를 구현합니다. (데이터 로딩, 분할, 저장, 검색, 답변)

*핵심 키워드*: RAG, DocumentLoader, TextSplitter, VectorStore, ChatModel

## 15장: [실전] RAG 챗봇 고도화 (데이터 파이프라인)

*학습 목표*: 14장의 RAG 챗봇에 ETL 파이프라인을 구축합니다. 애플리케이션 시작 시 자동으로 문서를 로드하고, 중복을 체크하며, 벡터 저장소를 업데이트하는 프로세스를 구현합니다.

*핵심 키워드*: ETL, @PostConstruct, 데이터 파이프라인, 문서 관리

## 16장: [실전] AI 기반 스마트 날씨 알리미

*학습 목표*: 9장의 Function Calling을 활용합니다. 사용자가 "오늘 서울 날씨 어때?"라고 물으면, LLM이 이를 '날씨 조회 함수'로 인식하고, 실제 외부 날씨 API를 호출한 결과를 바탕으로 답변을 생성합니다.

*핵심 키워드*: Function Calling, 외부 API 연동, RestTemplate / WebClient

## 17장: [실전] AI 에이전트: 주문 관리 봇

*학습 목표*: Function Calling을 심화하여, '주문 상태 조회', '배송지 변경' 등 여러 Kotlin 함수(Service Bean)를 AI가 상황에 맞게 선택하고 호출하는 멀티-턴(Multi-turn) 에이전트를 구현합니다.

*핵심 키워드*: AI Agent, Multi-turn, Function Calling, Spring Service 연동

## 18장: [실전] 상품 이미지 태그 생성기

*학습 목표*: 10장의 멀티모달 기능을 활용하여, 상품 이미지를 업로드하면 AI가 이미지의 특징, 색상, 스타일 등을 분석하여 마케팅 태그(JSON 형식)를 생성하는 API를 구현합니다.

*핵심 키워드*: Vision API, BeanOutputParser, 이미지 분석, 상품 태깅

## 19장: [실전] 대화형 챗봇 (채팅 기록 관리)

*학습 목표*: ChatModel에 이전 대화 기록(List<Message>)을 함께 전달하여, 문맥을 기억하고 연속적인 대화가 가능한 챗봇을 구현합니다. (Spring AI의 ChatMemory 활용 또는 수동 관리)

*핵심 키워드*: ChatMemory, 대화 기록(Context), UserMessage, AssistantMessage, ChatModel

## 20장: [종합] Spring Boot + Kotlin + Spring AI 풀스택 챗봇

*학습 목표*: 지금까지 배운 모든 기술(RAG, Function Calling, Chat Memory)을 통합하고, 간단한 웹 UI(Thymeleaf 또는 React)를 연동하여 실제 작동하는 RAG 기반의 대화형 AI 챗봇 서비스를 완성합니다.

*핵심 키워드*: Capstone Project, Full-Stack, RAG, Function Calling, WebSocket (옵션)