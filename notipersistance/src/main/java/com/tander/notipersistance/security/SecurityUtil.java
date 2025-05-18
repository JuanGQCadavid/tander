package com.tander.notipersistance.security;

import java.security.Key;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import com.tander.notipersistance.exceptions.NotificationException;

@Slf4j
@Component
public class SecurityUtil {
    public static final String SECRET = "4D6351665468576D5A7134743777217A25432A462D4A614E645267556B586E32";

    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserId(String token){
        return  extractAllClaims(token).get("userId", String.class);
    }

    public String getUserIdLOrThrowError(String token){
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }else {
            throw new NotificationException("Someone is doing something iligal... good attempt.");
        }
        
        try {
            Integer userIDInt = extractAllClaims(token).get("userId", Integer.class);

            if(userIDInt == null) {
                throw new NotificationException("Someone is doing something iligal...");
            } 
            String userID = String.valueOf(userIDInt);

            if(userID == null || userID.isEmpty()){
                throw new NotificationException("Someone is missing to authenticate...");
            }

            return userID;
        } catch(Exception e) {
            throw new NotificationException("Someone is doing something iligal...");
        }
        
    }


}
