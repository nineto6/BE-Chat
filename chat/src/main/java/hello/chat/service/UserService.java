package hello.chat.service;

import hello.chat.model.UserDto;

import java.util.Optional;

public interface UserService {
    Optional<UserDto> login(UserDto userDto);
    void signUp(UserDto userDto);
}

