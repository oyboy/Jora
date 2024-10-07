package com.main.Jora.services;

import com.main.Jora.models.Project;
import com.main.Jora.models.User;
import com.main.Jora.models.UserProjectRole;
import com.main.Jora.repositories.UserProjectRoleReposirory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GroupService {
    @Autowired
    UserProjectRoleReposirory userProjectRoleReposirory;

    public void banUser(User user, Project project){
        UserProjectRole userProjectRole = userProjectRoleReposirory.getUserProjectRoleByUserAndProject(user, project);
        userProjectRole.setBanned(!userProjectRole.isBanned());
        log.info("Changing ban status for user {}", userProjectRole.getUser());
        userProjectRoleReposirory.save(userProjectRole);
    }
}
