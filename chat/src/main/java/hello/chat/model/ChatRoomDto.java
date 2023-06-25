package hello.chat.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
