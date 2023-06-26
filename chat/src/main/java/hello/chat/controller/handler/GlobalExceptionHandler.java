package hello.chat.controller.handler;

import hello.chat.common.codes.ErrorCode;
import hello.chat.config.exception.BusinessExceptionHandler;
import hello.chat.controller.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessExceptionHandler.class)
    public ResponseEntity<ApiResponse> handleBusinessException(final RuntimeException e) {
        log.info("Business Exception Handling Stack Trace : ", e);

        ApiResponse ar = ApiResponse.builder()
                .result(e.getMessage())
                .resultCode(ErrorCode.BUSINESS_EXCEPTION_ERROR.getStatus())
                .resultMsg(ErrorCode.BUSINESS_EXCEPTION_ERROR.getMessage())
                .build();

        return ResponseEntity.ok().body(ar);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(final RuntimeException e) {
        log.info("Global Exception Handling Stack Trace : ", e);

        ApiResponse ar = ApiResponse.builder()
                .result("")
                .resultCode(ErrorCode.BAD_REQUEST.getStatus())
                .resultMsg(ErrorCode.BAD_REQUEST.getMessage())
                .build();

        return ResponseEntity.ok().body(ar);
    }
}
