package com.main.Jora.services;

import com.main.Jora.models.Project;
import com.main.Jora.models.Task;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.repositories.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    //Фильтрация задач по времени
    public Iterable<Task> findTasksByTimeLine(Long project_id, String deadlineFilter){
        if (deadlineFilter == null || deadlineFilter.isEmpty()) return findAllTasks(project_id);
        return switch (deadlineFilter) {
            case "today" -> findTasksByDeadlineToday(project_id);
            case "tomorrow" -> findTasksByDeadlineTomorrow(project_id);
            case "thisMonth" -> findTasksByDeadlineThisMonth(project_id);
            case "noDeadline" -> findTasksWithoutDeadline(project_id);
            default -> findAllTasks(project_id);
        };
    }
    private Iterable<Task> findTasksByDeadlineToday(Long projectId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return taskRepository.findByProjectIdAndDeadlineBetween(projectId, startOfDay, endOfDay);
    }
    private Iterable<Task> findTasksByDeadlineTomorrow(Long projectId) {
        LocalDateTime startOfTomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime endOfTomorrow = startOfTomorrow.plusDays(1);
        return taskRepository.findByProjectIdAndDeadlineBetween(projectId, startOfTomorrow, endOfTomorrow);
    }
    private Iterable<Task> findTasksByDeadlineThisMonth(Long projectId) {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().plusMonths(1).withDayOfMonth(1).atStartOfDay();
        return taskRepository.findByProjectIdAndDeadlineBetween(projectId, startOfMonth, endOfMonth);
    }
    private Iterable<Task> findTasksWithoutDeadline(Long projectId) {
        return taskRepository.findByProjectIdAndDeadlineIsNull(projectId);
    }


    //Продолжение танцев с бубнами из @PostMapping("/edit")
    public void changeTaskFieldsAndSave(Long task_id, Task form){
        Task task = taskRepository.findById(task_id).orElse(null);
        task.setName(form.getName());
        task.setDescription(form.getDescription());
        task.setPriority(form.getPriority());
        task.setStatus(form.getStatus());
        task.setDeadline(form.getDeadline());
        log.info("Saving new params to task: {}", task);
        taskRepository.save(task);
    }
}
