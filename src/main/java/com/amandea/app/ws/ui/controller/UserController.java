package com.amandea.app.ws.ui.controller;

import com.amandea.app.ws.service.UserService;
import com.amandea.app.ws.shared.dto.UserDto;
import com.amandea.app.ws.ui.model.request.UserDetailsRequestModel;
import com.amandea.app.ws.ui.model.response.UserRest;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//  "/login" Api endpoint we don't need to make this Api endpoint Spring framework provides it.

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    public UserService userService;

    @GetMapping
    public String getUser(){
        return "get user was called";
    }

    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails){
        UserRest returnValue = new UserRest();
        //Here we are creating a user data transfer object,
        // and we are going to populate this object with information that we received from the request body.
        UserDto userDto = new UserDto();
        //it comes from spring framework and it copies properties from source object to target object.
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser,returnValue);

        return returnValue;
    }

    @PutMapping
    public String updateUser(){
        return "update user was called";
    }

    @DeleteMapping
    public String deleteUser(){
        return "delete user was called";
    }
}
