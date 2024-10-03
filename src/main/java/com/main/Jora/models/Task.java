package com.main.Jora.models;

import com.main.Jora.enums.Priority;
import com.main.Jora.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @Size(max=50)
    private String name;
    @Size(max=255)
    private String description;
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.LOW;
    @Enumerated(EnumType.STRING)
    private Status status = Status.CREATED;
    private Date createdAt = new Date();
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn
    private Project project;

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
