package com.main.Jora.controllers;

import com.main.Jora.models.Project;
import com.main.Jora.models.User;
import com.main.Jora.models.UserProjectRole;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.repositories.UserProjectRoleReposirory;
import com.main.Jora.repositories.UserRepository;
import com.main.Jora.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/projects/{project_hash}/group")
public class GroupController {
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    UserProjectRoleReposirory userProjectRoleReposirory;
    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupService groupService;
    //В шаблоне нужно вывести данные о пользователях и их ролях
    @ModelAttribute(name = "usersAndRoles")
    public List<UserProjectRole> getUsersAndRoles(@PathVariable("project_hash") String project_hash){
        Long project_id = projectRepository.findIdByHash(project_hash);
        return userProjectRoleReposirory.findUsersAndRolesByProjectId(project_id);
    }
    //Достаточно было бы usersAndRoles, но тут также нужно передать и текущую сессию
    //Поскольку данных о роли нет в таблице user, нужно сделать запрос в связанную таблицу
    @GetMapping
    public String getGroup(@AuthenticationPrincipal User currentUser,
                           @PathVariable("project_hash") String project_hash,
                           Model model){
        Project project = projectRepository.findProjectByHash(project_hash);
        UserProjectRole currentUserRole = userProjectRoleReposirory
                .getUserProjectRoleByUserAndProject(currentUser, project);
        model.addAttribute("currentUserRole", currentUserRole);
        return "group";
    }
    @PostMapping("/ban")
    public String banUser(@PathVariable("project_hash") String project_hash,
                          @RequestParam("email") String email,
                          Model model,
                          @AuthenticationPrincipal User currentUser){
        User user = userRepository.findByEmail(email);
        Project project = projectRepository.findProjectByHash(project_hash);
        if (user == null){
            model.addAttribute("emailNotFound", "Нет пользователя с таким email");
            return "group";
        }
        //Проверка, не пытается ли создатель забанить сам себя
        if (user.getEmail().equals(currentUser.getEmail())){
            model.addAttribute("suicideError", "Самовыпил запрещён!");
            return "group";
        }
        groupService.banUser(user, project);
        return "redirect:/projects/"+ project_hash + "/group";
    }
}
