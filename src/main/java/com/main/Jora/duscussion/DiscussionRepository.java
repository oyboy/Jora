package com.main.Jora.duscussion;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DiscussionRepository extends CrudRepository<DiscussionComment, Long> {

    List<DiscussionComment> getDiscussionCommentsByProjectId(Long projectId);
}
