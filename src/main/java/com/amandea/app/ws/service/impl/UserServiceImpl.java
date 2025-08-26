package com.amandea.app.ws.service.impl;

import com.amandea.app.ws.io.entity.UserEntity;
import com.amandea.app.ws.repository.UserRepository;
import com.amandea.app.ws.service.UserService;
import com.amandea.app.ws.shared.Utils;
import com.amandea.app.ws.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    Utils utils;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) {
        if(userRepository.findByEmail(user.getEmail()) != null){
            throw new RuntimeException("Record already exists");
        }
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user,userEntity);

        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));


       UserEntity storedUserDetails = userRepository.save(userEntity);

       UserDto returnValue = new UserDto();
       BeanUtils.copyProperties(storedUserDetails,returnValue);

        return returnValue;
    }

    //it will be invoked by spring framework to load user details do that it can verify username and password.

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       UserEntity userEntity =  userRepository.findByEmail(username);
       if(userEntity == null){
           throw new UsernameNotFoundException(username);
       }
       //this user class comes from spring framework. it implements user details and this is what spring framework is expecting
        //us to return from this method.

        //it has to be encrypted password and spring framework will then use the Bcrypt password encoder object to encode the passwoerd
        //that is provided in the login request and then check if the encoded password matches the login password.

        //we don't need to do password verification manually ourselves spring framework will do it for us behind the scenes.
        // this arraylist contains users with roles and authorities.
        return new User(username,userEntity.getEncryptedPassword(),new ArrayList<>());
    }
}
