package com.main.Jora.duscussion;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class DiscussionCommentDTO {
    private String text;
    private String project_hash;
    private String authorName;
    private Long authorId;
    private LocalDateTime createdAt;
    private List<FileAttachmentDTO> fileAttachmentDTOS;
}
