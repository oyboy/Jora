package com.main.Jora.services;

import com.main.Jora.models.Project;
import com.main.Jora.repositories.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProjectService {
    @Autowired
    ProjectRepository projectRepository;

    public void saveProject(Project project){
        projectRepository.save(project);
        log.info("Saving project: {}", project);
    }
}
