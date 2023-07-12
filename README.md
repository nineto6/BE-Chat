# 🛠️NINETO6 사이드프로젝트 만들기

## 시작하기 전에...
WebSocket 위에서 동작하는 STOMP(Simple Text Oriented Messaging Protocol)를 이용하여 실시간 채팅방을 구현해보려고 한다.

> 현재 `Front-End` 와 `Back-End` 는 다른 환경에서 개발하고 있음

## 요구사항
어떤 사용자는 회원가입 후 로그인을 진행하여 인증된 사용자인 경우에만 채팅방 생성 및 입장할 수 있으며,
생성된 채팅방은 다른 사용자의 채팅방 목록에 뜨게 되어 채팅방 입장시 실시간 채팅을 할 수 있게 한다.

## 출처
- STOMP를 참고한 사이트 출처
    - [dldmswjd322 블로그](https://velog.io/@dldmswjd322/Spring-boot-React-STOMP%EB%A1%9C-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%B1%84%ED%8C%85-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-1-Spring-boot-%EC%84%9C%EB%B2%84-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0)

## 개발 환경
- Project : Gradle
- SpringBoot 버전 : 2.7.12
- Java 버전 : 11
- 초기 Dependencies
   - Spring Web, Websocket : 5.3.27
   - Lombok : 1.18.26
- 추가된 Dependencies
   - Spring Security : 5.7.8
   - Mybatis : 3.5.11
   - H2 Database : 2.1.214
   - Redis : 2.7.11
   - Jwt : 0.9.1
   - Jaxb-Runtime(DataTypeConverter) : 2.3.2
   - Json-Simple : 1.1.1
<br/>
<hr/>

###### 20230608
> ## 계획
- STOMP를 이용하여 간단하게 구독 및 발행 과 MessageMapping을 이용하여 Front와 연결 및 실시간 채팅이 가능한지 코드 작성 및 확인
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
 * RestController 의 경우 @RequestBody 가 쓰일 Dto 는 Setter 가 필요 없다.
 * (ObjectMapper 를 통해 변환이 이루어지기 때문)
 */
@Getter
@Builder
public class ChatDto {
    private String channelId;
    private String writerNm;
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
        log.info("Channel : {}, getWriterNm : {}, sendMessage : {}", chatDto.getChannelId(), chatDto.getWriterNm(), chatDto.getMessage());
        simpleMessagingTemplate.convertAndSend("/sub/chat/" + chatDto.getChannelId(), chatDto);
    }
}
```
> ## 실행 결과
- Front 이미지
<br/>
<img src="https://github.com/nineto6/BE-Chat/blob/main/md_resource/fe_resource_02.png">
<br/>
<hr/>

###### 20230616
> ## 계획
- 사이드 프로젝트 BE-Login에서 진행했던 JWT 인증 가져오기(Board 관련 부분 제거)
- 기능 : 회원가입, 로그인, 로그아웃, 토큰 재발급

> ## 작성
- 상세 정보 [BE-Login](https://github.com/nineto6/BE-Login) 20230603까지 참고
- 코드 작성 리스트
    - build.gradle dependency 추가
    - application.properties 코드 추가
    ---
    - TB_USER.sql 작성
    - UserDto 작성
    - UserMapper 작성
    - UserMapper XML 작성 (mybatis)
    - UserMapperTest 작성
    - UserService 인터페이스 작성
    - UserServiceImpl 작성
    ---
    - RedisConfig 작성
    - RedisRepository 작성
    - RefreshToken 작성
    ---
    - ErrorCode 작성
    - SuccessCode 작성
    - BusinessExceptionHandler 작성
    ---
    - UserDetailsDto 작성
    - UserDetailsServiceImpl 작성
    - CustomAuthenticationFilter 작성
    - CustomAuthenticationProvider 작성
    - CustomAuthFailureHandler 작성
    ---
    - AuthConstants 작성
    - NetUtils 작성
    - TokenUtils 작성
    - JwtAuthorziationFilter 작성
    - CustomAuthSuccessHandler 작성
    - JwtToken 작성
    - WebSecurityConfig 작성
    ---
    - ApiResponse 작성 (result 타입 String -> Object 형식으로 변경)
    - ErrorResponse 작성
    - UserController 작성

> ## 계획
- STOMP 연결시 요청 방식이 다르기 때문에  JwtAuthorizationFilter에서 토큰 확인 및 인증이 안되는 현상이 발생하여
- STOMP 전용 Jwt 인증 인터셉터를 만들어서 연결 커맨드가 Connect시에 인증 절차를 밟게한다.
    - STOMP 전용 Jwt 인증 인터셉터에 보내야 하기 때문에 JwtAuthroziationFilter에서 /ws 엔드포인트로 된 URI 요청시 doFilter와 함께 인증 로직 없이 다음 필터로 이동하게 해야한다.  
- JWT 만료 및 인증이 불가능하게 되어 Exception 발생시 Exception과 함께 Error 전용 핸들러에 보내게 하여 STOMP 연결이 불가능하게 에러 메세지와 함께 커맨드를 ERROR로 바꾸어준다.
> ## ErrorCode 코드 추가
- UNAUTHORIZED_ERROR 추가
```Java
/**
 * [공통 코드] API 통신에 대한 '에러 코드'를 Enum 형태로 관리를 한다.
 * Global Error CodeList : 전역으로 발생하는 에러코드를 관리한다.
 * custom Error CodeList : 업무 페이지에서 발생하는 에러코드를 관리한다.
 * Error Code Constructor : 에러코드를 직접적으로 사용하기 위한 생성자를 구성한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum ErrorCode {
    BUSINESS_EXCEPTION_ERROR(200, "B999", "Business Exception Error"),

    /**
     * *********************************** custom Error CodeList ********************************************
     */
    // Transaction Insert Error
    INSERT_ERROR(200, "9999", "Insert Transaction Error Exception"),

    // Transaction Update Error
    UPDATE_ERROR(200, "9999", "Update Transaction Error Exception"),

    // Transaction Delete Error
    DELETE_ERROR(200, "9999", "Delete Transaction Error Exception"),

    // Authorization 관련 Error
    UNAUTHORIZED_ERROR(200, "7777", "Unauthenticated User"), // 코드 추가

    ; // End

    /**
     * *********************************** Error Code Constructor ********************************************
     */
    // 에러 코드의 '코드 상태'을 반환한다.
    private int status;

    // 에러 코드의 '코드간 구분 값'을 반환한다.
    private String divisionCode;

    // 에러코드의 '코드 메시지'을 반환한다.
    private String message;

    // 생성자 구성
    ErrorCode(final int status, final String divisionCode, final String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
```

> ## TokenUtils 코드 변경
- isValidAccessToken 메서드 변경
```Java
/**
 * JWT 관련된 토큰 Util
 */
@Slf4j
@Component
public class TokenUtils {

    private static String accessSecretKey;
    private static String refreshSecretKey;

    // ... 기존 코드

    /**
     * 유효한 엑세스 토큰인지 확인 해주는 메서드
     * @param token String  : 토큰
     * @return      boolean : 유효한지 여부 반환
     */
    public static boolean isValidAccessToken(String token) {
        try {
            Claims claims = getAccessTokenToClaimsFormToken(token);

            log.info("expireTime : {}", claims.getExpiration());
            log.info("userId : {}", claims.get("uid"));
            log.info("userNm : {}", claims.get("unm"));

            return true;
        } catch (ExpiredJwtException exception) {
            log.error("Token Expired");
            throw exception;
        } catch (JwtException exception) {
            log.error("Token Tampered", exception);
            throw exception;
        } catch(NullPointerException exception) {
            throw exception;
        }
    }

    // ... 기존 코드
}
```

> ## JwtAuthorizationFilter 코드 변경 및 추가
- 추가 부분 : 2-1
- 변경 부분 : throw new BusinessExceptionHandler("에러 내용", ErrorCode.UNAUTHORIZED_ERROR)
```Java
/**
 * 지정한 URL 별 JWT 유효성 검증을 수행하며 직접적인 사용자 '인증'을 확인합니다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 토큰이 필요하지 않은 API URL 에 대해서 배열로 구성합니다.
        List<String> list = Arrays.asList(
                "/api/users/login",  // 로그인
                "/api/users/reissue", // 리프레쉬 토큰으로 재발급
                "/api/users/signup", // 회원가입
                "/api/users/duplicheck" // 회원가입 하위 사용 가능 ID 확인
        );

        // 2. 토큰이 필요하지 않은 API URL 의 경우 => 로직 처리 없이 다음 필터로 이동
        if(list.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("[] header URI : {}", request.getRequestURI());

        //  --- 코드 추가 ---
        // 2-1. 첫 /ws 엔드포인트가 붙은 URL 일 경우 로직 처리 없이 다음 필터로 이동 (preHandler 로 JWT 인증 처리) 코드 추가
        if(request.getRequestURI().startsWith("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }
        //  ----------------

        // 3. OPTIONS 요청일 경우 => 로직 처리 없이 다음 필터로 이동
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        // [STEP1] Client 에서 API 를 요청할 때 Header 를 확인합니다.
        String header = request.getHeader(AuthConstants.AUTH_HEADER);
        log.debug("[+] header Check: {}", header);

        try {
            // [STEP2-1] Header 내에 토큰이 존재하는 경우
            if(header != null && !header.equalsIgnoreCase("")) {

                // [STEP2] Header 내에 토큰을 추출합니다.
                String token = TokenUtils.getTokenFormHeader(header);

                // [STEP3] 추출한 엑세스 토큰이 유효한지 여부를 체크합니다.
                if(token != null && TokenUtils.isValidAccessToken(token)) {

                    // [STEP 3-1] Redis 에 해당 Access-Token 로그아웃 확인
                    String isLogout = redisTemplate.opsForValue().get(token);

                    // 로그아웃이 되어 있지 않은 경우 해당 토큰은 정상적으로 작동
                    if(ObjectUtils.isEmpty(isLogout)){
                        // [STEP4] 토큰을 기반으로 사용자 아이디를 반환 받는 메서드
                        String userId = TokenUtils.getUserIdFormAccessToken(token);
                        log.debug("[+] userId Check: {}", userId);

                        // [STEP5] 사용자 아이디가 존재하는지 여부 체크
                        if(userId != null && !userId.equalsIgnoreCase("")) {
                            filterChain.doFilter(request, response);
                        } else {
                            // 사용자 아이디가 존재 하지 않을 경우
                            throw new BusinessExceptionHandler("Token isn't userId", ErrorCode.UNAUTHORIZED_ERROR); // 변경
                        }
                    } else {
                        // 현재 토큰이 로그아웃 되어 있는 경우
                        throw new BusinessExceptionHandler("Token is logged out", ErrorCode.UNAUTHORIZED_ERROR); // 변경
                    }
                } else {
                    // 토큰이 유효하지 않은 경우
                    throw new BusinessExceptionHandler("Token is invalid", ErrorCode.UNAUTHORIZED_ERROR); // 변경
                }
            }
            else {
                // [STEP2-1] 토큰이 존재하지 않는 경우
                throw new BusinessExceptionHandler("Token is null", ErrorCode.UNAUTHORIZED_ERROR); // 변경
            }
        } catch (Exception e) {
            // Token 내에 Exception 이 발생 하였을 경우 => 클라이언트에 응답값을 반환하고 종료합니다.
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            JSONObject jsonObject = jsonResponseWrapper(e);
            printWriter.print(jsonObject);
            printWriter.flush();
            printWriter.close();
        }
    }
    // ... 기존 코드
}
```

> ## ChatPreHandler 작성
```Java
@RequiredArgsConstructor
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class ChatPreHandler implements ChannelInterceptor {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // 연결 요청일 경우
        if(StompCommand.CONNECT.equals(headerAccessor != null ? headerAccessor.getCommand() : null)) {
            String authorizationHeader = String.valueOf(headerAccessor.getNativeHeader(AuthConstants.AUTH_HEADER));

            String token = "";
            // Header 에 Authorization 값 추출 (대괄호 제거)
            String authorizationHeaderStr = authorizationHeader.replace("[","").replace("]","");
            log.info("authorization Header String : {}", authorizationHeaderStr);

            // Bearer 형식으로 되어있는지 검증
            if (authorizationHeaderStr.startsWith("Bearer ")) {
                // Bearer 형식일 경우 token 추출
                token = authorizationHeaderStr.replace("Bearer ", "");
                log.info("token : {}", token);
            } else {
                log.error("Authorization 헤더 형식이 틀립니다. : {}", authorizationHeader);
                throw new MalformedJwtException("Token is Invalid");
            }

            try{
                // 토큰 값이 유효한지 검증
                if(TokenUtils.isValidAccessToken(token)) {
                    // 토큰으로부터 userId 값 추출
                    String userId = TokenUtils.getUserIdFormAccessToken(token);
                    if(userId.isEmpty()) { // 토큰에 userId 값이 없을 경우
                        throw new MalformedJwtException("Token is Invalid");
                    }
                }
            } catch (ExpiredJwtException exception) {
                throw new MalformedJwtException("Token Expired");
            } catch (Exception exception) {
                throw new MalformedJwtException("Token is Invalid");
            }
        }

        else if (StompCommand.ERROR.equals(headerAccessor.getCommand())) {
            throw new MessageDeliveryException("error");
        }

        return message;
    }
}
```

> ## ChatErrorHandler 작성
```Java
@Component
@Slf4j
public class ChatErrorHandler extends StompSubProtocolErrorHandler {

    public ChatErrorHandler() {
        super();
    }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]>clientMessage, Throwable ex) {
        Throwable exception = ex;

        // exception 타입이 MessageDeliveryException일 경우
        if (exception instanceof MessageDeliveryException) {
            log.info("메세지 예외 : {}", exception.getMessage());
            return handleUnauthorizedException(clientMessage, ex.getMessage());
        }

        // exception 타입이 MalformedJwtException 경우
        if(exception instanceof MalformedJwtException) {
            log.info("멀폼 예외 : {}", exception.getMessage());
            return handleUnauthorizedException(clientMessage, ex.getMessage());
        }

        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Message<byte[]> handleUnauthorizedException(Message<byte[]> clientMessage, String message) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .result(message)
                .resultCode(ErrorCode.UNAUTHORIZED_ERROR.hashCode())
                .resultMsg(ErrorCode.UNAUTHORIZED_ERROR.getDivisionCode())
                .build();

        return prepareErrorMessage(clientMessage, errorResponse, ErrorCode.UNAUTHORIZED_ERROR.getMessage());
    }

    private Message<byte[]> prepareErrorMessage(Message<byte[]> clientMessage, ErrorResponse errorResponse, String message) {

        // Command를 ERROR로 변경
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);

        accessor.setMessage(message);
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(message.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }
}
```

> ## WebSocketConfig 코드 추가
- setErrorHandler로 chatErrorHandler 추가
- interceptor에 chatPreHandler 추가
```Java
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final ChatPreHandler chatPreHandler; // 추가
    private final ChatErrorHandler chatErrorHandler; // 추가

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
        registry.setErrorHandler(chatErrorHandler); // 추가
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

    @Override // 추가
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatPreHandler);
    }
}
```
> ## 실행 결과
- 25일에 찍은 ChatPreHandler Log 이미지
    - JwtAuthorizationPreHandler -> ChatPreHandler
<img src="https://github.com/nineto6/BE-Chat/blob/main/md_resource/be_resource_02_authorization.png">

<br/>
<hr/>

##### 20230620
> ## 계획
- 인증된 사용자가 채팅방 생성 및 제거를 할 수 있으며, 채팅방을 생성 후 입장해야지만 실시간 채팅을 할 수 있게 한다. (채팅방 생성시 UUID를 부여한다)
- 인증된 사용자는 자신이 생성한 채팅방만 제거를 할 수 있다.
- subscribe(구독)의 경우 ChatRoomPreHandler 를 이용하여 생성 된 채팅방인지 체크한다.
- publish(발행)의 경우 WebSocketController에서 MessageMapping을 이용한 메서드에서 구독한 클라이언트에게 발행한 메세지를 보내기 전에 생성 된 채팅방인지 체크한다.

> ## TB_CHAT_ROOM.sql 추가
- CHANNEL_ID는 UUID를 사용할 계획이므로 VARCHAR(36)을 부여한다. 
```SQL
create table tb_chat_room(
   chat_room_sq       INT AUTO_INCREMENT PRIMARY KEY,
   channel_id         VARCHAR(36) NOT NULL,
   title              VARCHAR(30) NOT NULL,
   writer_id          VARCHAR(20) NOT NULL,
   writer_nm          VARCHAR(20) NOT NULL,
   date_time          TIMESTAMP NOT NULL
);
```

> ## SuccessCode 코드 변경
- 성공 코드의 '코드 값'을 반환하는 code 제거
```Java
@Getter
public enum SuccessCode {

    /**
     * ******************************* Success CodeList ***************************************
     */
    // 조회 성공 코드 (HTTP Response: 200 OK)
    SELECT_SUCCESS(200, "SELECT SUCCESS"),
    // 삭제 성공 코드 (HTTP Response: 200 OK)
    DELETE_SUCCESS(200, "DELETE SUCCESS"),
    // 삽입 성공 코드 (HTTP Response: 201 Created)
    INSERT_SUCCESS(201, "INSERT SUCCESS"),
    // 수정 성공 코드 (HTTP Response: 201 Created)
    UPDATE_SUCCESS(204,  "UPDATE SUCCESS"),

    ; // End

    /**
     * ******************************* Success Code Constructor ***************************************
     */
    // 성공 코드의 '코드 상태'를 반환한다.
    private final int status;
    
    // 성공 코드의 '코드 값'을 반환한다.
    //private final String code;

    // 성공 코드의 '코드 메시지'를 반환한다.s
    private final String message;

    // 생성자 구성
    SuccessCode(final int status, final String message) {
        this.status = status;
        this.message = message;
    }
}
```
> ## ChatPreHandler를 JwtAuthorizationPreHandler로 이름 변경

> ## ChatRoomDto 작성
```Java
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomDto {
    private int chatRoomSq; // 기본키
    private String channelId;
    private String title;
    private String writerId;
    private String writerNm;
    private LocalDateTime dateTime;// 생성 날짜

    @Builder
    public ChatRoomDto(int chatRoomSq, String channelId, String title, String writerId, String writerNm, LocalDateTime dateTime) {
        this.chatRoomSq = chatRoomSq;
        this.channelId = channelId;
        this.title = title;
        this.writerId = writerId;
        this.writerNm = writerNm;
        this.dateTime = dateTime;
    }
}
```

> ## ChatRoomMapper 작성
```Java
@Mapper
public interface ChatRoomMapper {
    void save(ChatRoomDto chatRoomDto); // 저장
    Optional<ChatRoomDto> findByChannelId(String chanelId); // 채널 아이디로 조회
    List<ChatRoomDto> findAll(); // 모두 조회
    void deleteByWriterIdAndChannelId(@Param("writerId") String writerId, @Param("channelId") String channelId); // 생성 아이디 AND 채널 아이디로 삭제
}
```

> ## ChatRoomMapper.xml 작성
```XML
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="hello.chat.mapper.ChatRoomMapper">

    <!-- 방 생성 -->
    <insert id="save" useGeneratedKeys="true" keyProperty="chatRoomSq">
        INSERT INTO TB_CHAT_ROOM
        (CHANNEL_ID, TITLE, WRITER_ID, WRITER_NM, DATE_TIME)
        VALUES (#{channelId}, #{title}, #{writerId}, #{writerNm}, #{dateTime})
    </insert>

    <!-- 채널 ID 로 조회 -->
    <select id="findByChannelId" resultType="hello.chat.model.ChatRoomDto">
        SELECT t1.*
        FROM TB_CHAT_ROOM t1
        WHERE CHANNEL_ID = #{channelId}
    </select>

    <!-- 채널 모두 조회 -->
    <select id="findAll" resultType="hello.chat.model.ChatRoomDto">
        SELECT t1.*
        FROM TB_CHAT_ROOM t1
    </select>

    <!-- writerId 와 channelId 로 ChatRoom 삭제 -->
    <delete id="deleteByWriterIdAndChannelId">
        DELETE FROM TB_CHAT_ROOM
        WHERE WRITER_ID = #{writerId} AND CHANNEL_ID = #{channelId}
    </delete>
</mapper>
```

> ## 테스트 케이스 전용 H2 Database 생성 및 Spring Profile 설정
- chat\src\main\resources\application.properties 에 local 전용 프로필 설정
```TEXT
# ADD profile 
spring.profiles.active=local

# h2 database
spring.datasource.url=jdbc:h2:tcp://localhost/~/test
spring.datasource.username=sa
spring.datasource.password=

#MyBatis log
mybatis.configuration.map-underscore-to-camel-case=true
logging.level.hello.chat.mapper.mybatis=trace

# Log
logging.level.hello.chat=trace

# Secret Key
custom.jwt-access-secret-key=accessSecretKey
custom.jwt-refresh-secret-key=refreshSecretKey

# Redis
spring.redis.host=localhost
spring.redis.port=6379
```

- chat\src\test\resources\application.properties 에 test 전용 프로필 설정

```Text
# ADD profile
spring.profiles.active=test

# h2 database (testcase)
spring.datasource.url=jdbc:h2:tcp://localhost/~/testcase
spring.datasource.username=sa
spring.datasource.password=

#MyBatis log
mybatis.configuration.map-underscore-to-camel-case=true
logging.level.hello.chat.mapper.mybatis=trace

# Log
logging.level.hello.chat=trace

# Secret Key
custom.jwt-access-secret-key=accessSecretKey
custom.jwt-refresh-secret-key=refreshSecretKey

# Redis
spring.redis.host=localhost
spring.redis.port=6379
```

> ## ChatRoomMapperTest 작성 및 테스트
```Java
@SpringBootTest
@Transactional
public class ChatRoomMapperTest {

    @Autowired
    ChatRoomMapper chatRoomMapper;

    @Test
    @DisplayName("ChatRoom 저장 테스트")
    void save() {
        // given
        ChatRoomDto chatRoom = ChatRoomDto.builder()
                .channelId("저장 테스트")
                .title("아무나")
                .writerId("asd123")
                .writerNm("대한민국")
                .dateTime(LocalDateTime.now())
                .build();

        // when
        chatRoomMapper.save(chatRoom);

        // then
        Optional<ChatRoomDto> selectedChatRoom = chatRoomMapper.findByChannelId("저장 테스트");
        assertThat(selectedChatRoom.get().getChannelId()).isEqualTo("저장 테스트");
    }

    @Test
    @DisplayName("ChatRoom 채널 아이디로 조회 테스트")
    void findByChanelId() {
        // given
        ChatRoomDto chatRoom = ChatRoomDto.builder()
                .channelId("조회 테스트")
                .title("아무나123")
                .writerId("asd123")
                .writerNm("고기")
                .dateTime(LocalDateTime.now())
                .build();
        chatRoomMapper.save(chatRoom);

        // when
        Optional<ChatRoomDto> selectedChatRoom = chatRoomMapper.findByChannelId("조회 테스트");

        // then
        assertThat(selectedChatRoom.get().getChannelId()).isEqualTo("조회 테스트");
    }

    @Test
    @DisplayName("ChatRoom 모두 조회 테스트")
    void findAll() {
        // given
        ChatRoomDto chatRoom1 = ChatRoomDto.builder()
                .channelId("12345")
                .title("채팅할사람~")
                .writerId("asd123")
                .writerNm("리듬")
                .dateTime(LocalDateTime.now())
                .build();
        ChatRoomDto chatRoom2 = ChatRoomDto.builder()
                .channelId("678910")
                .title("채팅만")
                .writerId("qwe456")
                .writerNm("소리")
                .dateTime(LocalDateTime.now())
                .build();

        chatRoomMapper.save(chatRoom1);
        chatRoomMapper.save(chatRoom2);

        // when
        List<ChatRoomDto> chatRoomMapperAll = chatRoomMapper.findAll();

        // then
        assertThat(chatRoomMapperAll.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("ChatRoom ChannelId AND writerId로 삭제 테스트")
    void deleteByChannelId() {
        // given
        ChatRoomDto chatRoom1 = ChatRoomDto.builder()
                .channelId("123123")
                .title("아무나1")
                .writerId("asd123")
                .writerNm("한국1")
                .dateTime(LocalDateTime.now())
                .build();
        ChatRoomDto chatRoom2 = ChatRoomDto.builder()
                .channelId("456456")
                .title("아무나2")
                .writerId("qwe456")
                .writerNm("한국2")
                .dateTime(LocalDateTime.now())
                .build();

        chatRoomMapper.save(chatRoom1);
        chatRoomMapper.save(chatRoom2);

        // when
        chatRoomMapper.deleteByWriterIdAndChannelId("asd123", "123123");

        // then
        List<ChatRoomDto> chatRoomMapperAll = chatRoomMapper.findAll();
        assertThat(chatRoomMapperAll.size()).isEqualTo(1);
        assertThat(chatRoomMapperAll.get(0).getChannelId()).isEqualTo("456456");
    }
}
```

<br/>
<hr/>

###### 20230625
> ## ChatRoomService 작성
```Java
public interface ChatRoomService {
    void create(ChatRoomDto chatRoomDto);
    Optional<ChatRoomDto> join(String chanelId);
    List<ChatRoomDto> findAll();
    void delete(String writerId, String channelId);
}
```

> ## ChatRoomServiceImpl 작성
```Java
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomMapper chatRoomMapper;

    @Override
    @Transactional
    public void create(ChatRoomDto chatRoomDto) {
        chatRoomMapper.save(chatRoomDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChatRoomDto> join(String chanelId) {
        return chatRoomMapper.findByChannelId(chanelId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomDto> findAll() {
        return chatRoomMapper.findAll();
    }

    @Override
    @Transactional
    public void delete(String writerId, String channelId) {
        Optional<ChatRoomDto> byChannelId = chatRoomMapper.findByChannelId(channelId);

        // channelId 로 조회 후 존재할 경우 삭제
        if(byChannelId.isPresent()) {
            chatRoomMapper.deleteByWriterIdAndChannelId(writerId, channelId);
        }

        // 채팅방이 존재하지 않을 경우 예외 throw
        throw new BusinessExceptionHandler("chat room does not exist", ErrorCode.BUSINESS_EXCEPTION_ERROR);
    }
}
```

> ## ErrorCode 코드 변경
```Java
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum ErrorCode {
    BUSINESS_EXCEPTION_ERROR(200, "B999", "Business Exception Error"),
    GLOBAL_EXCEPTION_ERROR(200, "C999", "Global Exception Error"),
    /**
     * *********************************** custom Error CodeList ********************************************
     */
    /**
     * Transaction Insert Error
     */
    INSERT_ERROR(200, "9999", "Insert Transaction Error Exception"),

    /**
     * Transaction Update Error
     */
    UPDATE_ERROR(200, "9999", "Update Transaction Error Exception"),

    /**
     * Transaction Delete Error
     */
    DELETE_ERROR(200, "9999", "Delete Transaction Error Exception"),

    /**
     * Authorization 관련 Error
     */
    UNAUTHORIZED_ERROR(200, "7777", "Unauthenticated User"),

    /**
     * 400 BAD_REQUEST: 잘못된 요청
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "6666", "Bad Request"),

    /**
     * 404 NOT_FOUND: 리소스를 찾을 수 없음
     */
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "6666", "Information not found")
    ; // End

    /**
     * *********************************** Error Code Constructor ********************************************
     */
    // 에러 코드의 '코드 상태'을 반환한다.
    private int status;

    // 에러 코드의 '코드간 구분 값'을 반환한다.
    private String divisionCode;

    // 에러코드의 '코드 메시지'을 반환한다.
    private String message;

    // 생성자 구성
    ErrorCode(final int status, final String divisionCode, final String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
```

> ## ChatRoomController 작성
```Java
@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    @PostMapping
    public ResponseEntity<ApiResponse> createChatRoom(HttpServletRequest request, @RequestBody Map<String, String> titleMap) {
        Map<String, String> userIdAndUserNmMap = getUserIdAndUserNmInMap(request);

        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .channelId(UUID.randomUUID().toString())
                .title(titleMap.get("title"))
                .writerId(userIdAndUserNmMap.get("writerId"))
                .writerNm(userIdAndUserNmMap.get("writerNm"))
                .dateTime(LocalDateTime.now())
                .build();

        chatRoomService.create(chatRoomDto);

        ApiResponse ar = ApiResponse.builder()
                .result("")
                .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .build();

        return ResponseEntity.ok().body(ar);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> findAllChatRoom() {

        List<ChatRoomDto> chatRoomDtoList = chatRoomService.findAll();

        ApiResponse ar = ApiResponse.builder()
                .result(chatRoomDtoList)
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .build();

        return ResponseEntity.ok().body(ar);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteChatRoom(HttpServletRequest request, @RequestBody Map<String, String> channelIdMap) {
        Map<String, String> userIdAndUserNmInMap = getUserIdAndUserNmInMap(request);

        chatRoomService.delete(userIdAndUserNmInMap.get("writerId"), channelIdMap.get("channelId"));

        ApiResponse ar = ApiResponse.builder()
                .result("")
                .resultMsg(SuccessCode.DELETE_SUCCESS.getMessage())
                .resultCode(SuccessCode.DELETE_SUCCESS.getStatus())
                .build();

        return ResponseEntity.ok().body(ar);
    }

    /**
     * Request 안에 존재하는 JWT token 정보를 기반으로 userId, userNm 을 Map 으로 반환하는 메서드
     * @param request
     * @return
     */
    private static Map<String, String> getUserIdAndUserNmInMap(HttpServletRequest request) {
        // 1. Request 에서 Header 추출
        String header = request.getHeader(AuthConstants.AUTH_HEADER);

        // 2. Header 에서 JWT Refresh Token 추출
        String token = TokenUtils.getTokenFormHeader(header);

        // 3. token 으로부터 userId, userNm 추출
        String userId = TokenUtils.getUserIdFormAccessToken(token);
        String userNm = TokenUtils.getUserNmFormAccessToken(token);

        Map<String, String> chatRoomIdAndNmMap = new HashMap<>();
        chatRoomIdAndNmMap.put("writerId", userId);
        chatRoomIdAndNmMap.put("writerNm", userNm);

        return chatRoomIdAndNmMap;
    }
}
```

> ## GlobalExceptionHandler 작성
```Java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessExceptionHandler.class)
    public ResponseEntity<ApiResponse> handleBusinessException(final RuntimeException e) {
        log.info("Business Exception Handling Stack Trace : ", e);

        ApiResponse ar = ApiResponse.builder()
                .result(e.getMessage())
                .resultCode(ErrorCode.BUSINESS_EXCEPTION_ERROR.getStatus())
                .resultMsg(ErrorCode.BUSINESS_EXCEPTION_ERROR.getMessage())
                .build();

        return ResponseEntity.ok().body(ar);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(final RuntimeException e) {
        log.info("Global Exception Handling Stack Trace : ", e);

        ApiResponse ar = ApiResponse.builder()
                .result("")
                .resultCode(ErrorCode.BAD_REQUEST.getStatus())
                .resultMsg(ErrorCode.BAD_REQUEST.getMessage())
                .build();

        return ResponseEntity.ok().body(ar);
    }
}
```

> ## ChatErrorHandler 코드 변경
```Java
@Component
@Slf4j
public class ChatErrorHandler extends StompSubProtocolErrorHandler {

    public ChatErrorHandler() {
        super();
    }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]>clientMessage, Throwable ex) {
        Throwable exception = ex;

        if (exception instanceof MessageDeliveryException) {
            log.info("메세지 예외 : {}", exception.getMessage(), exception); // 변경 부분
            return handleMessageDeliveryException(clientMessage, ex.getMessage(), exception); // 변경 부분
        }

        if(exception instanceof MalformedJwtException) {
            log.info("멀폼 예외 : {}", exception.getMessage(), exception); // 변경 부분
            return handleUnauthorizedException(clientMessage, ex.getMessage(), exception); // 변경 부분
        }

        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Message<byte[]> handleUnauthorizedException(Message<byte[]> clientMessage, String message, Throwable ex) { // 변경 부분
        ErrorResponse errorResponse = ErrorResponse.builder()
                .result(message)
                .resultCode(ErrorCode.UNAUTHORIZED_ERROR.getStatus()) // 변경 부분
                .resultMsg(ErrorCode.UNAUTHORIZED_ERROR.getDivisionCode())
                .build();

        return prepareErrorMessage(clientMessage, errorResponse, ErrorCode.UNAUTHORIZED_ERROR.getMessage());
    }

    // 변경 부분
    private Message<byte[]> handleMessageDeliveryException(Message<byte[]> clientMessage, String message, Throwable ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .result(message)
                .resultCode(ErrorCode.BUSINESS_EXCEPTION_ERROR.getStatus())
                .resultMsg(ErrorCode.BUSINESS_EXCEPTION_ERROR.getDivisionCode())
                .build();

        return prepareErrorMessage(clientMessage, errorResponse, ErrorCode.UNAUTHORIZED_ERROR.getMessage());
    }

    private Message<byte[]> prepareErrorMessage(Message<byte[]> clientMessage, ErrorResponse errorResponse, String message) {

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);

        accessor.setMessage(message);
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(message.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }
}
```

> ## ChatRoomPreHandler 작성
```Java
@RequiredArgsConstructor
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 98)
public class ChatRoomPreHandler implements ChannelInterceptor {
    private final ChatRoomService chatRoomService;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        StompCommand stompCommand = headerAccessor != null ? headerAccessor.getCommand() : null;

        // SUBSCRIBE(구독) 일 때 만 ChatRoom 에 등록한 채팅방이 있을 경우 로직
        if(StompCommand.SUBSCRIBE.equals(stompCommand)) {
            log.info("Destination : {}", headerAccessor.getDestination());
            String destination = headerAccessor.getDestination() != null ? headerAccessor.getDestination() : null;
            String channelId = null;

            if(destination == null) {
                throw new MessageDeliveryException("Invalid payload");
            }

            String[] split = headerAccessor.getDestination().split("/sub/chat/");
            channelId = split[split.length - 1];
            log.info("channelId : {}", channelId);

            // ChatRoomService 의 join 후 채팅방이 있을 경우 연결 성공
            Optional<ChatRoomDto> join = chatRoomService.join(channelId);

            if(join.isEmpty()) {
                // 값이 존재하지 않을 경우 연결 실패
                throw new MessageDeliveryException("ChatRoom does not exist");
            }
        }
        return message;
    }
}
```

> ## WebSocketConfig 코드 변경
- JwtAuthorizationPreHandler 추가
- ChatRoomPreHandler 추가
```Java
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtAuthorizationPreHandler jwtAuthorizationPreHandler; // 변경 부분
    private final ChatRoomPreHandler chatRoomPreHandler; // 변경 부분
    private final ChatErrorHandler chatErrorHandler;

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
        registry.setErrorHandler(chatErrorHandler);
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

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtAuthorizationPreHandler); // 변경 부분
        registration.interceptors(chatRoomPreHandler); // 변경 부분
    }
}
```

> ## WebSocketController 코드 변경
```Java
@RestController
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    private final SimpMessagingTemplate simpleMessagingTemplate;
    private final ChatRoomService chatRoomService;

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
    @MessageMapping("/chat") // Publish
    public void sendMessage(@RequestBody ChatDto chatDto, SimpMessageHeaderAccessor accessor) {
        log.info("Channel : {}, getWriterNm : {}, sendMessage : {}", chatDto.getChannelId(), chatDto.getWriterNm(), chatDto.getMessage());

        // publish 요청시 channelId 로 조회 후 값이 없을 경우 Exception Throw, 있을 경우 구독한 클라이언트에게 Send
        Optional<ChatRoomDto> join = chatRoomService.join(chatDto.getChannelId());

        if(join.isEmpty()) {
            throw new BusinessExceptionHandler("ChatRoom does not exist", ErrorCode.BUSINESS_EXCEPTION_ERROR);
        }

        simpleMessagingTemplate.convertAndSend("/sub/chat/" + chatDto.getChannelId(), chatDto);
    }
}
```

> ## 실행 결과
- WebSocket 요청 처리 로그 이미지
    - publish(발행)
    <br><img src="https://github.com/nineto6/BE-Chat/blob/main/md_resource/be_resource_03_pub.png">
    - subscribe(구독)
    <br><img src="https://github.com/nineto6/BE-Chat/blob/main/md_resource/be_resource_04_sub.png">

<br/>
<hr/>

###### 20230709
> ## 계획
- Spring Validation을 이용하여 JSON -> Object로 바인딩시 검사
- UserRequest 객체를 만들어서 바인딩 시 검사 조건 정의
- 바인딩에 실패시 Exception을 처리 할 ExceptionHandler 추가
- 사용자의 연결 및 연결 해제 로그를 출력하기 위해 ChatLogHandler를 만들어서WebSocketConfig에 인터셉터 등록

> ## UserRequest 작성

> ## ChatRoomController 코드 변경

> ## UserController 코드 변경

> ## ChatLogHandler 작성

> ## WebSocketConfig 코드 추가

> ## GlobalExceptionHandler 코드 변경

> ## 실행 결과
