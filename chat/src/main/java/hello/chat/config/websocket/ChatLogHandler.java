package hello.chat.config.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class ChatLogHandler extends ChannelInterceptorAdapter {
    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        switch(Objects.requireNonNull(accessor.getCommand())) {
            case CONNECT :
                log.info("웹소켓 연결");
                break;
            case DISCONNECT :
                log.info("웹소켓 연결 해제");
                break;
            default:
                break;
        }
    }
}
