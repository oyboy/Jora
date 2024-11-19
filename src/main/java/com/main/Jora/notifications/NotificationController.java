package com.main.Jora.notifications;

import com.main.Jora.models.User;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Void> markAsRead(@PathVariable("id") Long id,
                                           @AuthenticationPrincipal User user){
        Notification notification = notificationService.getNotificationById(id);
        if (notification == null ) return ResponseEntity.notFound().build();
        notificationService.markNotificationAsRead(notification, user);
        simpMessagingTemplate.convertAndSendToUser(user.getId().toString(), "/topic/notifications", notification);
        return ResponseEntity.ok().build();
    }
}
