package com.main.Jora.duscussion;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
public class DiscussionCommentDTO {
    private String text;
    private String project_hash;
    private Long authorId;
    private DiscussionUserDTO userDTO;
    private LocalDateTime createdAt;
    private List<FileAttachmentDTO> fileAttachmentDTOS;
}