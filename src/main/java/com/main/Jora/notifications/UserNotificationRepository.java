package com.main.Jora.notifications;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserNotificationRepository extends CrudRepository<UserNotification, Long> {
    @Query("SELECT n " +
            "FROM UserNotification un " +
            "JOIN Notification n " +
            "ON n.id = un.notification.id " +
            "WHERE un.user.id = :userId AND un.is_read = false")
    List<Notification> findByUserIdAndReadIsFalse(@Param("userId") Long userId);
    @Query("SELECT un " +
            "FROM UserNotification un " +
            "JOIN Notification n " +
            "ON n.id = un.notification.id " +
            "WHERE n.id = :id AND un.user.id = :userId")
    UserNotification findByNotificationIdAndUserId(@Param("id") Long id,
                                                   @Param("userId") Long userId);

    @Query("SELECT COUNT(*) > 0 " +
            "FROM UserNotification un " +
            "JOIN Notification n " +
            "ON n.id = un.notification.id " +
            "WHERE un.user.id = :userId AND n.message = :message")
    boolean existsByUserIdAndMessage(@Param("userId") Long userId,
                                          @Param("message") String message);
}
