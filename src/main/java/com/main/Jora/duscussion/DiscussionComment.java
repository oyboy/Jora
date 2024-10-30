package com.main.Jora.duscussion;

import com.main.Jora.models.Project;

import com.main.Jora.models.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
//@Document
@Entity
@ToString
@EqualsAndHashCode
public class DiscussionComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User author;
    @ManyToOne
    @JoinColumn(name = "project_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Project project;
    private String text;
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "discussionComment", cascade = CascadeType.ALL)
    private List<FileAttachment> attachments;

    DiscussionCommentDTO convertToDto(){
        return new DiscussionCommentDTO(
                this.text,
                this.project.getHash(),
                this.author.getUsername(),
                this.author.getId(),
                this.createdAt,
                Optional.ofNullable(this.attachments)
                        .orElse(Collections.emptyList()) // Если attachments null, используем пустой список
                        .stream()
                        .map(attachment -> attachment.convertToDto(this.project.getHash()))
                        .collect(Collectors.toList())
        );
    }
}
