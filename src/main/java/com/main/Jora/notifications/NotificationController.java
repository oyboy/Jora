package com.main.Jora.notifications;

import com.main.Jora.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    NotificationService notificationService;
    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }
    @PostMapping("/read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable("id") Long id,
                                           @AuthenticationPrincipal User user){
        Notification notification = notificationService.getNotificationById(id);
        if (notification == null || !Objects.equals(notification.getUser().getId(), user.getId())) {
            return ResponseEntity.notFound().build();
        }
        notificationService.markNotificationAsRead(notification);
        return ResponseEntity.ok().build();
    }
}
