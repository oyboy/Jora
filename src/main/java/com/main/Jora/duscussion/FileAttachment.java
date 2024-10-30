package com.main.Jora.duscussion;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Data
//@Document
@Entity
public class FileAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "discussion_comment_id")
    private DiscussionComment discussionComment;
    private String fileName;
    @Lob
    private byte[] bytes;
    private LocalDateTime uploadedAt;

    FileAttachmentDTO convertToDto(String projectHash){
        return new FileAttachmentDTO(
                this.fileName,
                "/projects/" + projectHash + "/api/discussion/download/" + this.id
        );
    }
}

