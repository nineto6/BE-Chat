package hello.chat.mapper;

import hello.chat.model.ChatRoomDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ChatRoomMapper {
    void save(ChatRoomDto chatRoomDto); // 저장
    Optional<ChatRoomDto> findByChannelId(String chanelId); // 채널 아이디로 조회
    List<ChatRoomDto> findAll(); // 모두 조회
    Optional<ChatRoomDto> findByWriterIdAndChannelId(@Param("writerId") String writerId, @Param("channelId") String channelId); // 새성 아이디 AND 채널 아이디로 조회
    void deleteByWriterIdAndChannelId(@Param("writerId") String writerId, @Param("channelId") String channelId); // 생성 아이디 AND 채널 아이디로 삭제
}
