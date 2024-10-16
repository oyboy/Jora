package com.main.Jora.notifications;

import com.main.Jora.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Long> {
    @Query("SELECT n " +
            "FROM Notification n " +
            "WHERE n.user.id = :userId AND n.is_read = false")
    List<Notification> findByUserIdAndReadIsFalse(@Param("userId") Long userId);

    boolean existsNotificationByMessageAndUser(String message, User user);
}
