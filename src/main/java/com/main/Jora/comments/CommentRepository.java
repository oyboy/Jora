package com.main.Jora.comments;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE c.task.id = :taskId")
    List<Comment> findAllByTaskId(@Param("taskId") Long taskId);
}
