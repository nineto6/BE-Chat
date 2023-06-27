package hello.chat.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.chat.model.ChatDto;
import hello.chat.model.ChatRoomDto;
import hello.chat.model.UserDto;
import hello.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

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
