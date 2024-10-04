package com.main.Jora.controllers;

import com.main.Jora.models.Project;
import com.main.Jora.models.Task;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/projects/{project_hash}/tasks")
public class TaskController {
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    TaskService taskService;

    @ModelAttribute("project")
    public Project getProject(@PathVariable String project_hash) {
        Long project_id = projectRepository.findIdByHash(project_hash);
        if (project_id == null) {
            return null;
        }
        return projectRepository.findById(project_id).orElse(null);
    }

    @ModelAttribute("tasks")
    public Iterable<Task> getTasksModel(@PathVariable String project_hash) {
        Long project_id = projectRepository.findIdByHash(project_hash);
        if (project_id == null) {
            return new ArrayList<>();
        }
        return taskService.findAllTasks(project_id);
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
                             Model model){
        if (errors.hasErrors()){
            errors.getAllErrors().forEach(error -> {
                System.out.println("Error in task_controller_creator: " + error);
            });
            model.addAttribute("errors", errors);
            return "tasks";
        }
        taskService.addTask(task, project_hash);
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
}
