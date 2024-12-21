package com.main.Jora.controllers;

import com.main.Jora.configs.CustomException;
import com.main.Jora.enums.Role;
import com.main.Jora.models.Project;
import com.main.Jora.models.User;
import com.main.Jora.models.UserProjectRole;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.repositories.UserProjectRoleReposirory;
import com.main.Jora.services.ProjectService;
import com.main.Jora.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {
    @Autowired
    ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserProjectRoleReposirory userProjectRoleReposirory;

    //Данные о текущем пользователе
    @ModelAttribute(name = "user")
    public User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return userService.getUserById(user.getId());
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
        } catch (CustomException.ObjectExistsException ex){
            model.addAttribute("userError", "Проект с таким хэшем не найден");
            return "home";
        }
        return "redirect:/home";
    }
    @PostMapping("/delete")
    public String deleteProject(@RequestParam("projectId") Long projectId,
                                @AuthenticationPrincipal User user,
                                Model model){
        Role role = userProjectRoleReposirory.getUserProjectRoleByUserAndProjectId(user, projectId).getRole();
        if (role != Role.ROLE_LEADER) return "redirect:/error/access-denied-error";
        try{
            projectService.deleteProject(projectId);
        } catch (CustomException.ObjectExistsException co){
            model.addAttribute("projectExistsException", "Проект не найден");
            return "home";
        }
        return "redirect:/home";
    }
}
