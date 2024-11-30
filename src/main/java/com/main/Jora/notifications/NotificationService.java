package com.main.Jora.notifications;

import com.main.Jora.configs.CustomException;
import com.main.Jora.models.Project;
import com.main.Jora.models.User;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.repositories.UserProjectRoleReposirory;
import com.main.Jora.services.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @Autowired
    private ProjectNotificationRepository projectNotificationRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    UserProjectRoleReposirory userProjectRoleReposirory;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    @Lazy
    private TaskService taskService;

    public void sendNotificationToAll(String project_hash, String title, String message){
        Long project_id = projectRepository.findIdByHash(project_hash);
        Project project = projectRepository.findById(project_id).orElse(null);
        List<User> users = userProjectRoleReposirory.findUsersByProjectId(project_id);

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        log.info("Saving new notification {}", notification);
        notificationRepository.save(notification);

        ProjectNotification projectNotification = new ProjectNotification();
        projectNotification.setNotification(notification);
        projectNotification.setProject(project);
        log.info("Saving new project notification {}", projectNotification);
        projectNotificationRepository.save(projectNotification);

        log.info("Trying to send all notification {}", users);
        for (User user : users){
            UserNotification userNotification = new UserNotification();
            userNotification.setUser(user);
            userNotification.setNotification(notification);
            userNotificationRepository.save(userNotification);
            messagingTemplate.convertAndSendToUser(user.getId().toString(), "/topic/notifications", notification);
        }
    }

    public void sendNotificationTo(User user, String title, String message, String link) throws
            CustomException.ObjectExistsException{
        if (userNotificationRepository.existsByUserIdAndMessage(user.getId(), message))
            throw new CustomException.ObjectExistsException("Запрос уже существует");

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setLink(link);

        UserNotification userNotification = new UserNotification();
        userNotification.setUser(user);
        userNotification.setNotification(notification);

        log.info("Saving new notification {}", notification);
        notificationRepository.save(notification);
        userNotificationRepository.save(userNotification);
        log.info("trying to send notif to {}", user);
        messagingTemplate.convertAndSendToUser(user.getId().toString(), "/topic/notifications", notification);
    }

    public List<Notification> getUnreadNotificationsForUser(Long user_id) {
        return userNotificationRepository.findByUserIdAndReadIsFalse(user_id);
    }

    public void markNotificationAsRead(Notification n, User user) throws CustomException.ObjectExistsException,
            CustomException.UserAlreadyJoinedException {
        UserNotification userNotification = userNotificationRepository.findByNotificationIdAndUserId(n.getId(), user.getId());
        log.info("Marking as read notification id {}", n.getId());
        userNotification.set_read(true);
        userNotificationRepository.save(userNotification);
        //Если это не массовое уведомление, а назначение задачи, то
        //пользователь при нажатии должен быть назначен на неё автоматически
        if (!projectNotificationRepository.existsByNotificationId(n.getId())) {
            Long extractedTaskId = extractTaskIdFromMessage(userNotification.getNotification().getMessage());
            if (extractedTaskId == null) throw new CustomException.ObjectExistsException("Id not found");
            taskService.addUserToTask(extractedTaskId, user);
        }
    }
    private Long extractTaskIdFromMessage(String message) {
        Pattern pattern = Pattern.compile("(\\d+)/");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String task_id = matcher.group(1);
            if (task_id != null && task_id.length() > 0){
                return Long.parseLong(task_id);
            }
        }
        return null;
    }
    public Notification getNotificationById(Long id){
        return notificationRepository.findById(id).orElse(null);
    }
}

