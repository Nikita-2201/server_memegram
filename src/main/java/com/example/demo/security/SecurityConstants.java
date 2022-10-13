package com.example.demo.security;

import org.springframework.web.jsf.el.SpringBeanFacesELResolver;

// класс в котором прописаны все константы для Security
public class SecurityConstants {

    public static final String SING_UP_URLS = "/api/auth/**"; // пользователь будет авторизироваться под этим URL

    public static final String SECRET = "SecretKeyGenJWT";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String CONTENT_TYPE = "application/json";
    public static final long EXPIRATION_TIME = 1200_000;//20min

    //Дальше сделаем так чтоб наш JWS Token иссяк(Как пример для будущих проектов, делается для защиты)



}
