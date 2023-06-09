package hello.chat.mapper;

import hello.chat.model.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    void save(UserDto userDto);
    Optional<UserDto> login(UserDto userDto);
}

