package com.main.Jora.models.dto;

import com.main.Jora.models.Tag;
import com.main.Jora.models.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskTagsDTO {
    private Task task;
    private List<Tag> tags;
}
