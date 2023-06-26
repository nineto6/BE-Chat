package hello.chat.controller;

import hello.chat.common.codes.AuthConstants;
import hello.chat.common.codes.SuccessCode;
import hello.chat.common.utils.TokenUtils;
import hello.chat.controller.response.ApiResponse;
import hello.chat.model.ChatRoomDto;
import hello.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    @PostMapping
    public ResponseEntity<ApiResponse> createChatRoom(HttpServletRequest request, @RequestBody Map<String, String> titleMap) {
        Map<String, String> userIdAndUserNmMap = getUserIdAndUserNmInMap(request);

        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .channelId(UUID.randomUUID().toString())
                .title(titleMap.get("title"))
                .writerId(userIdAndUserNmMap.get("writerId"))
                .writerNm(userIdAndUserNmMap.get("writerNm"))
                .dateTime(LocalDateTime.now())
                .build();

        chatRoomService.create(chatRoomDto);

        ApiResponse ar = ApiResponse.builder()
                .result("")
                .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .build();

        return ResponseEntity.ok().body(ar);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> findAllChatRoom() {

        List<ChatRoomDto> chatRoomDtoList = chatRoomService.findAll();

        ApiResponse ar = ApiResponse.builder()
                .result(chatRoomDtoList)
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .build();

        return ResponseEntity.ok().body(ar);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteChatRoom(HttpServletRequest request, @RequestBody Map<String, String> channelIdMap) {
        Map<String, String> userIdAndUserNmInMap = getUserIdAndUserNmInMap(request);

        chatRoomService.delete(userIdAndUserNmInMap.get("writerId"), channelIdMap.get("channelId"));

        ApiResponse ar = ApiResponse.builder()
                .result("")
                .resultMsg(SuccessCode.DELETE_SUCCESS.getMessage())
                .resultCode(SuccessCode.DELETE_SUCCESS.getStatus())
                .build();

        return ResponseEntity.ok().body(ar);
    }

    /**
     * Request 안에 존재하는 JWT token 정보를 기반으로 userId, userNm 을 Map 으로 반환하는 메서드
     * @param request
     * @return
     */
    private static Map<String, String> getUserIdAndUserNmInMap(HttpServletRequest request) {
        // 1. Request 에서 Header 추출
        String header = request.getHeader(AuthConstants.AUTH_HEADER);

        // 2. Header 에서 JWT Refresh Token 추출
        String token = TokenUtils.getTokenFormHeader(header);

        // 3. token 으로부터 userId, userNm 추출
        String userId = TokenUtils.getUserIdFormAccessToken(token);
        String userNm = TokenUtils.getUserNmFormAccessToken(token);

        Map<String, String> chatRoomIdAndNmMap = new HashMap<>();
        chatRoomIdAndNmMap.put("writerId", userId);
        chatRoomIdAndNmMap.put("writerNm", userNm);

        return chatRoomIdAndNmMap;
    }
}
