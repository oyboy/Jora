package com.main.Jora.notifications;

import com.main.Jora.configs.CustomException;
import com.main.Jora.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    NotificationService notificationService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }
    @PostMapping("/read/{id}")
    public ResponseEntity<String> markAsRead(@PathVariable("id") Long id,
                                             @AuthenticationPrincipal User user) {
        Notification notification = notificationService.getNotificationById(id);
        if (notification == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not found.");
        }
        try {
            notificationService.markNotificationAsRead(notification, user);
        } catch (CustomException.ObjectExistsException oe) {
            return ResponseEntity.badRequest().body("The notification is already marked as read.");
        } catch (CustomException.UserAlreadyJoinedException ua) {
            return ResponseEntity.badRequest().body("User has already joined this notification.");
        }
        simpMessagingTemplate.convertAndSendToUser(user.getId().toString(), "/topic/notifications", notification);
        return ResponseEntity.ok("Notification marked as read successfully.");
    }

}
