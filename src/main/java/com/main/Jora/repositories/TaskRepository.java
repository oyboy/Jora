package com.main.Jora.repositories;

import com.main.Jora.models.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findTasksByProjectId(Long id);

    List<Task> findByProjectIdAndDeadlineBetween(Long projectId, LocalDateTime start, LocalDateTime end);
    List<Task> findByProjectIdAndDeadlineIsNull(Long projectId);

}
