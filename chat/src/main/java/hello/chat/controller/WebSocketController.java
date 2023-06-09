package hello.chat.controller;

import hello.chat.dto.ChatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
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
    public void sendMessage(ChatDto chatDto, SimpMessageHeaderAccessor accessor) {
        simpleMessagingTemplate.convertAndSend("/sub/chat" + chatDto.getChannelId(), chatDto);
    }
}
