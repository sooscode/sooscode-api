# SoosCode API Server

SoosCode 플랫폼의 **핵심 비즈니스 로직을 담당하는 메인 백엔드 서버**입니다.  
사용자 인증, 실시간 협업(채팅/코드), 화상 강의(LiveKit), 그리고 컴파일 워커와의 오케스트레이션을 담당합니다.

MSA(Microservices Architecture) 구조의 허브 역할을 수행하며,  
 WebSocket, Redis를 활용해 고성능 실시간 처리를 지원합니다.

---

## 핵심 특징

- **강력한 보안 인증 (Security)**
  - JWT (Access/Refresh) + OAuth2 (Google) 하이브리드 인증
  - Redis 기반 Refresh Token 관리 및 로그아웃 시 Blacklist 처리

- **실시간 협업 환경 (Real-time)**
  - STOMP 프로토콜 기반의 채팅 및 코드 에디터 동기화
  - 강의실별 채널(`topic`) 구독/발행 구조로 데이터 브로드캐스팅

- **화상 강의 연동 (LiveKit)**
  - LiveKit Token 동적 발급 및 룸 관리
  - Webhook 이벤트를 수신하여 강의실 상태 자동 동기화

- **컴파일 오케스트레이션**
  - 클라이언트 요청을 검증(Validation) 후 워커 서버로 실행 위임
  - `CompletableFuture`를 사용한 비동기 콜백 처리 구조
---

## 처리 흐름 (Process Flow)

### 1. 인증 및 로그인 (Auth Flow)
1. **Request**: 클라이언트가 이메일/비밀번호 또는 구글 소셜 로그인 요청 (`/api/auth/login`).
2. **Verification**: `AuthService`에서 계정 정보 검증 및 활성화 상태 확인.
3. **Token Issue**: Access Token(Stateless)과 Refresh Token 생성.
4. **Caching**: Refresh Token을 **Redis**에 저장 (TTL 설정)하여 관리.
5. **Response**: Access Token을 반환하고 쿠키 또는 헤더에 설정.

### 2. 강의실 입장 및 소켓 연결 (Classroom Entry)
1. **Join Request**: 사용자가 강의실 입장 요청 (`/api/class/{id}`).
2. **Validation**: `ClassRoomService`에서 강의 시간, 참여 권한, 강사 여부 등을 검증.
3. **Socket Connection**: 검증 통과 시 `/ws` 엔드포인트로 WebSocket 연결 시도.
4. **Interceptor**: `StompSessionInterceptor`가 헤더의 Access Token을 가로채 유효성 재검증.
5. **Subscribe**: 인증 성공 시 해당 강의실의 토픽(`/topic/class/{id}`) 구독 시작.

### 3. 실시간 채팅 및 코드 동기화 (Real-time Interaction)
1. **Publish**: 클라이언트가 메시지 또는 코드 변경 사항 전송 (`/app/chat/send` 등).
2. **Handling**: `ChatMessageController`가 메시지를 수신하여 DB 저장(채팅) 또는 로직 처리.
3. **Broadcast**: `SimpMessagingTemplate`을 통해 해당 강의실 구독자 전원에게 메시지 발행 (`/topic/class/{id}`).
4. **Update**: 구독 중인 클라이언트들이 실시간으로 화면 업데이트.

### 4. 화상 강의 연결 (LiveKit Flow)
1. **Token Request**: 클라이언트가 화상 강의 접속 요청 (`/api/livekit/token`).
2. **Generation**: `LivekitTokenService`가 LiveKit SDK를 사용해 사용자 식별 정보가 담긴 **JWT Token** 생성.
3. **Connect**: 클라이언트가 해당 토큰으로 LiveKit Media Server에 직접 연결.
4. **Webhook**: LiveKit 서버의 이벤트(참여자 입장, 방 종료 등)가 API 서버로 전송되어 강의실 상태 업데이트.

### 5. 코드 실행 (Compile Pipeline)
1. **Request**: 클라이언트가 코드 실행 요청 (`/api/compile/run`, Base64 코드 포함).
2. **Validation**: `CodeValidator` 및 `BlacklistFilter`를 통해 악성 코드(System.exit 등) 차단.
3. **Async Wait**: `CompileFutureStore`에 `jobId`를 키로 하는 `CompletableFuture` 생성 후 대기 상태 전환.
4. **Delegation**: `CompileWorkerClient`를 통해 Worker 서버로 HTTP 실행 요청 전송.
5. **Callback**: Worker가 실행 완료 후 결과값을 `/api/compile/callback/{jobId}`로 전송.
6. **Completion**: 대기 중이던 Future가 완료(complete)되며 클라이언트에게 최종 결과 응답.

---
