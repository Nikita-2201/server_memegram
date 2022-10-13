package com.example.demo.security;

import com.example.demo.entity.User;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTTokenProvider {
    public static final Logger LOG = LoggerFactory.getLogger(JWTTokenProvider.class);// чтоб было легче найти ту или иную ошибку ну или знали что происходит

    public String generateToken(Authentication authentication){
        User user =(User) authentication.getPrincipal();//Principal будет хранить в себе данные юзера. Делаем каст на User, т.к. возращает объект
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + SecurityConstants.EXPIRATION_TIME);//время сессии

        String userId = Long.toString(user.getId());// перевод из Long в String

        //Создание клеймс(объект который уже будет передовать в JWT), он будет содержать данные user
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", userId);
        claimsMap.put("username", user.getEmail());//вход
        claimsMap.put("firstname", user.getName());
        claimsMap.put("lastname", user.getLastname());

        return Jwts.builder()
                .setSubject(userId)
                .addClaims(claimsMap)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET)
                .compact();
    }

    public boolean validateToken(String token){
        try{
           Jwts.parser()
                   .setSigningKey(SecurityConstants.SECRET)
                   .parseClaimsJws(token);
           //будет принимать токен парсировать его, балгодаря секретному слову декодировать и брать все claims
            return true;
        }
        catch (SignatureException |
                MalformedJwtException |
                ExpiredJwtException |
                UnsupportedJwtException |
                IllegalArgumentException ex){
                LOG.error(ex.getMessage());//для себя
                return false;
        }
    }

    public Long getUserIdFromToken(String token){//мотод поможет брать в дальнейшем id из Map(метод generateToken)
       Claims claims = Jwts.parser()
               .setSigningKey(SecurityConstants.SECRET)
               .parseClaimsJws(token)
               .getBody();
       String id = (String) claims.get("id");// т.к String уже а не Long
       return Long.parseLong(id);
    }
}
