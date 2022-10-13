package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    public static final Logger LOG = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //Получаем запрос(request) делаем что-то с ним
       try {// Если все ок то авторизируем пользователя и даем ему какие-то данные
           String jwt = getJWTFromRequest(request);
           if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {//если все ок то можем брать наши данные из эотого токена
               Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
               User userDetails = customUserDetailsService.loadUserById(userId);

               UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                       userDetails, null, Collections.emptyList());
               authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
               SecurityContextHolder.getContext().setAuthentication(authentication);
           }
       } catch(Exception ex){
           LOG.error("Could not set user authentication ");
       }
       //потом отвечаем на запрос
       filterChain.doFilter(request, response);

    }

    //Напишем метод который будет помогать брать нам JWT прям из запроса, который будет поступать нам на сервер
    private String getJWTFromRequest(HttpServletRequest request){
        String bearToken = request.getHeader(SecurityConstants.HEADER_STRING);//каждый раз когда будем делать запрос на сервер
        //мы будем передавать JWT внутри нашего хэдора
        if(StringUtils.hasText(bearToken) && bearToken.startsWith(SecurityConstants.TOKEN_PREFIX)){
            return bearToken.split(" ")[1];
        }
        return null;
    }
}
