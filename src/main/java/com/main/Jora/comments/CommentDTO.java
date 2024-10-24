package com.main.Jora.comments;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
public class CommentDTO {
    private String text;
    private String username;
    private LocalDateTime createdAt;
}


