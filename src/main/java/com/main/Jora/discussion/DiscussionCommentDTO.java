package com.main.Jora.discussion;

import com.main.Jora.models.dto.UserAvatarDTO;
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
    private UserAvatarDTO userDTO;
    private LocalDateTime createdAt;
    private List<FileAttachmentDTO> fileAttachmentDTOS;
}