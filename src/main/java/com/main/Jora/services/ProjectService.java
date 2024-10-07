package com.main.Jora.services;

import com.main.Jora.configs.CustomException;
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

        project.getUserProjectRoles().add(userProjectRole);
        user.getUserProjectRoles().add(userProjectRole);

        //Сохранение
        projectRepository.save(project);
        log.info("Saving project: {}", project);
        userProjectRoleReposirory.save(userProjectRole);
    }
    public List<Project> getProjectsForUser(User user){
        return userProjectRoleReposirory.findProjectsByUserId(user.getId());
    }
    public void addUserToProject(String project_hash, User user) throws CustomException.UserAlreadyJoinedException,
            CustomException.UserBannedException{
        Project project = projectRepository.findProjectByHash(project_hash);

        // Проверяем существует ли уже связь пользователя с проектом
        if (userProjectRoleReposirory.existsByUserIdAndProjectId(user.getId(), project.getId())) {
            if (userProjectRoleReposirory.isUserBanned(user.getId(), project.getId())) {
                log.warn("User {} is banned in project {}", user.getId(), project.getId());
                throw new CustomException.UserBannedException("Пользователь забанен в этом проекте");
            }
            log.warn("User {} is already a member of project {}", user.getId(), project.getId());
            throw new CustomException.UserAlreadyJoinedException("Пользователь уже добавлен к проекту"); // Или выбрасываем исключение, если нужно
        }

        UserProjectRole userProjectRole = new UserProjectRole();
        userProjectRole.setProject(project);
        userProjectRole.setUser(user);
        userProjectRole.setRole(Role.ROLE_PARTICIPANT);

        log.info("Saving relation: {}", userProjectRole);
        userProjectRoleReposirory.save(userProjectRole);
    }
}
