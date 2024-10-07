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
    //Ситация: пользователь оказался на странице, которую удалил админ
    // (напр, удалили из таблицы проект) и получил ошибку 500.
    // Нужно её обработать и перебросить пользователя на страницу, которая точно не может быть удалена
    //Для этого перехвачено нужное исключение и выполнен редирект на главную страницу
    //Это вроде бы ещё работает, если пользователь пытается в url перейти по неправильному хешу
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleSQLException(SQLSyntaxErrorException ex) {
        log.info("SQL Syntax Error: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
        return "redirect:/home";
    }
}