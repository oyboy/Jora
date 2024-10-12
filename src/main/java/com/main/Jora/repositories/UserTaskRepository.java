package com.main.Jora.repositories;

import com.main.Jora.models.UserTask;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface UserTaskRepository extends CrudRepository<UserTask, Long> {
    boolean existsByUserIdAndTaskId(Long user_id, Long task_id);
    @Query("SELECT ut " +
            "FROM UserTask ut " +
            "JOIN Task t ON ut.task.id = t.id " +
            "WHERE t.project.id = :projectId")
    List<UserTask> findAllByProjectId(@RequestParam("projectId") Long projectId);
}
