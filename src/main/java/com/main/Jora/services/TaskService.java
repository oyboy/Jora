package com.main.Jora.services;

import com.main.Jora.configs.CustomException;
import com.main.Jora.enums.Status;
import com.main.Jora.models.*;
import com.main.Jora.notifications.NotificationService;
import com.main.Jora.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    UserProjectRoleReposirory userProjectRoleReposirory;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    NotificationService notificationService;
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

    public void sendHelp(Long task_id) throws CustomException.UserNotFoundException,
            CustomException.UserAlreadyJoinedException, CustomException.ObjectExistsException{
        if (userTaskRepository.isModeratorAssignedToTask(task_id)) throw new CustomException.UserAlreadyJoinedException("");
        List<Tag> task_tags = tagRepository.findTagsByTaskId(task_id);
        Long project_id = taskRepository.findProjectIdByTaskId(task_id);
        List<User> moderators = userProjectRoleReposirory.findModeratorsByProjectId(project_id);

        User bestModerator = null;
        int maxScore = Integer.MIN_VALUE;

        for (User moderator : moderators) {
            List<Tag> moderatorTags = tagRepository.findTagsByUserIdAndProjectId(moderator.getId(), project_id);
            int score = calculateTagScore(moderatorTags, task_tags);
            score -= calculateLoadScore(moderator, project_id);

            if (score > maxScore) {
                maxScore = score;
                bestModerator = moderator;
            }
        }
        if (bestModerator == null) throw new CustomException.UserNotFoundException("");
        log.info("Найден модер {} с коэффициентом {}", bestModerator, maxScore);
        log.info("Попытка отправить сообщение");
        notificationService.sendNotificationTo(bestModerator, "Запрос о помощи",
                generateMessage(task_id, project_id));
    }
    private String generateMessage(Long task_id, Long project_id){
        Task task = taskRepository.findById(task_id).orElse(null);
        String project_hash = projectRepository.findHashById(project_id);
        return "Пользователь запросил помощь с задачей " +
                task.getId() + "/" + task.getName() +
                "\nСсылка: http://localhost:8080/projects/" + project_hash + "/tasks";
    }
    // Oценка по совпадениями тегов
    private int calculateTagScore(List<Tag> moderatorTags, List<Tag> taskTags){
        // Set для повышения производительности поиска
        Set<String> moderatorTagNames = new HashSet<>();

        for (Tag tag : moderatorTags) {
            moderatorTagNames.add(tag.getName());
        }
        int matchScore = 0;
        for (Tag tag : taskTags) {
            if (moderatorTagNames.contains(tag.getName())) {
                matchScore++;
            }
        }
        return matchScore;
    }
    // Оценка по загруженности
    private int calculateLoadScore(User user, Long project_id){
        List<Task> tasks = taskRepository.findTaskByUserAndProject(user.getId(), project_id);
        if (tasks.isEmpty()) {
            return 0; // Если нет задач, коэффициент загруженности равен 0
        }
        //Общее количество задач, которые можно взять
        int totalTasks = tasks.size()
                - (int) tasks.stream().filter(task -> task.getStatus() == Status.DONE).count()
                - (int) tasks.stream().filter(task -> task.getStatus() == Status.DELETED).count();
        //Количество взятых задач
        int takedTasks = (int) tasks.stream().filter(task -> task.getStatus() == Status.IN_PROGRESS).count();
        //Количество просроченных задач
        int overdueTasks = (int) tasks.stream().filter(task ->
                task.getDeadline() != null &&
                        task.getDeadline().isBefore(LocalDateTime.now()) &&
                        task.getStatus() != Status.DONE).count();

        // Итог
        int occupancyRate = (totalTasks > 0) ? (takedTasks * 100 / totalTasks) + overdueTasks : 0;
        return Math.max(occupancyRate, occupancyRate*(-1));
    }
}
