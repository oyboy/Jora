package com.main.Jora.controllers;

import com.main.Jora.configs.CustomException;
import com.main.Jora.models.User;
import com.main.Jora.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/home/user/{user_id}")
public class UserController {
    @Autowired
    UserService userService;

    @ModelAttribute(name="currentUser")
    public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return userService.getUserById(user.getId());
        }
        return new User();
    }
    @GetMapping
    public String editUserPage(){
        return "user-edit";
    }
    @PostMapping
    public String editUser(@PathVariable Long user_id,
                           @Valid User user,
                           Errors errors,
                           Model model){
        if (errors.hasErrors()){
            errors.getAllErrors().forEach(error -> System.out.println("Error in user_controller: " + error));
            model.addAttribute("errors", errors);
            return "user-edit";
        }
        try{
            userService.editUser(user_id, user);
        } catch (IllegalArgumentException e){
            errors.rejectValue("confirmPassword", "error.user", "Пароли не совпадают");
            model.addAttribute("errors", errors);
            return "user-edit";
        } catch (CustomException.ObjectExistsException oe){
            model.addAttribute("userExistsError",
                    "Пользователь уже существует");
            return "user-edit";
        }
        model.addAttribute("changesSaved", "Изменения успешно применены");
        return "user-edit";
    }
    @PostMapping("/upload-avatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file,
                               @PathVariable Long user_id,
                               Model model){
        Set<String> formats = new HashSet<>(){{
            add("jpg");
            add("jpeg");
            add("png");
            add("bmp");
            add("gif");
        }};
        if (file.isEmpty()) {
            model.addAttribute("contentError", "Файл не выбран");
            return "user-edit";
        }
        else if (formats.stream().noneMatch(type -> file.getContentType().equals("image/" + type))) {
            model.addAttribute("contentError", "Некорректный формат изображения");
            return "user-edit";
        }
        try{
            userService.setAvatar(file, user_id);
        } catch (IOException e) {
            model.addAttribute("contentError", "Ошибка сохранения аватара");
            return "user-edit";
        }
        model.addAttribute("changesSaved", "Изменения успешно применены");
        return "user-edit";
    }
    @PostMapping("/avatar-delete")
    public String deleteAvatar(@RequestParam Long userId) {
        userService.deleteAvatarForUser(userId);
        return "redirect:/home/user/{user_id}"; // Предполагается, что это URL для отображения профиля пользователя
    }
}