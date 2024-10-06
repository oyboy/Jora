package com.main.Jora.repositories;

import com.main.Jora.models.Project;
import com.main.Jora.models.UserProjectRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserProjectRoleReposirory extends CrudRepository<UserProjectRole, Long> {
   /* List<Project> findProjectsByUserId(Long userId);*/
   @Query("SELECT p FROM Project p JOIN UserProjectRole upr ON p.id = upr.project.id WHERE upr.user.id = :userId")
   List<Project> findProjectsByUserId(@Param("userId") Long userId);
}
