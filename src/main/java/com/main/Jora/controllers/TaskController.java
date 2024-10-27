package com.main.Jora.controllers;

import com.main.Jora.configs.CustomException;
import com.main.Jora.enums.Role;
import com.main.Jora.models.*;
import com.main.Jora.models.dto.TaskTagsDTO;
import com.main.Jora.models.dto.UserTagsDTO;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.repositories.TagRepository;
import com.main.Jora.repositories.UserProjectRoleReposirory;
import com.main.Jora.repositories.UserTaskRepository;
import com.main.Jora.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/projects/{project_hash}/tasks")
public class TaskController {
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    TaskService taskService;
    @Autowired
    UserTaskRepository userTaskRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    UserProjectRoleReposirory userProjectRoleReposirory;

    @ModelAttribute("project")
    public Project getProject(@PathVariable String project_hash) {
        Long project_id = projectRepository.findIdByHash(project_hash);
        if (project_id == null) {
            return null;
        }
        return projectRepository.findById(project_id).orElse(null);
    }

    @ModelAttribute("tasks")
    public Iterable<Task> getTasksModel(@PathVariable String project_hash,
                                        @RequestParam(required = false) String deadlineFilter) {
        Long project_id = projectRepository.findIdByHash(project_hash);
        if (project_id == null) {
            return new ArrayList<>();
        }
        return taskService.findTasksByTimeLine(project_id, deadlineFilter);
    }
    @ModelAttribute("usersAndTasks")
    public List<UserTask> getUsersAndTasks(@PathVariable("project_hash") String project_hash){
        Long project_id = projectRepository.findIdByHash(project_hash);
        return userTaskRepository.findAllByProjectId(project_id);
    }
    @ModelAttribute("currentRole")
    public Role getRole(@AuthenticationPrincipal User user,
                        @PathVariable("project_hash") String project_hash){
        Long project_id = projectRepository.findIdByHash(project_hash);
        return userProjectRoleReposirory.findRoleByUserAndProject(user.getId(), project_id);
    }
    @ModelAttribute("currentUser")
    public User getCurrentUser(@AuthenticationPrincipal User user){
        return user;
    }
    @GetMapping
    public String getTasks(@PathVariable String project_hash){
        Long project_id = projectRepository.findIdByHash(project_hash);
        if (project_id == null) return "redirect:/home";
        return "tasks";
    }
    @PostMapping//Нормально
    public String createTask(@Valid @ModelAttribute("task") Task task,
                             Errors errors,
                             @PathVariable String project_hash,
                             Model model,
                             @AuthenticationPrincipal User user){
        task.setCreatedAt(LocalDateTime.now());
        if (task.getDeadline() != null && task.getDeadline().isBefore(task.getCreatedAt())) {
            errors.rejectValue("deadline", "Deadline must be after creation date",
                    "Deadline cannot be earlier than the created date.");
        }
        if (errors.hasErrors()){
            errors.getAllErrors().forEach(error -> {
                System.out.println("Error in task_controller_creator: " + error);
            });
            model.addAttribute("errors", errors);
            return "tasks";
        }
        taskService.addTask(task, project_hash, user);
        return "redirect:/projects/{project_hash}/tasks";
    }
    //На уровне контроллера устанавливаем ограничение на редактирование
    //Редактировать задачу может либо модератор, либо её создатель
    @GetMapping("/edit/{task_id}")
    public String editTask(@PathVariable Long task_id,
                           @PathVariable String project_hash,
                           @AuthenticationPrincipal User user,
                           Model model){
        Task task = taskService.getTaskById(task_id);

        Role role = getRole(user, project_hash);
        if (!isAuthorizedToEditTask(task, user, role)) {
            return "redirect:/projects/{project_hash}/tasks";
        }
        model.addAttribute("task", task);
        model.addAttribute("project_hash", project_hash);
        return "task-edit";
    }
    private boolean isAuthorizedToEditTask(Task task, User user, Role role) {
        return userTaskRepository.existsByUserIdAndTaskId(user.getId(), task.getId()) || role == Role.ROLE_MODERATOR;
    }
    @PostMapping("/edit") //Чё-то воняет. Главное, что работает!
    public String editTask(@RequestParam("task_id") Long task_id,
                           @Valid @ModelAttribute Task form,
                           Errors errors,
                           Model model){
        if (errors.hasErrors()){
            errors.getAllErrors().forEach(error -> {
                System.out.println("Error in task_controller_creator: " + error);
            });
            Task task = taskService.getTaskById(task_id);
            model.addAttribute("task", task);
            model.addAttribute("errors", errors);
            return "task-edit";
        }
        taskService.changeTaskFieldsAndSave(task_id, form);
        return "redirect:/projects/{project_hash}/tasks";
    }
    @ModelAttribute(name = "tagsForProject")
    public List<Tag> getTagsForThisProject(@PathVariable("project_hash") String project_hash){
        Long project_id = projectRepository.findIdByHash(project_hash);
        return tagRepository.findTagsByProjectId(project_id);
    }
    @ModelAttribute(name = "tasksAndTags")
    public List<TaskTagsDTO> getTasksWithTags(@PathVariable("project_hash") String project_hash) {
        Long project_id = projectRepository.findIdByHash(project_hash);
        Iterable<Task> tasks = taskService.findAllTasks(project_id);

        List<TaskTagsDTO> tasksWithTags = new ArrayList<>();
        for (Task task : tasks) {
            List<Tag> tags = task.getTags();
            tasksWithTags.add(new TaskTagsDTO(task, tags));
        }
        return tasksWithTags;
    }
    @PostMapping("/tag-set")
    public String setTag(@RequestParam(value = "taskId", required = false) Long taskId,
                         @RequestParam("tagName") String tagName,
                         @PathVariable("project_hash") String project_hash,
                         Model model){
        if (taskId == null){
            model.addAttribute("idException", "Нужно указать id");
            return "tasks";
        }
        if (taskService.getTaskById(taskId) == null){
            model.addAttribute("idException", "Нет задачи с таким id");
            return "tasks";
        }
        taskService.setTagToTask(taskId, tagName);
        return "redirect:/projects/" + project_hash + "/tasks";
    }
    @PostMapping("/edit/join")
    public String joinToTask(@RequestParam("task_id") Long task_id,
                             @AuthenticationPrincipal User user,
                             @PathVariable("project_hash") String project_hash,
                             Model model){
        try{
            taskService.addUserToTask(taskService.getTaskById(task_id), user);
        } catch (CustomException.UserAlreadyJoinedException ex){
            Task task = taskService.getTaskById(task_id);
            model.addAttribute("task", task);
            model.addAttribute("userError", "Вы уже участник");
            return "task-edit";
        }
        return "redirect:/projects/" + project_hash + "/tasks";
    }
    @PostMapping("/edit/help")
    public String sendHelp(@RequestParam("task_id") Long task_id,
                           Model model){
        try{
            Task task = taskService.getTaskById(task_id);
            model.addAttribute("task", task);
            taskService.sendHelp(task_id);
        } catch (CustomException.UserNotFoundException ex){
            model.addAttribute("moderNotFound", "Модератор не найден, вам никто не поможет");
            return "task-edit";
        } catch (CustomException.UserAlreadyJoinedException ex){
            model.addAttribute("moderNotFound", "Модератор уже привязан к этой задаче");
            return "task-edit";
        } catch (CustomException.ObjectExistsException ex){
            model.addAttribute("moderNotFound", "Запрос уже был отправлен");
            return "task-edit";
        }
        model.addAttribute("notificationResult", "Просьба отправлена");
        return "task-edit";
    }
}