package com.main.Jora.duscussion;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Document
@ToString
@EqualsAndHashCode
public class DiscussionComment {
    @Id
    private String id;
    private Long authorId;
    private String authorName;
    private String projectHash;
    private String text;
    private LocalDateTime createdAt;
    private List<FileAttachment> attachments;
    DiscussionCommentDTO convertToDto(){
        return new DiscussionCommentDTO(
                this.text,
                this.projectHash,
                this.authorName,
                this.authorId,
                this.createdAt,
                Optional.ofNullable(this.attachments)
                        .orElse(Collections.emptyList()) // Если attachments null, используем пустой список
                        .stream()
                        .map(attachment -> attachment.convertToDto(this.projectHash))
                        .collect(Collectors.toList())
        );
    }
}
