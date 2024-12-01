package com.main.Jora.services;

import com.main.Jora.configs.CustomException;
import com.main.Jora.enums.Role;
import com.main.Jora.models.Project;
import com.main.Jora.models.Tag;
import com.main.Jora.models.User;
import com.main.Jora.models.UserProjectRole;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.repositories.TagRepository;
import com.main.Jora.repositories.UserProjectRoleReposirory;
import com.main.Jora.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GroupService {
    @Autowired
    UserProjectRoleReposirory userProjectRoleReposirory;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    UserRepository userRepository;
    @CachePut(value = "user", key = "#user.getId()")
    public User changeUserRole(User user, Project project, String action) {
        Role currentRole = userProjectRoleReposirory.findRoleByUserAndProject(user.getId(), project.getId());
        Role newRole = currentRole;
        if ("PROMOTE".equals(action)) {
            if (currentRole == Role.ROLE_PARTICIPANT) {
                newRole = Role.ROLE_MODERATOR;
            } else if (currentRole == Role.ROLE_MODERATOR) {
                newRole = Role.ROLE_LEADER;
            }
        } else if ("DEMOTE".equals(action)) {
            if (currentRole == Role.ROLE_LEADER) {
                newRole = Role.ROLE_MODERATOR;
            } else if (currentRole == Role.ROLE_MODERATOR) {
                newRole = Role.ROLE_PARTICIPANT;
            }
        }
        if (newRole != currentRole) {
            changeRole(user, newRole, project);
        }
        return user;
    }
    private void changeRole(User user, Role role, Project project) {
        UserProjectRole userProjectRole = userProjectRoleReposirory.getUserProjectRoleByUserAndProject(user, project);
        userProjectRole.setRole(role);
        log.info("Changing role {} for user {}", role, user);
        userProjectRoleReposirory.save(userProjectRole);
    }
    @CachePut(value = "user", key = "#user.getId()")
    public User banUser(User user, Project project){
        UserProjectRole userProjectRole = userProjectRoleReposirory.getUserProjectRoleByUserAndProject(user, project);
        userProjectRole.setBanned(!userProjectRole.isBanned());
        log.info("Changing ban status for user {}", userProjectRole.getUser());
        userProjectRoleReposirory.save(userProjectRole);
        return user;
    }
    public void createTag(String project_hash, String tagName)
            throws CustomException.LargeSizeException, CustomException.ObjectExistsException {
        if (tagName.length() > 50) throw new CustomException.LargeSizeException("");
        Project project = projectRepository.findProjectByHash(project_hash);

        if (tagRepository.findTagByTagNameAndProjectId(tagName, project.getId()) != null){
            throw new CustomException.ObjectExistsException("Тег уже создан");
        }
        Tag tag = new Tag();
        tag.setName(tagName);
        tag.getProjects().add(project);
        log.info("Saving new tag {}", tag);
        tagRepository.save(tag);

        project.getTags().add(tag);
        projectRepository.save(project);
        log.info("Setting tag to project");
    }
    public void setTagToUser(String email, String project_hash, String tagName){
        User user = userRepository.findByEmail(email);

        Tag tag = tagRepository.findTagByTagNameAndProjectId(tagName,
                projectRepository.findIdByHash(project_hash));

        if (user.getTags().contains(tag)){
            log.info("removing tag {} from user {}", tag, user);
            user.getTags().remove(tag);
        } else{
            log.info("setting tag {} to user {}", tag, user);
            user.getTags().add(tag);
        }
        log.info("saving changes");
        userRepository.save(user);
    }
}
