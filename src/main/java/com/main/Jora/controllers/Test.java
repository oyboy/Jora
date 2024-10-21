package com.main.Jora.controllers;

import com.main.Jora.notifications.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("projects/{project_hash}")
public class Test{
    @Autowired
    NotificationService notificationService;
    @GetMapping("/test")
    public String test(@PathVariable String project_hash,
                       Model model){
        model.addAttribute("project_hash", project_hash);
        return "test";
    }
    @PostMapping("/test/all")
    public String sendAll(@PathVariable String project_hash,
                        Model model){
        notificationService.sendNotificationToAll(project_hash, "test title", "Массовая рассылка");
        return "redirect:/projects/{project_hash}/test";
    }
}
