package com.main.Jora.controllers;

import com.main.Jora.configs.CustomException;
import com.main.Jora.models.Project;
import com.main.Jora.models.User;
import com.main.Jora.services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {
    @Autowired
    ProjectService projectService;
    //Данные о текущем пользователе
    @ModelAttribute(name = "user")
    public User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return new User();
    }
    //Список доступных пользователю проектов (те, в которых он состоит)
    @ModelAttribute(name = "projects")
    public List<Project> getProjects(){
        User user = getUser();
        return projectService.getProjectsForUser(user);
    }
    @GetMapping
    public String home(){return "home";}
    @GetMapping("/create")
    public String newProject(){
        return "create-project";
    }
    @PostMapping("/create")
    public String createProject(@Valid Project project, Errors errors,
                                Model model,
                                @AuthenticationPrincipal User user){
        if (errors.hasErrors()){
            errors.getAllErrors().forEach(error -> {
                System.out.println("Error in home_controller: " + error);
            });
            model.addAttribute("errors", errors);
            return "create-project";
        }
        projectService.saveProject(project, user);
        return "redirect:/home";
    }
    //Присоединение к проекту
    //@Valid по полям отсутствует, поэтому созданы собственные исключения
    @PostMapping("/join")
    public String joinToProject(@RequestParam("project_hash") String project_hash,
                                @AuthenticationPrincipal User user,
                                Model model){
        try{
            projectService.addUserToProject(project_hash, user);
        } catch (CustomException.UserAlreadyJoinedException ex){
            model.addAttribute("userError", "Пользователь уже добавлен");
            return "home";
        } catch (CustomException.UserBannedException ex){
            model.addAttribute("userError", "Вы забанены");
            return "home";
        }

        return "redirect:/projects/" + project_hash + "/group";
    }
}
