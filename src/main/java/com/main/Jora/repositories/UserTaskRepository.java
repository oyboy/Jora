package com.main.Jora.repositories;

import com.main.Jora.models.UserTask;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserTaskRepository extends CrudRepository<UserTask, Long> {
    boolean existsByUserIdAndTaskId(Long user_id, Long task_id);
    List<UserTask> findAllByProjectId(Long project_id);
}
