package com.main.Jora.calendar;

import com.main.Jora.models.User;
import com.main.Jora.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home/calendar")
public class CalendarController {
    @Autowired
    UserService userService;
    @ModelAttribute(name = "user")
    public User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return userService.getUserById(user.getId());
        }
        return new User();
    }
    @GetMapping
    public String getCalendar() {
        return "calendar";
    }
}