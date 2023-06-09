package hello.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

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
                // 클라이언트는 다른 origin 이므로 CORS 오류를 방지하기 위해 setAllowedOrigins 를 미리 사용해서
                // 허용할 origin 을 등록해둔다.
                .setAllowedOrigins("*");
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
