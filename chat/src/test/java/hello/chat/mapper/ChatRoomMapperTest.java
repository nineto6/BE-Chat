package hello.chat.mapper;

import hello.chat.model.ChatDto;
import hello.chat.model.ChatRoomDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

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
