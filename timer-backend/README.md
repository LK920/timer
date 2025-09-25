# Timer Backend

## 개요

Spring Boot 3와 Java 17을 기반으로 구현된 RESTful API 서버입니다.
Domain Driven Design (DDD) 아키텍처를 적용하여 유지보수성과 확장성을 고려한 설계로 구현되었습니다.

## 목적

- **아키텍처**: DDD 패턴을 통한 계층별 책임 분리
- **API 문서화**: Swagger/OpenAPI를 통한 자동 문서 생성
- **품질 보증**: JUnit 5 기반 100% 테스트 커버리지
- **에러 처리**: 전역 예외 처리를 통한 일관된 에러 응답

## 실행 방법

### 1. Gradle을 통한 실행
```bash
# 애플리케이션 실행
./gradlew bootRun

# 테스트 실행
./gradlew test

# 빌드
./gradlew build
```

### 2. Docker를 통한 실행
```bash
# Docker 이미지 빌드
docker build -t timer-backend .

# 컨테이너 실행
docker run -p 8080:8080 timer-backend
```

### 3. 서비스 접근
- **API 서버**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI 문서**: http://localhost:8080/v3/api-docs

## API 엔드포인트

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/timer` | 타이머 상태 조회 |
| POST | `/api/timer/start?durationSeconds={초}` | 타이머 시작 |
| POST | `/api/timer/pause` | 타이머 일시정지 |
| POST | `/api/timer/resume` | 타이머 재개 |
| POST | `/api/timer/reset` | 타이머 초기화 |

### 요청/응답 예제

#### 타이머 시작
```bash
POST /api/timer/start?durationSeconds=300
```

#### 응답
```json
{
  "running": true,
  "startTime": 1703075200000,
  "durationSeconds": 300,
  "remainingSeconds": 300,
  "currentRemainingSeconds": 295
}
```

## 기술 스택

- **Framework**: Spring Boot 3.5.6
- **Language**: Java 17
- **Build Tool**: Gradle
- **Testing**: JUnit 5, Mockito
- **Documentation**: Swagger/OpenAPI 3
- **Architecture**: Domain Driven Design (DDD)

## DDD 아키텍처 구조

### 패키지 구조
```
src/main/java/com/acupofcoffee/timer/
├── TimerApplication.java                    # Spring Boot 메인 클래스
├── presentation/                            # 표현 계층
│   └── timer/
│       ├── TimerApi.java                   # API 인터페이스 (Swagger)
│       ├── TimerController.java            # REST 컨트롤러
│       ├── dto/                            # 표현 계층 DTO
│       └── mapper/                         # 계층 간 매핑
├── application/                            # 응용 계층
│   └── timer/
│       ├── TimerApplicationService.java   # 응용 서비스
│       ├── TimerStateDto.java             # 응용 계층 DTO
│       └── StartTimerCommand.java         # 커맨드 객체
├── domain/                                 # 도메인 계층
│   └── timer/
│       ├── Timer.java                     # 도메인 엔티티
│       ├── TimerId.java                   # Value Object
│       ├── Duration.java                  # Value Object
│       ├── TimerStatus.java               # Enum
│       ├── TimerRepository.java           # Repository 인터페이스
│       └── TimerDomainService.java        # 도메인 서비스
├── infrastructure/                         # 인프라 계층
│   └── timer/
│       └── InMemoryTimerRepository.java   # Repository 구현체
├── common/                                 # 공통 모듈
│   ├── dto/                               # 공통 DTO
│   ├── exception/                         # 예외 처리
│   └── filter/                            # 필터
└── config/                                # 설정
    └── SwaggerConfig.java                 # Swagger 설정
```

### 계층별 책임

#### Presentation Layer
- REST API 엔드포인트 정의
- HTTP 요청/응답 처리
- DTO 변환 및 검증
- 예외 처리

#### Application Layer
- 비즈니스 유스케이스 조율
- 트랜잭션 관리
- 도메인 서비스 호출
- DTO 변환

#### Domain Layer
- 핵심 비즈니스 로직
- 도메인 규칙 검증
- 엔티티 및 Value Object
- Repository 인터페이스 정의

#### Infrastructure Layer
- 외부 시스템 연동
- Repository 구현
- 데이터 영속성

## 주요 특징

### 1. Swagger 기반 API 문서화
- 인터페이스 기반 컨트롤러 설계
- 자동 API 문서 생성
- Mock API 지원

### 2. 전역 예외 처리
- `@RestControllerAdvice` 기반 통합 에러 처리
- 표준화된 에러 응답 포맷
- 커스텀 예외 지원

### 3. 포괄적인 테스트 커버리지
- 계층별 단위 테스트 (15개 테스트 파일)
- 통합 테스트
- 100% 코드 커버리지 목표

### 4. API 로깅
- Spring Boot Filter 기반 자동 로깅
- 요청/응답 정보 기록
- 성능 모니터링

## 비즈니스 규칙

### 타이머 제약사항
- 최소 시간: 1초
- 최대 시간: 24시간 (86400초)
- 동시 실행: 단일 타이머만 지원

### 상태 전이
```
[STOPPED] → start() → [RUNNING]
[RUNNING] → pause() → [PAUSED]
[PAUSED] → resume() → [RUNNING]
[RUNNING/PAUSED] → reset() → [STOPPED]
```

## 에러 코드

| 코드 | 메시지 | HTTP 상태 |
|------|--------|-----------|
| TIMER_001 | 타이머가 이미 실행 중입니다 | 409 |
| TIMER_002 | 타이머가 실행 중이 아닙니다 | 409 |
| TIMER_003 | 타이머를 일시정지할 수 없는 상태입니다 | 409 |
| VALIDATION_001 | 잘못된 시간 설정입니다 | 400 |
| VALIDATION_002 | 타이머 시간이 너무 짧습니다 | 400 |
| VALIDATION_003 | 타이머 시간이 너무 깁니다 (최대 24시간) | 400 |
| SYSTEM_001 | 서버 내부 오류가 발생했습니다 | 500 |

## 개발 환경 요구사항

- **Java**: 17 이상
- **Gradle**: 8.0 이상
- **Spring Boot**: 3.5.6

## 확장 방향

- [ ] 데이터베이스 영속성 (JPA/Hibernate)
- [ ] 다중 타이머 지원
- [ ] 사용자 인증/권한 관리
- [ ] 타이머 히스토리 기능
- [ ] 알림/웹훅 기능
- [ ] 메트릭 및 모니터링