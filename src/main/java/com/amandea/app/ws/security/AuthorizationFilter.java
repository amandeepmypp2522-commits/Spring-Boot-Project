package com.amandea.app.ws.security;

//this class is for validating JWT Token for all secure Apis endpoint.

import com.amandea.app.ws.SpringApplicationContext;
import com.amandea.app.ws.service.UserService;
import com.amandea.app.ws.shared.dto.UserDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

//this BasicAuthenticationFilter class , it is a class in spring security that processes HTTP requests with a basic authorization headers,
//and then put the result into spring security context

//we are going to read JWT access token from authorization header, we will validate JSON web token and if it is correct, we will put
//user authentication information into spring security context holder.

//so for me to read authorization header from HTTP request, i will need to override one method doFilterInternal method.

public class AuthorizationFilter extends BasicAuthenticationFilter {
    public AuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(SecurityConstants.HEADER_STRING);
        if(header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)){
            //if jwt token is null or not start with Bearer than , we will pass to the next filter in chain, and then we will return from this method.
            chain.doFilter(request,response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication  = getAuthentication(request);
        //if JWT is valid, this method will return user password authentication token object, and we will put this object into spring security context holder.
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //passing execution to the next filter in chain.
        chain.doFilter(request,response);

    }

    // if we return null from this method then request authorization will not be successful.
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request){
        String header = request.getHeader(SecurityConstants.HEADER_STRING);
        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) return null;

        String token = header.substring(SecurityConstants.TOKEN_PREFIX.length()).trim();
        if (token.isEmpty()) return null;

        try {
            SecretKey secretKey = KeyHelper.hmacKey();

            // jjwt 0.12.6: verifyWith + parseSignedClaims
            var parser = Jwts.parser().verifyWith(secretKey).build();
            io.jsonwebtoken.Jws<io.jsonwebtoken.Claims> jws = parser.parseSignedClaims(token);

            String subject = jws.getPayload().getSubject();
            if (subject == null || subject.isBlank()) return null;

            return new UsernamePasswordAuthenticationToken(
                    subject, null, new ArrayList<>());

        } catch (JwtException | IllegalArgumentException e) {
            // expired, invalid signature, malformed, etc.
            return null;
        }

    }

}
