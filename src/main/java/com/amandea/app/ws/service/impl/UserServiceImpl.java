package com.amandea.app.ws.service.impl;

import com.amandea.app.ws.io.entity.UserEntity;
import com.amandea.app.ws.repository.UserRepository;
import com.amandea.app.ws.service.UserService;
import com.amandea.app.ws.shared.Utils;
import com.amandea.app.ws.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    Utils utils;
    @Autowired
    UserRepository userRepository;
    @Override
    public UserDto createUser(UserDto user) {
        if(userRepository.findByEmail(user.getEmail()) != null){
            throw new RuntimeException("Record already exists");
        }
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user,userEntity);
        String publicUserId = utils.generateUserId(30);
        userEntity.setEncryptedPassword("test");
        userEntity.setUserId(publicUserId);

       UserEntity storedUserDetails = userRepository.save(userEntity);

       UserDto returnValue = new UserDto();
       BeanUtils.copyProperties(storedUserDetails,returnValue);

        return returnValue;
    }
}
