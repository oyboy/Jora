package com.main.Jora.repositories;

import com.main.Jora.models.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Long> {
    @Query("SELECT t FROM Tag t JOIN t.projects p WHERE p.id = :projectId")
    List<Tag> findTagsByProjectId(@Param("projectId") Long projectId);
    @Query("SELECT t FROM Tag t " +
            "JOIN t.projects p " +
            "WHERE t.name = :tagName")
    Tag findTagByTagName(@Param("tagName") String tagName);
    @Query("SELECT t FROM Tag t JOIN t.users u WHERE u.id = :userId")
    List<Tag> findTagsByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Tag t " +
            "JOIN t.users u " +
            "JOIN t.projects p " +
            "WHERE u.id = :userId AND p.id = :projectId")
    List<Tag> findTagsByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);

}
