package com.amandea.app.ws.security;

//so when our application receives an HTTP request to perform user login, this filter will trigger.it will read username and
//password from HTTP request, and it will pass this username and password on to a spring framework.

//spring framework will validate the provided user credential and if they are correct, it will handle the control back to us
//so that we can generate access token.

import com.amandea.app.ws.SpringApplicationContext;
import com.amandea.app.ws.service.UserService;
import com.amandea.app.ws.shared.dto.UserDto;
import com.amandea.app.ws.ui.model.request.UserLoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;


//this UsernamePasswordAuthenticationFilter is a class in spring security that processes authentication information when it
//is submitted in HTTP request. it is part of the default filter chain, when a login request submission, then a HTTP request
//will pass through this filter.

//Authentication Manager is used during authentication process , it has only one method that is called authenticated, which has
//all the logic for authenticating user request.

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    //once authenticate method is called spring framework will try to locate user details and this is when it
    //Calls the loadUserByUsername spring framework will validate username and password, and once user
    //authentication is successful,spring framework will invoke another method that is called successfulAuthentication
    //where we generate JWT token and sending in response header.

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException{
        try{
            UserLoginRequestModel creds = new ObjectMapper().readValue(req.getInputStream(),UserLoginRequestModel.class);
            //we use this user login request model object to read username and password to create a new object is called
            //UsernamePasswordAuthenticationToken. it is a simple class represents username and password.
            //we then pass an object of this class as a parameter to authenticate method which will trigger the authentication
            //process and it is after we invoke this authenticate method , that spring framework will invoke the load user by username
            //method which we have implemented, if user authentication is successful, then spring framework will invoke another method
            //that is called successful authentication
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(creds.getEmail(),creds.getPassword(),new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res,
                                            FilterChain chain, Authentication auth)
            throws IOException, ServletException {

        var now = java.time.Instant.now();

        // FIX: use a proper HS512 key
        SecretKey secretKey = KeyHelper.hmacKey();

        String userName = ((User) auth.getPrincipal()).getUsername();

        String token = io.jsonwebtoken.Jwts.builder()
                .setSubject(userName) // (or .subject(userName) in new style)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(SecurityConstants.EXPIRATION_TIME)))
                .signWith(secretKey)
                .compact();

        // your code unchanged
        UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
        UserDto userDto = userService.getUser(userName);

        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        res.addHeader("UserId", userDto.getUserId());
    }
}
