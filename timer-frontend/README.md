# Timer Frontend

## 개요

Svelte 5와 SvelteKit을 기반으로 구현된 현대적인 웹 타이머 애플리케이션입니다.
TypeScript를 사용하여 타입 안정성을 확보하고, 반응형 디자인으로 다양한 디바이스에서 사용할 수 있습니다.

## 목적

- **사용자 경험**: 직관적이고 반응성 좋은 타이머 인터페이스
- **현대적 기술**: Svelte 5의 최신 기능 활용
- **타입 안정성**: TypeScript를 통한 안전한 코드 작성
- **성능 최적화**: Vite 번들러를 통한 빠른 빌드와 개발 경험

## 실행 방법

### 1. 개발 모드
```bash
# 의존성 설치
npm install

# 개발 서버 실행
npm run dev
```

### 2. 프로덕션 빌드
```bash
# 정적 파일 빌드
npm run build

# 빌드 결과 미리보기
npm run preview
```

### 3. Docker를 통한 실행
```bash
# Docker 이미지 빌드
docker build -t timer-frontend .

# 컨테이너 실행
docker run -p 80:80 timer-frontend
```

### 4. 서비스 접근
- **개발 모드**: http://localhost:5173
- **프로덕션**: http://localhost (Docker 사용 시)

## 기술 스택

- **Framework**: Svelte 5, SvelteKit
- **Language**: TypeScript
- **Build Tool**: Vite
- **Styling**: Native CSS
- **HTTP Client**: Fetch API
- **Container**: Docker + Nginx

## 프로젝트 구조

```
src/
├── routes/
│   ├── +layout.svelte              # 전역 레이아웃
│   └── +page.svelte               # 메인 타이머 페이지
├── lib/
│   ├── components/                # 재사용 가능한 컴포넌트
│   ├── stores/                    # Svelte 상태 관리
│   ├── services/                  # API 서비스
│   └── types/                     # TypeScript 타입 정의
├── app.html                       # HTML 템플릿
└── app.css                        # 전역 스타일
```

## 주요 기능

### 타이머 UI
- ✅ 분 단위 시간 설정 (1-1440분)
- ✅ 시작/일시정지/재개/초기화 버튼
- ✅ 실시간 카운트다운 표시
- ✅ 진행률 표시 (원형 프로그레스)
- ✅ 반응형 디자인

### 사용자 경험
- ✅ 직관적인 버튼 레이아웃
- ✅ 상태별 시각적 피드백
- ✅ 키보드 단축키 지원
- ✅ 모바일 터치 최적화

## API 통신

### 서비스 구조
```typescript
// API 호출 예제
const timerService = {
  getState: () => fetch('/api/timer'),
  start: (duration: number) => fetch(`/api/timer/start?durationSeconds=${duration}`, {
    method: 'POST'
  }),
  pause: () => fetch('/api/timer/pause', { method: 'POST' }),
  resume: () => fetch('/api/timer/resume', { method: 'POST' }),
  reset: () => fetch('/api/timer/reset', { method: 'POST' })
};
```

### 상태 관리
```typescript
// Svelte Store 활용
interface TimerState {
  running: boolean;
  startTime: number;
  durationSeconds: number;
  remainingSeconds: number;
  currentRemainingSeconds: number;
}
```

## 스타일링

### CSS 특징
- **Native CSS**: 프레임워크 독립적인 순수 CSS 사용
- **CSS Grid/Flexbox**: 현대적 레이아웃 기법
- **CSS Variables**: 테마 색상 관리
- **미디어 쿼리**: 반응형 디자인 구현

### 디자인 시스템
```css
:root {
  --primary-color: #4CAF50;
  --secondary-color: #FFC107;
  --danger-color: #F44336;
  --background-color: #f5f5f5;
  --text-color: #333;
  --border-radius: 8px;
}
```

## Nginx 설정

프로덕션 환경에서는 Nginx를 통해 정적 파일 서빙과 API 프록시를 처리합니다.

```nginx
server {
    listen 80;
    root /usr/share/nginx/html;
    index index.html;

    # SPA 라우팅 지원
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 프록시
    location /api/ {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 개발 가이드

### 컴포넌트 작성
```svelte
<script lang="ts">
  // TypeScript 사용
  interface Props {
    duration: number;
    onStart: (duration: number) => void;
  }

  const { duration, onStart }: Props = $props();
</script>

<!-- 마크업 -->
<button onclick={() => onStart(duration)}>
  시작
</button>
```

### 상태 관리
```typescript
// stores/timer.ts
import { writable } from 'svelte/store';

export const timerState = writable<TimerState>({
  running: false,
  startTime: 0,
  durationSeconds: 0,
  remainingSeconds: 0,
  currentRemainingSeconds: 0
});
```

## 브라우저 호환성

- **Chrome**: 90+
- **Firefox**: 88+
- **Safari**: 14+
- **Edge**: 90+

## 성능 최적화

### Vite 설정
- **Hot Module Replacement**: 개발 시 빠른 리로드
- **Tree Shaking**: 사용하지 않는 코드 제거
- **Code Splitting**: 청크 기반 로딩
- **Asset Optimization**: 이미지/CSS 최적화

### 번들 크기 최적화
- 미사용 라이브러리 제거
- Dynamic Import 활용
- CSS 압축 및 최적화

## 배포 전략

### 정적 파일 생성
```bash
# SvelteKit Adapter Static 사용
npm run build
# → build/ 디렉토리에 정적 파일 생성
```

### Docker 다단계 빌드
```dockerfile
# Build stage
FROM node:18-alpine AS builder
COPY . .
RUN npm ci && npm run build

# Production stage
FROM nginx:alpine
COPY --from=builder /app/build /usr/share/nginx/html
```

## 확장 계획

- [ ] PWA (Progressive Web App) 지원
- [ ] 오프라인 모드
- [ ] 다국어 지원 (i18n)
- [ ] 다크모드 토글
- [ ] 사운드/알림 기능
- [ ] 커스텀 타이머 프리셋

## 개발 환경 요구사항

- **Node.js**: 18 이상
- **npm**: 9 이상
- **TypeScript**: 5.0 이상