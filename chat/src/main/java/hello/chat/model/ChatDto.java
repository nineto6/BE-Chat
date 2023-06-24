package hello.chat.model;

import lombok.Builder;
import lombok.Getter;

/**
 * 통신시에 주고 받을 메시지 형식을 작성
 * RestController 의 경우 @RequestBody 가 쓰알 Dto 는 Setter 가 필요 없다.
 * (ObjectMapper 를 통해 변환이 이루어지기 때문)
 */
@Getter
@Builder
public class ChatDto {
    private String channelId;
    private String writerNm;
    private String message;
}
