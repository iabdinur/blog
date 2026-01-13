package com.iabdinur.mapper;

import com.iabdinur.dto.UserDTO;
import com.iabdinur.model.User;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserDTOMapper implements Function<User, UserDTO> {

    @Override
    public UserDTO apply(User user) {
        return UserDTO.fromEntity(user);
    }
}
