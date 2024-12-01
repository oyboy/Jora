package com.main.Jora.repositories;

import com.main.Jora.models.User;
import com.main.Jora.models.UserTask;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Repository
public interface UserTaskRepository extends CrudRepository<UserTask, Long> {
    boolean existsByUserIdAndTaskId(Long user_id, Long task_id);
    @Query("SELECT ut " +
            "FROM UserTask ut " +
            "JOIN Task t ON ut.task.id = t.id " +
            "WHERE t.project.id = :projectId")
    List<UserTask> findAllByProjectId(@RequestParam("projectId") Long projectId);

    @Query("SELECT COUNT(*) > 0 " +
            "FROM UserTask ut " +
            "JOIN UserProjectRole upr " +
            "ON ut.user.id = upr.user.id " +
            "WHERE upr.role = 'ROLE_MODERATOR' AND ut.task.id = :taskId")
    boolean isModeratorAssignedToTask(@Param("taskId") Long taskId);
    @Query("SELECT u " +
            "FROM User u " +
            "JOIN UserTask ut " +
            "ON ut.user.id = u.id " +
            "WHERE ut.task.id = :taskId")
    List<User> getUsersByTaskId(@Param("taskId") Long taskId);

    List<UserTask> findAllByTaskId(Long taskId);
}
