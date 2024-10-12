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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
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
    @Autowired
    TagRepository tagRepository;
    //Вывод данных о пользователях и их тегах
    @ModelAttribute(name = "tagsForProject")
    public List<Tag> getTagsForThisProject(@PathVariable("project_hash") String project_hash){
        Long project_id = projectRepository.findIdByHash(project_hash);
        return tagRepository.findTagsByProjectId(project_id);
    }
    @ModelAttribute(name = "usersAndTags")
    public List<UserTagsDTO> getUsersWithTags(@PathVariable("project_hash") String project_hash) {
        Long project_id = projectRepository.findIdByHash(project_hash);
        List<UserProjectRole> usersAndRoles = userProjectRoleReposirory.findUsersAndRolesByProjectId(project_id);

        List<UserTagsDTO> usersWithTags = new ArrayList<>();
        for (UserProjectRole userRole : usersAndRoles) {
            User user = userRole.getUser();
            List<Tag> tags = user.getTags();
            usersWithTags.add(new UserTagsDTO(user, tags, userRole.getRole()));
        }
        return usersWithTags;
    }
    //Нужно также добавить текущую сессию для разграничения прав
    @ModelAttribute("currentUserRole")
    public UserProjectRole getCurrentUserRole(@AuthenticationPrincipal User currentUser,
                                              @PathVariable("project_hash") String project_hash){
        Project project = projectRepository.findProjectByHash(project_hash);
        return userProjectRoleReposirory
                .getUserProjectRoleByUserAndProject(currentUser, project);
    }
    @GetMapping
    public String getGroup(){
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
    @PostMapping("/change-role")
    public String changeUserRole(@PathVariable("project_hash") String project_hash,
                                 @RequestParam("email") String email,
                                 @RequestParam("action") String action) {
        User user = userRepository.findByEmail(email);
        Project project = projectRepository.findProjectByHash(project_hash);
        groupService.changeUserRole(user, project, action);
        return "redirect:/projects/" + project_hash + "/group";
    }
}