package com.main.Jora.controllers;

import com.main.Jora.enums.Priority;
import com.main.Jora.models.Task;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/projects/{project_hash}/tasks")
public class TaskController {
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    TaskService taskService;

    @GetMapping
    public String getTasks(@PathVariable String project_hash, Model model){
        Long project_id = projectRepository.findIdByHash(project_hash);
        if (project_id == null) return "redirect:/home";

        Iterable<Task> tasks = taskService.findAllTasks(project_id);
        model.addAttribute("tasks", tasks);
        model.addAttribute("project", projectRepository.findById(project_id).orElse(null));
        return "tasks";
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
    @PostMapping("/edit")
    public String editTask(@RequestParam("task_id") Task task,
                           @RequestParam Map<String, String> form){
        taskService.changeTaskFieldsAndSave(task, form);
        return "redirect:/projects/{project_hash}/tasks";
    }
    @PostMapping
    public String createTask(@Valid @ModelAttribute Task task,
                             @PathVariable String project_hash,
                             Errors errors,
                             Model model){
        if (errors.hasErrors()){
            errors.getAllErrors().forEach(error -> {
                System.out.println("Error in task_controller: " + error);
            });
            model.addAttribute("errors", errors);
            return "tasks";
        }
        taskService.addTask(task, project_hash);
        return "redirect:/projects/{project_hash}/tasks";
    }
}
