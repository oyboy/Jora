package com.main.Jora.services;

import com.main.Jora.calendar.CalendarTaskDTO;
import com.main.Jora.comments.Comment;
import com.main.Jora.comments.CommentRepository;
import com.main.Jora.comments.UserCommentDTO;
import com.main.Jora.comments.UserCommentRepository;
import com.main.Jora.configs.CustomException;
import com.main.Jora.enums.Status;
import com.main.Jora.models.*;
import com.main.Jora.notifications.NotificationService;
import com.main.Jora.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Lazy
    NotificationService notificationService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserCommentRepository userCommentRepository;

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
    public void addUserToTask(Long task_id, User user) throws CustomException.UserAlreadyJoinedException{
        if (userTaskRepository.existsByUserIdAndTaskId(user.getId(), task_id)) {
            log.warn("User {} is already a member of task {}", user.getId(), task_id);
            throw new CustomException.UserAlreadyJoinedException("Пользователь уже добавлен к задаче");
        }
        Task task = taskRepository.findById(task_id).orElse(null);
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

    public void setTagToTask(Long task_id, String project_hash, String tagName) throws
            CustomException.ObjectExistsException {
        Task task = taskRepository.findById(task_id).orElse(null);
        Long projectId = projectRepository.findIdByHash(project_hash);

        if (!taskRepository.existsByProjectIdAndId(projectId, task.getId()))
            throw new CustomException.ObjectExistsException("");

        Tag tag = tagRepository.findTagByTagNameAndProjectId(tagName, projectId);
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
            log.info("Moderator {} has scores {}", moderator, score);
            if (score > maxScore) {
                maxScore = score;
                bestModerator = moderator;
            }
        }
        if (bestModerator == null) throw new CustomException.UserNotFoundException("");
        log.info("Найден модер {} с коэффициентом {}", bestModerator, maxScore);
        log.info("Попытка отправить сообщение");
        notificationService.sendNotificationTo(bestModerator, "Новое исполнение",
                generateMessage(task_id), createLinkToTasks(project_id));
    }
    private String generateMessage(Long task_id){
        Task task = taskRepository.findById(task_id).orElse(null);
        return "Вы назначены на задачу: " +
                task_id + "/" + task.getName();
    }
    private String createLinkToTasks(Long project_id){
        String project_hash = projectRepository.findHashById(project_id);
        //MUST BE CHANGED IN DEPLOY
        return "http://localhost:8081/projects/" + project_hash + "/tasks";
    }
    // Oценка по совпадениями тегов
    private int calculateTagScore(List<Tag> moderatorTags, List<Tag> taskTags){
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
        List<Task> tasks = taskRepository.findTasksByUserAndProject(user.getId(), project_id);
        if (tasks.isEmpty()) {
            return 0;
        }
        //Количество взятых задач
        int takedTasks = (int) tasks.stream().filter(task -> task.getStatus() == Status.IN_PROGRESS ||
                task.getStatus() == Status.CREATED).count();
        //Количество просроченных задач
        int overdueTasks = (int) tasks.stream().filter(task ->
                task.getDeadline() != null &&
                        task.getDeadline().isBefore(LocalDateTime.now()) &&
                        task.getStatus() != Status.DONE).count();
        return overdueTasks + takedTasks;
    }
    //Получить все задачи для пользователя (для календаря)
    public List<Task> getAllTasksForUser(Long userId){
        log.info("Searching event-tasks for user {}", userId);
        return taskRepository.findAllTasksByUserId(userId);
    }
    public void changeDate(CalendarTaskDTO calendarTaskDTO){
        Task task = taskRepository.findById(calendarTaskDTO.getTaskId()).orElse(null);
        log.info("Changing date: {}", calendarTaskDTO);
        task.setCreatedAt(calendarTaskDTO.getCreatedAt());
        task.setDeadline(calendarTaskDTO.getDeadline());
        taskRepository.save(task);
    }
    public void changeStatus(Task taskForm){
        Task task = taskRepository.findById(taskForm.getId()).orElse(null);
        log.info("Changing status: {}", task);
        task.setStatus(taskForm.getStatus());
        taskRepository.save(task);
    }
    @Transactional
    public void deleteTasksByIds(List<Long> taskIds) throws CustomException.ObjectExistsException{
        if (taskIds == null || taskIds.isEmpty()) throw new CustomException.ObjectExistsException("");

        log.info("Deleting user-comment dtos");
        userCommentRepository.deleteAllByTaskIds(taskIds);

        log.info("Deleting comments");
        commentRepository.deleteAllByTaskIds(taskIds);

        Iterable<Task> tasks = taskRepository.findAllById(taskIds);
        tasks.forEach(task -> {
            log.info("Deleting user-tasks");
            task.getUserTasks().clear();

            log.info("Deleting tags");
            task.getTags().clear();

            log.info("Deleting task {} from project", task.getId());
            task.getProject().getTaskList().remove(task);
        });

        taskRepository.deleteAll(tasks);
        log.info("Tasks deleted");
    }
}