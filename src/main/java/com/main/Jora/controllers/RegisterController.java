package com.main.Jora.controllers;

import com.main.Jora.models.User;
import com.main.Jora.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
public class RegisterController {
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        //В случае, если был введён неправильный логин или пароль
        //нужно вывести пользователю сообщение об этом
        //Попытка отловить данное исключение:
        Exception exception = (Exception) request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (exception != null) {
            model.addAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception);
        }
        return "login";
    }
    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }
    @PostMapping("/registration")
    public String addUser(@Valid User user, Errors errors, Model model){
        //Проверка на невалидность данных
        if (errors.hasErrors()){
            errors.getAllErrors().forEach(error -> {
                System.out.println("Error in register_controller: " + error);
            });
            model.addAttribute("errors", errors);
            return "registration";
        }
        //Проверка совпадения паролей
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "error.user", "Пароли не совпадают");
            model.addAttribute("errors", errors);
            return "registration";
        }
        //Попытка создать пользователя
        boolean created = userService.createUser(user);
        if (!created) {
            model.addAttribute("userExistsError",
                    "Пользователь уже существует");
            return "registration";
        }
        return "redirect:/login";
    }
}
