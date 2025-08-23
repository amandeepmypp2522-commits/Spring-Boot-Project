package com.amandea.app.ws.service;

import com.amandea.app.ws.shared.dto.UserDto;

public interface UserService {
  UserDto createUser(UserDto user);
}
