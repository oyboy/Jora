package com.main.Jora.notifications;

import com.main.Jora.configs.CustomException;
import com.main.Jora.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public void sendNotificationTo(User user, String title, String message) throws
            CustomException.ObjectExistsException{
        if (notificationRepository.existsNotificationByMessageAndUser(message, user))
            throw new CustomException.ObjectExistsException("Запрос уже существует");

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setUser(user);

        log.info("Saving new notification {}", notification);
        notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotificationsForUser(Long user_id) {
        return notificationRepository.findByUserIdAndReadIsFalse(user_id);
    }

    public void markNotificationAsRead(Notification notification) {
        log.info("Marking as read notification id {}", notification.getId());
        notification.set_read(true);
        notificationRepository.save(notification);
    }
    public Notification getNotificationById(Long id){
        return notificationRepository.findById(id).orElse(null);
    }
}

