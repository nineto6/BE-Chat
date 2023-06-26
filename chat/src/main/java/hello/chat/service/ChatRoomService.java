package hello.chat.service;

import hello.chat.model.ChatRoomDto;

import java.util.List;
import java.util.Optional;

public interface ChatRoomService {
    void create(ChatRoomDto chatRoomDto);
    Optional<ChatRoomDto> join(String chanelId);
    List<ChatRoomDto> findAll();
    void delete(String writerId, String channelId);
}
