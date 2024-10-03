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
        //Избежание ситуации, когда проект с данным хешем уже существует
        while (projectRepository.findIdByHash(project.getHash()) != null) project.setHash(project.generateHash());
        projectRepository.save(project);
        log.info("Saving project: {}", project);
    }
}
