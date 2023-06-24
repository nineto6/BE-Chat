package hello.chat.mapper;

import hello.chat.model.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
public class UserMapperTest {
    @Autowired
    UserMapper userMapper;

    @Test
    @DisplayName("유저 저장 테스트")
    public void save() {
        //given
        UserDto user = UserDto.builder()
                .userId("hello123")
                .userPw("123123")
                .userNm("헬로")
                .userSt("X")
                .build();

        // when
        userMapper.save(user);
        log.info("userSq = {}", user.getUserSq());

        // then
        Optional<UserDto> login = userMapper.login(user);

        log.info("login is empty = {}", login.isEmpty());
        assertThat(login.get().getUserId()).isEqualTo("hello123");
    }

    @Test
    @DisplayName("유저 Dto 로 조회 테스트")
    public void login() {
        // given
        UserDto user1 = UserDto.builder()
                .userId("helloWorld")
                .userPw("13579")
                .userNm("한국")
                .userSt("X")
                .build();
        userMapper.save(user1);

        UserDto findUser = UserDto.builder() // 찾을 유저 Dto 생성
                .userId("helloWorld")
                .userPw("13579")
                .userNm("한국")
                .userSt("X")
                .build();

        // when
        Optional<UserDto> login = userMapper.login(findUser);

        // then
        assertThat(login.get().getUserId()).isEqualTo("helloWorld");
    }
}

