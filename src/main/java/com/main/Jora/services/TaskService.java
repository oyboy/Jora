package com.main.Jora.services;

import com.main.Jora.models.Project;
import com.main.Jora.models.Task;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.repositories.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaskService {
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ProjectRepository projectRepository;
    public void addTask(Task task, Long project_id){
        Project projectFromDb = projectRepository.findById(project_id).orElse(null);
        projectFromDb.getTaskList().add(task);
        log.info("Setting task {} to project {}", task, projectFromDb);
        task.setProject(projectFromDb);
        log.info("Saving task: {}", task);
        taskRepository.save(task);
    }
    public Iterable<Task> findAllTasks(Long project_id){
        return taskRepository.findTasksByProjectId(project_id);
    }
}
