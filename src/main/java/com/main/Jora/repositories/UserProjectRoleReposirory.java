package com.main.Jora.repositories;

import com.main.Jora.enums.Role;
import com.main.Jora.models.Project;
import com.main.Jora.models.User;
import com.main.Jora.models.UserProjectRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserProjectRoleReposirory extends CrudRepository<UserProjectRole, Long> {
   @Query("SELECT p " +
           "FROM Project p " +
           "JOIN UserProjectRole upr " +
           "ON p.id = upr.project.id " +
           "WHERE upr.user.id = :userId AND upr.banned = false")
   List<Project> findProjectsByUserId(@Param("userId") Long userId);
   @Query("SELECT upr " +
           "FROM UserProjectRole upr " +
           "WHERE upr.project.id = :projectId AND upr.banned = false")
   List<UserProjectRole> findUsersAndRolesByProjectId(@Param("projectId") Long projectId);

   @Query("SELECT COUNT(*) > 0 " +
           "FROM UserProjectRole upr " +
           "WHERE upr.user.id = :userId AND upr.project.id = :projectId")
   boolean existsByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);

   @Query("SELECT upr.banned " +
           "FROM UserProjectRole upr " +
           "WHERE upr.user.id = :userId AND upr.project.id = :projectId")
   boolean isUserBanned(@Param("userId") Long userId, @Param("projectId") Long projectId);

   UserProjectRole getUserProjectRoleByUserAndProject(User user, Project project);

   @Query("SELECT upr.role " +
           "FROM UserProjectRole upr " +
           "WHERE upr.user.id = :userId AND upr.project.id = :projectId")
   Role findRoleByUserAndProject(@Param("userId") Long userId, @Param("projectId") Long projectId);

   @Query("SELECT u " +
           "FROM User u " +
           "JOIN UserProjectRole upr " +
           "ON u.id = upr.user.id " +
           "WHERE upr.project.id = :projectId AND upr.role = 'ROLE_MODERATOR'")
   List<User> findModeratorsByProjectId(@Param("projectId") Long projectId);
   @Query("SELECT u " +
           "FROM User u " +
           "JOIN UserProjectRole upr " +
           "ON u.id = upr.user.id " +
           "WHERE upr.project.id = :projectId")
   List<User> findUsersByProjectId(@Param("projectId") Long projectId);
}
