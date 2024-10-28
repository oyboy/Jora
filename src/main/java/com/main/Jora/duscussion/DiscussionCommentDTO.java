package com.main.Jora.duscussion;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DiscussionCommentDTO {
    private String text;
    private String project_hash;
    private String authorName;
    private Long authorId;
    private LocalDateTime createdAt;
}
