package hello.chat.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 통신시에 주고 받을 메시지 형식을 작성
 */
@Getter @Setter
@Builder
public class ChatDto {
    private String channelId;
    private String writerId;
    private String chat;
}
