package com.main.Jora.repositories;

import com.main.Jora.models.Task;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findTasksByProjectId(Long id);

    Task findTaskById(Long id);

    List<Task> findByProjectIdAndDeadlineBetween(Long projectId, LocalDateTime start, LocalDateTime end);
    List<Task> findByProjectIdAndDeadlineIsNull(Long projectId);

    @Query("SELECT t.project.id " +
            "FROM Task t " +
            "WHERE t.id = :taskId")
    Long findProjectIdByTaskId(@Param("taskId") Long taskId);

    @Query("SELECT t " +
            "FROM Task t " +
            "JOIN UserTask ut " +
            "ON t.id = ut.task.id " +
            "WHERE ut.user.id = :userId AND t.project.id = :projectId")
    List<Task> findTaskByUserAndProject(@Param("userId") Long userId,
                                               @Param("projectId") Long projectId);
}
