package com.main.Jora.comments;

import com.fasterxml.jackson.annotation.*;
import com.main.Jora.models.Task;
import com.main.Jora.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
public class Comment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 500)
    @Size(max = 500, message = "Содержание комментария не должно превышать 500 символов")
    private String text;
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDateTime createdAt;

    CommentDTO convertToDTO(){
        return new CommentDTO(this.text, this.user.getUsername(), this.createdAt, this.id);
    }
}
