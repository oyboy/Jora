package com.main.Jora.configs;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleSQLException(SQLSyntaxErrorException ex) {
        log.info("SQL Syntax Error: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
        return "redirect:/home";
    }
}