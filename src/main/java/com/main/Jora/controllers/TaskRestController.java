package com.main.Jora.controllers;

import com.main.Jora.models.Task;
import com.main.Jora.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{project_hash}/api/tasks")
public class TaskRestController {
    @Autowired
    TaskService taskService;
    @PostMapping("/update")
    public ResponseEntity<Void> changeStatus(@RequestBody Task task) {
        taskService.changeStatus(task);
        return ResponseEntity.ok().build();
    }
}
