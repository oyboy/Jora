package com.main.Jora.controllers;

import com.main.Jora.configs.CustomException;
import com.main.Jora.enums.Role;
import com.main.Jora.models.Project;
import com.main.Jora.models.Tag;
import com.main.Jora.models.User;
import com.main.Jora.models.UserProjectRole;
import com.main.Jora.models.dto.UserTagsDTO;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.repositories.TagRepository;
import com.main.Jora.repositories.UserProjectRoleReposirory;
import com.main.Jora.repositories.UserRepository;
import com.main.Jora.services.GroupService;
import com.main.Jora.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/projects/{project_hash}/group")
public class GroupController {
    @Autowired
    UserProjectRoleReposirory userProjectRoleReposirory;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupService groupService;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    private ProjectService projectService;

    //Вывод данных о пользователях и их тегах
    @ModelAttribute(name = "tagsForProject")
    public List<Tag> getTagsForThisProject(@PathVariable("project_hash") String project_hash){
        Long project_id = projectService.findIdByHash(project_hash);
        return tagRepository.findTagsByProjectId(project_id);
    }
    @ModelAttribute(name = "usersAndTags")
    public List<UserTagsDTO> getUsersWithTags(@PathVariable("project_hash") String project_hash) {
        Long project_id = projectService.findIdByHash(project_hash);
        List<UserProjectRole> usersAndRoles = userProjectRoleReposirory.findUsersAndRolesByProjectId(project_id);

        List<UserTagsDTO> usersWithTags = new ArrayList<>();
        for (UserProjectRole userRole : usersAndRoles) {
            User user = userRole.getUser();
            List<Tag> tags = user.getTags();
            usersWithTags.add(new UserTagsDTO(user, tags, userRole.getRole()));
        }
        return usersWithTags;
    }
    @ModelAttribute("currentUserRole")
    public UserProjectRole getCurrentUserRole(@AuthenticationPrincipal User currentUser,
                                              @PathVariable("project_hash") String project_hash){
        Long project_id = projectService.findIdByHash(project_hash);
        return userProjectRoleReposirory
                .getUserProjectRoleByUserAndProjectId(currentUser, project_id);
    }
    @ModelAttribute(name = "user")
    public User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return userRepository.findById(user.getId()).orElse(null);
        }
        return new User();
    }
    @GetMapping
    public String getGroup(@PathVariable("project_hash") String project_hash){
        Role role = getCurrentUserRole(getUser(), project_hash).getRole();
        if (role == Role.ROLE_PARTICIPANT) return "redirect:/error/access-denied-error";
        return "group";
    }
    @PostMapping("/tag-add")
    public String addTag(@PathVariable("project_hash") String project_hash,
                         @RequestParam("tagName") String tagName,
                         Model model){
        try{
            groupService.createTag(project_hash, tagName);
        } catch (CustomException.LargeSizeException ex){
            model.addAttribute("sizeException",
                    "Название тега не может превышать 50 символов");
            return "group";
        } catch (CustomException.ObjectExistsException ex){
            model.addAttribute("existsException",
                    "Тег с таким именем уже создан");
            return "group";
        }
        return "redirect:/projects/"+ project_hash + "/group";
    }
    @PostMapping("/tag-set")
    public String setTag(@PathVariable("project_hash") String project_hash,
                         @RequestParam("tagName") String tagName,
                         @RequestParam("email") String email){
        groupService.setTagToUser(email, project_hash, tagName);
        return "redirect:/projects/"+ project_hash + "/group";
    }
    @PostMapping("/ban")
    public String banUser(@PathVariable("project_hash") String project_hash,
                          @RequestParam("email") String email,
                          Model model,
                          @AuthenticationPrincipal User currentUser){
        User user = userRepository.findByEmail(email);
        Project project = projectService.findProjectByHash(project_hash);
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
    @PostMapping("/change-role")
    public String changeUserRole(@PathVariable("project_hash") String project_hash,
                                 @RequestParam("email") String email,
                                 @RequestParam("action") String action) {
        User user = userRepository.findByEmail(email);
        Long projectId = projectService.findIdByHash(project_hash);
        groupService.changeUserRole(user, projectId, action);
        return "redirect:/projects/" + project_hash + "/group";
    }
}