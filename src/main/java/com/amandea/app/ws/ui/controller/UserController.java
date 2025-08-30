package com.amandea.app.ws.ui.controller;

import com.amandea.app.ws.exceptionHandling.UserServiceException;
import com.amandea.app.ws.exceptionHandling.UserServiceExceptionModel;
import com.amandea.app.ws.service.AddressService;
import com.amandea.app.ws.service.UserService;
import com.amandea.app.ws.shared.dto.AddressDto;
import com.amandea.app.ws.shared.dto.UserDto;
import com.amandea.app.ws.ui.model.request.UserDetailsRequestModel;
import com.amandea.app.ws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.internal.bytebuddy.description.method.MethodDescription;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//  "/login" Api endpoint we don't need to make this Api endpoint Spring framework provides it.
//By default, our web service endpoint will respond back with Json representation.

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    public UserService userService;

    @Autowired
    public AddressService addressesService;
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
//        UserDto userDto = new UserDto();
//        //it comes from spring framework, and it copies properties from source object to target object.
//        BeanUtils.copyProperties(userDetails, userDto);

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
//        BeanUtils.copyProperties(createdUser,returnValue);
         returnValue = modelMapper.map(createdUser, UserRest.class);

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
//        returnValue = modelMapper.map(updatedUser,UserRest.class);


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

    //below is the way to read query string from http url.
    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page",defaultValue = "0") int page,@RequestParam(value = "limit",defaultValue = "25") int limit){
        List<UserRest> returnValue = new ArrayList<>();
        List<UserDto> users= userService.getUsers(page,limit);

        for(UserDto userDto :users){
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(userDto,userRest);
            returnValue.add(userRest);
        }

        return returnValue;
    }

    @GetMapping(path = "/{id}/addresses",produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id){
        List<AddressesRest> returnValue = new ArrayList<>();

        List<AddressDto> addressDto = addressesService.getAddresses(id);


        if (addressDto!=null || !addressDto.isEmpty()) {
            Type listType = new TypeToken<List<AddressesRest>>() {
            }.getType();
            ModelMapper modelMapper = new ModelMapper();
            returnValue = modelMapper.map(addressDto, listType);

            for (AddressesRest addressesRest : returnValue){
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(id,addressesRest.getAddressId()))
                        .withSelfRel();
                addressesRest.add(selfLink);
            }
        }
       //to add links to a collection, we will need to wrap the list of user addresses into collection model.
        //.of method  accepts an object,which is our return value.
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class)
                .slash(id)
                .withRel("user");
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id))
                .withSelfRel();

        return CollectionModel.of(returnValue,userLink,selfLink);
    }
    @GetMapping(path = "/{userId}/addresses/{addressId}",produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId){

        AddressDto addressDto =  addressesService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();
        AddressesRest returnValue = modelMapper.map(addressDto,AddressesRest.class);
        //it will inspect our controller class, and it will create link :- http://localhost:8080/users
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class)
                .slash(userId)
                .withRel("user");
//        Link userAddressesLink = WebMvcLinkBuilder.linkTo(UserController.class)
//                .slash(userId)
//                        .slash("addresses")
//                                .withRel("addresses");
        Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
                .withRel("addresses");
//        Link selfLink = WebMvcLinkBuilder.linkTo(UserController.class)
//                .slash(userId)
//                        .slash("addresses")
//                                .slash(addressId)
//                                        .withSelfRel();

        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId,addressId))
                .withSelfRel();
//        returnValue.add(userLink);
//        returnValue.add(userAddressesLink);
//        returnValue.add(selfLink);

        //another way to add links is to use Entity Model Type.
        //so this entity model is a convenience type that we can use to wrap a single object that returning and it will allow us to add links to it as well.
        // it is used only when we need to return a single object
        EntityModel.of(returnValue, Arrays.asList(userLink,userAddressesLink,selfLink));

        return EntityModel.of(returnValue, Arrays.asList(userLink,userAddressesLink,selfLink));

    }

}
