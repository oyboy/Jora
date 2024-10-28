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
    private String filePath;
    private LocalDateTime uploadedAt;
}

