## WebSocket Decorator 구성

❗️ 이슈 원인
- FE useEffect 의존성 배열에 pathname, handleMatchNotification 등이 포함돼 있어 컴포넌트 렌더링마다 재실행됨
- 그 결과, 하나의 브라우저 탭에서 기존 연결 해제 없이 WebSocket 중복 연결됨

✅ 이슈 해결
- 정책: 브라우저 탭마다 WebSocket 세션 1개만 연결 가능
  - 사용자는 여러 브라우저 탭에서 동시에 접근할 수 있으므로, 사용자 단위가 아닌 탭 단위로 연결 허용
- 방법: WebSocketHandlerDecorator 상속한 커스텀 Decorator 생성해 서버 단에서 연결 제어
  - WebSocket 세션 연결 감지 Decorator (중복 연결 방지)
  - 세션 비정상 종료 감지 Decorator (디스코드 알림용)
  - Decorator Chain 방식으로 구성하여 기능 확장 용이