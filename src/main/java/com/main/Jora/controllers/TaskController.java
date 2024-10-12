package com.main.Jora.controllers;

import com.main.Jora.configs.CustomException;
import com.main.Jora.models.*;
import com.main.Jora.models.dto.TaskTagsDTO;
import com.main.Jora.models.dto.UserTagsDTO;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.repositories.TagRepository;
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
    @GetMapping("/edit/{task_id}")
    public String editTask(@PathVariable Long task_id,
                           @PathVariable String project_hash,
                           Model model){
        Task task = taskService.getTaskById(task_id);
        model.addAttribute("task", task);
        model.addAttribute("project_hash", project_hash);
        return "task-edit";
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
            taskService.addUserToTask(taskService.getTaskById(task_id), project_hash, user);
        } catch (CustomException.UserAlreadyJoinedException ex){
            Task task = taskService.getTaskById(task_id);
            model.addAttribute("task", task);
            model.addAttribute("userError", "Вы уже участник");
            return "task-edit";
        }
        return "redirect:/projects/" + project_hash + "/tasks";
    }
}