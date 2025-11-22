# 개발 가이드

## 코딩 컨벤션

### Kotlin

#### 네이밍

- 클래스: PascalCase (예: `UserService`)
- 함수/변수: camelCase (예: `getUserById`)
- 상수: UPPER_SNAKE_CASE (예: `MAX_RETRY_COUNT`)
- 패키지: 소문자 (예: `com.example.service`)

#### 코드 스타일

```kotlin
// Good
class UserService(
    private val userRepository: UserRepository,
    private val emailService: EmailService
) {
    fun createUser(request: CreateUserRequest): User {
        val user = User(
            name = request.name,
            email = request.email
        )
        return userRepository.save(user)
    }
}

// Bad
class UserService(private val userRepository: UserRepository, private val emailService: EmailService) {
    fun createUser(request: CreateUserRequest): User {
        return userRepository.save(User(request.name, request.email))
    }
}
```

### Git 커밋 메시지

#### 형식

```
<type>: <subject>

<body>

<footer>
```

#### Type

- `feat`: 새로운 기능
- `fix`: 버그 수정
- `docs`: 문서 수정
- `style`: 코드 포맷팅
- `refactor`: 코드 리팩토링
- `test`: 테스트 코드
- `chore`: 빌드 설정 등

#### 예시

```
feat: 사용자 프로필 수정 기능 추가

- 프로필 이미지 업로드 기능 구현
- 닉네임 변경 API 추가
- 프로필 수정 이력 저장

Closes #123
```

## 브랜치 전략

### Git Flow

- `main`: 프로덕션 배포 브랜치
- `develop`: 개발 통합 브랜치
- `feature/*`: 기능 개발 브랜치
- `release/*`: 릴리스 준비 브랜치
- `hotfix/*`: 긴급 수정 브랜치

### 브랜치 네이밍

```
feature/user-profile-edit
feature/payment-integration
bugfix/login-error
hotfix/critical-security-patch
```

## 코드 리뷰

### 리뷰 체크리스트

- [ ] 코드가 요구사항을 충족하는가?
- [ ] 테스트 코드가 작성되었는가?
- [ ] 코딩 컨벤션을 준수하는가?
- [ ] 성능 이슈가 없는가?
- [ ] 보안 취약점이 없는가?
- [ ] 문서가 업데이트되었는가?

### 리뷰 규칙

1. 모든 PR은 최소 1명의 승인 필요
2. 리뷰 요청 후 24시간 이내 응답
3. 건설적인 피드백 제공
4. 코드 작성자는 모든 코멘트에 응답

## 테스트

### 테스트 피라미드

```
       /\
      /E2E\
     /------\
    /  통합  \
   /----------\
  /    단위    \
 /--------------\
```

### 단위 테스트

```kotlin
@Test
fun `사용자 생성 시 이메일 중복 체크`() {
    // given
    val email = "test@example.com"
    every { userRepository.existsByEmail(email) } returns true
    
    // when & then
    assertThrows<DuplicateEmailException> {
        userService.createUser(CreateUserRequest(
            name = "Test User",
            email = email
        ))
    }
}
```

### 통합 테스트

```kotlin
@SpringBootTest
@Testcontainers
class UserServiceIntegrationTest {
    
    @Container
    val postgres = PostgreSQLContainer<Nothing>("postgres:15")
    
    @Test
    fun `사용자 생성 및 조회`() {
        // 테스트 코드
    }
}
```

## API 설계

### RESTful API

- GET: 조회
- POST: 생성
- PUT: 전체 수정
- PATCH: 부분 수정
- DELETE: 삭제

### URL 규칙

```
GET    /api/users          # 사용자 목록
GET    /api/users/{id}     # 사용자 조회
POST   /api/users          # 사용자 생성
PUT    /api/users/{id}     # 사용자 수정
DELETE /api/users/{id}     # 사용자 삭제
```

### 응답 형식

```json
{
  "status": "success",
  "data": {
    "id": 1,
    "name": "홍길동"
  },
  "message": "사용자 조회 성공"
}
```

## 배포

### 환경

- **개발(dev)**: 개발자 테스트
- **스테이징(staging)**: QA 테스트
- **프로덕션(prod)**: 실제 서비스

### 배포 프로세스

1. develop 브랜치에 기능 머지
2. 스테이징 환경 배포
3. QA 테스트 수행
4. release 브랜치 생성
5. 프로덕션 배포
6. 모니터링

## 문서화

### API 문서

- Swagger/OpenAPI 사용
- 모든 엔드포인트 문서화
- 예제 요청/응답 포함

### 코드 문서

- 복잡한 로직에 주석 추가
- KDoc 형식 사용
- README 파일 작성
