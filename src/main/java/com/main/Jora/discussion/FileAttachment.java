package com.main.Jora.discussion;

import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileAttachment {
    @Id
    private String id;
    private String fileName;
    private byte[] bytes;
    private LocalDateTime uploadedAt;

    FileAttachmentDTO convertToDto(String projectHash){
        return new FileAttachmentDTO(
                this.fileName,
                "/projects/" + projectHash + "/api/discussion/download/" + this.id
        );
    }
}