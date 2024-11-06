package com.main.Jora.calendar;

import com.main.Jora.enums.Priority;
import com.main.Jora.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CalendarTaskDTO {
    private Long taskId;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
}