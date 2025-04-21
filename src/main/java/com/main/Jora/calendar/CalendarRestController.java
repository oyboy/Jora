package com.main.Jora.calendar;

import com.main.Jora.enums.Role;
import com.main.Jora.models.Project;
import com.main.Jora.models.Task;
import com.main.Jora.models.User;
import com.main.Jora.repositories.TaskRepository;
import com.main.Jora.repositories.UserProjectRoleReposirory;
import com.main.Jora.repositories.UserTaskRepository;
import com.main.Jora.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/home/calendar")
public class CalendarRestController {
    @Autowired
    TaskService taskService;
    @Autowired
    UserTaskRepository userTaskRepository;
    @Autowired
    UserProjectRoleReposirory userProjectRoleReposirory;
    @Autowired
    TaskRepository taskRepository;

    @GetMapping("/tasks/my")
    public ResponseEntity<List<CalendarTaskDTO>> getCalendarTasksForUser(@AuthenticationPrincipal User user) {
        List<Task> tasks = taskService.getAllTasksForUser(user.getId());
        if (tasks != null) return ResponseEntity.ok(tasks.stream()
                .map(Task::convertToCalendarTask)
                .collect(Collectors.toList())
        );
        return ResponseEntity.notFound().build();
    }
    //Get all tasks, including team's, from all projects
    @GetMapping("/tasks/all")
    public ResponseEntity<List<CalendarTaskDTO>> getAllCalendarTasksForUser(@AuthenticationPrincipal User user) {
        List<Project> projects = userProjectRoleReposirory.findProjectsByUserId(user.getId());
        if (projects == null) return ResponseEntity.notFound().build();
        List<Task> all_tasks = new ArrayList<>();
        for (Project project : projects) {
            all_tasks.addAll(taskRepository.findTasksByProjectId(project.getId()));
        }
        return ResponseEntity.ok(all_tasks.stream()
                .map(Task::convertToCalendarTask)
                .collect(Collectors.toList())
        );
    }
    @GetMapping("/tasks/my/{projectId}")
    public ResponseEntity<List<CalendarTaskDTO>> getCalendarTasksForUserInProject(@PathVariable Long projectId, @AuthenticationPrincipal User user) {
        List<Task> tasks = taskRepository.findTasksByUserAndProject(user.getId(), projectId);
        if (tasks != null) return ResponseEntity.ok(tasks.stream()
                .map(Task::convertToCalendarTask)
                .collect(Collectors.toList())
        );
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/tasks/all/{projectId}")
    public ResponseEntity<List<CalendarTaskDTO>> getAllCalendarTasksForUserInProject(@PathVariable Long projectId) {
        List<Task> tasks = taskRepository.findTasksByProjectId(projectId);
        if (tasks != null) return ResponseEntity.ok(tasks.stream()
                .map(Task::convertToCalendarTask)
                .collect(Collectors.toList())
        );
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/tasks/update")
    public ResponseEntity<Void> changeDate(@RequestBody CalendarTaskDTO calendarTaskDTO,
                                           @AuthenticationPrincipal User user) {
        //Из календаря можно менять только свои задачи
        if (!isAuthorizedToEditTask(calendarTaskDTO, user)) {
            return ResponseEntity.status(403).build();
        }
        taskService.changeDate(calendarTaskDTO);
        return ResponseEntity.ok().build();
    }
    private boolean isAuthorizedToEditTask(CalendarTaskDTO calendarTaskDTO, User user) {
        return userTaskRepository.existsByUserIdAndTaskId(user.getId(), calendarTaskDTO.getTaskId());
    }
    @GetMapping("/tasks/{taskId}/users")
    public ResponseEntity<List<Map<Long, String>>> getUsersByTaskId(@PathVariable Long taskId) {
        List<User> users = userTaskRepository.getUsersByTaskId(taskId);
        List<Map<Long, String>> userList = new ArrayList<>();
        for (User user : users) {
            Map<Long, String> userMap = new HashMap<>();
            userMap.put(user.getId(), user.getUsername());
            userList.add(userMap);
        }
        return ResponseEntity.ok(userList);
    }
    @GetMapping("/projects")
    public ResponseEntity<List<Map<Long, String>>> getAllProjectsForUser(@AuthenticationPrincipal User user) {
        List<Project> projects = userProjectRoleReposirory.findProjectsByUserId(user.getId());
        if (projects == null) return ResponseEntity.notFound().build();

        List<Map<Long, String>> projectList = new ArrayList<>();
        for (Project project : projects) {
            Map<Long, String> projectMap = new HashMap<>();
            projectMap.put(project.getId(), project.getTitle());
            projectList.add(projectMap);
        }
        return ResponseEntity.ok(projectList);
    }
}