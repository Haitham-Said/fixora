package com.fixora.security.infrastructure.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;



@Component
public class JWTUtil {

    private final Key key;

    private final JWTConfiguration jwtConfiguration;

    private final Logger log= LoggerFactory.getLogger(JWTUtil.class);

    public JWTUtil(JWTConfiguration jwtConfiguration) {
        this.jwtConfiguration = jwtConfiguration;
        this.key= Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtConfiguration.secret()));
    }

    public String generateToken(Map<String,Object> claims){
       return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+jwtConfiguration.expirationMs()))
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public boolean isTokenValid(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }
        catch (JwtException | IllegalArgumentException e){
            log.error("Error while parsing the token :: {}",e.getMessage());
            return false;
        }
    }

}
