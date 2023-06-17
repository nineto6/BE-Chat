package hello.chat.config.websocket;

import hello.chat.common.codes.AuthConstants;
import hello.chat.common.utils.TokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
            String authorizationHeaderStr = authorizationHeader.replace("[","").replace("]","");
            log.info("authorization Header String : {}", authorizationHeaderStr);

            if (authorizationHeaderStr.startsWith("Bearer ")) {
                token = authorizationHeaderStr.replace("Bearer ", "");
                log.info("token : {}", token);
            } else {
                log.error("Authorization 헤더 형식이 틀립니다. : {}", authorizationHeader);
                throw new MalformedJwtException("Token is Invalid");
            }

            try{
                if(TokenUtils.isValidAccessToken(token)) {
                    String userId = TokenUtils.getUserIdFormAccessToken(token);
                    if(userId.isEmpty()) {
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
