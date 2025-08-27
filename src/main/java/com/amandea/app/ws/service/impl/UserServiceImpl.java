package com.amandea.app.ws.service.impl;

import com.amandea.app.ws.exceptionHandling.UserServiceException;
import com.amandea.app.ws.io.entity.UserEntity;
import com.amandea.app.ws.repository.UserRepository;
import com.amandea.app.ws.service.UserService;
import com.amandea.app.ws.shared.Utils;
import com.amandea.app.ws.shared.dto.UserDto;
import com.amandea.app.ws.ui.model.response.ErrorMessages;
import com.amandea.app.ws.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity == null){
            throw new UsernameNotFoundException(email);
        }
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity,returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity == null){
            throw new UsernameNotFoundException(userId);
        }
        BeanUtils.copyProperties(userEntity,returnValue);
        return returnValue;
    }

    @Override
    public UserDto updateUser(String userId, UserDto userDto) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity == null){
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        UserEntity updatedUserDetails = userRepository.save(userEntity);
        BeanUtils.copyProperties(updatedUserDetails,returnValue);
        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity == null){
            throw new UserServiceException(ErrorMessages.COULD_NOT_DELETE_RECORD.getErrorMessage());
        }
        userRepository.delete(userEntity);
//        userRepository.deleteByUserId(userId);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();
        if(page>0)
            page=page-1;

        Pageable pageableRequest = PageRequest.of(page, limit);
        Page<UserEntity> userPage = userRepository.findAll(pageableRequest);

        List<UserEntity> users = userPage.getContent();
        for(UserEntity userEntity : users){
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity,userDto);
            returnValue.add(userDto);
        }
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
