package com.amandea.app.ws.security;

public class SecurityConstants {

    public static final long EXPIRATION_TIME=864000000; //10 days [in millisecond]
    public static final String TOKEN_PREFIX="Bearer";
    public static final String HEADER_STRING="Authorization";
    public static final String SIGN_UP_URL="/users";
    //we will use token secret value at the time when we need to generate and sign JWT access token with a secure signature.
    // the value of this token is random alphanumeric String of characters [64 characters at least for secure]
    public static final String TOKEN_SECRET ="ajhsjkjedindmmdkjid753has7823jsb82hjs926676379hsbnbsjAwjkjhihweinn9326wnceiuiw3";


}
