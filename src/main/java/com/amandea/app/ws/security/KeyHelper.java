package com.amandea.app.ws.security;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class KeyHelper {
    private KeyHelper() {}

    public static SecretKey hmacKey() {
        // OPTION A: plain text secret (ensure >= 64 bytes for HS512)
        return Keys.hmacShaKeyFor(SecurityConstants.getTokenSecret().getBytes(StandardCharsets.UTF_8));

        // OPTION B: if TOKEN_SECRET is Base64 text, use this instead:
        // return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SecurityConstants.TOKEN_SECRET));
    }
}
