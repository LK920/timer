# Web Timer Application

## 개요

웹 기반 타이머 애플리케이션으로, 사용자가 시간을 설정하여 타이머를 시작/일시정지/재개/초기화할 수 있는 기능을 제공합니다.
현대적인 웹 기술 스택을 활용하여 구현되었으며, 마이크로서비스 아키텍처와 컨테이너화를 통해 확장 가능하고 유지보수가 용이한 구조로 설계되었습니다.

## 목적

- **학습**: 현대적인 웹 개발 기술 스택 실습
- **실무**: DDD(Domain Driven Design) 아키텍처 패턴 적용
- **운영**: Docker를 활용한 컨테이너화 및 배포 자동화
- **품질**: 100% 테스트 커버리지를 통한 신뢰성 있는 코드베이스 구축

## 프로젝트 구조

### 📁 timer-frontend
Svelte 5 + SvelteKit + TypeScript 기반의 프론트엔드 애플리케이션
[→ 프론트엔드 README](./timer-frontend/README.md)

### 📁 timer-backend
Spring Boot 3 + Java 17 기반의 백엔드 REST API 서버
[→ 백엔드 README](./timer-backend/README.md)

## 사용 기술

### Frontend
- **Framework**: Svelte 5, SvelteKit
- **Language**: TypeScript
- **Build Tool**: Vite
- **Styling**: Native CSS
- **HTTP Client**: Fetch API

### Backend
- **Framework**: Spring Boot 3.5.6
- **Language**: Java 17
- **Architecture**: Domain Driven Design (DDD)
- **Documentation**: Swagger/OpenAPI 3
- **Testing**: JUnit 5, Mockito
- **Build Tool**: Gradle

### Infrastructure
- **Containerization**: Docker, Docker Compose
- **Reverse Proxy**: Nginx
- **Database**: In-Memory (향후 확장 가능)

## 아키텍처

### 전체 시스템 아키텍처
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Nginx Proxy   │    │   Backend       │
│   (Svelte)      │◄──►│   (Port 3000)   │◄──►│   (Spring Boot) │
│   Port 80       │    │                 │    │   Port 8080     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Backend DDD 레이어 구조
```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  Controllers, DTOs, Mappers, Exception Handlers            │
├─────────────────────────────────────────────────────────────┤
│                    Application Layer                        │
│  Application Services, Commands, DTOs                      │
├─────────────────────────────────────────────────────────────┤
│                    Domain Layer                             │
│  Entities, Value Objects, Domain Services, Repositories    │
├─────────────────────────────────────────────────────────────┤
│                    Infrastructure Layer                     │
│  Repository Implementations, External Integrations         │
└─────────────────────────────────────────────────────────────┘
```

### 주요 설계 원칙

1. **Domain Driven Design (DDD)**
   - 계층별 명확한 책임 분리
   - 도메인 로직의 중앙 집중화
   - 인터페이스를 통한 의존성 역전

2. **API First Development**
   - Swagger/OpenAPI 기반 API 문서화
   - 인터페이스 기반 컨트롤러 설계
   - Mock API 지원

3. **Test Driven Development**
   - JUnit 5 기반 단위/통합 테스트
   - 100% 테스트 커버리지 목표
   - 계층별 독립적 테스트

4. **Containerization**
   - Docker 기반 서비스 분리
   - Docker Compose를 통한 오케스트레이션
   - 환경 독립적 배포

## 실행 방법

### 1. Docker Compose 사용 (권장)
```bash
# 전체 시스템 시작
docker-compose up --build

# 백그라운드 실행
docker-compose up -d --build

# 시스템 종료
docker-compose down
```

### 2. 개별 서비스 실행

#### Frontend
```bash
cd timer-frontend
npm install
npm run dev
```

#### Backend
```bash
cd timer-backend
./gradlew bootRun
```

### 3. 서비스 접근

- **웹 애플리케이션**: http://localhost:3000
- **API 서버**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **API 문서**: http://localhost:8080/v3/api-docs

## 주요 기능

### 타이머 기본 기능
- ✅ 타이머 시작 (1초 ~ 24시간)
- ✅ 타이머 일시정지/재개
- ✅ 타이머 초기화
- ✅ 실시간 남은 시간 표시

### 기술적 특징
- ✅ RESTful API 설계
- ✅ 실시간 상태 동기화
- ✅ 전역 에러 처리
- ✅ 자동 API 문서화
- ✅ 컨테이너 오케스트레이션
- ✅ 포괄적인 테스트 커버리지

## 확장 계획

- [ ] 사용자 인증/권한 관리
- [ ] 다중 타이머 지원
- [ ] 타이머 히스토리 저장
- [ ] 알림/사운드 기능
- [ ] 모바일 반응형 UI
- [ ] PWA (Progressive Web App) 지원

## 개발 환경

- **Java**: 17+
- **Node.js**: 18+
- **Docker**: 20+
- **Docker Compose**: 2+

---

이 프로젝트는 현대적인 웹 개발 기술 스택을 학습하고 실제 프로덕션 환경에서 사용 가능한 애플리케이션을 구축하기 위한 종합적인 예제입니다.