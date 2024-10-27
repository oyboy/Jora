package com.main.Jora.comments;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
    @Query("SELECT new com.main.Jora.comments.CommentReader(u.username, u.email, u.id, uc.readAt) " +
            "FROM User u " +
            "JOIN UserCommentDTO uc " +
            "ON uc.userId = u.id " +
            "WHERE uc.commentId = :commentId AND uc.readAt IS NOT NULL")
    List<CommentReader> getReadersForComment(@Param("commentId") Long commentId);

    @Modifying
    @Query(value = """
    DELETE FROM user_commentdto
    WHERE comment_id IN (
        SELECT cr1.comment_id
        FROM user_commentdto AS cr1
        WHERE cr1.read_at IS NOT NULL
        GROUP BY cr1.comment_id, cr1.task_id
        HAVING COUNT(cr1.user_id) = (
            SELECT COUNT(cr2.user_id)
            FROM user_commentdto AS cr2
            WHERE cr1.comment_id = cr2.comment_id
            GROUP BY cr2.comment_id
        )
    );
""", nativeQuery = true)
    void deleteReadComments();

}
