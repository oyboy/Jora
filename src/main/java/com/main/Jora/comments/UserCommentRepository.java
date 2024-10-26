package com.main.Jora.comments;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserCommentRepository extends CrudRepository<UserCommentDTO, Long> {
    @Query("SELECT COUNT(*) " +
            "FROM UserCommentDTO uc " +
            "WHERE uc.userId = :userId AND uc.readAt IS NULL AND uc.taskId = :taskId")
    Long getUnreadCommentsCount(@Param("userId") Long userId, @Param("taskId") Long taskId);

    @Modifying //Прямое обращение к базе без извлечения поля
    @Query("UPDATE UserCommentDTO uc " +
            "SET uc.readAt = CURRENT_TIMESTAMP " +
            "WHERE uc.userId = :userId " +
            "AND uc.taskId = :taskId " +
            "AND uc.commentId = :commentId " +
            "AND uc.readAt IS NULL")
    void updateCommentAsRead(@Param("userId") Long userId,
                             @Param("taskId") Long taskId,
                             @Param("commentId") Long commentId);

}
