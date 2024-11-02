package com.main.Jora.duscussion;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileAttachmentRepository extends MongoRepository<FileAttachment, String> {
}
