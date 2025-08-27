package com.amandea.app.ws.ui.controller;

import com.amandea.app.ws.exceptionHandling.UserServiceException;
import com.amandea.app.ws.exceptionHandling.UserServiceExceptionModel;
import com.amandea.app.ws.service.UserService;
import com.amandea.app.ws.shared.dto.UserDto;
import com.amandea.app.ws.ui.model.request.UserDetailsRequestModel;
import com.amandea.app.ws.ui.model.response.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//  "/login" Api endpoint we don't need to make this Api endpoint Spring framework provides it.
//By default, our web service endpoint will respond back with Json representation.

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    public UserService userService;
//here we are setting response in xml format by default.
    //if we leave our web services endpoint produces a xml representation only then, if client application sends a request containing
    //accepted http header request in json representation,then it will get back an error message saying that our web service could not
    //find acceptable representation.

    //if we have configured more than one media type, then the orders matters. if the client application doesn't include http header
    //accept in the request, then our web service  will respond back with a resource using representation that is configured first
    //in the list of mediaType
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id){
        UserRest returnValue = new UserRest();
        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto,returnValue);
        return returnValue;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createUser(@RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();
        if(userDetails.getFirstName().isEmpty()){
//            UserServiceExceptionModel errorModel = new UserServiceExceptionModel(404,"Bad Request",ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage(),"/users");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorModel);
            throw  new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        }
        //Here we are creating a user data transfer object,
        // and we are going to populate this object with information that we received from the request body.
        UserDto userDto = new UserDto();
        //it comes from spring framework, and it copies properties from source object to target object.
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser,returnValue);

        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }

    @PutMapping(path = "/{id}",
            consumes = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(@RequestBody UserDetailsRequestModel userDetails, @PathVariable String id){
        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails,userDto);

        UserDto updatedUser = userService.updateUser(id,userDto);
        BeanUtils.copyProperties(updatedUser,returnValue);


        return returnValue;
    }

    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id){
        OperationStatusModel returnValue = new OperationStatusModel();

        userService.deleteUser(id);

        returnValue.setOperationName(RequestOperationName.DELETE.name());
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }
}
