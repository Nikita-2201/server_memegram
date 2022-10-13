package com.example.demo.facade;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import org.springframework.stereotype.Component;

//класс помогает брать из основной модели данные и передавать в DTO
@Component
public class UserFacade {

    public UserDTO userToUserDTO(User user){
        UserDTO userDTO=new UserDTO();
        userDTO.setBio(user.getBio());
        userDTO.setFirstname(user.getName());
        userDTO.setLastname(user.getLastname());
        userDTO.setUsername(user.getUsername());
        userDTO.setId(user.getId());
        return userDTO;
    }

}
