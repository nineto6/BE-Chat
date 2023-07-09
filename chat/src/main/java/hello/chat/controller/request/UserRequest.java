package hello.chat.controller.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRequest {

    // 사용자 아이디 4 ~ 20자
    @Pattern(regexp = "^[a-z0-9]{4,20}$")
    @NotBlank
    private String userId;

    // 사용자 패스워드 "8 ~ 20자 영문 대 소문자, 숫자, 특수문자를 사용
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[#?!@$%^&*-]).{8,20}$")
    @NotBlank
    private String userPw;

    // 사용자 이름
    @NotBlank
    @Pattern(regexp = "[가-힣]{2,5}")
    private String userNm;

    @Builder
    public UserRequest(String userId, String userPw, String userNm) {
        this.userId = userId;
        this.userPw = userPw;
        this.userNm = userNm;
    }
}
