package pl.edu.pjwstk.library_spring.mapper;

import pl.edu.pjwstk.library_spring.dto.UserDto;
import pl.edu.pjwstk.library_spring.model.Borrow;
import pl.edu.pjwstk.library_spring.model.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
