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
- 로그 출력
```Text
logging.level.hello.chat=trace
```

> ## WebSocketConfig 작성
```Java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * 엔드 포인트를 등록하기 위해 registerStompEndpoints 를 override 한다.
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 앞으로 웹 소켓 서버의 엔드포인트는 /ws 이다.
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS();
    }

    /**
     * Message Broker 를 설정하기 위해 configureMessageBroker 를 override 한다.
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // enableSimpleBroker() 를 사용해서 /sub 가 prefix 로 붙은 destination 의 클라이언트에게
        // 메세지를 보낼 수 있도록 Simple Broker 를 등록한다.
        registry.enableSimpleBroker("/sub"); // 구독

        // setApplicationDestinationPrefixes() 를 사용해서 /pub 가 prefix 로 붙은 메시지들은
        // @MessageMapping 이 붙은 method 로 바운드된다.
        registry.setApplicationDestinationPrefixes("/pub"); // 발행
    }
}
```
> ## ChatDto 작성
```Java
/**
 * 통신시에 주고 받을 메시지 형식을 작성
 * RestController 의 경우 @RequestBody 가 쓰알 Dto 는 Setter 가 필요 없다.
 * (ObjectMapper 를 통해 변환이 이루어지기 때문)
 */
@Getter
@Builder
public class ChatDto {
    private String channelId;
    private String writerId;
    private String message;
}
```

> ## WebSocketController 작성
```Java
@RestController
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    private final SimpMessagingTemplate simpleMessagingTemplate;

    /**
     * @MessageMapping annotation 은 메시지의 destination 이 /hello 였다면 해당 sendMessage() method 가 불리도록 해준다.
     * - sendMessage() 에서는 simpMessagingTemplate.convertAndSend 를 통해
     *   /sub/chat/{channelId} 채널을 구독 중인 클라이언트에게 메시지를 전송한다.
     * - SimpMessagingTemplate 는 특정 브로커로 메시지를 전달한다.
     * - 현재는 외부 브로커를 붙이지 않았으므로 인메모리에 정보를 저장한다.
     * 메시지의 payload 는 인자(chatDto)로 들어온다.
     * @param chatDto
     * @param accessor
     */
    @MessageMapping("/chat")
    public void sendMessage(@RequestBody ChatDto chatDto, SimpMessageHeaderAccessor accessor) {
        log.info("sendMessage : {}", chatDto.getMessage());
        simpleMessagingTemplate.convertAndSend("/sub/chat/" + chatDto.getChannelId(), chatDto);
    }
}

```

######
> ## build.gradle 코드 추가
```Text
dependencies {
   // 추가된 dependencies
   implementation 'org.springframework.boot:spring-boot-starter-security'
}
```