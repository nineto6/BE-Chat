# 🛠️NINETO6 사이드프로젝트 만들기

## 시작하기 전에...
WebSocket 위에서 동작하는 STOMP(Simple Text Oriented Messaging Protocol)를 이용하여 실시간 채팅방을 구현해보려고 한다.

> 현재 `Front-End` 와 `Back-End` 는 다른 환경에서 개발하고 있음

## 요구사항
어떤 사용자는 회원가입 후 로그인을 진행하여 인증된 사용자인 경우 채팅방 생성 및 입장할 수 있으며,
생성된 채팅방은 다른 사용자의 채팅방 목록에 뜨게 되어 채팅방 입장시 실시간 채팅을 할 수 있게 한다.

## 출처

## 개발 환경
- Project : Gradle
- SpringBoot 버전 : 2.7.12
- Java 버전 : 11
- 초기 Dependencies
   - Spring Web, Websocket:5.3.27
   - Lombok:1.18.26
- 추가된 Dependencies
   - Spring Security:5.7.8
<br/>
<hr/>

###### 20230608
> ## application.properties 코드 추가

> ## WebSocketConfig 작성

> ## WebSocketController 작성

> ## ChatDto 작성