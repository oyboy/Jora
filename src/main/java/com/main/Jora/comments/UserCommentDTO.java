package com.main.Jora.comments;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Entity
@NoArgsConstructor
public class UserCommentDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private Long userId;
    private Long commentId;
    private Long taskId;
    @JsonIgnore
    private LocalDateTime readAt;

    public UserCommentDTO(Long userId, Long commentId, Long taskId){
        this.userId = userId;
        this.commentId = commentId;
        this.taskId = taskId;
    }
}