package hello.chat.common.codes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * [공통 코드] API 통신에 대한 '에러 코드'를 Enum 형태로 관리를 한다.
 * Global Error CodeList : 전역으로 발생하는 에러코드를 관리한다.
 * custom Error CodeList : 업무 페이지에서 발생하는 에러코드를 관리한다.
 * Error Code Constructor : 에러코드를 직접적으로 사용하기 위한 생성자를 구성한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public enum ErrorCode {
    BUSINESS_EXCEPTION_ERROR(200, "B999", "Business Exception Error"),
    GLOBAL_EXCEPTION_ERROR(200, "C999", "Global Exception Error"),

    /**
     * *********************************** custom Error CodeList ********************************************
     */

    /**
     * Transaction Insert Error
     */
    INSERT_ERROR(200, "9999", "Insert Transaction Error Exception"),

    /**
     * Transaction Update Error
     */
    UPDATE_ERROR(200, "9999", "Update Transaction Error Exception"),

    /**
     * Transaction Delete Error
     */
    DELETE_ERROR(200, "9999", "Delete Transaction Error Exception"),

    /**
     * Authorization 관련 Error
     */
    UNAUTHORIZED_ERROR(200, "7777", "Unauthenticated User"),

    /**
     * 400 BAD_REQUEST: 잘못된 요청
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "6666", "Bad Request"),

    /**
     * 404 NOT_FOUND: 리소스를 찾을 수 없음
     */
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "6666", "Information not found")
    ; // End

    /**
     * *********************************** Error Code Constructor ********************************************
     */
    // 에러 코드의 '코드 상태'을 반환한다.
    private int status;

    // 에러 코드의 '코드간 구분 값'을 반환한다.
    private String divisionCode;

    // 에러코드의 '코드 메시지'을 반환한다.
    private String message;

    // 생성자 구성
    ErrorCode(final int status, final String divisionCode, final String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
