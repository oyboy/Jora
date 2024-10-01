package com.main.Jora.models;

import com.main.Jora.enums.Priority;
import com.main.Jora.enums.Status;
import lombok.Data;

import java.util.Date;

@Data
public class Task {
    private Long id;
    private String name;
    private String description;
    private Priority priority;
    private Status status;
    private Date createdAt = new Date();
}
