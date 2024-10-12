package com.main.Jora.services;

import com.main.Jora.configs.CustomException;
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
    //Тут нужно только вернуть запись из связанной таблицы и поменять в ней поле banned
    public void banUser(User user, Project project){
        UserProjectRole userProjectRole = userProjectRoleReposirory.getUserProjectRoleByUserAndProject(user, project);
        userProjectRole.setBanned(!userProjectRole.isBanned());
        log.info("Changing ban status for user {}", userProjectRole.getUser());
        userProjectRoleReposirory.save(userProjectRole);
    }
    public void createTag(String project_hash, String tagName)
            throws CustomException.LargeSizeException, CustomException.ObjectExistsException {
        if (tagName.length() > 50) throw new CustomException.LargeSizeException("");
        Project project = projectRepository.findProjectByHash(project_hash);

        if (tagRepository.findTagByTagName(tagName) != null){ //Can be failed
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
        Long project_id = projectRepository.findIdByHash(project_hash);
        User user = userRepository.findByEmail(email);
        Tag tag = tagRepository.findTagByTagName(tagName);
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
