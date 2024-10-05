package com.main.Jora.services;

import com.main.Jora.models.Project;
import com.main.Jora.models.User;
import com.main.Jora.repositories.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@Slf4j
public class ProjectService {
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    UserService userService;

    public void saveProject(Project project, User user){
        //Избежание ситуации, когда проект с данным хешем уже существует
        while (projectRepository.findIdByHash(project.getHash()) != null) project.setHash(project.generateHash());

        //Связывание проекта с пользователями
        log.info("Попытка связать {} \n\t с {}", project, user);
        project.getUsers().add(user);
        user.getProjects().add(project);

        //Сохранение
        projectRepository.save(project);
        log.info("Saving project: {}", project);
        userService.saveUser(user);
        log.info("Updating project list in user: {} ", user);
    }
}
