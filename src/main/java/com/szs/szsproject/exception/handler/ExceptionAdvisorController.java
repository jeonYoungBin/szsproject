package com.szs.szsproject.exception.handler;

import com.szs.szsproject.domain.ResponseDto;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvisorController {

    @ExceptionHandler(Exception.class)
    ResponseDto processExceptionError(Exception e) {
        if(e instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            FieldError fieldError = bindingResult.getFieldError();
            return new ResponseDto(206, fieldError.getDefaultMessage(), null);
        } else {
            return new ResponseDto(206, e.getMessage(), null);
        }
    }
}
