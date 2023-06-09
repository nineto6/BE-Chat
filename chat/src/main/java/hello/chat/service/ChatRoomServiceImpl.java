package hello.chat.service;

import hello.chat.common.codes.ErrorCode;
import hello.chat.config.exception.BusinessExceptionHandler;
import hello.chat.mapper.ChatRoomMapper;
import hello.chat.model.ChatRoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomMapper chatRoomMapper;

    @Override
    @Transactional
    public void create(ChatRoomDto chatRoomDto) {
        chatRoomMapper.save(chatRoomDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChatRoomDto> join(String chanelId) {
        return chatRoomMapper.findByChannelId(chanelId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomDto> findAll() {
        return chatRoomMapper.findAll();
    }

    @Override
    @Transactional
    public void delete(String writerId, String channelId) {
        Optional<ChatRoomDto> byChannelId = chatRoomMapper.findByChannelId(channelId);

        // 채팅방이 존재하지 않을 경우 예외 throw
        if(byChannelId.isEmpty()) {
            throw new BusinessExceptionHandler("chat room does not exist", ErrorCode.BUSINESS_EXCEPTION_ERROR);
        }

        Optional<ChatRoomDto> byWriterIdAndChannelId = chatRoomMapper.findByWriterIdAndChannelId(writerId, channelId);

        // 사용자 정보로 된 채팅방이 아닐경우 예외 throw
        if(byWriterIdAndChannelId.isEmpty()) {
            throw new BusinessExceptionHandler("not your chat room", ErrorCode.BUSINESS_EXCEPTION_ERROR);
        }

        // 존재할 경우 삭제
        chatRoomMapper.deleteByWriterIdAndChannelId(writerId, channelId);
    }
}
