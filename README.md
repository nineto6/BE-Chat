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
> ## 실행 결과
- front 이미지
<br/>
<img src="https://github.com/nineto6/BE-Chat/blob/main/md_resource/fe_resource_02.png">
<br/>
<hr/>

###### 20230616
> ## 계획
- 사이드 프로젝트 BE-Login에서 진행했던 JWT 인증 가져오기(Board 관련 부분 제거)
- 기능 : 회원가입, 로그인, 로그아웃, 토큰 재발급

> ## 작성
- 상세 정보 [BE-Login](https://github.com/nineto6/BE-Login) 참고
- 코드 작성 리스트
    - build.gradle dependency 추가
    - application.properties 코드 추가
    ---
    - TB_USER.sql 작성
    - UserDto 작성
    - UserMapper 작성
    - UserMapper XML 작성 (mybatis)
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

    private final RedisTemplate<String, String> redisTemplate;

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
        if(StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
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
> ## 테스트
<br/>
<hr/>