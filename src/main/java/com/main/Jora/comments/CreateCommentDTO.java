package com.main.Jora.comments;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

@Data
public class CreateCommentDTO {
    private String text;
    private Long taskId;
}
