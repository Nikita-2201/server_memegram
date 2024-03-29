package com.example.demo.web;

import com.example.demo.payload.response.JWTTokenSuccessResponse;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.SignupRequest;
import com.example.demo.security.JWTTokenProvider;
import com.example.demo.security.SecurityConstants;
import com.example.demo.service.UserService;
import com.example.demo.validations.ResponceErrorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@PreAuthorize("permitAll()")
public class AuthController {

    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ResponceErrorValidation responceErrorValidation;
    @Autowired
    private UserService userService;



    //Создадим mpi куда мы можем заслать данные юзера(пароль и юзернайм) для того чтоб он авторизировался

    @PostMapping("/signin")
    public ResponseEntity<Object>  authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult ){
        ResponseEntity<Object> errors = responceErrorValidation.mapValidationServer(bindingResult);
        if(!ObjectUtils.isEmpty(errors)) return errors;// возвращает объект

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);//задаем авторизацию
        String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);// генерируем токен

        return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt));


    }


    //api/auth/signup

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signupRequest, BindingResult bindingResult){
        ResponseEntity<Object> errors = responceErrorValidation.mapValidationServer(bindingResult);
        if(!ObjectUtils.isEmpty(errors)) return errors;// если нет ошибок при регистрации, то создаем нового пользователя

        userService.createUser(signupRequest);// можем вернуть user-а но в данном случае не будем(т.к. происходи  регистрация)
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));

    }


}
