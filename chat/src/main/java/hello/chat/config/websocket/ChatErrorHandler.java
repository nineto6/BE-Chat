package hello.chat.config.websocket;

import hello.chat.common.codes.ErrorCode;
import hello.chat.config.exception.BusinessExceptionHandler;
import hello.chat.controller.response.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class ChatErrorHandler extends StompSubProtocolErrorHandler {

    public ChatErrorHandler() {
        super();
    }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]>clientMessage, Throwable ex) {
        Throwable exception = ex;

        if (exception instanceof MessageDeliveryException) {
            log.info("메세지 예외 : {}", exception.getMessage());
            return handleUnauthorizedException(clientMessage, ex.getMessage());
        }

        if(exception instanceof MalformedJwtException) {
            log.info("멀폼 예외 : {}", exception.getMessage());
            return handleUnauthorizedException(clientMessage, ex.getMessage());
        }

        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Message<byte[]> handleUnauthorizedException(Message<byte[]> clientMessage, String message) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .result(message)
                .resultCode(ErrorCode.UNAUTHORIZED_ERROR.hashCode())
                .resultMsg(ErrorCode.UNAUTHORIZED_ERROR.getDivisionCode())
                .build();

        return prepareErrorMessage(clientMessage, errorResponse, ErrorCode.UNAUTHORIZED_ERROR.getMessage());
    }

    private Message<byte[]> prepareErrorMessage(Message<byte[]> clientMessage, ErrorResponse errorResponse, String message) {

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);

        accessor.setMessage(message);
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(message.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }
}