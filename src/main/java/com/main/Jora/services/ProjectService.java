package com.main.Jora.services;

import com.main.Jora.configs.CustomException;
import com.main.Jora.discussion.*;
import com.main.Jora.enums.Role;
import com.main.Jora.models.*;
import com.main.Jora.notifications.NotificationRepository;
import com.main.Jora.notifications.ProjectNotificationRepository;
import com.main.Jora.notifications.UserNotificationRepository;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.repositories.TagRepository;
import com.main.Jora.repositories.UserProjectRoleReposirory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProjectService {
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    UserProjectRoleReposirory userProjectRoleReposirory;
    @Autowired
    private ProjectNotificationRepository projectNotificationRepository;
    @Autowired
    @Lazy
    private TaskService taskService;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @Autowired
    private DiscussionRepository discussionRepository;
    @Autowired
    private DiscussionService discussionService;

    @CachePut(value = "project", key = "#project.getHash()")
    public void saveProject(Project project, User user) {
        //Избежание ситуации, когда проект с данным хешем уже существует
        while (projectRepository.findIdByHash(project.getHash()) != null) project.setHash(project.generateHash());

        //Связывание проекта с пользователями
        log.info("Попытка связать {} \n\t с {}", project, user);
        UserProjectRole userProjectRole = new UserProjectRole();
        userProjectRole.setProject(project);
        userProjectRole.setUser(user);
        userProjectRole.setRole(Role.ROLE_LEADER);

        project.getUserProjectRoles().add(userProjectRole);
        user.getUserProjectRoles().add(userProjectRole);

        //Сохранение
        projectRepository.save(project);
        log.info("Saving project: {}", project);
        userProjectRoleReposirory.save(userProjectRole);
    }

    public List<Project> getProjectsForUser(User user) {
        return userProjectRoleReposirory.findProjectsByUserId(user.getId());
    }
    //@Cacheable(value = "project", key = "#hash")
    public Project findProjectByHash(String hash) {
        return projectRepository.findProjectByHash(hash);
    }
    @Cacheable(value = "project_id", key = "#hash")
    public Long findIdByHash(String hash) {
        return projectRepository.findIdByHash(hash);
    }

    public void addUserToProject(String project_hash, User user) throws CustomException.UserAlreadyJoinedException,
            CustomException.UserBannedException, CustomException.ObjectExistsException {
        Project project = projectRepository.findProjectByHash(project_hash);
        if (project == null) throw new CustomException.ObjectExistsException("Проекта не существует");

        // Проверяем существует ли уже связь пользователя с проектом
        if (userProjectRoleReposirory.existsByUserIdAndProjectId(user.getId(), project.getId())) {
            if (userProjectRoleReposirory.isUserBanned(user.getId(), project.getId())) {
                log.warn("User {} is banned in project {}", user.getId(), project.getId());
                throw new CustomException.UserBannedException("Пользователь забанен в этом проекте");
            }
            log.warn("User {} is already a member of project {}", user.getId(), project.getId());
            throw new CustomException.UserAlreadyJoinedException("Пользователь уже добавлен к проекту"); // Или выбрасываем исключение, если нужно
        }
        //Если связи нет, создаём новую запись в связанной таблице
        UserProjectRole userProjectRole = new UserProjectRole();
        userProjectRole.setProject(project);
        userProjectRole.setUser(user);
        userProjectRole.setRole(Role.ROLE_PARTICIPANT);
        //Сохраняем её
        log.info("Saving relation: {}", userProjectRole);
        userProjectRoleReposirory.save(userProjectRole);
    }

    @Transactional
    public void deleteProject(Long projectId) throws CustomException.ObjectExistsException {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) throw new CustomException.ObjectExistsException("");
        log.info("------------------------");
        log.info("Removing user-project-role");
        project.getUserProjectRoles().clear();

        log.info("Removing tags");
        List<Tag> tags = new ArrayList<>(project.getTags());
        for (Tag tag : tags) {
            tag.getUsers().forEach(user -> user.getTags().clear());
            tagRepository.deleteById(tag.getId());
        }
        project.getTags().clear();

        log.info("Removing notifications");
        List<Long> notif_ids = projectNotificationRepository.getNotificationIdsByProjectId(projectId);
        projectNotificationRepository.deleteAllByProjectId(projectId);
        userNotificationRepository.deleteAllByNotificationIds(notif_ids);
        notificationRepository.deleteAllById(notif_ids);

        log.info("Removing tasks");
        List<Long> taskIds = project.getTaskList().stream()
                .map(Task::getId)
                .toList();
        try{
            taskService.deleteTasksByIds(taskIds);
            project.getTaskList().clear();
        } catch (CustomException.ObjectExistsException co) {
            log.info("Tasks not found");
        }

        log.info("Removing discussion");
        String projectHash = project.getHash();
        List<DiscussionComment> comments = discussionRepository.getDiscussionCommentsByProjectHash(projectHash);
        List<String> attachmentIds = comments.stream()
                .flatMap(comment -> comment.getAttachments().stream()
                .map(FileAttachment::getId))
                .toList();
        discussionRepository.deleteByProjectHash(projectHash);
        for (String id : attachmentIds){
            discussionService.deleteAttachmentById(id);
        }

        log.info("Removing project");
        projectRepository.delete(project);
        log.info("Project removed");
        log.info("------------------------");
    }
}