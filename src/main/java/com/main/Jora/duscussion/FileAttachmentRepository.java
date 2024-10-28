package com.main.Jora.duscussion;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileAttachmentRepository extends CrudRepository<FileAttachment, Long> {
}
