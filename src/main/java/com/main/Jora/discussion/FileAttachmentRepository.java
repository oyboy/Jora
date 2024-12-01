package com.main.Jora.discussion;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileAttachmentRepository extends MongoRepository<FileAttachment, String> {
}
