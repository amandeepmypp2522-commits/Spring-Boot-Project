package com.amandea.app.ws.service;

import com.amandea.app.ws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

//it is an interface that we have created and for spring framework able to use it during authentication process , we have made
//this user service interface extend user details service that comes from spring framework

public interface UserService extends UserDetailsService {
  UserDto createUser(UserDto user);
  UserDto getUser(String email);
  UserDto getUserByUserId(String id);
  UserDto updateUser(String userId, UserDto userDto);
  void deleteUser(String userId);
}
