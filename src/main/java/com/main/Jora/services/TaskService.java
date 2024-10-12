package com.main.Jora.services;

import com.main.Jora.configs.CustomException;
import com.main.Jora.models.*;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.repositories.TagRepository;
import com.main.Jora.repositories.TaskRepository;
import com.main.Jora.repositories.UserTaskRepository;
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
    @Autowired
    UserTaskRepository userTaskRepository;
    @Autowired
    TagRepository tagRepository;
    public void addTask(Task task, String project_hash, User user){
        Long project_id = projectRepository.findIdByHash(project_hash);
        Project projectFromDb = projectRepository.findById(project_id).orElse(null);

        projectFromDb.getTaskList().add(task);
        log.info("Setting task {} to project {}", task, projectFromDb);
        task.setProject(projectFromDb);

        UserTask userTask = new UserTask();
        userTask.setUser(user);
        userTask.setTask(task);
        userTaskRepository.save(userTask);
        task.getUserTasks().add(userTask);

        log.info("Saving task: {}", task);
        taskRepository.save(task);
    }
    public void addUserToTask(Task task, String project_hash, User user) throws CustomException.UserAlreadyJoinedException{
        if (userTaskRepository.existsByUserIdAndTaskId(user.getId(), task.getId())) {
            log.warn("User {} is already a member of task {}", user.getId(), task.getId());
            throw new CustomException.UserAlreadyJoinedException("Пользователь уже добавлен к задаче"); // Или выбрасываем исключение, если нужно
        }
        UserTask userTask = new UserTask();
        userTask.setUser(user);
        userTask.setTask(task);
        log.info("Saving user {} to task {}", user, task);
        userTaskRepository.save(userTask);
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

    public void setTagToTask(Long task_id, String tagName){
        Task task = taskRepository.findById(task_id).orElse(null);
        Tag tag = tagRepository.findTagByTagName(tagName);
        if (!task.getTags().contains(tag)){
            log.info("Setting tag {} to task {}", tag, task);
            task.getTags().add(tag);
        } else {
            log.info("Removing tag {} from task {}", tag, task);
            task.getTags().remove(tag);
        }
        log.info("Saving changes");
        taskRepository.save(task);
    }
}
