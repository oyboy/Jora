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
    public void addTask(Task task, String project_hash){
        Long project_id = projectRepository.findIdByHash(project_hash);
        Project projectFromDb = projectRepository.findById(project_id).orElse(null);
        projectFromDb.getTaskList().add(task);
        log.info("Setting task {} to project {}", task, projectFromDb);
        task.setProject(projectFromDb);
        log.info("Saving task: {}", task);
        taskRepository.save(task);
    }
    public Task getTaskById(Long task_id){
        return taskRepository.findById(task_id).orElse(null);
    }
    public Iterable<Task> findAllTasks(Long project_id){
        return taskRepository.findTasksByProjectId(project_id);
    }

    //Продолжение танцев с бубнами из @PostMapping("/edit")
    public void changeTaskFieldsAndSave(Long task_id, Task form){
        Task task = taskRepository.findById(task_id).orElse(null);
        task.setName(form.getName());
        task.setDescription(form.getDescription());
        task.setPriority(form.getPriority());
        task.setStatus(form.getStatus());
        log.info("Saving new params to task: {}", task);
        taskRepository.save(task);
    }
}
