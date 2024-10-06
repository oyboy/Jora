package com.main.Jora.services;

import com.main.Jora.enums.Role;
import com.main.Jora.models.Project;
import com.main.Jora.models.User;
import com.main.Jora.models.UserProjectRole;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.repositories.UserProjectRoleReposirory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProjectService {
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    UserService userService;
    @Autowired
    UserProjectRoleReposirory userProjectRoleReposirory;

    public void saveProject(Project project, User user){
        //Избежание ситуации, когда проект с данным хешем уже существует
        while (projectRepository.findIdByHash(project.getHash()) != null) project.setHash(project.generateHash());

        //Связывание проекта с пользователями
        log.info("Попытка связать {} \n\t с {}", project, user);
        UserProjectRole userProjectRole = new UserProjectRole();
        userProjectRole.setProject(project);
        userProjectRole.setUser(user);
        userProjectRole.setRole(Role.ROLE_LEADER);

        //Сохранение
        projectRepository.save(project);
        log.info("Saving project: {}", project);
        userProjectRoleReposirory.save(userProjectRole);
    }
    public List<Project> getProjectsForUser(User user){
        return userProjectRoleReposirory.findProjectsByUserId(user.getId());
    }
}
