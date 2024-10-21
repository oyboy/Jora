package com.main.Jora.notifications;

import com.main.Jora.configs.CustomException;
import com.main.Jora.models.Project;
import com.main.Jora.models.User;
import com.main.Jora.repositories.ProjectRepository;
import com.main.Jora.repositories.UserProjectRoleReposirory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    UserProjectRoleReposirory userProjectRoleReposirory;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    public void sendNotificationToAll(String project_hash, String title, String message){
        Long project_id = projectRepository.findIdByHash(project_hash);
        Project project = projectRepository.findById(project_id).orElse(null);
        List<User> users = userProjectRoleReposirory.findUsersByProjectId(project_id);

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        project.getNotifications().add(notification);

        log.info("Saving new notification {}", notification);
        notificationRepository.save(notification);

        log.info("Trying to send all notification {}", users);
        for (User user : users){
            UserNotification userNotification = new UserNotification();
            userNotification.setUser(user);
            userNotification.setNotification(notification);
            userNotificationRepository.save(userNotification);
            messagingTemplate.convertAndSendToUser(user.getId().toString(), "/topic/notifications", notification);
        }
    }

    public void sendNotificationTo(User user, String title, String message) throws
            CustomException.ObjectExistsException{
        if (userNotificationRepository.existsByUserIdAndMessage(user.getId(), message))
            throw new CustomException.ObjectExistsException("Запрос уже существует");

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);

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

    public void markNotificationAsRead(Notification n, User user) {
        UserNotification notification = userNotificationRepository.findByNotificationIdAndUserId(n.getId(), user.getId());
        log.info("Marking as read notification id {}", n.getId());
        notification.set_read(true);
        userNotificationRepository.save(notification);
    }
    public Notification getNotificationById(Long id){
        return notificationRepository.findById(id).orElse(null);
    }
}

