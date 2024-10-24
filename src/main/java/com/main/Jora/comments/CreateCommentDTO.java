package com.main.Jora.comments;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

@Data
@JsonAutoDetect
public class CreateCommentDTO {
    private String text;
    private Long taskId;
    public CreateCommentDTO(String text, Long taskId){
        this.text = text;
        this.taskId = taskId;
    }
    public CreateCommentDTO(){}
}
