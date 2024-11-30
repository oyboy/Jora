package com.main.Jora.notifications;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectNotificationRepository extends CrudRepository<ProjectNotification, Long> {
    @Query("SELECT count(*) > 0 " +
            "FROM ProjectNotification p " +
            "WHERE p.notification.id = :notificationId")
    boolean existsByNotificationId(@Param("notificationId") Long notificationId);
}
