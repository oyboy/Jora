package com.main.Jora.controllers;

import com.main.Jora.models.Task;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.security.PathTraverseDef;
import com.main.Jora.services.TaskService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projects/{project_id}/tasks")
public class TaskController {
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    TaskService taskService;

    @GetMapping
    public String getTasks(@PathVariable Long project_id, Model model,
                           HttpSession httpSession){
        PathTraverseDef pathTraverseDef = new PathTraverseDef(httpSession, projectRepository);
        String response = pathTraverseDef.preventingEntryIntoProject(project_id);
        if (response != null) return response;

        Iterable<Task> tasks = taskService.findAllTasks(project_id);
        model.addAttribute("tasks", tasks);
        model.addAttribute("project", projectRepository.findById(project_id).orElse(null));
        return "tasks";
    }
    @PostMapping
    public String createTask(@Valid @ModelAttribute Task task,
                             @PathVariable Long project_id,
                             Errors errors,
                             Model model){
        if (errors.hasErrors()){
            errors.getAllErrors().forEach(error -> {
                System.out.println("Error in task_controller: " + error);
            });
            model.addAttribute("errors", errors);
            return "tasks";
        }
        taskService.addTask(task, project_id);
        return "redirect:/projects/{project_id}/tasks";
    }
}
