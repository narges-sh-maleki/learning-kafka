package com.maleki.narges.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class LibraryEventControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> getException(MethodArgumentNotValidException exp){
        String collect = exp.getBindingResult().getFieldErrors()
                .stream().map(fieldError -> fieldError.getField() + "-" + fieldError.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(","));

        log.info("error message:{}" , collect);
        return new ResponseEntity<String>(  collect, HttpStatus.BAD_REQUEST);
    }

}
