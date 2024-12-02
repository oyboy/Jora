package com.main.Jora.comments;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE c.task.id = :taskId")
    List<Comment> findAllByTaskId(@Param("taskId") Long taskId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.task.id IN :taskIds")
    void deleteAllByTaskIds(@Param("taskIds") List<Long> taskIds);
}
