package com.example.demo.validations;
//Валидатор для тех или иных ошибок которые будут приходить к нам на сервер

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResponceErrorValidation {

    //BindResult будет славливать ошибку из request(то что пусто), и прежде чем она выскачит мы должны её получить
    //и она будет приходить в этом объекте(BindResult)
    public ResponseEntity<Object> mapValidationServer (BindingResult result){
        if (result.hasErrors()){
            Map<String, String> errorMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(result.getAllErrors())){//если были ошибки в этом результате, то
                for(ObjectError error: result.getAllErrors()){
                    errorMap.put(error.getCode(), error.getDefaultMessage());
                }
            }

            for (FieldError error: result.getFieldErrors()){//если не было
                  errorMap.put(error.getField(), error.getDefaultMessage());//getField() берет именно в филде
            }
            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }
        return  null;
    }
}
