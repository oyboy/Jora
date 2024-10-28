package com.main.Jora.duscussion;

import com.main.Jora.models.Project;

import com.main.Jora.models.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

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
                this.createdAt
        );
    }
}
