package com.main.Jora.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheUpdater {
    @Autowired
    UserNotificationRepository userNotificationRepository;

    @CachePut(value = "notifications", key = "#user_id")
    public List<Notification> refreshUnreadNotificationsForUser(Long user_id) {
        return userNotificationRepository.findByUserIdAndReadIsFalse(user_id);
    }
}
