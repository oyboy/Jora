package com.main.Jora.discussion;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DiscussionRepository extends MongoRepository<DiscussionComment, String> {

    List<DiscussionComment> getDiscussionCommentsByProjectHash(String projectHash);
}
