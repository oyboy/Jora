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

    @ModelAttribute(name = "usersAndRoles")
    public List<UserProjectRole> getUsersAndRoles(@PathVariable("project_hash") String project_hash){
        Long project_id = projectRepository.findIdByHash(project_hash);
        return userProjectRoleReposirory.findUsersAndRolesByProjectId(project_id);
    }

    @GetMapping
    public String getGroup(){
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
        if (user.getEmail().equals(currentUser.getEmail())){
            model.addAttribute("suicideError", "Самовыпил запрещён!");
            return "group";
        }
        groupService.banUser(user, project);
        return "redirect:/projects/"+ project_hash + "/group";
    }
}
