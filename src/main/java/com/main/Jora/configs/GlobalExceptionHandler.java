package com.main.Jora.configs;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

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
        log.error("SQL Syntax Error: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
        return "redirect:/home";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        log.error("Upload size Error: " + exc.getMessage(), HttpStatus.PAYLOAD_TOO_LARGE);
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body("Файл слишком большого размера! Максимальный размер: " + exc.getMaxUploadSize());
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/error/access-denied-error");
        return modelAndView;
    }
}