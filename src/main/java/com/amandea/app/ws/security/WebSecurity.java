package com.amandea.app.ws.security;

import com.amandea.app.ws.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurity {

    //to make this work, we need to make spring Framework use this User Details Service for Spring Framework to able to load user details from
    //my database table , i will need to make this UserService extends UserDetailsService interface that comes from spring framework.

    private final UserService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurity(UserService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {


        // we will use this object to configure which service class in our application will be responsible to load user Details from the database and when user performs logging
        //spring framework will then use this class to see if our database does have a user with provided login credentials.

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        //this configuration is needed for user authentication process. with this configuration here we tell spring
        //framework which class in our application it should use to load user details from the database and which encryption object
        // it should use to verify password in the login request matches the encrypted password stored in a database.

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
//        http.authenticationManager(authenticationManager);

        //Customize Login URL path
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager);
        authenticationFilter.setFilterProcessesUrl("/users/login");

//        AuthorizationFilter authorizationFilter = new AuthorizationFilter(authenticationManager);

        //Spring framework call this method at the time when our application starts up, our code will then be executed,
        // and the HTTP security object that we will configure in this method will be placed into application context and
        //once this object in spring application context, spring framework will then be able to use it whenever it needs to.
        //eg:- when we send the HTTP request to an API endpoint spring framework will take this request through a chain of filters
        //and one of these filters will validate HTTP request against the security configuration that we will configure in this method


        //csrf:- cross site request forgery, we are developing stateless Api, and in stateless Rest Apis this feature is usually disabled.


        http
                .csrf(csrf -> csrf.disable()) // for REST/Postman testing
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL).permitAll() // note the leading slash
                        .anyRequest().authenticated()

                )
                .authenticationManager(authenticationManager)
                .addFilter(authenticationFilter)
                .addFilter(new AuthorizationFilter(authenticationManager))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //line 77  is to make this application stateless.this will mean that spring security will never create HTTp session
        //and will never use it to obtain security context and this means that there is no HTTP session created for user authorization
        //spring security will rely only on information that is inside of JWT.

//                .addFilter(new AuthenticationFilter(authenticationManager));

//                .httpBasic(Customizer.withDefaults()); // use Basic Auth for other endpoints

        return http.build();
    }

}
