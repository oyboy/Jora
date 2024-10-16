package com.main.Jora.repositories;

import com.main.Jora.models.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {
    @Query("SELECT p.id FROM Project p WHERE p.hash = :hash")
    Long findIdByHash(@Param("hash") String hash);
    Project findProjectByHash(String project_hash);
    @Query("SELECT p.hash FROM Project p WHERE p.id = :projectId")
    String findHashById(@Param("projectId") Long projectId);
}
