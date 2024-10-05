package com.main.Jora.controllers;

import com.main.Jora.models.Project;
import com.main.Jora.models.User;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeController {
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ProjectService projectService;

    @ModelAttribute(name = "user")
    public User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return new User();
    }
    @GetMapping
    public String home(Model model){
        Iterable<Project> projectList = projectRepository.findAll();
        model.addAttribute("projects", projectList);
        return "home";
    }
    @GetMapping("/create")
    public String newProject(){
        return "create-project";
    }
    @PostMapping("/create")
    public String createProject(@Valid Project project, Errors errors,
                                Model model){
        if (errors.hasErrors()){
            errors.getAllErrors().forEach(error -> {
                System.out.println("Error in home_controller: " + error);
            });
            model.addAttribute("errors", errors);
            return "create-project";
        }
        projectService.saveProject(project);
        return "redirect:/home";
    }
}
